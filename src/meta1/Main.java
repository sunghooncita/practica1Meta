package meta1;

import configuracion.Configurador;
import algoritmos.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        // Cargar configuración desde archivo
        Configurador config = new Configurador("src/configuracion/config.txt");

        ArrayList<String> archivosConfig = config.getArchivos();
        ArrayList<String> algoritmosConfig = config.getAlgoritmos();
        ArrayList<Integer> semillaConfig = config.getSemillas();
        Integer iter = config.getIteraciones();
        int K= config.getK();
        int tenenciaTabu = config.getTenenciaTabu();
        double oscilacionEstrategica = config.getOscilacion();
        double estancamiento = config.getEstancamiento();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (String rutaArchivo : archivosConfig) {

            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                System.out.println(" No se encontró el archivo: " + archivo.getAbsolutePath());
                continue;
            }

            try {
                System.out.println("\nProcesando archivo: " + archivo.getName());

                // Leer las matrices de flujo y distancia
                int[][][] matrices = LeerMatriz.leerArchivo(archivo.getPath());
                int[][] flujos = matrices[0];
                int[][] distancias = matrices[1];

                int[] solGreedy = Greedy.algoritmoGreedy(flujos, distancias);
                int costoGreedy = Greedy.calcularCosto(solGreedy, flujos, distancias);
                System.out.println("\n  Greedy -> Costo: " + costoGreedy);



                // Bucle de las 5 ejecuciones
                for (int i = 1; i <= semillaConfig.size(); i++) {

                    long semillaBase =  semillaConfig.get(i-1).longValue();
                    Random rnd = new Random(semillaBase);
                    // 3. Inicializar el Random con la semilla maestra (rotada o el DNI original).
                    System.out.println("\n Ejecución " + i + " con semilla " + semillaBase + "\n");

                    CountDownLatch cdl = new CountDownLatch(algoritmosConfig.size());

                    for (String algoritmo : algoritmosConfig) {

                        long inicioTiempo = System.currentTimeMillis();
                        Logs log = null; // Inicializamos la referencia a null

                        switch (algoritmo) {
                            case "GreedyAleatorio":
                                int[] solGA = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int costoGA = Greedy.calcularCosto(solGA, flujos, distancias);
                                long tiempoGA = System.currentTimeMillis() - inicioTiempo;

                                //Inicialización de Logs con los resultados finales
                                log = new Logs(config, "GreedyAleatorio", archivo.getName(), cdl, semillaBase,
                                        solGA, costoGA,     // Solución inicial
                                        solGA, costoGA,     // Solución final
                                        tiempoGA);
                                break;

                            case "BusquedaLocal":

                                // 1. Solución Inicial (Greedy Aleatorio, usa la misma semilla)
                                int[] solGA2 = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int costoInicialBL = Greedy.calcularCosto(solGA2, flujos, distancias);

                                // 2. Creamos el objeto Logs ANTES de la ejecución de BL para que pueda registrar
                                log = new Logs(config, "BusquedaLocal", archivo.getName(), cdl, semillaBase,
                                        solGA2, costoInicialBL, // Solución inicial (GA)
                                        null, 0, 0); // Temporales para final

                                int[] solBL = BusquedaLocal.busquedaLocalPrimerMejor(solGA2, flujos, distancias, iter,log);
                                int costoBL = Greedy.calcularCosto(solBL, flujos, distancias);

                                long tiempoBL = System.currentTimeMillis() - inicioTiempo;

                                // 🚨 Sobreescribir 'log' con los resultados finales y el tiempo
                                log.solucionFinal = solBL.clone();
                                log.costoFinal = costoBL;
                                log.tiempoTotalMs = tiempoBL;
                                break;

                            case "BusquedaTabu":

                                // 1. Solución Inicial (de BL, que viene de GA con la misma semilla)
                                int[] solGA3 = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int[] solInicialTabu = BusquedaLocal.busquedaLocalPrimerMejor(solGA3, flujos, distancias, iter,null);
                                int costoInicialTabu = Greedy.calcularCosto(solInicialTabu, flujos, distancias);

                                // 2. Creamos el objeto Logs ANTES de la ejecución de BT
                                log = new Logs(config, "BusquedaTabu", archivo.getName(), cdl, semillaBase,
                                        solInicialTabu, costoInicialTabu, // Solución inicial (BL)
                                        null, 0, 0); // Temporales para final

                                // 🚨 Debes pasar el objeto 'log' al método ejecutar para que registre los intercambios
                                BusquedaTabu bt = new BusquedaTabu();
                                int[] solTabu = bt.ejecutar(solInicialTabu, 1000, flujos, distancias, tenenciaTabu, oscilacionEstrategica, estancamiento,log);
                                int costoTabu = Greedy.calcularCosto(solTabu, flujos,distancias);
                                long tiempoTabu = System.currentTimeMillis() - inicioTiempo;
                                log.solucionFinal = solTabu.clone();
                                log.costoFinal = costoTabu;
                                log.tiempoTotalMs = tiempoTabu;
                                break;
                            default:
                                System.out.println(" Algoritmo no reconocido: " + algoritmo);
                                cdl.countDown(); // Contar abajo para que la espera no se bloquee por algoritmos no existentes
                        }
                        if (log != null) {
                            executor.submit(log);
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
        executor.shutdown();
    }

}



