package algoritmos;

public class BusquedaLocal {

    public static int[] busquedaLocalPrimerMejor(int[] solucionInicial, int[][] flujos, int[][] distancias, int iterMax) {

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

//comprobar q i da la vuelta completa
//la i no vale 0 si no lo q nos quedamos en la anterior posicion
//yo hago las comprobaciones del dlb y la 3 se intercambia con la 6, la siguiente vez que empiece empieza en el 4 no en el 0
//la i del dlb tiene eq comprobarse n veces
//la j del dlb tiene tambien q hacer la vuelta completa
//es ciclico
//lo hacemos porque si siempre empezamos en el 0 las primeras unidades tienen mas probabilidad de cambiarse; sin embargo, haciendolo asi todos tienen
//  las mismas posibilidades

//Fichero de parametros el algoritmo tiene 2 parametros, tiene que ejecutarse entero del tiron, sean 2 parametros o 5000
//el random es indepe de cada ejecucion pero lo tenemos bien chat la inicializacion de la semilla tiene que estar en el main al principio,
// tenemos q llamar 64 veces al random
//FALLO GORDO LO DEL CICLICO
//los logs: lanzo el greedy aleatorizado su resultado es tanto, entra el dlb y su resultado es tanto, lo vamos registrando todo, si hay estancamiento
// o no, tenemos tantos ficheros logs como instancias es decir 64 ficheros, estos se suben a drive y en el informa ponemos el enlace