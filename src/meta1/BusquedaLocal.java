package meta1;

public class BusquedaLocal {
    public static int[] busquedaLocalPrimerMejor(int[] solucionInicial, int[][] flujos, int[][] distancias, int iterMax) {

        int n = solucionInicial.length;
        int[] solucion = solucionInicial.clone(); //para no modificar el original
        boolean[] noPrometedor = new boolean[n]; // false = se puede mover, true = no se revisa porque ya no mejora
        int iteraciones = 0;

        while (iteraciones < iterMax) {
            boolean mejoraGlobal = false;

            for (int i = 0; i < n; i++) {
                if (noPrometedor[i] == true) continue; // saltar los desactivados
                boolean mejoraLocal = false;

                for (int j = i + 1; j < n; j++) {
                    int delta = funcionEvaluacion(solucion, flujos, distancias, i, j);
                    if (delta < 0) { //si disminuye el costo, aplicamos el intercambio
                        int temp = solucion[i];
                        solucion[i] = solucion[j];
                        solucion[j] = temp;

                        noPrometedor[i] = noPrometedor[j] = false; // reactivar ambos
                        mejoraLocal = true;
                        mejoraGlobal = true;
                        break; // primera mejora
                    }
                }

                if (mejoraLocal == false) {
                    noPrometedor[i] = true; // desactivar si no mejoró
                }
            }

            if (mejoraGlobal == false) {
                break; // no hay más mejoras
            }

            iteraciones++;
        }

        System.out.println("  Búsqueda Local terminada en " + iteraciones + " iteraciones.");
        return solucion;
    }

    // Calculamos cuanto cambia el costo al intercambiar i y j
    public static int funcionEvaluacion(int[] solucion, int[][] flujos, int[][] distancias, int i, int j) {
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
