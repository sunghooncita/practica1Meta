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
        int evaluacionesMax = config.getEvaluacionesMax();
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


                    System.out.println("\nEjecutando Algoritmo: Memético Generacional (MGen)");

                    // Bucle anidado para las 9 combinaciones requeridas
                    for (Integer eval : evaluaciones) { // 1000, 2000, 5000 (Frecuencia/Anchura)
                        for (Integer iter : iteraciones) { // 10, 50, 100 (Profundidad/Explotación)
                            for (Integer semilla : semillaConfig) {

                                int tipoCruce = cruce.equals("MOC") ? 1 : 0;
                                ArrayList<Integer> mejorSolIn = new ArrayList<>();

                                System.out.printf("Configuración MGen -> Evaluaciones: %d, Iteraciones: %d, Semilla: %d\n", eval, iter, semilla);
                                long inicioTiempo = System.currentTimeMillis();

                                // Llamada al nuevo método AlgMGenMemetico
                                long[] resultados = AlgMGen_Clase01_Grupo05.AlgMGen(
                                        tamPobl, flujos.length, evaluacionesMax, flujos, distancias, mejorSolIn,
                                        tiempoMax, kBestMGen, kWorstMGen, probMutaMGen, probCruceMGen, elite,
                                        porPoblAle, tipoCruce, K, semilla,
                                        eval, // evaluacionLanzamientoBT
                                        iter    // iteracionesBT
                                );

                                long resultado = resultados[0];
                                int generaciones = (int) resultados[1];

                                long tiempoGen = System.currentTimeMillis() - inicioTiempo;

                                System.out.println("Tiempo total: " + tiempoGen + "\nResultado MGen: " + resultado + "\n");

                                // Registro de logs para el Algoritmo Memético
                                Logs log = new Logs(
                                        "Memetico",
                                        archivo.getName(), semilla,
                                        elite, kBestMGen, kWorstMGen, cruce, tamPobl,
                                        new ArrayList<>(mejorSolIn),
                                        resultado,
                                        tiempoGen, evaluacionesMax, generaciones,
                                        eval, // Añadir parámetro de lanzamiento BT
                                        iter    // Añadir parámetro de iteraciones BT
                                );
                                executor.execute(log);
                            }
                        }
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

