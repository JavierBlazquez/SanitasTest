package es.sanitas.soporte;

import java.util.Arrays;
import java.util.List;


/**
 * Enumerado que contiene las frecuencias que puede tener un plan
 *
 * @author ajimenezpi
 */
public enum FrecuenciaEnum {

    MENSUAL( 1 ), TRIMESTRAL( 2 ), SEMESTRAL( 3 ), ANUAL( 4 );

    /**
     * Valor de la frecuencia
     */
    private int valor;

    private FrecuenciaEnum( final int valor ) {
        this.valor = valor;
    }

    /**
     * @return the valor
     */
    public int getValor() {
        return valor;
    }

    /**
     * Devuelve la lista de FrecuenciaEnum
     *
     * @return - lista de FrecuenciaEnum
     */
    public static List< FrecuenciaEnum > getListaFrecuencias() {
        return Arrays.asList( FrecuenciaEnum.values() );
    }

    /**
     * Permite obtener la frecuencia a partir del valor.
     *
     * @param valor
     *            el valor deseado de la frecuencia.
     * @return la frecuencia.
     */
    public static FrecuenciaEnum obtenerFrecuencia( final int valor ) {
        FrecuenciaEnum frecuencia = null;
        for( int i = 0; i < FrecuenciaEnum.values().length; i++ ) {
            if( FrecuenciaEnum.values()[ i ].getValor() == valor ) {
                frecuencia = FrecuenciaEnum.values()[ i ];
                break;
            }
        }
        return frecuencia;
    }

    /**
     * Permite obtener la frecuencia a partir del nombre.
     *
     * @param valor
     *            el nombre de la frecuencia.
     * @return la frecuencia.
     */
    public static FrecuenciaEnum obtenerFrecuencia( final String nombre ) {
        FrecuenciaEnum frecuencia = null;
        for( int i = 0; i < FrecuenciaEnum.values().length; i++ ) {
            frecuencia = FrecuenciaEnum.values()[ i ];
            if( frecuencia.name().equals( nombre ) ) {
                break;
            }
        }
        return frecuencia;
    }

    /** Obtiene el nÃºmero de meses correspondientes a la frecuencia.
     *
     * @param frecuencia frecuencia de pago
     *
     * @return nÃºmero de meses correspondientes a la frecuencia.
     */
    public static int obtenerMesesFrecuencia (final FrecuenciaEnum frecuencia) {
        int meses = 0;
        if (frecuencia != null) {
            switch (frecuencia.getValor()) {
             case 1: {
                 meses = 1;
                 break;
             }
             case 2: {
                 meses = 3;
                 break;
             }
             case 3: {
                 meses = 6;
                 break;
             }
             case 4: {
                 meses = 12;
                 break;
             }
            }
        }
        return meses;
    }
}
