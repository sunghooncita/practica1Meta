package algoritmos;
import java.util.*;

public class BusquedaTabu {

    // Búsqueda Tabú simple con parámetros pasados por el main
    public int[] ejecutar(int[] solucion, int maxIter, int[][] dist, int[][] flujo,
                          int tenenciaTabu, double oscilacionEstrategica, double estancamiento, meta1.Logs logHelper) {
        int n = solucion.length;
        int[] mejor = solucion.clone();
        double mejorValor = costo(solucion, dist, flujo);
        int[] actual = solucion.clone();
        double valorActual = mejorValor;

        Deque<int[]> listaTabu = new ArrayDeque<>();
        int[][] frecuencia = new int[n][n];
        int sinMejora = 0;
        Random rand = new Random();

        for (int iter = 0; iter < maxIter; iter++) {
            int[] mejorMovimiento = null; double mejorValMov = Double.MAX_VALUE;
            int[] peorMovimiento = null; double peorValMov = Double.MAX_VALUE;

            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    intercambiar(actual, i, j);
                    double valor = costo(actual, dist, flujo);
                    boolean esTabu = esMovimientoTabu(listaTabu, i, j);

                    if (esTabu && valor >= mejorValor) { intercambiar(actual, i, j); continue; }

                    if (valor < valorActual && valor < mejorValMov) { mejorMovimiento = new int[]{i,j}; mejorValMov = valor; }
                    if (valor >= valorActual && valor < peorValMov) { peorMovimiento = new int[]{i,j}; peorValMov = valor; }

                    intercambiar(actual, i, j);
                }
            }

            int[] mov = mejorMovimiento != null ? mejorMovimiento : peorMovimiento;
            if (mov == null) break;

            intercambiar(actual, mov[0], mov[1]);
            valorActual = costo(actual, dist, flujo);

            logHelper.registrarIntercambio(mov[0], mov[1], (int) valorActual, mejorMovimiento != null);

            listaTabu.addLast(mov);
            if (listaTabu.size() > tenenciaTabu) listaTabu.removeFirst();

            for (int i = 0; i < n; i++) frecuencia[actual[i]][i]++;

            if (valorActual < mejorValor) { mejorValor = valorActual; mejor = actual.clone(); sinMejora = 0; }
            else sinMejora++;

            if (mejorMovimiento == null) { listaTabu.clear(); sinMejora = 0; }

            if (sinMejora > estancamiento * maxIter) {
                if (rand.nextDouble() < oscilacionEstrategica) intensificar(actual, frecuencia);
                else diversificar(actual, frecuencia);
                sinMejora = 0;
            }
        }
        return mejor;
    }

    boolean esMovimientoTabu(Deque<int[]> listaTabu, int i, int j) {
        for (int[] m : listaTabu)
            if ((m[0]==i&&m[1]==j)||(m[0]==j&&m[1]==i)) return true;
        return false;
    }

    void intensificar(int[] sol, int[][] freq) {
        int n = sol.length; double mejor = 0; int a=-1,b=-1;
        for(int i=0;i<n-1;i++) for(int j=i+1;j<n;j++) {
            double antes = freq[sol[i]][i]+freq[sol[j]][j];
            double despues = freq[sol[i]][j]+freq[sol[j]][i];
            if(despues-antes>mejor){mejor=despues-antes;a=i;b=j;}
        }
        if(a!=-1) intercambiar(sol,a,b);
    }

    void diversificar(int[] sol, int[][] freq) {
        int n = sol.length; double mejor = 0; int a=-1,b=-1;
        for(int i=0;i<n-1;i++) for(int j=i+1;j<n;j++) {
            double antes = freq[sol[i]][i]+freq[sol[j]][j];
            double despues = freq[sol[i]][j]+freq[sol[j]][i];
            if(antes-despues>mejor){mejor=antes-despues;a=i;b=j;}
        }
        if(a!=-1) intercambiar(sol,a,b);
    }

    double costo(int[] sol, int[][] dist, int[][] flujo) {
        double c=0; int n=sol.length;
        for(int i=0;i<n;i++) for(int j=0;j<n;j++) c += flujo[i][j] * dist[sol[i]][sol[j]];
        return c;
    }

    void intercambiar(int[] s, int i, int j) { int t=s[i]; s[i]=s[j]; s[j]=t; }

}