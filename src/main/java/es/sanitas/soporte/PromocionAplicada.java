package es.sanitas.soporte;

import java.io.Serializable;

/**
 * Bean para recoger las promociones aplicadas en la simulación.
 */
@SuppressWarnings( "serial" )
public class PromocionAplicada implements Serializable {

    /** Identificador de la promocion. */
    private Long idPromocion;

    /** Código de la promoción. */
    private String codigoPromocion;

    /** Descripción de la promoción. */
    private String descripcion;

    /** Tipo de promocion. */
    private TipoPromocionEnum tipoPromocion;


    /**
     * @return the idPromocion
     */
    public Long getIdPromocion() {
        return idPromocion;
    }


    /**
     * @param idPromocion the idPromocion to set
     */
    public void setIdPromocion( final Long idPromocion ) {
        this.idPromocion = idPromocion;
    }


    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }


    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion( final String descripcion ) {
        this.descripcion = descripcion;
    }


    /**
     * @return the tipoPromocion
     */
    public TipoPromocionEnum getTipoPromocion() {
        return tipoPromocion;
    }


    /**
     * @param tipoPromocion the tipoPromocion to set
     */
    public void setTipoPromocion( final TipoPromocionEnum tipoPromocion ) {
        this.tipoPromocion = tipoPromocion;
    }

    /**
     * @return the codigoPromocion
     */
    public String getCodigoPromocion() {
        return codigoPromocion;
    }

    /**
     * @param codigoPromocion the codigoPromocion to set
     */
    public void setCodigoPromocion( final String codigoPromocion ) {
        this.codigoPromocion = codigoPromocion;
    }
}
