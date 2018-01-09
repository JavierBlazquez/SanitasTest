package es.sanitas.soporte;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import wscontratacion.contratacion.fuentes.parametros.DatosAlta;


/**
 * La Class DatosAltaAsegurados.
 *
 * @author cricor
 */
public class DatosAltaAsegurados extends DatosAlta implements Serializable {

    private static final long serialVersionUID = 3863372087982840997L;

    /** Lista de asegurados con los números de tarjeta. */
    List lNumTarjAsegurados = new ArrayList();

    /** El s num tarjeta titular. */
    String sNumTarjetaTitular = null;

    /** El s codigo postal. */
    String sCodigoPostal = null;

    /** El s codigo postal tomador. */
    String sCodigoPostalTomador = null;

    /** El scodigo postal envio. */
    String scodigoPostalEnvio = null;

    /** El s codigo banco. */
    String sCodigoBanco = null;

    /** El s cod sucursal. */
    String sCodSucursal = null;

    /** El s cuenta corriente. */
    String sCuentaCorriente = null;

    /** El s digito cuenta. */
    String sDigitoCuenta = null;

    /** El s direccion envio. */
    String sDireccionEnvio = null;

    /** El s politica. */
    String sPolitica = null;

    /** El int promocion: almacena la promoción activa. */
    int idPromocion = 0;

    /** El s teléfono entrante. */
    String sTelefonoEntrante = null;

    /** El s Web C pagina. */
    int sWebCPagina;

    /** El s Web T pagina. */
    int sWebTPagina;

    /** El s Web C Tipo Documento Guardar */
    int webCTipoDocumentoGuardar;

    /** El s Web T Tipo Documento Guardar */
    int webTTipoDocumentoGuardar;

    /** El s Web C Motivo Alta */
    int webCMotivoAlta;

    /** El s Web T Motivo Alta */
    int webTMotivoAlta;

    /** El s Observaciones */
    String observaciones;

    /** El s Numero Documento Guardar */
    String NumeroDocAgredGuardar;

    /** El s Email Guardar */
    String EmailGuardar;

    private boolean lopd;

    private boolean infoComercialSanitas;

    private boolean infoComercialEntidades;

    private boolean infoComercialOtras;

    private String swSolicitudFirmada;

    private String fechaFirmaSolicitud;

    private List<Cuestionario> respuestasVo;
    private boolean docuemtancionLOPD;

    // Campos para el desarrollo de Kutxabank
    private String token;
    private String appId;
    private Integer idioma = null;
    private String usuarioKutxa="";
    private String centroKutxa="";
    private String numPersonaKutxa = "";

    // Campo para los coeficientes de tierizacion de BBVA
    private String coeficientesTier;

    // Campo para diferenciar entre simulaciones de autonomos y particulares
    private String tipoSimulacion;

    // Campos para el mediador de SantaLucia
    private String mediadorExterno;
    private String mediadorAgencia;
    private String mediadorZona;
    private String mediadorAuxiliarExterno;
    private String mediadorNombre;
    private String mediadorAgenciaNombre;

    /** Código promocional */
    private String codigoPromocional;

    // Número de poliza anterior
    private String numPoliza;

    // Envío de la documentación al mediador
    private boolean swEnvioDocMediador;

    // Es cliente ya de Sanitas
    private String swCheckEsCliente;

    // Tarjeta de seguro único BBVA
    private String sTarjetaSU;
    /**
     * @return the idPromocion
     */
    public int getIdPromocion() {
        return idPromocion;
    }

    /**
     * @param idPromocion
     *            the idPromocion to set
     */
    public void setIdPromocion( final int idPromocion ) {
        this.idPromocion = idPromocion;
    }

    /** El listado productos alta. */
    List listadoProductosAlta = new ArrayList();

    /** El id estado. */
    int idEstado = 0;

    /** El id tipo operacion. */
    int idTipoOperacion = 0;

    /**
     * 1: Elige plan todos 2: Presupuestocalcula 3: Presupuesto escoge 4:Presupuesto escoge seguro
     * 5: datos asegurado pÃ³liza 6: Datos bancarios poliza 7: Cuestionario salud 8: Confirmacion
     * datos poliza 9: FinalizaciÃ³n proceso 10: Bienvenido
     */
    int paginaGuardar = 0;

    public int getPaginaGuardar() {
        return paginaGuardar;
    }

