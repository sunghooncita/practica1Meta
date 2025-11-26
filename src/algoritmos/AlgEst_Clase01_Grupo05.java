package algoritmos;

import java.util.ArrayList;
import java.util.Random;
import meta1.utilities;

public class AlgEst_Clase01_Grupo05 {

    public static long[] AlgEst(int tamPobl, int tamCrom, int evaluacionesMax, int[][] flujos, int[][] localizaciones,
                                ArrayList<Integer> mejorSolIn, int tiempoMax, int kBest, int kWorst,
                                double kProbMuta, int porPoblAle, int tipoCruce, int k, int semilla) {

        Random rand = new Random(semilla);

        int generaciones = 0;
        ArrayList<int[]> poblacion = new ArrayList<>(); //almacena los cromosomas (soluciones).
        ArrayList<Long> costes = new ArrayList<>(); //almacena los costos asociados a cada cromosoma de la población

        // Inicializar población
        for (int i = 0; i < tamPobl; i++) {
            int[] crom;
            if (i < porPoblAle * tamPobl / 100) {
                crom = utilities.generarSolucionAleatoria(tamCrom, rand); //sol aleatoria
            } else {
                crom = AlgGA15.algoritmoGreedyAleatorio(flujos, localizaciones, k, rand); //sol greedy aleatorio
            }
            poblacion.add(crom);
            costes.add(utilities.calcularCosto(crom, flujos, localizaciones)); //calcula coste cromosoma añadido a la poblacion
        }

        int contadorE = tamPobl; //contador evaluaciones es igual al num de soluciones hasta ahora
        long inicio = 0;
        double tiempoTranscurrido = 0;

        while (contadorE < evaluacionesMax && tiempoTranscurrido < tiempoMax * 1000) {
            if (generaciones == 0) inicio = System.currentTimeMillis();
            generaciones++;

            // SELECCIÓN DE DOS PADRES POR TORNEO BINARIO
            int padre1 = torneoBinario(poblacion, costes, kBest, rand);
            int padre2;
            do {
                padre2 = torneoBinario(poblacion, costes, kBest, rand);
            } while (padre2 == padre1);

            int[] hijo1 = poblacion.get(padre1).clone(); //usa cromosoma para q sea la base del hijo
            int[] hijo2 = poblacion.get(padre2).clone(); //lo mismo pero con el hijo 2

            //CRUCE 100%
            if (tipoCruce == 0) {
                utilities.cruceOx2(hijo1, hijo2, rand);
            } else {
                utilities.cruceMOC(hijo1, hijo2, rand);
            }

            // MUTACIÓN
            mutar(hijo1, kProbMuta, tamCrom, rand);
            mutar(hijo2, kProbMuta, tamCrom, rand);

            // Calcular costes de los hijos
            long costeH1 = utilities.calcularCosto(hijo1, flujos, localizaciones);
            long costeH2 = utilities.calcularCosto(hijo2, flujos, localizaciones);
            contadorE += 2;

            // REEMPLAZAMIENTO POR TORNEO DE PERDEDORES
            reemplazarPeores(poblacion, costes, hijo1, hijo2, costeH1, costeH2, kWorst, rand);

            tiempoTranscurrido = System.currentTimeMillis() - inicio;
        }

        // Obtener mejor solución
        int mejorIdx = 0; //índice mejor sol
        long mejorCosto = costes.get(0); //mejor costo encontrado
        for (int i = 1; i < tamPobl; i++) {
            if (costes.get(i) < mejorCosto) { //coste actual mejor q mejor costo
                mejorCosto = costes.get(i); //actualiza costo
                mejorIdx = i; //actualiza indice mejor individuo
            }
        }

        mejorSolIn.clear();
        for (int val : poblacion.get(mejorIdx)) mejorSolIn.add(val); // copia el contenido del mejor cromosoma encontrado a la lista 'mejorSolIn'.

        System.out.println("Total Evaluaciones:" + contadorE);
        System.out.println("Total Generaciones:" + generaciones);

        return new long[]{mejorCosto, (long)generaciones};
    }

    // TORNEO BINARIO
    private static int torneoBinario(ArrayList<int[]> poblacion, ArrayList<Long> costes, int k, Random rand) {
        int mejor = rand.nextInt(poblacion.size()); //mejor indice inicial aleatorio
        for (int i = 1; i < k; i++) {
            int idx = rand.nextInt(poblacion.size()); //otro indice aleatorio
            if (costes.get(idx) < costes.get(mejor)) mejor = idx; //compara indices, si el nuevo es mejor se actualiza
        }
        return mejor;
    }

    // MUTACIÓN
    private static void mutar(int[] cromosoma, double probMuta, int tamCrom,  Random rand) {
        double x = rand.nextDouble() * 100; //num aleatorio entre 0.0 y 99.99...
        if (x < probMuta) {
            int pos1 = rand.nextInt(tamCrom); //elige pos aleatoria
            int pos2;
            do { pos2 = rand.nextInt(tamCrom); } while (pos2 == pos1); //otra pos asegurando q es != a la 1
            int temp = cromosoma[pos1];
            cromosoma[pos1] = cromosoma[pos2];
            cromosoma[pos2] = temp;
            //intercambia pos 1 y 2
        }
    }

    // REEMPLAZAR LOS DOS PEORES
    private static void reemplazarPeores(ArrayList<int[]> poblacion, ArrayList<Long> costes,
                                         int[] hijo1, int[] hijo2, long costeH1, long costeH2, int kWorst, Random rand) {

        int idxPeor1 = torneoPeor(costes, kWorst, rand); //Usa el Torneo de Perdedores para encontrar el índice del individuo con el PEOR costo entre 'kWorst' seleccionados.
        int idxPeor2;
        do { idxPeor2 = torneoPeor(costes, kWorst, rand); } while (idxPeor2 == idxPeor1); //lo mismo

        poblacion.set(idxPeor1, hijo1);
        costes.set(idxPeor1, costeH1);
        poblacion.set(idxPeor2, hijo2);
        costes.set(idxPeor2, costeH2);
    }

    // TORNEO DE PERDEDORES
    private static int torneoPeor(ArrayList<Long> costes, int k, Random rand) {
        int peor = rand.nextInt(costes.size()); //indice al azar
        for (int i = 1; i < k; i++) {
            int idx = rand.nextInt(costes.size()); //indice aleatorio
            if (costes.get(idx) > costes.get(peor)) peor = idx; //si costo nurvo indice es mayor, actualiza peor
        }
        return peor;
    }
}
