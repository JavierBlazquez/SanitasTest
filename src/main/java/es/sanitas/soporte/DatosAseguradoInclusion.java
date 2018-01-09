package es.sanitas.soporte;

import java.io.Serializable;
import java.util.List;

import wscontratacion.contratacion.fuentes.parametros.DatosAsegurado;


/**
 * La Class DatosAseguradoInclusion.
 *
 * @author cricor
 */
public class DatosAseguradoInclusion extends DatosAsegurado implements Serializable {

    /** La constante serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** El s id cliente. */
    Long sIdCliente = null;

    /** El id oficina. */
    int idOficina = 0;

    /** El num asegurado. */
    int numAsegurado = 0;

    /** Correo ElÃ©ctronico */
    protected String eMail = "";

    /** TelÃ©fono particualar */
    protected String sNumParticular = "";

    /** TelÃ©fono mÃ³vil */
    protected String sNumMovil = "";

    private String swMadre;
    private String nombreMadre;
    private String numTarjetaMadre;
    private String nombrePadre;
    private String numTarjetaPadre;
    private String swPadre;
    private String swCompetencia;
    private String nombreCompetencia;
    private int compania;
    private List<Cuestionario> respuestasVo;
    // Es cliente ya de Sanitas
    private String numPoliza;
    private String swCheckEsCliente;
    /**
     * Constructor de la clase DatosAseguradoInclusion.java
     */
    public DatosAseguradoInclusion() {

    }

    /**
     * Constructor de la clase DatosAseguradoInclusion.java
     *
     * @param da
     */
    public DatosAseguradoInclusion( final DatosAsegurado da ) {

        this.setDatosPersonales( da.getDatosPersonales() );
        this.setGenParentesco( da.getGenParentesco() );
        this.setProblemas( da.getProblemas() );
        this.setProductosContratados( da.getProductosContratados() );
        this.setRespuestas( da.getRespuestas() );
        this.setSwCoberturaEmbarazo( da.getSwCoberturaEmbarazo() );
        this.setSwCuestionario( da.getSwCuestionario() );
        this.setSwPolizaAnterior( da.getSwPolizaAnterior() );

    }

    /**
     * Obtiene el objeto num asegurado.
     *
     * @return Returns the numAsegurado.
     */
    public int getNumAsegurado() {
        return numAsegurado;
    }

    /**
     * Asigna valor a num asegurado.
     *
     * @param numAsegurado
     *            The numAsegurado to set.
     */
    public void setNumAsegurado( final int numAsegurado ) {
        this.numAsegurado = numAsegurado;
    }

    /**
     * Obtiene el objeto s id cliente.
     *
     * @return Returns the sIdCliente.
     */
    public Long getSIdCliente() {
        return sIdCliente;
    }

    /**
     * Asigna valor a s id cliente.
     *
     * @param idCliente
     *            The sIdCliente to set.
     */
    public void setSIdCliente( final Long idCliente ) {
        sIdCliente = idCliente;
    }

    /**
     * Obtiene el objeto id oficina.
     *
     * @return Returns the idOficina.
     */
    public int getIdOficina() {
        return idOficina;
    }

    /**
     * Asigna valor a id oficina.
     *
     * @param idOficina
     *            The idOficina to set.
     */
    public void setIdOficina( final int idOficina ) {
        this.idOficina = idOficina;
    }

    /**
     * MÃ©todo que devuelve el valor de la variable eMail.
     *
     * @return Devuelve el valor de la variable eMail de tipo String.
     */
    public String geteMail() {
        return eMail;
    }

    /**
     * @param eMail
     *            the eMail to set
     */
    public void seteMail( final String eMail ) {
        this.eMail = eMail;
    }

    /**
     * MÃ©todo que devuelve el valor de la variable sNumParticular.
     *
     * @return Devuelve el valor de la variable sNumParticular de tipo String.
     */
    public String getsNumParticular() {
        return sNumParticular;
    }

    /**
     * @param sNumParticular
     *            the sNumParticular to set
     */
    public void setsNumParticular( final String sNumParticular ) {
        this.sNumParticular = sNumParticular;
    }

    /**
     * MÃ©todo que devuelve el valor de la variable sNumMovil.
     *
     * @return Devuelve el valor de la variable sNumMovil de tipo String.
     */
    public String getsNumMovil() {
        return sNumMovil;
    }

    /**
     * @param sNumMovil
     *            the sNumMovil to set
     */
    public void setsNumMovil( final String sNumMovil ) {
        this.sNumMovil = sNumMovil;
    }


    /**
     * @return the swMadre
     */
    public String getSwMadre() {
        return swMadre;
    }


    /**
     * @param swMadre the swMadre to set
     */
    public void setSwMadre( final String swMadre ) {
        this.swMadre = swMadre;
    }


    /**
     * @return the nombreMadre
     */
    public String getNombreMadre() {
        return nombreMadre;
    }


    /**
     * @param nombreMadre the nombreMadre to set
     */
    public void setNombreMadre( final String nombreMadre ) {
        this.nombreMadre = nombreMadre;
    }


    /**
     * @return the numTarjetaMadre
     */
    public String getNumTarjetaMadre() {
        return numTarjetaMadre;
    }


    /**
     * @param numTarjetaMadre the numTarjetaMadre to set
     */
    public void setNumTarjetaMadre( final String numTarjetaMadre ) {
        this.numTarjetaMadre = numTarjetaMadre;
    }


    /**
     * @return the nombrePadre
     */
    public String getNombrePadre() {
        return nombrePadre;
    }


    /**
     * @param nombrePadre the nombrePadre to set
     */
    public void setNombrePadre( final String nombrePadre ) {
        this.nombrePadre = nombrePadre;
    }


    /**
     * @return the numTarjetaPadre
     */
    public String getNumTarjetaPadre() {
        return numTarjetaPadre;
    }


    /**
     * @param numTarjetaPadre the numTarjetaPadre to set
     */
    public void setNumTarjetaPadre( final String numTarjetaPadre ) {
        this.numTarjetaPadre = numTarjetaPadre;
    }


    /**
     * @return the swPadre
     */
    public String getSwPadre() {
        return swPadre;
    }


    /**
     * @param swPadre the swPadre to set
     */
    public void setSwPadre( final String swPadre ) {
        this.swPadre = swPadre;
    }


    /**
     * @return the swCompetencia
     */
    public String getSwCompetencia() {
        return swCompetencia;
    }


    /**
     * @param swCompetencia the swCompetencia to set
     */
    public void setSwCompetencia( final String swCompetencia ) {
        this.swCompetencia = swCompetencia;
    }


    /**
     * @return the nombreCompetencia
     */
    public String getNombreCompetencia() {
        return nombreCompetencia;
    }


    /**
     * @param nombreCompetencia the nombreCompetencia to set
     */
    public void setNombreCompetencia( final String nombreCompetencia ) {
        this.nombreCompetencia = nombreCompetencia;
    }

	public List<Cuestionario> getRespuestasVo() {
		return respuestasVo;
	}

	public void setRespuestasVo(final List<Cuestionario> respuestasVo) {
		this.respuestasVo = respuestasVo;
	}

	public int getCompania() {
		return compania;
	}

	public void setCompania(final int compania) {
		this.compania = compania;
	}

	public String getNumPoliza() {
		return numPoliza;
	}

	public void setNumPoliza(final String numPoliza) {
		this.numPoliza = numPoliza;
	}

	public String getSwCheckEsCliente() {
		return swCheckEsCliente;
	}

	public void setSwCheckEsCliente(final String swCheckEsCliente) {
		this.swCheckEsCliente = swCheckEsCliente;
	}

}