    public void setPaginaGuardar( final int paginaGuardar ) {
        this.paginaGuardar = paginaGuardar;
    }

    /** El renovacion. */
    Calendar fRenovacion = null;

    /** listado de nifs utilizados. */
    List listadoDocumentos = null;

    /** El id coelctivo. */
    int idColectivo = 0;

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
     * Obtiene el objeto s codigo banco.
     *
     * @return Devuelve sCodigoBanco.
     */
    public String getSCodigoBanco() {
        if( ( sCodigoBanco == null || "".equals( sCodigoBanco ) ) && this.getIdBanco() > 0 ) {
            sCodigoBanco = String.valueOf( this.getIdBanco() );
        }
        return sCodigoBanco;
    }

    /**
     * Asigna valor a s codigo banco.
     *
     * @param codigoBanco
     *            asigna sCodigoBanco.
     */
    public void setSCodigoBanco( final String codigoBanco ) {
        sCodigoBanco = codigoBanco;
    }

    /**
     * Obtiene el objeto s codigo postal.
     *
     * @return Devuelve sCodigoPostal.
     */
    public String getSCodigoPostal() {
        return sCodigoPostal;
    }

    /**
     * Asigna valor a s codigo postal.
     *
     * @param codigoPostal
     *            asigna sCodigoPostal.
     */
    public void setSCodigoPostal( final String codigoPostal ) {
        sCodigoPostal = codigoPostal;
    }

    /**
     * Obtiene el objeto scodigo postal envio.
     *
     * @return Devuelve scodigoPostalEnvio.
     */
    public String getScodigoPostalEnvio() {
        return scodigoPostalEnvio;
    }

    /**
     * Asigna valor a scodigo postal envio.
     *
     * @param scodigoPostalEnvio
     *            asigna scodigoPostalEnvio.
     */
    public void setScodigoPostalEnvio( final String scodigoPostalEnvio ) {
        this.scodigoPostalEnvio = scodigoPostalEnvio;
    }

    /**
     * Obtiene el objeto s codigo postal tomador.
     *
     * @return Devuelve sCodigoPostalTomador.
     */
    public String getSCodigoPostalTomador() {
        return sCodigoPostalTomador;
    }

    /**
     * Asigna valor a s codigo postal tomador.
     *
     * @param codigoPostalTomador
     *            asigna sCodigoPostalTomador.
     */
    public void setSCodigoPostalTomador( final String codigoPostalTomador ) {
        sCodigoPostalTomador = codigoPostalTomador;
    }

    /**
     * Obtiene el objeto s cod sucursal.
     *
     * @return Devuelve sCodSucursal.
     */
    public String getSCodSucursal() {
        if( ( sCodSucursal == null || "".equals( sCodSucursal ) ) && this.getIdSucursal() > 0 ) {
            sCodSucursal = String.valueOf( this.getIdSucursal() );
        }
        return sCodSucursal;
    }

    /**
     * Asigna valor a s cod sucursal.
     *
     * @param codSucursal
     *            asigna sCodSucursal.
     */
    public void setSCodSucursal( final String codSucursal ) {
        sCodSucursal = codSucursal;
    }

    /**
     * Obtiene el objeto s cuenta corriente.
     *
     * @return Devuelve sCuentaCorriente.
     */
    public String getSCuentaCorriente() {
        if( ( sCuentaCorriente == null || "".equals( sCuentaCorriente ) )
                && getCuentaCorriente() != null ) {
            sCuentaCorriente = getCuentaCorriente();
        }
        return sCuentaCorriente;
    }

    /**
     * Asigna valor a s cuenta corriente.
     *
     * @param cuentaCorriente
     *            asigna sCuentaCorriente.
     */
    public void setSCuentaCorriente( final String cuentaCorriente ) {
        sCuentaCorriente = cuentaCorriente;
    }

    /**
     * Obtiene el objeto s digito cuenta.
     *
     * @return Devuelve sDigitoCuenta.
     */
    public String getSDigitoCuenta() {
        if( ( sDigitoCuenta == null || "".equals( sDigitoCuenta ) ) && getDigitoCuenta() > 0 ) {
            sDigitoCuenta = String.valueOf( getDigitoCuenta() );
        }
        return sDigitoCuenta;
    }

    /**
     * Asigna valor a s digito cuenta.
     *
     * @param digitoCuenta
     *            asigna sDigitoCuenta.
     */
    public void setSDigitoCuenta( final String digitoCuenta ) {
        sDigitoCuenta = digitoCuenta;
    }

