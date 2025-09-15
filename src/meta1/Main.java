package meta1;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {

            int[][][] matrices = LeerMatriz.leerArchivo("src/datos/ford04.dat");
            int[][] flujos = matrices[0];
            int[][] distancias = matrices[1];

            // Mostrar matrices
            //System.out.println("\nMatriz de Flujos:");
            //LeerMatriz.imprimirMatriz(flujos);

            //System.out.println("\nMatriz de Distancias:");
            //LeerMatriz.imprimirMatriz(distancias);

            int[] solucion = Greedy.algoritmoGreedy(flujos, distancias);
            int costo = Greedy.calcularCosto(solucion, flujos, distancias);

            System.out.println(" Costo total: " + costo);
/*
            //Mostrar asignaciones
            System.out.println("\n Asignación unidades a ubicaciones:");
            for (int i = 0; i < solucion.length; i++) {
                System.out.println("   Unidad " + i + ": Ubicación " + solucion[i]);
            }
*/
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
