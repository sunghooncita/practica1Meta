package algoritmos;

import java.util.Arrays;

public class AlgGE15 {
    public static int[] algoritmoGreedy(int[][] flujos, int[][] distancias) {

        int n = flujos.length;

        // Calcular suma de flujos por unidad, cuanto mayor sea la suma, más activo es ese departamento (es prioritario)
        int[] sumaFlujos = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sumaFlujos[i] += flujos[i][j];
            }
        }

        // Calcular suma de distancias por ubicación, si una localizacion tiene poca suma es mas centrica
        int[] sumaDistancias = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sumaDistancias[i] += distancias[i][j];
            }
        }

        // Ordenar unidades por flujo (de mayor(mas activa) a menor)
        Integer[] unidades = new Integer[n];
        for (int i = 0; i < n; i++) unidades[i] = i;
        Arrays.sort(unidades, (a, b) -> Integer.compare(sumaFlujos[b], sumaFlujos[a]));

        // Ordenar ubicaciones por distancia total (de menor (mas centrica) a mayor)
        Integer[] ubicaciones = new Integer[n];
        for (int i = 0; i < n; i++) ubicaciones[i] = i;
        Arrays.sort(ubicaciones, (a, b) -> Integer.compare(sumaDistancias[a], sumaDistancias[b]));

        // Asignar mayor flujo a menor distancia
        int[] solucion = new int[n];
        for (int i = 0; i < n; i++) {
            solucion[unidades[i]] = ubicaciones[i];
        }

        //Devuelve un vector solucion que indica en qué localización se coloca cada departamento.
        return solucion;
    }

    // Metodo para calcular costos
    public static int calcularCosto(int[] solucion, int[][] flujos, int[][] distancias) {
        //Recorres todos los pares (i, j), tomas los flujos y la distancia entre las posiciones, multiplicas y acumulas en costo.
        int costo = 0;
        int n = solucion.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                costo += flujos[i][j] * distancias[solucion[i]][solucion[j]];
            }
        }
        return costo;
    }
}
