package es.sanitas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosCobertura;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosContratacionPlan;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosPlanProducto;
import es.sanitas.bravo.ws.stubs.contratacionws.documentacion.Primas;
import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.*;
import es.sanitas.soporte.*;
import es.sanitas.soporte.Recibo;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wscontratacion.beneficiario.vo.ProductoCobertura;
import wscontratacion.contratacion.fuentes.parametros.DatosAlta;
import wscontratacion.contratacion.fuentes.parametros.DatosAsegurado;
import wscontratacion.contratacion.fuentes.parametros.DatosDomicilio;
import wscontratacion.contratacion.fuentes.parametros.DatosProductoAlta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


public class RealizarSimulacion implements IRealizarSimulacion {

    private static final String LINE_BREAK = "<br/>";
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final int NUMERO_HILOS = 4;
    private static final int TIMEOUT = 30;
    private final ExecutorService pool = Executors.newFixedThreadPool(NUMERO_HILOS);

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final Logger LOG = LoggerFactory.getLogger(RealizarSimulacion.class);

    private SimulacionWS servicioSimulacion;

    private static final String SEPARADOR_TIER = "#";

    /**
     * Método que realiza las llamadas a las diferentes clases de simulación, para tarificar
     *
     * @param oDatosAlta Objeto del tipo DatosAlta
     * @param lProductos Listado de productos que sólo se tendrán en cuenta en caso de inclusión de
     *                   productos, en el resto de casos no aplica
     * @return Map con diferentes valores obtenidos de la simulación, incluida la lista de precios
     * por asegurado
     * @throws Exception             Excepción lanzada en caso de que haya errores
     * @throws ExcepcionContratacion Excepción controlada
     */
    public Map<String, Object> realizarSimulacion(final DatosAlta oDatosAlta, final List<ProductoPolizas> lProductos,
                                                  final List<BeneficiarioPolizas> lBeneficiarios,
                                                  final boolean desglosar, final Map<String, Object> hmValores)
            throws Exception {

        final Map<String, Object> hmSimulacion = new HashMap<>();
        @SuppressWarnings("unchecked") final List<String> lExcepciones = (List<String>) hmValores.get("EXCEPCIONES");
        final DatosContratacionPlan oDatosPlan = (DatosContratacionPlan) hmValores.get(StaticVarsContratacion.DATOS_PLAN);

        final List<Primas> primas = new ArrayList<>();
        final Double descuentosTotales[] = {0.0, 0.0, 0.0, 0.0};
        final Double pagoTotal[] = {0.0, 0.0, 0.0, 0.0};
        final Double precioConPromocion[] = {0.0, 0.0, 0.0, 0.0};
        final List<List<PrimasPorProducto>> primasDesglosadas = new ArrayList<>();
        final List<List<PromocionAplicada>> promociones = new ArrayList<>();
        final List<List<es.sanitas.soporte.Recibo>> recibos = new ArrayList<>();
        final List<String> errores = new ArrayList<>();

        Set<FrecuenciaEnum> frecuenciasTarificar = EnumSet.noneOf(FrecuenciaEnum.class);
        if (hmValores.containsKey(StaticVarsContratacion.FREC_MENSUAL)) {
            frecuenciasTarificar.clear();
            frecuenciasTarificar.add(FrecuenciaEnum.MENSUAL);
        }
        if (lBeneficiarios != null) {
            frecuenciasTarificar.clear();
            frecuenciasTarificar.add(FrecuenciaEnum.obtenerFrecuencia(oDatosAlta.getGenFrecuenciaPago()));
        }
        if (frecuenciasTarificar.isEmpty()) {
            frecuenciasTarificar = EnumSet.allOf(FrecuenciaEnum.class);
        }

        final Collection<Callable<TarificacionPoliza>> solvers = new ArrayList<>(0);
        for (final FrecuenciaEnum frecuencia : frecuenciasTarificar) {
            solvers.add(simularPolizaFrecuencia(hmValores, oDatosAlta, lProductos, lBeneficiarios, frecuencia));
        }
        final CompletionService<TarificacionPoliza> ecs = new ExecutorCompletionService<>(pool);
        int n = 0;
        for (final Callable<TarificacionPoliza> s : solvers) {
            try {
                ecs.submit(s);
                n++;
            } catch (final RuntimeException ree) {
                LOG.error("RejectedExecutionException con el metodo " + s.toString(), ree);
            }
        }
        final List<TarificacionPoliza> resultadoSimulaciones = new ArrayList<>();
        final List<ExecutionException> resultadoExcepciones = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            try {
                final Future<TarificacionPoliza> future = ecs.poll(TIMEOUT, TimeUnit.SECONDS);
                if (future != null) {
                    resultadoSimulaciones.add(future.get());
                } else {
                    LOG.error("La llamada asincrona al servicio de simulacion ha fallado por timeout");
                }
            } catch (final InterruptedException e) {
                LOG.error("InterruptedException", e);
            } catch (final ExecutionException e) {
                LOG.error("ExecutionException", e);
                resultadoExcepciones.add(e);
            }
        }

