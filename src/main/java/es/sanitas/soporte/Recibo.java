package es.sanitas.soporte;

import java.io.Serializable;
import java.util.Calendar;


/**
 * Bean que representa el resumen de un recibo fruto de la simulaciÃ³n.
 */
public class Recibo implements Serializable {

    private static final long serialVersionUID = 2380410183405582478L;

    /** Fecha de emisiÃ³n del recibo */
    private Calendar fechaEmision;

    /** Importe del recibo. */
    private Double importe;

    /**
     * @return the fechaEmision
     */
    public Calendar getFechaEmision() {
        return fechaEmision;
    }

    /**
     * @param fechaEmision
     *            the fechaEmision to set
     */
    public void setFechaEmision( final Calendar fechaEmision ) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * @return the importe
     */
    public Double getImporte() {
        return importe;
    }

    /**
     * @param importe
     *            the importe to set
     */
    public void setImporte( final Double importe ) {
        this.importe = importe;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "SimulacionReciboParam [" );
        if( fechaEmision != null ) {
            builder.append( "fechaEmision=" );
            builder.append( fechaEmision );
            builder.append( ", " );
        }
        if( importe != null ) {
            builder.append( "importe=" );
            builder.append( importe );
        }
        builder.append( "]" );
        return builder.toString();
    }

}
