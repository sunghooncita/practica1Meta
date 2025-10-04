package meta1;

public class BusquedaLocal {
    public static int[] busquedaLocalPrimerMejor(int[] solucionInicial, int[][] flujos, int[][] distancias, int iterMax) {

        int n = solucionInicial.length;
        int[] solucion = solucionInicial.clone();
        int costoActual = Greedy.calcularCosto(solucion, flujos, distancias);

        boolean[] noPrometedor = new boolean[n]; // false = se puede mover, true = no se mueve
        int iteraciones = 0;

        while (iteraciones < iterMax) {
            boolean mejoraGlobal = false;

            for (int i = 0; i < n; i++) {
                if (noPrometedor[i]) continue; // saltar los desactivados
                boolean mejoraLocal = false;

                for (int j = i + 1; j < n; j++) {
                    int delta = calcularDelta(solucion, flujos, distancias, i, j);
                    if (delta < 0) {
                        // aplicar el intercambio
                        int temp = solucion[i];
                        solucion[i] = solucion[j];
                        solucion[j] = temp;

                        costoActual += delta;
                        noPrometedor[i] = noPrometedor[j] = false; // reactivar ambos
                        mejoraLocal = true;
                        mejoraGlobal = true;
                        break; // first improvement
                    }
                }

                if (!mejoraLocal) {
                    noPrometedor[i] = true; // desactivar si no mejoró
                }
            }

            if (!mejoraGlobal) {
                break; // no hay más mejoras
            }

            iteraciones++;
        }

        System.out.println("  Búsqueda Local terminada en " + iteraciones + " iteraciones.");
        return solucion;
    }


    // Calcular cambio de costo al intercambiar i y j
    public static int calcularDelta(int[] solucion, int[][] flujos, int[][] distancias, int i, int j) {
        int n = solucion.length;
        int delta = 0;
        for (int k = 0; k < n; k++) {
            if (k != i && k != j) {
                delta += (flujos[i][k] - flujos[j][k]) * (distancias[solucion[j]][solucion[k]] - distancias[solucion[i]][solucion[k]])
                        + (flujos[k][i] - flujos[k][j]) * (distancias[solucion[k]][solucion[j]] - distancias[solucion[k]][solucion[i]]);
            }
        }
        return delta;
    }
}
