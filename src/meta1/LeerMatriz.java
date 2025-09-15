package meta1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LeerMatriz {

    // int de tres dimensiones porque devuelve un arreglo de 2 matrices
    public static int[][][] leerArchivo(String rutaArchivo) throws FileNotFoundException {

        // Abrir el fichero
        File file = new File(rutaArchivo); // Ruta al fichero
        Scanner sc = new Scanner(file);

        // Leer tamaño del problema
        int n = sc.nextInt();

        // Inicializar matrices
        int[][] flujos = new int[n][n];
        int[][] distancias = new int[n][n];

        // Sean p departamentos (pi, i=1, …, n) y localizaciones (lj, j=1, … ,n)

        // Leer matriz de flujos (n de piezas q pasan del departamento i al j)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                flujos[i][j] = sc.nextInt();
            }
        }

        // Leer matriz de distancias (distancia entre los departamentos i y j)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = sc.nextInt();
            }
        }

        sc.close();

        // devuelve las matrices
        return new int[][][]{flujos, distancias};

    }


// Método auxiliar para imprimir una matriz
public static void imprimirMatriz(int[][] matriz) {
    for (int[] fila : matriz) {
        for (int valor : fila) {
            System.out.print(valor + " ");
        }
        System.out.println();
    }
}

}
