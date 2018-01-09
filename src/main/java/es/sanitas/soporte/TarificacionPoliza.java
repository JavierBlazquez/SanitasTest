package es.sanitas.soporte;

import java.io.Serializable;

import es.sanitas.seg.simulacionpoliza.services.api.simulacion.vo.Tarificacion;


/**
 * Clase que engloba la tarificación devuelta por el servicio de Bravo.
 *
 */
public class TarificacionPoliza implements Serializable {

    /** UID */
    private static final long serialVersionUID = 1440949283270782503L;

    /** Tarificación simulada. */
    private Tarificacion tarificacion;

    /** Código de error. */
    private String codigoError;


    /**
     * @return the tarificacion
     */
    public Tarificacion getTarificacion() {
        return tarificacion;
    }


    /**
     * @param tarificacion the tarificacion to set
     */
    public void setTarificacion( final Tarificacion tarificacion ) {
        this.tarificacion = tarificacion;
    }


    /**
     * @return the codigoError
     */
    public String getCodigoError() {
        return codigoError;
    }


    /**
     * @param codigoError the codigoError to set
     */
    public void setCodigoError( final String codigoError ) {
        this.codigoError = codigoError;
    }



}