    /**
     * Obtiene el objeto s direccion envio.
     *
     * @return Devuelve sDireccionEnvio.
     */
    public String getSDireccionEnvio() {
        return sDireccionEnvio;
    }

    /**
     * Asigna valor a s direccion envio.
     *
     * @param direccionEnvio
     *            asigna sDireccionEnvio.
     */
    public void setSDireccionEnvio( final String direccionEnvio ) {
        sDireccionEnvio = direccionEnvio;
    }

    /**
     * Obtiene el objeto s politica.
     *
     * @return Devuelve sPolitica.
     */
    public String getSPolitica() {
        return sPolitica;
    }

    /**
     * Asigna valor a s politica.
     *
     * @param politica
     *            asigna sPolitica.
     */
    public void setSPolitica( final String politica ) {
        sPolitica = politica;
    }

    /**
     * Obtiene el objeto s num tarjeta titular.
     *
     * @return Devuelve sNumTarjetaTitular.
     */
    public String getSNumTarjetaTitular() {
        return sNumTarjetaTitular;
    }

    /**
     * Asigna valor a s num tarjeta titular.
     *
     * @param numTarjetaTitular
     *            asigna sNumTarjetaTitular.
     */
    public void setSNumTarjetaTitular( final String numTarjetaTitular ) {
        sNumTarjetaTitular = numTarjetaTitular;
    }

    /**
     * Obtiene el objeto listado productos alta.
     *
     * @return Returns the listadoProductosAlta.
     */
    public List getListadoProductosAlta() {
        return listadoProductosAlta;
    }

    /**
     * Asigna valor a listado productos alta.
     *
     * @param listadoProductosAlta
     *            The listadoProductosAlta to set.
     */
    public void setListadoProductosAlta( final List listadoProductosAlta ) {
        this.listadoProductosAlta = listadoProductosAlta;
    }

    /**
     * Obtiene el objeto id estado.
     *
     * @return Returns the idEstado.
     */
    public int getIdEstado() {
        return idEstado;
    }

    /**
     * Asigna valor a id estado.
     *
     * @param idEstado
     *            The idEstado to set.
     */
    public void setIdEstado( final int idEstado ) {
        this.idEstado = idEstado;
    }

    /**
     * Obtiene el objeto id tipo operacion.
     *
     * @return Returns the idTipoOperacion.
     */
    public int getIdTipoOperacion() {
        return idTipoOperacion;
    }

    /**
     * Asigna valor a id tipo operacion.
     *
     * @param idTipoOperacion
     *            The idTipoOperacion to set.
     */
    public void setIdTipoOperacion( final int idTipoOperacion ) {
        this.idTipoOperacion = idTipoOperacion;
    }

    /**
     * Obtiene el objeto f renovacion.
     *
     * @return Returns the fRenovacion.
     */
    public Calendar getFRenovacion() {
        return fRenovacion;
    }

    /**
     * Asigna valor a f renovacion.
     *
     * @param renovacion
     *            The fRenovacion to set.
     */
    public void setFRenovacion( final Calendar renovacion ) {
        fRenovacion = renovacion;
    }

    /**
     * MÃ©todo que devuelve el valor de la variable listadoDocumentos.
     *
     * @return Devuelve el valor de la variable listadoDocumentos de tipo List.
     */
    public List getListadoDocumentos() {
        return listadoDocumentos;
    }

    /**
     * Asigna valor a listado documentos.
     *
     * @param listadoDocumentos
     *            the listadoDocumentos to set
     */
    public void setListadoDocumentos( final List listadoDocumentos ) {
        this.listadoDocumentos = listadoDocumentos;
    }

    /**
     * MÃ©todo que devuelve el valor de la variable idColectivo.
     *
     * @return Devuelve el valor de la variable idColectivo de tipo int.
     */
    public int getIdColectivo() {
        return idColectivo;
    }

    /**
     * Asigna valor a id colectivo.
     *
     * @param idColectivo
     *            the idColectivo to set
     */
    public void setIdColectivo( final int idColectivo ) {
        this.idColectivo = idColectivo;
    }

    public String getsTelefonoEntrante() {
        return sTelefonoEntrante;
    }

    public void setsTelefonoEntrante( final String sTelefonoEntrante ) {
        this.sTelefonoEntrante = sTelefonoEntrante;
    }

    public int getsWebTPagina() {
        return sWebCPagina;
    }

