package es.sanitas.soporte;

import java.io.Serializable;


/**
 * La Class Cuestionario.
 *
 * @author afernandezpu
 */
public class Cuestionario implements Serializable {

    private static final long serialVersionUID = -1119778426628837731L;

    /** El id solicitud. */
    protected String idSolicitud;

    /** El num asegurado. */
    protected int numAsegurado;

    private int idPregunta;

    private String idRespuesta;

    private int nivel;

    private String respuesta;

    private String swContestada;

    private int idBloque;

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud( final String idSolicitud ) {
        this.idSolicitud = idSolicitud;
    }

    public int getNumAsegurado() {
        return numAsegurado;
    }

    public void setNumAsegurado( final int numAsegurado ) {
        this.numAsegurado = numAsegurado;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta( final int idPregunta ) {
        this.idPregunta = idPregunta;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel( final int nivel ) {
        this.nivel = nivel;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta( final String respuesta ) {
        this.respuesta = respuesta;
    }

    public String getSwContestada() {
        return swContestada;
    }

    public void setSwContestada( final String swContestada ) {
        this.swContestada = swContestada;
    }

    public int getIdBloque() {
        return idBloque;
    }

    public void setIdBloque( final int idBloque ) {
        this.idBloque = idBloque;
    }

    public String getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta( final String idRespuesta ) {
        this.idRespuesta = idRespuesta;
    }

}
