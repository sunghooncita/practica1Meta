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
        Integer semillaConfig = config.getSemillas();
        Integer iter = config.getIteraciones();
        int K= config.getK();
        int tenenciaTabu = config.getTenenciaTabu();
        double oscilacionEstrategica = config.getOscilacion();
        double estancamiento = config.getEstancamiento();



        ExecutorService executor = Executors.newFixedThreadPool(5);
        // Recorremos los archivos configurados
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

                CountDownLatch cdl = new CountDownLatch(5);
                Random rnd = new Random(semillaConfig);
                for (int i = 1; i <= 5; i++) {

                    System.out.println("\n Ejecución " + i + ": ");

                    // Ejecutar los algoritmos definidos en el archivo de configuración
                    for (String algoritmo : algoritmosConfig) {
                        CountDownLatch cdl = new CountDownLatch(1);
                        switch (algoritmo) {
                            case "GreedyAleatorio":
                                ArrayList<Logs> l = new ArrayList<>();
                                int[] solGA = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int costoGA = Greedy.calcularCosto(solGA, flujos, distancias);
                                System.out.println("  Greedy Aleatorio -> Costo: " + costoGA);
                                break;

                            case "BusquedaLocal":
                                // Se parte de una solución generada por el Greedy Aleatorio
                                int[] solGA2 = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int[] solBL = BusquedaLocal.busquedaLocalPrimerMejor(solGA2, flujos, distancias, iter);
                                int costoBL = Greedy.calcularCosto(solBL, flujos, distancias);
                                System.out.println("  Búsqueda Local -> Costo: " + costoBL);
                                break;

                            case "BusquedaTabu":
                                int[] solGA3 = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                                int[] solInicialTabu = BusquedaLocal.busquedaLocalPrimerMejor(solGA3, flujos, distancias, iter);
                                BusquedaTabu bt = new BusquedaTabu();
                                int[] solTabu = bt.ejecutar(solInicialTabu, 1000, flujos, distancias, tenenciaTabu, oscilacionEstrategica, estancamiento);
                                int costoTabu = Greedy.calcularCosto(solTabu, distancias, flujos);
                                System.out.println("  Búsqueda Tabú -> Costo: " + costoTabu);
                                break;

                            default:
                                System.out.println(" Algoritmo no reconocido: " + algoritmo);
                        }
                    }
                }

                System.out.println("\n--------------------------------------------\n");

            } catch (FileNotFoundException e) {
                System.out.println("Error leyendo el archivo: " + e.getMessage());
            }
        }
    }

    public void guardarArchivos(String ruta, String texto){
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(ruta);
            pw = new PrintWriter(fichero);

            pw.println(texto);
        }catch (IOException e){
        }finally {{
            try{
                if(fichero != null){
                    fichero.close();
                }
            }catch (IOException e2){}
        }
        }
    }
}