        if (!resultadoExcepciones.isEmpty()) {
            throw new ExcepcionContratacion(resultadoExcepciones.get(0).getCause().getMessage());
        }

        for (final FrecuenciaEnum frecuencia : frecuenciasTarificar) {
            final TarificacionPoliza retornoPoliza = IterableUtils.find(resultadoSimulaciones,
                    new Predicate<TarificacionPoliza>() {

                        public boolean evaluate(final TarificacionPoliza object) {
                            return object != null && object.getTarificacion() != null;
                        }
                    });

            if (retornoPoliza == null) {
                throw new ExcepcionContratacion("No se ha podido obtener un precio para el presupuesto. Por favor, inténtelo de nuevo más tarde.");
            }
            final Tarificacion retorno = retornoPoliza.getTarificacion();
            final String codigoError = retornoPoliza.getCodigoError();
            if (codigoError != null && !StringUtils.isEmpty(codigoError)) {
                errores.add(codigoError);
            }

            int contadorBeneficiario = 0;
            double css = 0;
            for (final TarifaBeneficiario tarifaBeneficiario : retorno.getTarifas()
                    .getTarifaBeneficiarios()) {
                List<PrimasPorProducto> listaProductoPorAseg = new ArrayList<>();
                if (primasDesglosadas.size() > contadorBeneficiario) {
                    listaProductoPorAseg = primasDesglosadas.get(contadorBeneficiario);
                } else {
                    primasDesglosadas.add(listaProductoPorAseg);
                }

                Primas primaAsegurado = new Primas();
                if (primas.size() <= contadorBeneficiario) {
                    primas.add(primaAsegurado);
                }

                int contadorProducto = 0;
                for (final TarifaProducto tarifaProducto : tarifaBeneficiario.getTarifasProductos()) {

                    if ((tarifaProducto.getIdProducto() != 389
                            || !comprobarExcepcion(lExcepciones,
                            StaticVarsContratacion.PROMO_ECI_COLECTIVOS)
                            || hayTarjetas(oDatosAlta)) && tarifaProducto.getIdProducto() != 670
                            || !comprobarExcepcion(lExcepciones,
                            StaticVarsContratacion.PROMO_FARMACIA)
                            || hayTarjetas(oDatosAlta)) {

                        PrimasPorProducto oPrimasProducto = new PrimasPorProducto();
                        if (listaProductoPorAseg.size() > contadorProducto) {
                            oPrimasProducto = listaProductoPorAseg.get(contadorProducto);
                        } else {
                            oPrimasProducto
                                    .setCodigoProducto(tarifaProducto.getIdProducto().intValue());
                            oPrimasProducto.setNombreProducto(tarifaProducto.getDescripcion());
                            final DatosPlanProducto producto = getDatosProducto(oDatosPlan,
                                    tarifaProducto.getIdProducto());
                            if (producto != null) {
                                oPrimasProducto
                                        .setObligatorio(producto.isSwObligatorio() ? "S" : "N");
                                oPrimasProducto.setNombreProducto(producto.getDescComercial());
                            }
                            listaProductoPorAseg.add(oPrimasProducto);
                        }

                        final TarifaDesglosada tarifaDesglosada = tarifaProducto.getTarifaDesglosada();
                        final Primas primaProducto = oPrimasProducto.getPrimaProducto();

                        // Se calcula el CSS total para poder calcular el precio con promoción
                        css += tarifaDesglosada.getCss();

                        /*
                         * No sumamos tarifaDesglosada.getCss() + tarifaDesglosada.getCssre() porque
                         * la Compensación del Consorcio de Seguros sólo se aplica en la primera
                         * mensualidad. Y queremos mostrar al usuario el precio de todos los meses.
                         */
                        final double pago = tarifaDesglosada.getPrima() + tarifaDesglosada.getISPrima();
                        final double descuento = tarifaDesglosada.getDescuento();
                        switch (frecuencia.getValor()) {
                            case 1:
                                // Mensual
                                primaProducto.setPrima("" + descuento);
                                break;
                            case 2:
                                // Trimestral
                                primaProducto.setPrima("" + descuento);
                                break;
                            case 3:
                                // Semestral
                                primaProducto.setPrima("" + descuento * 2);
                                break;
                            case 4:
                                // Anual
                                primaProducto.setPrima("" + descuento * 2);
                                break;
                        }
                        descuentosTotales[frecuencia.getValor() - 1] += tarifaDesglosada.getDescuento();
                        pagoTotal[frecuencia.getValor() - 1] += pago + tarifaDesglosada.getDescuento();

                    }
                    contadorProducto++;
                }
                contadorBeneficiario++;
            }

            // Promociones aplicadas a la simulación
            promociones.add(recuperarPromocionesAgrupadas(retorno.getPromociones().getListaPromocionesPoliza(), contadorBeneficiario));

            // Lista de recibos del primer año
            if (retorno.getRecibos() != null) {
                recibos.add(toReciboList(retorno.getRecibos().getListaRecibosProductos()));

                // Se calcula el precio total con promoción
                // Es el importe del primer recibo sin el impuesto del consorcio
                precioConPromocion[frecuencia.getValor() - 1] = retorno.getRecibos().getReciboPoliza().getRecibos()[0].getImporte() - css;
            }
        }

