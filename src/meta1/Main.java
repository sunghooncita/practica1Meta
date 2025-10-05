package meta1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        File carpeta = new File("src/datos");

        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".dat"));

        // Semilla base
        int semillaBase = 77645037;
        int K = 5;
        int iterMax = 5000;

        for (File archivo : archivos) {
            try {
                System.out.println("Archivo: " + archivo.getName());

                int[][][] matrices = LeerMatriz.leerArchivo(archivo.getPath());
                int[][] flujos = matrices[0];
                int[][] distancias = matrices[1];

                // GREEDY
                int[] solGreedy = Greedy.algoritmoGreedy(flujos, distancias);
                int costoGreedy = Greedy.calcularCosto(solGreedy, flujos, distancias);
                System.out.println("  Greedy -> Costo: " + costoGreedy);

                Random rnd = new Random(semillaBase);
                for (int i = 0; i < 5; i++) {

                    System.out.println(" Ejecucion "+(i+1)+": ");
                    // GREEDY ALEATORIO
                    int[] solGA = GreedyAleatorio.algoritmoGreedyAleatorio(flujos, distancias, K, rnd);
                    int costoGA = Greedy.calcularCosto(solGA, flujos, distancias);
                    System.out.println("  Greedy Aleatorio -> Costo: " + costoGA);

                    // BÚSQUEDA LOCAL
                    int[] solBL = BusquedaLocal.busquedaLocalPrimerMejor(solGA, flujos, distancias, iterMax);
                    int costoBL = Greedy.calcularCosto(solBL, flujos, distancias);
                    System.out.println("  Búsqueda Local -> Costo: " + costoBL);

                }
                System.out.println();

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
