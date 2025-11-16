package meta1;

import java.util.*;


public class utilities {

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

    public static int[] generarSolucionAleatoria(int n, Random rng) {

        // 1. Crear una lista de enteros con los valores de 0 a n-1 (Equivalente a list(range(n)))
        List<Integer> solucionList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            solucionList.add(i);
        }

        // 2. Mezclar (shuffle) la lista utilizando el generador 'rng' (Equivalente a rng.shuffle(solucion))
        // Collections.shuffle permuta los elementos de la lista aleatoriamente.
        Collections.shuffle(solucionList, rng);

        // 3. Convertir la lista permutada a un array int[]
        int[] solucionArray = new int[n];
        for (int i = 0; i < n; i++) {
            solucionArray[i] = solucionList.get(i);
        }

        return solucionArray;
    }

    public static int posMenorCoste(List<Integer> elegidos, List<Integer> costes) {

        int indicePoblacionMenor = elegidos.get(0);
        int menorCoste = costes.get(indicePoblacionMenor);

        for (int i = 1; i < elegidos.size(); i++) {
            int indicePoblacionActual = elegidos.get(i);
            int costeActual = costes.get(indicePoblacionActual);
            if (costeActual < menorCoste) {
                // Si encontramos un coste menor, actualizamos.
                menorCoste = costeActual;
                indicePoblacionMenor = indicePoblacionActual;
            }
        }
        return indicePoblacionMenor;
    }

    public static void cruceOx2(int[] h1, int[] h2) {
        int size = h1.length;
        int numGenes = RAND.nextInt(size - 1) + 1; // número de genes a tomar de h2

        Set<Integer> posiciones = new HashSet<>();
        while (posiciones.size() < numGenes) {
            posiciones.add(RAND.nextInt(size));
        }

        int[] tempH1 = h1.clone();
        int[] tempH2 = h2.clone();

        // Copiar genes seleccionados de h2 en h1
        for (int pos : posiciones) {
            h1[pos] = tempH2[pos];
        }

        // Completar h1 con los genes de tempH1 respetando el orden
        int idx = 0;
        for (int i = 0; i < size; i++) {
            int gene = tempH1[i];
            if (!contiene(h1, gene)) {
                while (h1[idx] != tempH2[idx] && posiciones.contains(idx)) {
                    idx++;
                }
                h1[idx] = gene;
                idx++;
            }
        }

        // Hacer lo mismo para h2 con genes de tempH2
        idx = 0;
        for (int i = 0; i < size; i++) {
            int gene = tempH2[i];
            if (!contiene(h2, gene)) {
                while (h2[idx] != tempH1[idx] && posiciones.contains(idx)) {
                    idx++;
                }
                h2[idx] = gene;
                idx++;
            }
        }
    }


    // Usaremos un objeto Random accesible, o lo crearemos localmente
    private static final Random RAND = new Random();

    public static void cruceMOC(int[] h1, int[] h2) {
        int size = h1.length;
        int start = RAND.nextInt(size);
        int end = RAND.nextInt(size - start) + start;

        // Clonar el segmento de h1 para h2 y viceversa
        int[] tempH1 = h1.clone();
        int[] tempH2 = h2.clone();

        // Copiar segmento de h1 en h2
        for (int i = start; i <= end; i++) {
            h2[i] = tempH1[i];
        }

        // Completar h2 con los genes de h1 en orden evitando duplicados
        int idx = (end + 1) % size;
        for (int i = 0; i < size; i++) {
            int gene = tempH1[(end + 1 + i) % size];
            if (!contiene(h2, gene)) {
                h2[idx] = gene;
                idx = (idx + 1) % size;
            }
        }

        // Hacer lo mismo para h1 con los genes de h2
        idx = (end + 1) % size;
        for (int i = 0; i < size; i++) {
            int gene = tempH2[(end + 1 + i) % size];
            if (!contiene(h1, gene)) {
                h1[idx] = gene;
                idx = (idx + 1) % size;
            }
        }
    }

    private static boolean contiene(int[] arr, int value) {
        for (int v : arr) {
            if (v == value) return true;
        }
        return false;
    }

}