        hmSimulacion.put(StaticVarsContratacion.PRIMAS_SIMULACION, primas);
        hmSimulacion.put(StaticVarsContratacion.PRIMAS_SIMULACION_DESGLOSE, primasDesglosadas);
        hmSimulacion.put(StaticVarsContratacion.SIMULACION_PROVINCIA, "Madrid");
        hmSimulacion.put(StaticVarsContratacion.HAY_DESGLOSE, desglosar);
        hmSimulacion.put(StaticVarsContratacion.DESCUENTOS_TOTALES, descuentosTotales);
        hmSimulacion.put(StaticVarsContratacion.TOTAL_ASEGURADOS, primas);
        hmSimulacion.put(StaticVarsContratacion.PROMOCIONES_SIMULACION, promociones);
        hmSimulacion.put(StaticVarsContratacion.RECIBOS_SIMULACION, recibos);
        hmSimulacion.put(StaticVarsContratacion.PAGO_TOTAL, pagoTotal);
        hmSimulacion.put(StaticVarsContratacion.ERROR, errores);

        // Si en la simulación hay apliacada alguna promoción
        // de descuento sobre la prima
        if (hayPromocionDescuento(promociones)) {
            hmSimulacion.put(StaticVarsContratacion.PAGO_TOTAL, precioConPromocion);
            hmSimulacion.put(StaticVarsContratacion.PRECIOS_SIN_PROMOCION_SIMULACION, pagoTotal);
        }
        return hmSimulacion;
    }

    private Callable<TarificacionPoliza> simularPolizaFrecuencia(
            final Map<String, Object> hmValores, final DatosAlta oDatosAlta,
            final List<ProductoPolizas> lProductos,
            final List<BeneficiarioPolizas> lBeneficiarios, final FrecuenciaEnum frecuencia) {
        return new Callable<TarificacionPoliza>() {

            public TarificacionPoliza call() throws ExcepcionContratacion {
                return simular(hmValores, oDatosAlta, lProductos, lBeneficiarios, frecuencia);
            }
        };
    }

    private DatosPlanProducto getDatosProducto(final DatosContratacionPlan oDatosPlan,
                                               final long idProducto) {
        for (final DatosPlanProducto producto : oDatosPlan.getProductos()) {
            if (producto.getIdProducto() == idProducto) {
                return producto;
            }
        }
        return null;
    }

    private TarificacionPoliza simular(final Map<String, Object> hmValores, final DatosAlta oDatosAlta,
                                       final List<ProductoPolizas> lProductos, final List<BeneficiarioPolizas> lBeneficiarios,
                                       final FrecuenciaEnum frecuencia) throws ExcepcionContratacion {

        TarificacionPoliza resultado;
        final Simulacion in = new Simulacion();
        final DatosContratacionPlan oDatosPlan = (DatosContratacionPlan) hmValores.get(StaticVarsContratacion.DATOS_PLAN);

        if (lBeneficiarios != null) {
            in.setOperacion(StaticVarsContratacion.INCLUSION_BENEFICIARIO);
        } else {
            in.setOperacion(StaticVarsContratacion.ALTA_POLIZA);
        }
        in.setInfoPromociones(obtenerInfoPromociones(oDatosAlta));
        in.setInfoTier(obtenerTier(oDatosAlta));
        in.setListaBeneficiarios(obtenerBeneficiarios(oDatosAlta, lProductos, lBeneficiarios, oDatosPlan));
        in.setInfoContratacion(obtenerInfoContratacion(oDatosAlta, frecuencia, in.getOperacion()));

        final RESTResponse<Tarificacion, es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Error> response = servicioSimulacion.simular(in);
        if (!response.hasError() && response.out.getTarifas() != null) {
            resultado = new TarificacionPoliza();
            resultado.setTarificacion(response.out);

            // Si se ha introducido un código promocional no válido se repite la simulación sin el
            // código promocional
        } else if (response.hasError() &&
                StaticVarsContratacion.SIMULACION_ERROR_COD_PROMOCIONAL.equalsIgnoreCase(response.error.getCodigo())) {
            if (oDatosAlta instanceof DatosAltaAsegurados) {
                final DatosAltaAsegurados oDatosAltaAsegurados = (DatosAltaAsegurados) oDatosAlta;
                oDatosAltaAsegurados.setCodigoPromocional(null);
            }
            LOG.info(toMensaje(in, response.rawResponse));

            resultado = simular(hmValores, oDatosAlta, lProductos, lBeneficiarios, frecuencia);
            resultado.setCodigoError(StaticVarsContratacion.SIMULACION_ERROR_COD_PROMOCIONAL);
            return resultado;
        } else {
            System.err.println(toMensaje(in, response.rawResponse));
            throw new ExcepcionContratacion(response.error.getDescripcion());
        }

        return resultado;
    }

    private String toMensaje(final Simulacion in, final String error) {
        final StringBuilder sb = new StringBuilder();
        final ObjectMapper om = new ObjectMapper();
        try {
            sb.append(error);
            sb.append(LINE_BREAK);
            sb.append(LINE_BREAK);
            sb.append(om.writeValueAsString(in));
        } catch (final JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    private InfoPromociones obtenerInfoPromociones(final DatosAlta oDatosAlta) {
        InfoPromociones infoPromociones = null;
        if (oDatosAlta instanceof DatosAltaAsegurados) {
            final DatosAltaAsegurados oDatosAltaAsegurados = (DatosAltaAsegurados) oDatosAlta;
            infoPromociones = new InfoPromociones();
            infoPromociones.setAutomaticas(StaticVarsContratacion.SIMULACION_PROMOCIONES_AUTOMATICAS);
            // Si no se ha introducido un código promocional se debe enviar
            // de cero elementos
            Promocion[] promociones = new Promocion[0];
            final String codigoPromocion = oDatosAltaAsegurados.getCodigoPromocional();
            if (codigoPromocion != null) {
                promociones = new Promocion[1];
                final Promocion promocion = new Promocion();
                promocion.setIdPromocion(codigoPromocion);
                promociones[0] = promocion;
            }
            infoPromociones.setListaPromociones(promociones);
        }
        return infoPromociones;
    }

    private InfoTier obtenerTier(final DatosAlta oDatosAlta) {
        InfoTier infoTier = null;
        if (oDatosAlta instanceof DatosAltaAsegurados) {
            final DatosAltaAsegurados oDatosAltaAsegurados = (DatosAltaAsegurados) oDatosAlta;
            final String coeficientesTier = oDatosAltaAsegurados.getCoeficientesTier();
            if (!StringUtils.isEmpty(coeficientesTier)) {
                final List<String> productos = Arrays.asList("producto-1", "producto-5", "producto-3");
                final String[] st = coeficientesTier.split(SEPARADOR_TIER);

                infoTier = new InfoTier();
                final List<TierProducto> tierProductos = new ArrayList<>();
                int i = 1;
                for (final String idProducto : productos) {
                    final TierProducto tier = new TierProducto();
                    tier.setIdProducto(Integer.valueOf(idProducto));
                    tier.setValor(Double.valueOf(st[i++]));
                    tierProductos.add(tier);
                }

                infoTier.setListaTierProductos(tierProductos.toArray(new TierProducto[0]));
                infoTier.setTierGlobal(Double.valueOf(st[st.length - 1]).intValue());
            }
        }
        return infoTier;
    }

    private InfoContratacion obtenerInfoContratacion(final DatosAlta oDatosAlta, final FrecuenciaEnum frecuencia, final Integer tipoOperacion) {
        final InfoContratacion infoContratacion = new InfoContratacion();

        infoContratacion.setCodigoPostal(String.format("%05d", ((DatosDomicilio) oDatosAlta.getDomicilios().get(0)).getCodPostal()));
        infoContratacion.setFechaEfecto(oDatosAlta.getFAlta());
        infoContratacion.setFrecuenciaPago(frecuencia.getValor());

        final Long idPoliza = oDatosAlta.getIdPoliza();
        // Si disponemos de la póliza se trata de una inclusión (productos o beneficiarios)
        // o un alta en un póliza colectiva
        if (idPoliza != null && idPoliza != 0L) {

            final DatosAltaAsegurados oDatosAltaAsegurados = (DatosAltaAsegurados) oDatosAlta;

            // El número de póliza debe indicarse para inclusiones de beneficiarios
            // y todas las operaciones (altas/inclusiones de productos) de pólizas colectivas
            // No debe indicarse para inclusiones de productos particulares
            if (StaticVarsContratacion.INCLUSION_BENEFICIARIO == tipoOperacion
                    || oDatosAltaAsegurados.getIdColectivo() > 0
                    || (oDatosAlta.getIdDepartamento() >= 0 && oDatosAlta.getIdEmpresa() != null)) {
                infoContratacion.setIdPoliza(idPoliza.intValue());
            }
            // El número de colectivo se debe incluir en inclusiones de beneficiarios
            if (StaticVarsContratacion.INCLUSION_BENEFICIARIO == tipoOperacion) {
                infoContratacion.setIdColectivo(oDatosAltaAsegurados.getIdColectivo());
            }
            // El número de departamento debe incluirse en operaciones con pólizas colectivas
            if (oDatosAlta.getIdDepartamento() >= 0) {
                infoContratacion.setIdDepartamento(oDatosAlta.getIdDepartamento());
            }

            // El número de empresa debe incluise en operaciones con pólizas colectivas
            if (oDatosAlta.getIdEmpresa() != null) {
                infoContratacion.setIdEmpresa(oDatosAlta.getIdEmpresa().intValue());
            }
        }
        if (oDatosAlta.getIdMediador() != null) {
            infoContratacion.setIdMediador(oDatosAlta.getIdMediador().intValue());
        }
        infoContratacion.setIdPlan(oDatosAlta.getIdPlan());

        return infoContratacion;
    }

    private Beneficiario[] obtenerBeneficiarios(final DatosAlta oDatosAlta, final List<ProductoPolizas> lProductos,
                                                final List<BeneficiarioPolizas> lBeneficiarios, final DatosContratacionPlan oDatosPlan) {
        final List<Beneficiario> beneficiarios = new ArrayList<>();

        // Si hay lista de beneficiarios se trata de una inclusion de beneficiarios
        if (lBeneficiarios != null && lBeneficiarios.size() > 0) {
            for (final BeneficiarioPolizas oBeneficiario : lBeneficiarios) {
                final es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Beneficiario beneficiario = new es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Beneficiario();
                beneficiario.setFechaNacimiento(cambiarFecha(oBeneficiario.getDatosPersonales().getFNacimiento()));
                beneficiario.setParentesco(11);
                beneficiario.setSexo(oBeneficiario.getDatosPersonales().getGenSexo());
                if (oBeneficiario.getDatosPersonales().getIdProfesion() > 0) {
                    beneficiario.setIdProfesion(oBeneficiario.getDatosPersonales().getIdProfesion());
                } else {
                    beneficiario.setIdProfesion(1);
                }
                beneficiario.setNombre(oBeneficiario.getDatosPersonales().getNombre());
                final Producto[] productos = obtenerProductosAsegurado(oDatosAlta.getTitular().getProductosContratados(), oDatosPlan);
                beneficiario.setListaProductos(productos);
                beneficiarios.add(beneficiario);
            }
        } else {
            // Si no hay lista de beneficiarios se trata de un alta
            // Primero se procesa el titular
            Beneficiario beneficiario = new Beneficiario();

            beneficiario.setFechaNacimiento(cambiarFecha(oDatosAlta.getTitular().getDatosPersonales().getFNacimiento()));
            beneficiario.setParentesco(1);
            // aunque se permite el genero 3 cuando no hay uno definido no podemos usarlo.
            // Así que enviamos un 2 (por temas de ginecologia tambien).
            beneficiario.setSexo(oDatosAlta.getTitular().getDatosPersonales().getGenSexo() == 0 ? 2 : oDatosAlta.getTitular().getDatosPersonales().getGenSexo());
            beneficiario.setIdProfesion(1);
            beneficiario.setNombre(String.valueOf(oDatosAlta.getTitular().getDatosPersonales().getNombre()));
            if (oDatosAlta.getTitular() instanceof DatosAseguradoInclusion) {
                final DatosAseguradoInclusion dai = (DatosAseguradoInclusion) oDatosAlta.getTitular();
                if (dai.getSIdCliente() != null && dai.getSIdCliente() > 0) {
                    beneficiario.setIdCliente(dai.getSIdCliente().intValue());
                }
            }

            // Si hay lista de productos se incluyen como productos añadidos al alta
            Producto[] productos = obtenerProductosAsegurado(oDatosAlta.getTitular().getProductosContratados(), oDatosPlan);
            if (lProductos != null && !lProductos.isEmpty()) {
                productos = ArrayUtils.addAll(productos, obtenerProductos(lProductos.get(0).getProductos(), oDatosPlan));
            }
            beneficiario.setListaProductos(productos);

            /*
             * // Las procedencias pueden venir de tarjetas Sanitas List<String> tarjetas = ( (
             * DatosAltaAsegurados )oDatosAlta ).getLNumTarjAsegurados(); if (tarjetas != null &&
             * !tarjetas.isEmpty()){ String tarjeta = tarjetas.get( 0 ); obtenerProcedencia(tarjeta,
             * oDatosAlta.getTitular().getDatosPersonales(), beneficiario); }else{
             * beneficiario.setProcedencia( obtenerProcedencia(lProductos) ); }
             */
            beneficiarios.add(beneficiario);

            // Y luego se procesan el resto de asegurados
            if (oDatosAlta.getAsegurados() != null && oDatosAlta.getAsegurados().size() > 0) {
                final Iterator<DatosAseguradoInclusion> iteradorAsegurados = oDatosAlta.getAsegurados().iterator();
                int contadorBeneficiario = 1;
                while (iteradorAsegurados.hasNext()) {
                    final DatosAseguradoInclusion oDatosAsegurado = iteradorAsegurados.next();

                    beneficiario = new es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Beneficiario();

                    beneficiario.setFechaNacimiento(cambiarFecha(oDatosAsegurado.getDatosPersonales().getFNacimiento()));
                    beneficiario.setParentesco(11);
                    // Bravo son unos tocapelotas y aunque permiten el genero 3 cuando no hay uno
                    // definido no podemos usarlo.
                    // Así que enviamos un 2 (por temas de ginecologia tambien).
                    beneficiario.setSexo(oDatosAsegurado.getDatosPersonales().getGenSexo() == 0 ? 2 : oDatosAsegurado.getDatosPersonales().getGenSexo());
                    beneficiario.setNombre(oDatosAsegurado.getDatosPersonales().getNombre());
                    beneficiario.setIdProfesion(1);
                    if (oDatosAsegurado.getSIdCliente() != null) {
                        beneficiario.setIdCliente(oDatosAsegurado.getSIdCliente().intValue());
                    }

                    productos = obtenerProductosAsegurado(oDatosAsegurado.getProductosContratados(), oDatosPlan);
                    if (lProductos != null && !lProductos.isEmpty()) {
                        productos = ArrayUtils.addAll(productos, obtenerProductos(lProductos.get(contadorBeneficiario).getProductos(), oDatosPlan));
                    }
                    beneficiario.setListaProductos(productos);

                    beneficiarios.add(beneficiario);
                    contadorBeneficiario++;
                }
            }
        }

        return beneficiarios.toArray(
                new es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Beneficiario[0]);
    }

    /**
     * Comprueba si alguna de las promociones aplicadas en la simulación es un descuento en la
     * prima.
     *
     * @param promocionesAplicadas simulación múltiple realizada
     */
    private boolean hayPromocionDescuento(final List<List<PromocionAplicada>> promocionesAplicadas) {
        boolean codigoAplicado = Boolean.FALSE;
        if (promocionesAplicadas != null) {
            for (final List<PromocionAplicada> promociones : promocionesAplicadas) {
                for (final PromocionAplicada promocion : promociones) {
                    if (promocion != null && TipoPromocionEnum.DESCUENTO_PORCENTAJE.equals(promocion.getTipoPromocion())) {
                        codigoAplicado = Boolean.TRUE;
                    }
                }
            }
        }
        return codigoAplicado;
    }

    private Producto[] obtenerProductos(final List<ProductoCobertura> productosCobertura, final DatosContratacionPlan oDatosPlan) {
        final List<Producto> productos = new ArrayList<>();
        if (productosCobertura != null && !productosCobertura.isEmpty()) {
            for (final ProductoCobertura producto : productosCobertura) {
                productos.add(obtenerProducto(producto, oDatosPlan));
            }
        }

        return productos.toArray(new Producto[0]);
    }

    private Producto[] obtenerProductosAsegurado(final List<DatosProductoAlta> productosCobertura, final DatosContratacionPlan oDatosPlan) {
        final List<Producto> productos = new ArrayList<>();
        if (productosCobertura != null && !productosCobertura.isEmpty()) {
            for (final DatosProductoAlta producto : productosCobertura) {
                productos.add(obtenerProducto(producto, oDatosPlan));
            }
        }

        return productos.toArray(new Producto[0]);
    }

    private Producto obtenerProducto(final DatosProductoAlta productoAlta, final DatosContratacionPlan oDatosPlan) {
        final Producto producto = new Producto();
        final int idProducto = productoAlta.getIdProducto();
        producto.setIdProducto(idProducto);
        producto.setListaCoberturas(obtenerCoberturas(idProducto, oDatosPlan));
        return producto;
    }

    private Producto obtenerProducto(final ProductoCobertura productoCobertura, final DatosContratacionPlan oDatosPlan) {
        final Producto producto = new Producto();
        final int idProducto = productoCobertura.getIdProducto();
        producto.setIdProducto(idProducto);
        producto.setListaCoberturas(obtenerCoberturas(idProducto, oDatosPlan));
        return producto;
    }

    private Cobertura[] obtenerCoberturas(final int idProducto, final DatosContratacionPlan oDatosPlan) {
        final List<Cobertura> coberturas = new ArrayList<>();

        final Iterator<DatosPlanProducto> iteradorProdsPlan = oDatosPlan.getProductos().iterator();
        boolean found = false;
        while (iteradorProdsPlan.hasNext() && !found) {
            final DatosPlanProducto productoPlan = iteradorProdsPlan.next();
            if (idProducto == productoPlan.getIdProducto()) {
                found = true;
                for (final DatosCobertura oDatosCobertura : productoPlan.getCoberturas()) {
                    if (oDatosCobertura.isSwObligatorio()
                            && oDatosCobertura.getCapitalMinimo() != null
                            && oDatosCobertura.getCapitalMinimo() > 0) {
                        final Cobertura cobertura = new Cobertura();
                        cobertura
                                .setCapital(Double.valueOf(oDatosCobertura.getCapitalMinimo()));
                        cobertura.setIdCobertura(oDatosCobertura.getIdCobertura().intValue());
                        coberturas.add(cobertura);
                    }
                }
            }
        }

        return coberturas.toArray(new Cobertura[0]);
    }


    /**
     * Método que recibe una fecha en formato String. Si la fecha está en formato edad, lo
     * transforma a formato fecha.
     *
     * @param fecha a cambiar
     * @return la nueva fecha
     **/
    private String cambiarFecha(String fecha) {
        String convertida = fecha;

        if (fecha == null || "//".equals(fecha)) {
            // Si viene null, le ponemos que tiene 18
            fecha = "18";
        }

        if (!fecha.contains("/")) {
            final int edad = Integer.valueOf(fecha);
            final Calendar dob = Calendar.getInstance();
            dob.add(Calendar.YEAR, -edad);
            dob.set(Calendar.DAY_OF_MONTH, 1);
            final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            convertida = sdf.format(dob.getTime());
        }
        return convertida;
    }

    /**
     * @param oDatosAlta datos de alta
     * @return true si el titular o alguno de los asegurados tiene tarjeta de sanitas.
     */
    private boolean hayTarjetas(final DatosAlta oDatosAlta) {
        boolean tieneTarjeta = false;
        if (oDatosAlta != null && oDatosAlta.getTitular() != null) {
            if ("S".equals(oDatosAlta.getTitular().getSwPolizaAnterior())) {
                tieneTarjeta = true;
            }
        }
        if (oDatosAlta != null && oDatosAlta.getAsegurados() != null && oDatosAlta.getAsegurados().size() > 0) {
            @SuppressWarnings("unchecked") final Iterator<DatosAseguradoInclusion> iterAseg = oDatosAlta
                    .getAsegurados().iterator();
            while (iterAseg.hasNext()) {
                final DatosAsegurado aseg = iterAseg.next();
                if ("S".equals(aseg.getSwPolizaAnterior())) {
                    tieneTarjeta = true;
                }
            }
        }
        return tieneTarjeta;
    }

    /**
     * Popula una lista de objetos PromocionAplicada con la información de las promociones
     * aplicadas.
     *
     * @param promociones promociones aplicadas a la tarificación.
     * @return lista de PromocionAplicada con la información de las promociones aplicadas.
     */
    private List<PromocionAplicada> toPromocionAplicadaList(final Promocion[] promociones) {
        final List<PromocionAplicada> promocionesParam = new ArrayList<>();

        for (final Promocion promocion : promociones) {
            final PromocionAplicada promocionParam = toPromocionAplicada(promocion);
            if (promocionParam != null) {
                promocionesParam.add(promocionParam);
            }
        }

        return promocionesParam;
    }

    /**
     * Recupera las promociones aplicadas a la póliza.
     *
     * @param promociones      promociones aplicadas a cada asegurado.
     * @param numeroAsegurados número asegurados de la póliza
     * @return promociones aplicadas a la póliza.
     */
    private List<PromocionAplicada> recuperarPromocionesAgrupadas(final Promocion[] promociones,
                                                                  final int numeroAsegurados) {

        List<PromocionAplicada> promocionesAgrupadas = new ArrayList<>();
        if (promociones != null && promociones.length > 0) {
            final int numPromociones = promociones.length / numeroAsegurados;
            promocionesAgrupadas = toPromocionAplicadaList(Arrays.copyOfRange(promociones, 0, numPromociones));
        }
        return promocionesAgrupadas;
    }

    /**
     * Popula un objeto PromocionAplicada con la información de una promoción aplicada a la
     * simulación.
     *
     * @param promocion promoción aplicada a la simulación
     * @return objeto PromocionAplicada con los datos de la promoción aplicada a la simulación.
     */
    private PromocionAplicada toPromocionAplicada(final Promocion promocion) {
        PromocionAplicada promocionParam = null;
        if (promocion != null) {
            promocionParam = new PromocionAplicada();
            promocionParam.setIdPromocion(promocion.getIdPromocion() != null ? Long.valueOf(promocion.getIdPromocion()) : null);
            promocionParam.setDescripcion(promocion.getDescripcion());
            promocionParam.setTipoPromocion(TipoPromocionEnum.obtenerTipoPromocion(promocion.getTipo()));
        }
        return promocionParam;
    }

    /**
     * Popula una lista de Recibo con la información de los recibos de la simulación.
     *
     * @param recibos recibos del primer año de la simulación
     * @return lista de Recibo con la información de los recibos de la simulación.
     */
    private List<Recibo> toReciboList(final ReciboProducto[] recibos) {
        final List<Recibo> recibosList = new LinkedList<>();

        if (recibos != null) {
            for (final ReciboProducto recibo : recibos) {
                final Recibo reciboParam = toRecibo(recibo);
                if (reciboParam != null) {
                    recibosList.add(reciboParam);
                }
            }
        }
        return recibosList;
    }

    /**
     * Popula un objeto ReciboProviderOutParam con la simulación de un recibo.
     *
     * @param recibo datos del recibo
     * @return objeto ReciboProviderOutParam con la simulación de un recibo.
     */
    private Recibo toRecibo(final ReciboProducto recibo) {
        Recibo reciboParam = null;
        if (recibo != null) {
            reciboParam = new Recibo();
            final Calendar fechaEmision = Calendar.getInstance();
            try {
                fechaEmision.setTime(sdf.parse("25/12/2016"));
            } catch (final ParseException e) {
                LOG.error("Error parse date", e);
            }
            reciboParam.setFechaEmision(fechaEmision);
            reciboParam.setImporte(recibo.getIdProducto() * 1000.);
        }
        return reciboParam;
    }

    /**
     * @return the servicioSimulacion
     */
    public SimulacionWS getServicioSimulacion() {
        return servicioSimulacion;
    }

    /**
     * @param servicioSimulacion the servicioSimulacion to set
     */
    public void setServicioSimulacion(final SimulacionWS servicioSimulacion) {
        this.servicioSimulacion = servicioSimulacion;
    }

    /**
     * Comprueba si pertenece la excepcion a la lista.
     *
     * @param lExcepciones Lista de excepciones.
     * @param comprobar    Dato a comprobar.
     * @return True si pertenece false en caso contrario.
     */
    private static boolean comprobarExcepcion(final List<String> lExcepciones, final String comprobar) {
        LOG.debug("Se va a comprobar si " + comprobar + " esta en la lista " + lExcepciones);
        boolean bExcepcion = false;
        if (comprobar != null && lExcepciones != null && lExcepciones.contains(comprobar)) {
            bExcepcion = true;
        }
        return bExcepcion;
    }

}
