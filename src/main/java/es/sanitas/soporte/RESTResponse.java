package es.sanitas.soporte;

import java.io.Serializable;

/**
 * @author vgarciagon
  */
 public class RESTResponse< T, E > implements Serializable {

     private static final long serialVersionUID = -2651063965266831737L;

     public T out;
     public E error;
     public String rawResponse;

     public boolean hasError() {
         return out == null;
     }
 }
