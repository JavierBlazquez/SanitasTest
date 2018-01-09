package es.sanitas;

import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosContratacionPlan;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosPlanProducto;
import es.sanitas.bravo.ws.stubs.contratacionws.documentacion.Primas;
import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.*;
import es.sanitas.soporte.*;
import es.sanitas.soporte.Recibo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wscontratacion.contratacion.fuentes.parametros.DatosAlta;
import wscontratacion.contratacion.fuentes.parametros.DatosAsegurado;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


public class RealizarSimulacion implements IRealizarSimulacion {


    private static final int NUMERO_HILOS = 4;
    private static final int TIMEOUT = 30;
    private final ExecutorService pool = Executors.newFixedThreadPool(NUMERO_HILOS);

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private static final Logger LOG = LoggerFactory.getLogger(RealizarSimulacion.class);

    private SimulacionWS servicioSimulacion;


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
            solvers.add(simularPolizaFrecuencia(oDatosPlan, oDatosAlta, lProductos, lBeneficiarios, frecuencia));
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
                if (future != null && future.get() != null && future.get().getTarificacion() != null) {
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
            if (resultadoSimulaciones.isEmpty()) {
                throw new ExcepcionContratacion("No se ha podido obtener un precio para el presupuesto. Por favor, inténtelo de nuevo más tarde.");
            }

            final TarificacionPoliza retornoPoliza = resultadoSimulaciones.get(0);


            final Tarificacion retorno = retornoPoliza.getTarificacion();
            final String codigoError = retornoPoliza.getCodigoError();
            if (codigoError != null && !StringUtils.isEmpty(codigoError)) {
                errores.add(codigoError);
            }

            int contadorBeneficiario = 0;
            double css = 0;
            for (final TarifaBeneficiario tarifaBeneficiario : retorno.getTarifas().getTarifaBeneficiarios()) {
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
                            || !comprobarExcepcion(lExcepciones, StaticVarsContratacion.PROMO_ECI_COLECTIVOS)
                            || hayTarjetas(oDatosAlta)) && tarifaProducto.getIdProducto() != 670
                            || !comprobarExcepcion(lExcepciones, StaticVarsContratacion.PROMO_FARMACIA)
                            || hayTarjetas(oDatosAlta)) {

                        PrimasPorProducto oPrimasProducto = new PrimasPorProducto();
                        if (listaProductoPorAseg.size() > contadorProducto) {
                            oPrimasProducto = listaProductoPorAseg.get(contadorProducto);
                        } else {
                            oPrimasProducto.setCodigoProducto(tarifaProducto.getIdProducto().intValue());
                            oPrimasProducto.setNombreProducto(tarifaProducto.getDescripcion());
                            final DatosPlanProducto producto = getDatosProducto(oDatosPlan, tarifaProducto.getIdProducto());
                            if (producto != null) {
                                oPrimasProducto.setObligatorio(producto.isSwObligatorio() ? "S" : "N");
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
                        switch (frecuencia) {
                            case MENSUAL:
                                // Mensual
                                primaProducto.setPrima("" + descuento);
                                break;
                            case TRIMESTRAL:
                                // Trimestral
                                primaProducto.setPrima("" + descuento);
                                break;
                            case SEMESTRAL:
                                // Semestral
                                primaProducto.setPrima("" + descuento * 2);
                                break;
                            case ANUAL:
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
            final DatosContratacionPlan oDatosPlan, final DatosAlta oDatosAlta, final List<ProductoPolizas> lProductos,
            final List<BeneficiarioPolizas> lBeneficiarios, final FrecuenciaEnum frecuencia) {
        return new SimulacionCallable(oDatosPlan, oDatosAlta, lProductos, lBeneficiarios, frecuencia, servicioSimulacion);
    }

    private DatosPlanProducto getDatosProducto(final DatosContratacionPlan oDatosPlan, final long idProducto) {
        for (final DatosPlanProducto producto : oDatosPlan.getProductos()) {
            if (producto.getIdProducto() == idProducto) {
                return producto;
            }
        }
        return null;
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