    public void setsWebTPagina( final int sWebTPagina ) {
        this.sWebTPagina = sWebTPagina;
    }

    public int getsWebCPagina() {
        return sWebCPagina;
    }

    public void setsWebCPagina( final int sWebCPagina ) {
        this.sWebCPagina = sWebCPagina;
    }

    public int getWebTTipoDocumentoGuardar() {
        return webTTipoDocumentoGuardar;
    }

    public void setWebTTipoDocumentoGuardar( final int webTTipoDocumentoGuardar ) {
        this.webTTipoDocumentoGuardar = webTTipoDocumentoGuardar;
    }

    public int getWebCTipoDocumentoGuardar() {
        return webCTipoDocumentoGuardar;
    }

    public void setWebCTipoDocumentoGuardar( final int webCTipoDocumentoGuardar ) {
        this.webCTipoDocumentoGuardar = webCTipoDocumentoGuardar;
    }

    public void setWebCMotivoAlta( final int webCMotivoAlta ) {
        this.webCMotivoAlta = webCMotivoAlta;
    }

    public int getWebCMotivoAlta() {
        return webCMotivoAlta;
    }

    public void setWebTMotivoAlta( final int webTMotivoAlta ) {
        this.webTMotivoAlta = webTMotivoAlta;
    }

    public int getWebTMotivoAlta() {
        return webTMotivoAlta;
    }

