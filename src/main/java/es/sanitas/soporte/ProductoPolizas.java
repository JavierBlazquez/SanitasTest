package es.sanitas.soporte;

import java.io.Serializable;

import wscontratacion.comunes.vo.Producto;


/**
 * La Class ProductoPolizas.
 *
 * @author cricor
 */
public class ProductoPolizas extends Producto implements Serializable {

    /** La constante serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** El id codigo. */
    private int idCodigo;

    /**
     * Obtiene el objeto id codigo.
     *
     * @return Returns the idCodigo.
     */
    public int getIdCodigo() {
        return idCodigo;
    }

    /**
     * Asigna valor a id codigo.
     *
     * @param idCodigo
     *            The idCodigo to set.
     */
    public void setIdCodigo( final int idCodigo ) {
        this.idCodigo = idCodigo;
    }

    /**
     * Obtiene el objeto id portal.
     *
     * @return Returns the idPortal.
     */
    @Override
    public int getIdPortal() {
        return super.getIdPortal();
    }

    /**
     * Asigna valor a id portal.
     *
     * @param idPortal
     *            The idPortal to set.
     */
    @Override
    public void setIdPortal( final int idPortal ) {
        super.setIdPortal( idPortal );
    }
}
