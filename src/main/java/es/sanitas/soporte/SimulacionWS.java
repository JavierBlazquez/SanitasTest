package es.sanitas.soporte;

import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Simulacion;
import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Tarificacion;


public interface SimulacionWS {

    RESTResponse< Tarificacion, es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Error > simular(Simulacion in);

}
