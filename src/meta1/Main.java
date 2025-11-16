package meta1;

import configuracion.Configurador;
import algoritmos.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


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

        // Parámetros para GEN (Evolutivo Generacional)
        List<Integer> eliteGEN = Arrays.asList(1, 2);
        List<Integer> kBestGEN = Arrays.asList(2, 3);
        List<String> crucesGEN = Arrays.asList("OX2", "MOC");
        int M_GEN = 50000;
        int tamPobl = 100;
        int porPoblAle = 80;
        int kWorstGEN = 3;
        double probMutaGEN = 0.1;
        double probCruceGEN = 0.7;
        int tiempoMax = 60;    // segundos

        // Parámetros para EST (Evolutivo Estacionario)
        List<Integer> kBestEST = Arrays.asList(2);
        List<String> crucesEST = Arrays.asList("OX2", "MOC");
        int M_EST = 50000;
        int kWorstEST = 2;
        double probMutaEST = 0.1;
        double probCruceEST = 1.0;
        int tamPoblEST = 100;

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

               for (String alg : algoritmosConfig) {
                   switch (alg) {

                       case "Generacional":

                           System.out.println("\nEjecutando Algoritmo: Generacional");
                           for (Integer E : eliteGEN) {
                               for (Integer kB : kBestGEN) {
                                   for (String cruce : crucesGEN) {

                                       int tipoCruce = cruce.equals("MOC") ? 1 : 0;
                                       ArrayList<Integer> mejorSolIn = new ArrayList<>();

                                       System.out.printf("Configuración GEN -> Elite: %d, kBest: %d, kWorst: %d, Cruce: %s\n", E, kB, kWorstGEN, cruce);

                                       double resultado = AlgGen15.AlgGen(tamPobl, flujos.length, M_GEN, flujos, distancias, mejorSolIn, tiempoMax, kB, kWorstGEN, probMutaGEN, probCruceGEN, E, porPoblAle, tipoCruce, K);
                                       System.out.println("Resultado GEN: " + resultado +"\n");
                                   }
                               }
                           }
                           break;

                       case "Estacionario":

                           System.out.println("\nEjecutando Algoritmo: Estacionario");
                           for (Integer kB : kBestEST) {
                               for (String cruce : crucesEST) {

                                   int tipoCruce = cruce.equals("MOC") ? 1 : 0;
                                   ArrayList<Integer> mejorSolIn = new ArrayList<>();

                                   System.out.printf("Configuración EST -> kBest: %d, kWorst: %d, Cruce: %s\n", kB, kWorstGEN, cruce);

                                   double resultado = AlgEst15.AlgEst(tamPoblEST,flujos.length,M_EST,flujos,distancias,mejorSolIn,tiempoMax,kB,kWorstEST,probMutaEST,porPoblAle,tipoCruce,K);
                                   System.out.println("Resultado EST: " + resultado+"\n");
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
    }

}



