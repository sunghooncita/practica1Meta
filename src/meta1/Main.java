package meta1;

import configuracion.Configurador;
import algoritmos.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        //Cargar configuración desde archivo
        Configurador config = new Configurador("src/configuracion/config.txt");

        ArrayList<String> archivosConfig = config.getArchivos();
        ArrayList<String> algoritmosConfig = config.getAlgoritmos();
        ArrayList<Integer> semillaConfig = config.getSemillas();
        Integer iter = config.getIteraciones();
        int K= config.getK();
        int tenenciaTabu = config.getTenenciaTabu();
        double oscilacionEstrategica = config.getOscilacion();
        double estancamiento = config.getEstancamiento();


        //Creamos varios hilos para guardar los logs al mismo tiempo,
        //así el programa no se queda esperando mientras se escriben los archivos.
        //ExecutorService executor = Executors.newFixedThreadPool(5);

       for (String rutaArchivo : archivosConfig) {

           File archivo = new File(rutaArchivo);
           if (!archivo.exists()) {
               System.out.println(" No se encontró el archivo: " + archivo.getAbsolutePath());
               continue;
           }

           try {
               System.out.println("\nProcesando archivo: " + archivo.getName());

               //Leer las matrices de flujo y distancia
               int[][][] matrices = LeerMatriz.leerArchivo(archivo.getPath());
               int[][] flujos = matrices[0];
               int[][] distancias = matrices[1];
               Random rnd = new Random();


               System.out.println("\n--------------------------------------------\n");

           } catch (FileNotFoundException e) {
               System.out.println("Error leyendo el archivo: " + e.getMessage());
           }
       }
    }

}



