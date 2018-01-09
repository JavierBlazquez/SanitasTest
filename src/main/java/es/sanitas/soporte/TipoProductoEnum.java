package es.sanitas.soporte;

/**
 *
 */
public enum TipoProductoEnum {

    BASE, OBLIGATORIO, COMPLEMENTO;

    /**
     * Permite obtener el tipo a partir del nombre.
     *
     * @param valor
     *            el nombre del tipo
     * @return el tipo
     */
    public static TipoProductoEnum obtenerTipo( final String nombre ) {
        TipoProductoEnum tipo = null;
        for( int i = 0; i < TipoProductoEnum.values().length; i++ ) {
            tipo = TipoProductoEnum.values()[ i ];
            if( tipo.name().equals( nombre ) ) {
                break;
            }
        }
        return tipo;
    }

}