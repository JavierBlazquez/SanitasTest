package es.sanitas.soporte;


/**
 * La Class ExcepcionContratacion.
 */
public class ExcepcionContratacion extends Exception {

    /** La constante serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instancia un nuevo excepcion contratacion.
     */
    public ExcepcionContratacion() {

    }

    /**
     * Instancia un nuevo excepcion contratacion.
     *
     * @param msg
     *            el msg
     */
    public ExcepcionContratacion( final String msg ) {
        super( msg );
    }

    /**
     * Instancia un nuevo excepcion contratacion.
     *
     * @param e
     *            el e
     */
    public ExcepcionContratacion( final Exception e ) {
        super( e );
    }
}