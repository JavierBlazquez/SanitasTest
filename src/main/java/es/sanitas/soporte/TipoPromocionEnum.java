package es.sanitas.soporte;

/**
 * Tipos de promociones aplicadas en una simulación.
 */
public enum TipoPromocionEnum {

    DESCUENTO_PORCENTAJE (0, "Se aplica un porcentaje de descuento menor al 100% en algunas mensualidades."),
    MESES_GRATIS(1, "Se aplica un porcentaje de descuento sobre la prima del producto en algunas mensualidades."),
    DESCUENTO_FIJO (2, "El precio del producto se fija a una prima."),
    DESCUENTO_PRIMA_FINAL (3, "Descuento fijo en prima final."),
    DESCUENTO_PORCENTAJE_DE_SUBIDA (4, "Se aplica un porcentaje de descuento sobre la prima de subida");

    /** Identificador de tipo de promoción. */
    private Integer idTipo;

    /** Descripción del tipo de */
    private String descripcion;

    /**
     * Permite obtener el tipo de promocion a partir del valor.
     *
     * @param idTipo
     *            el identificador del tipo deseado.
     * @return el tipo de promocion.
     */
    public static TipoPromocionEnum obtenerTipoPromocion( final Integer idTipo ) {
        TipoPromocionEnum tipoPromocion = null;
        if( idTipo != null ) {
            for( int i = 0; i < TipoPromocionEnum.values().length; i++ ) {
                tipoPromocion = TipoPromocionEnum.values()[ i ];
                if( tipoPromocion.getIdTipo().compareTo( idTipo ) == 0 ) {
                    break;
                }
            }
        }
        return tipoPromocion;
    }

    /**
     * Constructor con parámetros.
     *
     * @param idTipo
     * @param descripcion
     */
    TipoPromocionEnum( final Integer idTipo, final String descripcion ) {
        this.idTipo = idTipo;
        this.descripcion = descripcion;
    }

    /**
     * @return the idTipo
     */
    public Integer getIdTipo() {
        return idTipo;
    }

    /**
     * @param idTipo
     *            the idTipo to set
     */
    public void setIdTipo( final Integer idTipo ) {
        this.idTipo = idTipo;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion
     *            the descripcion to set
     */
    public void setDescripcion( final String descripcion ) {
        this.descripcion = descripcion;
    }

}
