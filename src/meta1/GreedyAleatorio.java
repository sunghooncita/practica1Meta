package meta1;

import java.util.*;

public class GreedyAleatorio {
    public static int[] algoritmoGreedyAleatorio(int[][] flujos, int[][] distancias, int K, Random rnd) {

        int n = flujos.length;

        // Calculamos suma de flujos y distancias
        int[] sumaFlujos = new int[n];
        int[] sumaDistancias = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sumaFlujos[i] += flujos[i][j];
                sumaDistancias[i] += distancias[i][j];
            }
        }

        // Ordenamos unidades por flujo descendente (las más activas primero)
        Integer[] unidades = new Integer[n];
        for (int i = 0; i < n; i++) unidades[i] = i;
        Arrays.sort(unidades, (a, b) -> Integer.compare(sumaFlujos[b], sumaFlujos[a]));

        // Ordenamos ubicaciones por distancia ascendente (las más centrales primero)
        Integer[] ubicaciones = new Integer[n];
        for (int i = 0; i < n; i++) ubicaciones[i] = i;
        Arrays.sort(ubicaciones, (a, b) -> Integer.compare(sumaDistancias[a], sumaDistancias[b]));

        // Creamos listas dinámicas para eliminar elementos dinamicamente
        List<Integer> unidadesRestantes = new ArrayList<>(Arrays.asList(unidades));
        List<Integer> ubicacionesRestantes = new ArrayList<>(Arrays.asList(ubicaciones));

        int[] solucion = new int[n];

        for (int i = 0; i < n; i++) {
            // Limitamos K si quedan menos elementos, para evitar errores por pasar un número mayor que el tamaño de la lista
            int limiteU = (K < unidadesRestantes.size()) ? K : unidadesRestantes.size();
            int limiteL = (K < ubicacionesRestantes.size()) ? K : ubicacionesRestantes.size();

            // Elegimos aleatoriamente entre los K mejores
            //Para que el algoritmo sea aleatorio, pero que puedas volver a obtener el mismo resultado si usas la misma configuración, usamos una semilla
            int indiceU = rnd.nextInt(limiteU);
            int indiceL = rnd.nextInt(limiteL);

            int unidad = unidadesRestantes.get(indiceU);
            int ubicacion = ubicacionesRestantes.get(indiceL);

            // Asignamos ubicación a la unidad
            solucion[unidad] = ubicacion;

            // Eliminamos unidad y ubicación ya usadas para que no se repitan
            unidadesRestantes.remove(indiceU);
            ubicacionesRestantes.remove(indiceL);
        }

        return solucion;
    }

    public static int calcularCosto(int[] solucion, int[][] flujos, int[][] distancias) {
        int n = solucion.length;
        int costo = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                costo += flujos[i][j] * distancias[solucion[i]][solucion[j]];
            }
        }
        return costo;
    }
}

