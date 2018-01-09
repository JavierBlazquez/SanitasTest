/*
 * Created on 06/10/2010 TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package es.sanitas.soporte;

import java.io.Serializable;

import es.sanitas.bravo.ws.stubs.contratacionws.documentacion.Primas;


/**
 * Clase PrimasPorProducto que contiene el identificador del producto y las primas correspondientes
 * para el desglose en facturación
 */
public class PrimasPorProducto implements Serializable {

    /** La constante serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private Primas primaProducto = new Primas();
    private int codigoProducto = 0;
    private String nombreProducto = "";
    private String obligatorio = "";

    /**
     * @return Returns the primaProducto.
     */
    public Primas getPrimaProducto() {
        return primaProducto;
    }

    /**
     * @param primaProducto
     *            The primaProducto to set.
     */
    public void setPrimaProducto( final Primas primaProducto ) {
        this.primaProducto = primaProducto;
    }

    /**
     * @return Returns the codigoProducto.
     */
    public int getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * @param codigoProducto
     *            The codigoProducto to set.
     */
    public void setCodigoProducto( final int codigoProducto ) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * @return Returns the nombreProducto.
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * @param nombreProducto
     *            The nombreProducto to set.
     */
    public void setNombreProducto( final String nombreProducto ) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * @return Returns the obligatorio.
     */
    public String getObligatorio() {
        return obligatorio;
    }

    /**
     * @param obligatorio
     *            The obligatorio to set.
     */
    public void setObligatorio( final String obligatorio ) {
        this.obligatorio = obligatorio;
    }

}
