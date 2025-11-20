package algoritmos;

import meta1.utilities;

public class AlgBL15 {

    //Metodo principal (el logHelper registra los intercambios)
    public static int[] busquedaLocalPrimerMejor(int[] solucionInicial, int[][] flujos, int[][] distancias, int iterMax, meta1.Logs logHelper) {
        int n = solucionInicial.length;
        int[] solucion = solucionInicial.clone();
        int[] dlb = new int[n];
        int start_i = 0;

        for (int iter = 0; iter < iterMax; iter++) {
            boolean improved = false;

            //Bucle principal para la primera posición (i) del intercambio
            for (int step = 0; step < n; step++) {
                int i = (start_i + step) % n;

                if (dlb[i] == 0) { // Si la posición 'i' es prometedora
                    boolean localImproved = false;

                    // Bucle para la segunda posición (j) del intercambio
                    for (int jStep = 1; jStep < n; jStep++) {
                        int j = (i + jStep) % n;
                        // Calcula el cambio de costo (delta) sin hacer el intercambio
                        int delta = checkMove(solucion, flujos, distancias, i, j);

                        if (delta < 0) { //Si hay mejora se hace el intercambio
                            int temp = solucion[i];
                            solucion[i] = solucion[j];
                            solucion[j] = temp;

                            //if (logHelper != null) { // Si el logHelper es nulo, no hacer nada
                            //    int nuevoCosto = utilities.calcularCosto(solucion, flujos, distancias);
                            //    logHelper.registrarIntercambio(i, j, nuevoCosto, true);
                            //}

                            //Lo reseteamos para las posiciones modificadas
                            dlb[i] = dlb[j] = 0;
                            localImproved = true;
                            improved = true;

                            break; //Sale del bucle, primer mejor
                        }
                    }

                    if (!localImproved) {
                        dlb[i] = 1;  // marcar como no prometedor
                    }
                }
            }

            //Actualizamos start_i para la proxima iteracion
            start_i = (start_i + 1) % n;

            //Criterio de parada, si no hubo mejora en un recorrido completo, se sale
            if (!improved) {
                break;
            }
        }

        return solucion;
    }

    //Calculamos cambio en costo al intercambiar i y j
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
