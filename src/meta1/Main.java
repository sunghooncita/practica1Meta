package meta1;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {

            int[][][] matrices = LeerMatriz.leerArchivo("src/datos/ford01.dat");
            int[][] flujos = matrices[0];
            int[][] distancias = matrices[1];

            // Mostrar matrices
            System.out.println("\nMatriz de Flujos:");
            LeerMatriz.imprimirMatriz(flujos);

            System.out.println("\nMatriz de Distancias:");
            LeerMatriz.imprimirMatriz(distancias);

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
