package algoritmos;

public class BusquedaLocal {

    public static int[] busquedaLocalPrimerMejor(int[] solucionInicial, int[][] flujos, int[][] distancias, int iterMax, meta1.Logs logHelper) {

        int n = solucionInicial.length;
        int[] solucion = solucionInicial.clone();
        int[] dlb = new int[n];  // 0 = prometedor, 1 = no prometedor
        int start_i = 0;         // índice cíclico de inicio

        for (int iter = 0; iter < iterMax; iter++) {
            boolean improved = false;

            for (int step = 0; step < n; step++) {
                int i = (start_i + step) % n;

                if (dlb[i] == 0) {
                    boolean localImproved = false;

                    // j también cíclico
                    for (int jStep = 1; jStep < n; jStep++) {
                        int j = (i + jStep) % n;
                        int delta = checkMove(solucion, flujos, distancias, i, j);

                        if (delta < 0) { // mejora
                            // intercambio
                            int temp = solucion[i];
                            solucion[i] = solucion[j];
                            solucion[j] = temp;

                            if (logHelper != null) { // Si el logHelper es nulo (ej. para inicialización de Tabu), no hacer nada
                                int nuevoCosto = Greedy.calcularCosto(solucion, flujos, distancias);
                                logHelper.registrarIntercambio(i, j, nuevoCosto, true);
                            }

                            dlb[i] = dlb[j] = 0;
                            localImproved = true;
                            improved = true;

                            break; // primer mejor
                        }
                    }

                    if (!localImproved) {
                        dlb[i] = 1;  // marcar como no prometedor
                    }
                }
            }

            // actualizar start_i para que sea cíclico
            start_i = (start_i + 1) % n;

            // estancamiento: ninguna mejora en esta iteración
            if (!improved) {
                break;
            }
        }

        return solucion;
    }

    // calcular cambio en costo al intercambiar i y j
    public static int checkMove(int[] solucion, int[][] flujos, int[][] distancias, int i, int j) {
        int n = solucion.length;
        int delta = 0;

        for (int k = 0; k < n; k++) {
            if (k != i && k != j) {
                delta += (flujos[i][k] - flujos[j][k]) *
                        (distancias[solucion[j]][solucion[k]] - distancias[solucion[i]][solucion[k]])
                        + (flujos[k][i] - flujos[k][j]) *
                        (distancias[solucion[k]][solucion[j]] - distancias[solucion[k]][solucion[i]]);
            }
        }
        return delta;
    }
}
