package es.sanitas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosCobertura;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosContratacionPlan;
import es.sanitas.bravo.ws.stubs.contratacionws.consultasoperaciones.DatosPlanProducto;
import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.*;
import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Error;
import es.sanitas.soporte.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wscontratacion.beneficiario.vo.ProductoCobertura;
import wscontratacion.contratacion.fuentes.parametros.DatosAlta;
import wscontratacion.contratacion.fuentes.parametros.DatosDomicilio;
import wscontratacion.contratacion.fuentes.parametros.DatosProductoAlta;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class SimulacionCallable implements Callable<TarificacionPoliza> {

    private static final Logger LOG = LoggerFactory.getLogger(SimulacionCallable.class);


    private static final String LINE_BREAK = "<br/>";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String SEPARADOR_TIER = "#";


    private final DatosContratacionPlan oDatosPlan;
    private final DatosAlta oDatosAlta;
    private final List<ProductoPolizas> lProductos;
    private final List<BeneficiarioPolizas> lBeneficiarios;
    private final FrecuenciaEnum frecuencia;
    private final SimulacionWS servicioSimulacion;

    public SimulacionCallable(final DatosContratacionPlan oDatosPlan, final DatosAlta oDatosAlta,
                              final List<ProductoPolizas> lProductos, final List<BeneficiarioPolizas> lBeneficiarios,
                              final FrecuenciaEnum frecuencia, SimulacionWS servicioSimulacion) {

        this.oDatosPlan = oDatosPlan;
        this.oDatosAlta = oDatosAlta;
        this.lProductos = lProductos;
        this.lBeneficiarios = lBeneficiarios;
        this.frecuencia = frecuencia;
        this.servicioSimulacion = servicioSimulacion;
    }
    @Override
    public TarificacionPoliza call() throws Exception {
        return simular();
    }

    private TarificacionPoliza simular() throws ExcepcionContratacion {

        TarificacionPoliza resultado;
        final Simulacion in = new Simulacion();

        if (lBeneficiarios != null) {
            in.setOperacion(StaticVarsContratacion.INCLUSION_BENEFICIARIO);
        } else {
            in.setOperacion(StaticVarsContratacion.ALTA_POLIZA);
        }
        in.setInfoPromociones(obtenerInfoPromociones(oDatosAlta));
        in.setInfoTier(obtenerTier(oDatosAlta));
        in.setListaBeneficiarios(obtenerBeneficiarios(oDatosAlta, lProductos, lBeneficiarios, oDatosPlan));
        in.setInfoContratacion(obtenerInfoContratacion(oDatosAlta, frecuencia, in.getOperacion()));

        final RESTResponse<Tarificacion, Error> response = servicioSimulacion.simular(in);
        if (!response.hasError() && response.out.getTarifas() != null) {
            resultado = new TarificacionPoliza();
            resultado.setTarificacion(response.out);

            // Si se ha introducido un código promocional no válido se repite la simulación sin el
            // código promocional
        } else if (response.hasError() && StaticVarsContratacion.SIMULACION_ERROR_COD_PROMOCIONAL.equalsIgnoreCase(response.error.getCodigo())) {
            if (oDatosAlta instanceof DatosAltaAsegurados) {
                final DatosAltaAsegurados oDatosAltaAsegurados = (DatosAltaAsegurados) oDatosAlta;
                oDatosAltaAsegurados.setCodigoPromocional(null);
            }
            LOG.info(toMensaje(in, response.rawResponse));

            resultado = simular();
            resultado.setCodigoError(StaticVarsContratacion.SIMULACION_ERROR_COD_PROMOCIONAL);
            return resultado;
        } else {
            LOG.error(toMensaje(in, response.rawResponse));
            throw new ExcepcionContratacion(response.error.getDescripcion());
        }

        return resultado;
    }

    /**
     * Parsea el la respuesta y el objeto de entrada al servicio REST
     * @param in entrada
     * @param error respuesta en raw
     * @return mensaje de entrada y salida
     */
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
}
