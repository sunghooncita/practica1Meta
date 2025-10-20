package algoritmos;

public class BusquedaLocal {

    public static int[] busquedaLocalPrimerMejor(int[] solucionInicial, int[][] flujos, int[][] distancias, int iterMax) {

        int n = solucionInicial.length;
        int[] solucion = solucionInicial.clone();   // para no modificar el original
        int[] dlb = new int[n];
        int iteraciones = 0;


        for (int i = 0; i < n; i++) {
            if (dlb[i] == 0) {
                boolean improve_flag = false;

                for (int j = i + 1; j < n; j++) {
                    int check = checkMove(solucion, flujos, distancias, i, j);
                    if (check < 0) { //si disminuye el costo, aplicamos el intercambio
                        int temp = solucion[i]; //aplicamos el movimiento
                        solucion[i] = solucion[j];
                        solucion[j] = temp;

                        dlb[i] = dlb[j] = 0;
                        improve_flag = true;


                        break;
                    }
                }

                // si no hubo mejora, marcamos dlb[i] = 1 (ya no mira más)
                if (!improve_flag) {
                    dlb[i] = 1;
                }
            }
        }

        iteraciones++;

        System.out.println("Búsqueda Local en " + iteraciones + " iteraciones.");
        return solucion;
    }

    // calculamos el cambio en el costo al intercambiar i y j
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