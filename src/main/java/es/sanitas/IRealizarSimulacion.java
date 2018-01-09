package es.sanitas;

import es.sanitas.soporte.BeneficiarioPolizas;
import es.sanitas.soporte.ProductoPolizas;
import wscontratacion.contratacion.fuentes.parametros.DatosAlta;

import java.util.List;
import java.util.Map;

public interface IRealizarSimulacion {

    Map<String, Object> realizarSimulacion(final DatosAlta oDatosAlta, final List<ProductoPolizas> lProductos,
                                           final List<BeneficiarioPolizas> lBeneficiarios,
                                           final boolean desglosar, final Map<String, Object> hmValores) throws Exception;
}
