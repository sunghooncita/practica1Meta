package meta1;

import configuracion.Configurador;
import algoritmos.*;
import algoritmos.modelos.*;
import java.io.*;
import java.util.ArrayList;
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

        System.out.println("🤖 Iniciando el Algoritmo Genético AlgGen15...");

        try {
            // 1. Instanciar la clase del algoritmo
            AlgGen15 ag = new AlgGen15();

            // 2. Ejecutar el algoritmo
            Individuo mejorSolucion = ag.ejecutar();

            // 3. Mostrar resultados finales
            System.out.println("\n--- SOLUCIÓN FINAL ---");
            System.out.println("Mejor Fitness Encontrado: " + mejorSolucion.getFitness());
            // Nota: La información sobre evaluaciones y tiempo se imprime en el método 'ejecutar' de AlgGen15.

        } catch (Exception e) {
            System.err.println(" Ocurrió un error grave durante la ejecución del AG.");
            e.printStackTrace();
        }

        //Creamos varios hilos para guardar los logs al mismo tiempo,
        //así el programa no se queda esperando mientras se escriben los archivos.
        //ExecutorService executor = Executors.newFixedThreadPool(5);

        //Bucle principal q itera sobre cada archivo de datos
       /* for (String rutaArchivo : archivosConfig) {

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

                //Registro del algoritmo greedy
                long inicioTiempoGreedy = System.currentTimeMillis();
                int[] solGreedy = AlgGE15.algoritmoGreedy(flujos, distancias);
                int costoGreedy = AlgGE15.calcularCosto(solGreedy, flujos, distancias);
                long tiempoGreedy = System.currentTimeMillis() - inicioTiempoGreedy;
                //Crear y ejecutar el log de Greedy
                CountDownLatch cdlGreedy = new CountDownLatch(1);
                Logs logGreedy = new Logs(config, "Greedy", archivo.getName(), cdlGreedy,
                        0,
                        solGreedy, costoGreedy,
                        solGreedy, costoGreedy,
                        tiempoGreedy);
                executor.submit(logGreedy);
                System.out.printf("\n  Greedy -> Costo: %d (Tiempo: %dms)\n", logGreedy.costoFinal, logGreedy.tiempoTotalMs);

                // Bucle con semillas diferentes
                for (int i = 1; i <= semillaConfig.size(); i++) {

                    long semillaBase =  semillaConfig.get(i-1).longValue();
                    Random rnd = new Random(semillaBase);

                    System.out.println("\n Ejecución " + i + " con semilla " + semillaBase + "\n");

                    // Contador para esperar a que todos los logs de este grupo terminen
                    CountDownLatch cdl = new CountDownLatch(algoritmosConfig.size());

                    for (String algoritmo : algoritmosConfig) {

                        long inicioTiempo = System.currentTimeMillis();
                        Logs log = null;

                        switch (algoritmo) {
                            case "GreedyAleatorio":
                                // Algoritmo de construccion greedy aleatorio
                                int[] solGA = AlgGA15.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int costoGA = AlgGE15.calcularCosto(solGA, flujos, distancias);
                                long tiempoGA = System.currentTimeMillis() - inicioTiempo;
                                //Preparamos el reporte final, como es un algoritmo no
                                //iterativo la solucion final e inicial son las mismas
                                log = new Logs(config, "GreedyAleatorio", archivo.getName(), cdl, semillaBase,
                                        solGA, costoGA, solGA, costoGA, tiempoGA);
                                break;

                            case "BusquedaLocal":
                                //Solucion inicial (greedy aleatorio)
                                int[] solGA2 = AlgGA15.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int costoInicialBL = AlgGE15.calcularCosto(solGA2, flujos, distancias);
                                //Creamos el logs y lo inicializamos con los datos iniciales
                                log = new Logs(config, "BusquedaLocal", archivo.getName(), cdl, semillaBase,
                                        solGA2, costoInicialBL, null, 0, 0);
                                int[] solBL = AlgBL15.busquedaLocalPrimerMejor(solGA2, flujos, distancias, iter,log);
                                int costoBL = AlgGE15.calcularCosto(solBL, flujos, distancias);
                                long tiempoBL = System.currentTimeMillis() - inicioTiempo;
                                //Sobreescribimos el log con los resultados finales y el tiempo
                                log.solucionFinal = solBL.clone();
                                log.costoFinal = costoBL;
                                log.tiempoTotalMs = tiempoBL;
                                break;

                            case "BusquedaTabu":

                                //Solucion inicial (busqueda tabu)
                                int[] solGA3 = AlgGA15.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int[] solInicialTabu = AlgBL15.busquedaLocalPrimerMejor(solGA3, flujos, distancias, iter,null);
                                int costoInicialTabu = AlgGE15.calcularCosto(solInicialTabu, flujos, distancias);

                                //Creamos el logs y lo inicializamos con los datos iniciales
                                log = new Logs(config, "BusquedaTabu", archivo.getName(), cdl, semillaBase,
                                        solInicialTabu, costoInicialTabu, null, 0, 0);
                                AlgBT15 bt = new AlgBT15();
                                int[] solTabu = bt.ejecutar(solInicialTabu, 1000, flujos, distancias, tenenciaTabu, oscilacionEstrategica, estancamiento,log);
                                int costoTabu = AlgGE15.calcularCosto(solTabu, flujos,distancias);
                                long tiempoTabu = System.currentTimeMillis() - inicioTiempo;
                                //Sobreescribimos el log con los resultados finales y el tiempo
                                log.solucionFinal = solTabu.clone();
                                log.costoFinal = costoTabu;
                                log.tiempoTotalMs = tiempoTabu;
                                break;

                            default:
                                System.out.println(" Algoritmo no reconocido: " + algoritmo);
                                cdl.countDown();
                        }
                        if (log != null) {
                            executor.submit(log);
                            // Muestra el resultado final en consola
                            System.out.printf("  %s -> Costo: %d (Tiempo: %dms)%n", algoritmo, log.costoFinal, log.tiempoTotalMs);
                        }
                    }

                    try {
                        cdl.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                System.out.println("\n--------------------------------------------\n");

            } catch (FileNotFoundException e) {
                System.out.println("Error leyendo el archivo: " + e.getMessage());
            }
        }
        executor.shutdown();*/
    }

}



