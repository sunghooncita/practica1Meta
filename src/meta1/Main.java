package meta1;

import configuracion.Configurador;
import algoritmos.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {

        //Cargar configuración desde archivo
        Configurador config = new Configurador("src/configuracion/config.txt");

        ArrayList<String> archivosConfig = config.getArchivos();
        ArrayList<String> algoritmosConfig = config.getAlgoritmos();
        ArrayList<Integer> semillaConfig = config.getSemillas();
        ArrayList<String> cruces = config.getCruces();

        // Parámetros Generales
        int K = config.getK();
        int tamPobl = config.getTamPoblacion();
        int evaluaciones = config.getEvaluaciones();
        int tiempoMax = config.getTiempoMax();
        int porPoblAle = config.getPorPoblAle();

        // Parámetros GEN
        ArrayList<Integer> eliteGen = config.getElite();
        ArrayList<Integer> kBestGen = config.getKBestGen();
        int kWorstGen = config.getKWorstGen();
        double probMutaGen = config.getProbMutaGen();
        double probCruceGen = config.getProbCruceGen();

        // Parámetros EST
        ArrayList<Integer> kBestEst = config.getKBestEst();
        int kWorstEst = config.getKWorstEst();
        double probMutaEst = config.getProbMutaEst();

        //Creamos varios hilos para guardar los logs al mismo tiempo,
        //así el programa no se queda esperando mientras se escriben los archivos.
        ExecutorService executor = Executors.newFixedThreadPool(5);


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

               for (String alg : algoritmosConfig) {

                   switch (alg) {

                       case "Generacional":

                           System.out.println("\nEjecutando Algoritmo: Generacional");
                           for (Integer semilla : semillaConfig) {
                               for (Integer E : eliteGen) {
                                   for (Integer kB : kBestGen) {
                                       for (String cruce : cruces) {

                                           int tipoCruce = cruce.equals("MOC") ? 1 : 0;
                                           ArrayList<Integer> mejorSolIn = new ArrayList<>();

                                           System.out.printf("Configuración GEN -> Elite: %d, kBest: %d, kWorst: %d, Cruce: %s, Semilla: %d\n", E, kB, kWorstGen, cruce,semilla);
                                           long inicioTiempo = System.currentTimeMillis();

                                           double resultado = AlgGen_Clase01_Grupo05.AlgGen(tamPobl, flujos.length, evaluaciones, flujos, distancias, mejorSolIn, tiempoMax, kB, kWorstGen, probMutaGen, probCruceGen, E, porPoblAle, tipoCruce, K,semilla);
                                           long tiempoGen = System.currentTimeMillis() - inicioTiempo;

                                           System.out.println("Tiempo total: " + tiempoGen + "\nResultado GEN: " + resultado + "\n");

                                           Logs log = new Logs(
                                                   "Generacional",
                                                   archivo.getName(), semilla,
                                                   E, kB, kWorstGen, cruce, probMutaGen, probCruceGen, tamPobl,
                                                   new ArrayList<>(mejorSolIn), //copia d la solución
                                                   resultado,
                                                   tiempoGen
                                           );
                                           executor.execute(log);

                                       }
                                   }
                               }
                           }
                           break;

                       case "Estacionario":

                           System.out.println("\nEjecutando Algoritmo: Estacionario");
                           for (Integer semilla : semillaConfig) {
                               for (Integer kB : kBestEst) {
                                   for (String cruce : cruces) {

                                       int tipoCruce = cruce.equals("MOC") ? 1 : 0;
                                       ArrayList<Integer> mejorSolIn = new ArrayList<>();

                                       System.out.printf("Configuración EST -> kBest: %d, kWorst: %d, Cruce: %s, Semilla: %d\n", kB, kWorstGen, cruce,semilla);
                                       long inicioTiempo = System.currentTimeMillis();

                                       double resultado = AlgEst_Clase01_Grupo05.AlgEst(tamPobl, flujos.length, evaluaciones, flujos, distancias, mejorSolIn, tiempoMax, kB, kWorstEst, probMutaEst, porPoblAle, tipoCruce, K, semilla);

                                       long tiempoEst = System.currentTimeMillis() - inicioTiempo;

                                       System.out.println("Tiempo total: " + tiempoEst + "\nResultado EST: " + resultado + "\n");

                                       Logs log = new Logs(
                                               "Estacionario",
                                               archivo.getName(), semilla,
                                               0, kB, kWorstEst, cruce, probMutaEst, 1.0, tamPobl,
                                               new ArrayList<>(mejorSolIn),
                                               resultado,
                                               tiempoEst
                                       );
                                       executor.execute(log);
                                   }
                               }
                           }
                           break;
                   }

               }


               System.out.println("\n--------------------------------------------\n");

           } catch (FileNotFoundException e) {
               System.out.println("Error leyendo el archivo: " + e.getMessage());
           }
       }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}



