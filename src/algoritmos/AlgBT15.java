package algoritmos;
import java.util.*;

public class AlgBT15 {

    //Metodo principal (el logHelper registra los intercambios)
    public int[] ejecutar(int[] solucion, int maxIter, int[][] dist, int[][] flujo,
                          int tenenciaTabu) {
        int n = solucion.length;
        int[] mejor = solucion.clone();
        double mejorValor = costo(solucion, dist, flujo);
        int[] actual = solucion.clone();
        double valorActual = mejorValor;

        // Memoria a corto plazo (Lista Tabú)
        Deque<int[]> listaTabu = new ArrayDeque<>();

        Random rand = new Random();

        for (int iter = 0; iter < maxIter; iter++) {
            int[] mejorMovimiento = null;
            double mejorValMov = Double.MAX_VALUE;

            int[] peorMovimiento = null;
            double peorValMov = Double.MAX_VALUE;

            //Busqueda del mejor movimiento en el vecindario
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    intercambiar(actual, i, j); //Realizar el movimiento temporalmente
                    double valor = costo(actual, dist, flujo);
                    boolean esTabu = esMovimientoTabu(listaTabu, i, j);

                    // Criterio de aspiración: Acepta el movimiento tabú si mejora el mejor global
                    if (esTabu && valor < mejorValor) {
                        // Si es tabú pero mejora al mejor global, se acepta por aspiración
                        if (valor < mejorValMov) {
                            mejorMovimiento = new int[]{i, j};
                            mejorValMov = valor;
                        }
                    } else if (!esTabu) {
                        // Identificamos el mejor movimiento no tabú
                        if (valor < mejorValMov) {
                            mejorMovimiento = new int[]{i, j};
                            mejorValMov = valor;
                        }
                    }

                    // Identificamos el mejor movimiento que no mejora la solución actual
                    // (para cuando no haya mejoras y no sea tabú)
                    if (!esTabu && valor >= valorActual && valor < peorValMov) {
                        peorMovimiento = new int[]{i,j};
                        peorValMov = valor;
                    }

                    intercambiar(actual, i, j);
                }
            }

            // Seleccionamos el movimiento
            int[] mov;
            if (mejorMovimiento != null && mejorValMov < valorActual) {
                // Seleccionamos el mejor movimiento de mejora (tabú por aspiración o no tabú)
                mov = mejorMovimiento;
                valorActual = mejorValMov;
            } else if (mejorMovimiento != null && mejorValMov < peorValMov && mejorValMov < valorActual) {
                // Mejor movimiento de mejora no es null, y es mejor que el peorMovimiento
                mov = mejorMovimiento;
                valorActual = mejorValMov;
            } else if (peorMovimiento != null) {
                // Si no hay mejoras, seleccionamos el mejor movimiento no tabú
                mov = peorMovimiento;
                valorActual = peorValMov;
            } else {
                // Criterio de parada si no hay movimientos válidos (el vecindario es tabú o no mejora)
                break;
            }

            // Aceptamos el movimiento
            intercambiar(actual, mov[0], mov[1]);
            // El valorActual ya se ha establecido arriba.

            // Actualizamos la lista tabú
            listaTabu.addLast(mov);
            // La tenencia Tabú debería ser un valor fijo o un parámetro,
            // se mantiene el parámetro pero sin el uso de las otras variables.
            if (listaTabu.size() > tenenciaTabu) listaTabu.removeFirst();


            // Actualizamos mejor solución global
            if (valorActual < mejorValor) {
                mejorValor = valorActual;
                mejor = actual.clone();
            }
        }
        return mejor;
    }

    // Metodo auxiliar para verificar la lista Tabu
    boolean esMovimientoTabu(Deque<int[]> listaTabu, int i, int j) {
        // Verifica si el par de índices (i, j) o (j, i) está en la lista Tabú
        for (int[] m : listaTabu)
            if ((m[0]==i&&m[1]==j)||(m[0]==j&&m[1]==i)) return true;
        return false;
    }

    // Metodo auxiliar para calcular el costo
    double costo(int[] sol, int[][] dist, int[][] flujo) {
        double c=0; int n=sol.length;
        for(int i=0;i<n;i++) for(int j=0;j<n;j++) c += flujo[i][j] * dist[sol[i]][sol[j]];
        return c;
    }

    // Metodo auxiliar para intercambiar
    void intercambiar(int[] s, int i, int j) { int t=s[i]; s[i]=s[j]; s[j]=t; }

}