# SanitasTest
Test Sanitas
Se deben instalar las dependencias que venían con el ejercicio
mvn install:install-file -Dfile=scontratacion-1.19.0.jar -DgroupId=sanitas.bravo.clientes -DartifactId=scontratacion -Dversion=1.19.0 -Dpackaging=jar
mvn install:install-file -Dfile=ws_contratacion-1.2.28.jar -DgroupId=sanitas.bravo.clientes -DartifactId=ws_contratacion -Dversion=1.2.28 -Dpackaging=jar
mvn install:install-file -Dfile=rest_simulacionpoliza-2.15.0.jar -DgroupId=sanitas.bravo.clientes -DartifactId=rest_simulacionpoliza -Dversion=2.15.0 -Dpackaging=jar

Compilar con la versión 1.8 de la jdk.