    public void setObservaciones( final String observaciones ) {
        this.observaciones = observaciones;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getNumeroDocAgredGuardar() {
        return NumeroDocAgredGuardar;
    }

    public void setNumeroDocAgredGuardar( final String numeroDocAgredGuardar ) {
        this.NumeroDocAgredGuardar = numeroDocAgredGuardar;
    }

    public String getEmailGuardar() {
        return EmailGuardar;
    }

    public void setEmailGuardar( final String EmailGuardar ) {
        this.EmailGuardar = EmailGuardar;
    }

    /**
     * Codigo IBAN correspondiente a la cuenta bancaria
     */
    String sCodigoIBAN ="";
    /**
     * Codigo BIC correspondiente a la cuenta bancaria
     */
    String sCodigoBIC ="";

    public String getSCodigoIBAN() {
		return sCodigoIBAN;
	}

	public void setSCodigoIBAN(final String sCodigoIBAN) {
		this.sCodigoIBAN = sCodigoIBAN;
	}

	public String getSCodigoBIC() {
		return sCodigoBIC;
	}

	public void setSCodigoBIC(final String sCodigoBIC) {
		this.sCodigoBIC = sCodigoBIC;
	}

    /**
     * @return the swSolicitudFirmada
     */
    public String getSwSolicitudFirmada() {
        return swSolicitudFirmada;
    }


    /**
     * @param swSolicitudFirmada the swSolicitudFirmada to set
     */
    public void setSwSolicitudFirmada( final String swSolicitudFirmada ) {
        this.swSolicitudFirmada = swSolicitudFirmada;
    }


    /**
     * @return the fechaFirmaSolicitud
     */
    public String getFechaFirmaSolicitud() {
        return fechaFirmaSolicitud;
    }


    /**
     * @param fechaFirmaSolicitud the fechaFirmaSolicitud to set
     */
    public void setFechaFirmaSolicitud( final String fechaFirmaSolicitud ) {
        this.fechaFirmaSolicitud = fechaFirmaSolicitud;
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

	public boolean isDocuemtancionLOPD() {
		return docuemtancionLOPD;
	}

	public void setDocuemtancionLOPD(final boolean docuemtancionLOPD) {
		this.docuemtancionLOPD = docuemtancionLOPD;
	}

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(final String appId) {
        this.appId = appId;
    }

    public Integer getIdioma() {
        return idioma;
    }

    public void setIdioma(final Integer idioma) {
        this.idioma = idioma;
    }

    public String getUsuarioKutxa() {
        return usuarioKutxa;
    }

    public void setUsuarioKutxa(final String usuarioKutxa) {
        this.usuarioKutxa = usuarioKutxa;
    }

    public String getCentroKutxa() {
        return centroKutxa;
    }

    public void setCentroKutxa(final String centroKutxa) {
        this.centroKutxa = centroKutxa;
    }

    public String getNumPersonaKutxa() {
        return numPersonaKutxa;
    }

    public void setNumPersonaKutxa(final String numPersonaKutxa) {
        this.numPersonaKutxa = numPersonaKutxa;
    }


    /**
     * @return the coeficientesTier
     */
    public String getCoeficientesTier() {
        return coeficientesTier;
    }


    /**
     * @param coeficientesTier the coeficientesTier to set
     */
    public void setCoeficientesTier( final String coeficientesTier ) {
        this.coeficientesTier = coeficientesTier;
    }


    /**
     * @return the mediadorAgencia
     */
    public String getMediadorAgencia() {
        return mediadorAgencia;
    }


    /**
     * @param mediadorAgencia the mediadorAgencia to set
     */
    public void setMediadorAgencia( final String mediadorAgencia ) {
        this.mediadorAgencia = mediadorAgencia;
    }


    /**
     * @return the mediadorZona
     */
    public String getMediadorZona() {
        return mediadorZona;
    }


    /**
     * @param mediadorZona the mediadorZona to set
     */
    public void setMediadorZona( final String mediadorZona ) {
        this.mediadorZona = mediadorZona;
    }


    /**
     * @return the mediadorAuxiliarExterno
     */
    public String getMediadorAuxiliarExterno() {
        return mediadorAuxiliarExterno;
    }


    /**
     * @param mediadorAuxiliarExterno the mediadorAuxiliarExterno to set
     */
    public void setMediadorAuxiliarExterno( final String mediadorAuxiliarExterno ) {
        this.mediadorAuxiliarExterno = mediadorAuxiliarExterno;
    }


    /**
     * @return the mediadorNombre
     */
    public String getMediadorNombre() {
        return mediadorNombre;
    }


    /**
     * @param mediadorNombre the mediadorNombre to set
     */
    public void setMediadorNombre( final String mediadorNombre ) {
        this.mediadorNombre = mediadorNombre;
    }


    /**
     * @return the mediadorAgenciaNombre
     */
    public String getMediadorAgenciaNombre() {
        return mediadorAgenciaNombre;
    }


    /**
     * @param mediadorAgenciaNombre the mediadorAgenciaNombre to set
     */
    public void setMediadorAgenciaNombre( final String mediadorAgenciaNombre ) {
        this.mediadorAgenciaNombre = mediadorAgenciaNombre;
    }


    /**
     * @return the mediadorExterno
     */
    public String getMediadorExterno() {
        return mediadorExterno;
    }


    /**
     * @param mediadorExterno the mediadorExterno to set
     */
    public void setMediadorExterno( final String mediadorExterno ) {
        this.mediadorExterno = mediadorExterno;
    }


    /**
     * @return the codigo_promocional
     */
    public String getCodigoPromocional() {
        return codigoPromocional;
    }


    /**
     * @param codigo_promocional the codigo_promocional to set
     */
    public void setCodigoPromocional( final String codigoPromocional ) {
        this.codigoPromocional = codigoPromocional;
    }

	/**
	 * @return the numPoliza
	 */
	public String getNumPoliza() {
		return numPoliza;
	}

	/**
	 * @param numPoliza the numPoliza to set
	 */
	public void setNumPoliza(final String numPoliza) {
		this.numPoliza = numPoliza;
	}

	public boolean isSwEnvioDocMediador() {
		return swEnvioDocMediador;
	}

	public void setSwEnvioDocMediador(final boolean swEnvioDocMediador) {
		this.swEnvioDocMediador = swEnvioDocMediador;
	}

	public String getSwCheckEsCliente() {
		return swCheckEsCliente;
	}

	public void setSwCheckEsCliente(final String swCheckEsCliente) {
		this.swCheckEsCliente = swCheckEsCliente;
	}

	/**
	 * @return the sTarjetaSU
	 */
	public String getsTarjetaSU() {
		return sTarjetaSU;
	}

	/**
	 * @param sTarjetaSU the sTarjetaSU to set
	 */
	public void setsTarjetaSU(final String sTarjetaSU) {
		this.sTarjetaSU = sTarjetaSU;
	}

    /**
     * @return the tipoSimulacion
     */
    public String getTipoSimulacion() {
        return tipoSimulacion;
    }

    /**
     * @param tipoSimulacion the tipoSimulacion to set
     */
    public void setTipoSimulacion( final String tipoSimulacion ) {
        this.tipoSimulacion = tipoSimulacion;
    }

	/* (non-Javadoc)
     * @see wscontratacion.contratacion.fuentes.parametros.DatosAlta#toString()
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString( this );
    }


}
