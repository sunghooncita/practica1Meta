package meta1;

import configuracion.Configurador;
import algoritmos.*;
import java.io.*;
import java.util.ArrayList;
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
        ArrayList<Integer> evaluaciones = config.getEvaluaciones();
        ArrayList<Integer> iteraciones = config.getIteraciones();

        int K = config.getK();
        int tamPobl = config.getTamPoblacion();
        String cruce = config.getCruce();
        int porPoblAle = config.getPorPoblAle();
        int tiempoMax = config.getTiempoMax();

        int elite = config.getElite();
        int kBestMGen = config.getKBestMGen();
        int kWorstMGen = config.getkWorstMGen();
        double probMutaMGen = config.getProbMutaMGen();
        double probCruceMGen = config.getProbCruceMGen();


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
                               for (Integer E : elite) {
                                   for (Integer kB : kBestMGen) {
                                       for (String cruceM : cruce) {

                                           int tipoCruce = cruceM.equals("MOC") ? 1 : 0;
                                           ArrayList<Integer> mejorSolIn = new ArrayList<>();

                                           System.out.printf("Configuración GEN -> Elite: %d, kBest: %d, kWorst: %d, Cruce: %s, Semilla: %d\n", E, kB, kWorstGen, cruceM,semilla);
                                           long inicioTiempo = System.currentTimeMillis();

                                           long[] resultados = AlgGen_Clase01_Grupo05.AlgMGen(tamPobl, flujos.length, evaluaciones, flujos, distancias, mejorSolIn, tiempoMax, kB, kWorstGen, probMutaGen, probCruceGen, E, porPoblAle, tipoCruce, K,semilla);
                                           long resultado = resultados[0];
                                           int generaciones = (int) resultados[1]; // Conversión a int

                                           long tiempoGen = System.currentTimeMillis() - inicioTiempo;

                                           System.out.println("Tiempo total: " + tiempoGen + "\nResultado GEN: " + resultado + "\n");

                                           Logs log = new Logs(
                                                   "Generacional",
                                                   archivo.getName(), semilla,
                                                   E, kB, kWorstMGen, cruceM, probMutaGen, probCruceGen, tamPobl,
                                                   new ArrayList<>(mejorSolIn), //copia d la solución
                                                   resultado,
                                                   tiempoGen, evaluaciones,generaciones
                                           );
                                           executor.execute(log);

                                       }
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



