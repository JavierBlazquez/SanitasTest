package es.sanitas.soporte;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wscontratacion.beneficiario.vo.Beneficiario;


/**
 * La Class BeneficiarioPolizas.
 *
 * @author everis
 */
public class BeneficiarioPolizas extends Beneficiario implements Serializable {

    /** La constante serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** El l num tarj asegurados. */
    List lNumTarjAsegurados = new ArrayList();

    /** El l sw poliza anterior. */
    List lSwPolizaAnterior = new ArrayList();

    /** El s num tarjeta sanitas. */
    String sNumTarjetaSanitas = null;

    /** El sw cliente anterior. */
    String swClienteAnterior = "N";

    /** El id codigo. */
    int idCodigo = 0;

    /** El id portal. */
    int idPortal = 0;

    /** El num asegurado. */
    int numAsegurado = 0;

    private String swMadre;
    private String nombreMadre;
    private String numTarjetaMadre;
    private String nombrePadre;
    private String numTarjetaPadre;
    private String swPadre;
    private String swCompetencia;
    private int compania;

    private boolean lopd;

    private boolean infoComercialSanitas;

    private boolean infoComercialEntidades;

    private boolean infoComercialOtras;

    private List<Cuestionario> respuestasVo;

    private String email;
    private String swCheckEsCliente;
    private String numPoliza;

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
     * Obtiene el objeto l num tarj asegurados.
     *
     * @return Devuelve lNumTarjAsegurados.
     */
    public List getLNumTarjAsegurados() {
        return lNumTarjAsegurados;
    }

    /**
     * Asigna valor a l num tarj asegurados.
     *
     * @param numTarjAsegurados
     *            asigna lNumTarjAsegurados.
     */
    public void setLNumTarjAsegurados( final List numTarjAsegurados ) {
        lNumTarjAsegurados = numTarjAsegurados;
    }

    /**
     * Obtiene el objeto l sw poliza anterior.
     *
     * @return Devuelve lSwPolizaAnterior.
     */
    public List getLSwPolizaAnterior() {
        return lSwPolizaAnterior;
    }

    /**
     * Asigna valor a l sw poliza anterior.
     *
     * @param swPolizaAnterior
     *            asigna lSwPolizaAnterior.
     */
    public void setLSwPolizaAnterior( final List swPolizaAnterior ) {
        lSwPolizaAnterior = swPolizaAnterior;
    }

    /**
     * Obtiene el objeto s num tarjeta sanitas.
     *
     * @return Returns the sNumTarjetaSanitas.
     */
    public String getSNumTarjetaSanitas() {
        return sNumTarjetaSanitas;
    }

    /**
     * Asigna valor a s num tarjeta sanitas.
     *
     * @param numTarjetaSanitas
     *            The sNumTarjetaSanitas to set.
     */
    public void setSNumTarjetaSanitas( final String numTarjetaSanitas ) {
        sNumTarjetaSanitas = numTarjetaSanitas;
    }

    /**
     * Obtiene el objeto sw cliente anterior.
     *
     * @return Returns the swClienteAnterior.
     */
    public String getSwClienteAnterior() {
        return swClienteAnterior;
    }

    /**
     * Asigna valor a sw cliente anterior.
     *
     * @param swClienteAnterior
     *            The swClienteAnterior to set.
     */
    public void setSwClienteAnterior( final String swClienteAnterior ) {
        this.swClienteAnterior = swClienteAnterior;
    }

    /**
     * Obtiene el objeto id codigo.
     *
     * @return Returns the idCodigo.
     */
    @Override
    public int getIdCodigo() {
        return idCodigo;
    }

    /**
     * Asigna valor a id codigo.
     *
     * @param idCodigo
     *            The idCodigo to set.
     */
    @Override
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
        return idPortal;
    }

    /**
     * Asigna valor a id portal.
     *
     * @param idPortal
     *            The idPortal to set.
     */
    @Override
    public void setIdPortal( final int idPortal ) {
        this.idPortal = idPortal;
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

	public List<Cuestionario> getRespuestasVo() {
		return respuestasVo;
	}

	public void setRespuestasVo(final List<Cuestionario> respuestasVo) {
		this.respuestasVo = respuestasVo;
	}

	public boolean isLopd() {
		return lopd;
	}

	public void setLopd(final boolean lopd) {
		this.lopd = lopd;
	}

	public boolean isInfoComercialSanitas() {
		return infoComercialSanitas;
	}

	public void setInfoComercialSanitas(final boolean infoComercialSanitas) {
		this.infoComercialSanitas = infoComercialSanitas;
	}

	public boolean isInfoComercialEntidades() {
		return infoComercialEntidades;
	}

	public void setInfoComercialEntidades(final boolean infoComercialEntidades) {
		this.infoComercialEntidades = infoComercialEntidades;
	}

	public boolean isInfoComercialOtras() {
		return infoComercialOtras;
	}

	public void setInfoComercialOtras(final boolean infoComercialOtras) {
		this.infoComercialOtras = infoComercialOtras;
	}

	public String getSwCompetencia() {
		return swCompetencia;
	}

	public void setSwCompetencia(final String swCompetencia) {
		this.swCompetencia = swCompetencia;
	}

	public int getCompania() {
		return compania;
	}

	public void setCompania(final int iCompania) {
		this.compania = iCompania;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getSwCheckEsCliente() {
		return swCheckEsCliente;
	}

	public void setSwCheckEsCliente(final String swCheckEsCliente) {
		this.swCheckEsCliente = swCheckEsCliente;
	}

	public String getNumPoliza() {
		return numPoliza;
	}

	public void setNumPoliza(final String numPoliza) {
		this.numPoliza = numPoliza;
	}

}