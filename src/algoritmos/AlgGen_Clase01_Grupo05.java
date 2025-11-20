package algoritmos;
import java.util.ArrayList;
import java.util.Random;

import meta1.utilities;

public class AlgGen_Clase01_Grupo05 {

    public static double AlgGen(int tamPobl, int tamCrom, int evaluacionesMax, int[][] flujos, int[][] localizaciones, ArrayList<Integer> mejorSolIn, int tiempoMax, int kbest, int kworst, double kProbMuta, double kProbCruce, int elitismo, int porPoblAle, int tipoCruce, int k,int semilla) {

        Random rand = new Random(semilla);

        int generaciones = 0;

        ArrayList<int[]> cromosomas = new ArrayList<>(); //poblacion actual
        ArrayList<int[]> nuevaGen = new ArrayList<>();
        ArrayList<int[]> nuevaGenDCruce = new ArrayList<>();

        ArrayList<Integer> costes = new ArrayList<>(); //costes de la poblacion
        ArrayList<Integer> costesGen = new ArrayList<>();
        ArrayList<Integer> costesGenDC = new ArrayList<>();

        ArrayList<int[]> mejorCromosoma = new ArrayList<>();
        ArrayList<Integer> mejorCoste = new ArrayList<>();

        ArrayList<Integer> posi = new ArrayList<>(); //posiciones seleccionadas por torneo

        //reservamos memoria - vectores
        for (int i = 0; i < tamPobl; i++) {
            cromosomas.add(new int[tamCrom]);
            nuevaGen.add(new int[tamCrom]);
            costes.add(0);
            costesGen.add(0);
            costesGenDC.add(0);
            posi.add(0);
        }

        //reservamos memoria - elitismo+1
        for (int i = 0; i < elitismo + 1; i++) {
            mejorCromosoma.add(new int[tamCrom]);
            mejorCoste.add(0);
        }

        // CARGA INICIAL
        for (int i = 0; i < tamPobl; i++) {

            if (i < porPoblAle * tamPobl / 100) {
                cromosomas.set(i,utilities.generarSolucionAleatoria(tamCrom, rand));
            } else {
                cromosomas.set(i,AlgGA15.algoritmoGreedyAleatorio(flujos, localizaciones, k, rand));
            }
            costes.set(i, utilities.calcularCosto(cromosomas.get(i), flujos, localizaciones));
        }
        int contadorE = tamPobl; //contador de las evaluaciones realizadas
        long inicio = 0;// instante en el que empieza el bucle
        double tiempoTranscurrido = 0;

        // BUCLE PRINCIPAL
        while (contadorE < evaluacionesMax && tiempoTranscurrido  < tiempoMax * 1000) {
            if (generaciones == 0) {
                inicio = System.currentTimeMillis();
            }
            generaciones++;

            // ELITISMO
            actualizarElitismo(tamPobl, elitismo, cromosomas, costes, mejorCromosoma, mejorCoste);

            // SELECCIÓN POR TORNEO k-best
            ArrayList<Integer> posicionesSeleccionadas = seleccionTorneoKBest(tamPobl, kbest, costes, rand);
            posi = posicionesSeleccionadas;

            // PADRES SELECCIONADOS
            copiarPadres(tamPobl, cromosomas, costes, posi, nuevaGen, costesGen);

            // CRUCE
            boolean[] marcados = realizarCruce(tamPobl, tamCrom, kProbCruce, tipoCruce, nuevaGen, costesGen, nuevaGenDCruce, costesGenDC, rand);
            nuevaGen.clear();
            nuevaGen.addAll(nuevaGenDCruce);
            costesGen = new ArrayList<>(costesGenDC);

            // MUTACIÓN
            mutarPoblacion(tamPobl, tamCrom, kProbMuta, nuevaGen, marcados, rand);

            // ACTUALIZACIÓN DE COSTES
            contadorE = actualizarCostes(tamPobl, flujos, localizaciones, nuevaGen, costesGen, contadorE, marcados);

            // MANTENER ELITISMO (reemplazo del peor por el élite si es necesario)
            reemplazoConTorneoDePerdedores(elitismo, kworst, mejorCromosoma, mejorCoste, nuevaGen, costesGen, rand);

            // Preparar para la siguiente iteración
            costes = new ArrayList<>(costesGen);
            cromosomas.clear();
            cromosomas.addAll(nuevaGen);

            tiempoTranscurrido = System.currentTimeMillis() - inicio;

        }

        int[] best = mejorCromosoma.get(0);
        mejorSolIn.clear();
        for (int v : best) mejorSolIn.add(v);

        System.out.println("Total Evaluaciones:" + contadorE);
        System.out.println("Total Generaciones:" + generaciones);

        return mejorCoste.get(0);
    }


    // METODOS

    /**
     * Guarda los mejores individuos de la población actual.
     */
    private static void actualizarElitismo(int tamPobl, int elitismo,
                                           ArrayList<int[]> cromosomas, ArrayList<Integer> costes,
                                           ArrayList<int[]> mejorCromosoma, ArrayList<Integer> mejorCoste) {
        mejorCoste.clear();
        for (int i = 0; i < elitismo + 1; i++) {
            mejorCoste.add(Integer.MAX_VALUE);
        }

        // Guardamos los mejores individuos de la población
        for (int i = 0; i < tamPobl; i++) {
            int c = costes.get(i);

            if (c < mejorCoste.get(0)) {
                mejorCoste.set(1, mejorCoste.get(0));
                mejorCromosoma.set(1, mejorCromosoma.get(0).clone());
                mejorCoste.set(0, c);
                mejorCromosoma.set(0, cromosomas.get(i).clone());

            } else if (elitismo == 2 && c < mejorCoste.get(1)) {
                mejorCoste.set(1, c);
                mejorCromosoma.set(1, cromosomas.get(i).clone());
            }
        }
    }


    /**
     * Realiza la selección por torneo k-best para toda la población.
     */
    private static ArrayList<Integer> seleccionTorneoKBest(int tamPobl, int kbest, ArrayList<Integer> costes, Random rand) {
        ArrayList<Integer> posi = new ArrayList<>();
        for (int kk = 0; kk < tamPobl; kk++) posi.add(0);

        for (int kk = 0; kk < tamPobl; kk++) {
            ArrayList<Integer> elegidos = new ArrayList<>();
            for (int i = 0; i < kbest; i++) elegidos.add(-1);
            //elegimos kbest individuos sin repetir
            for (int i = 0; i < kbest; i++) {
                boolean enc;
                int valor;
                do {
                    valor = rand.nextInt(0, tamPobl - 1);
                    enc = false;
                    //evitamos duplicados
                    for (int j = 0; j < i; j++) {
                        if (valor == elegidos.get(j)) {
                            enc = true;
                            break;
                        }
                    }
                } while (enc);
                elegidos.set(i, valor);
            }
            //posición del mejor de los elegidos
            posi.set(kk, utilities.posMenorCoste(elegidos, costes));
        }
        return posi;
    }


    /**
     * Copia los padres seleccionados a la nueva generación (antes del cruce).
     */
    private static void copiarPadres(int tamPobl, ArrayList<int[]> cromosomas, ArrayList<Integer> costes,
                                     ArrayList<Integer> posi, ArrayList<int[]> nuevaGen, ArrayList<Integer> costesGen) {
        for (int i = 0; i < tamPobl; i++) {
            int p = posi.get(i);
            nuevaGen.set(i, cromosomas.get(p).clone());
            costesGen.set(i, costes.get(p));
        }
    }


    /**
     * Realiza el cruce con la probabilidad kProbCruce y los tipos OX2 o MOC.
     */
    private static boolean[] realizarCruce(int tamPobl, int tamCrom, double kProbCruce, int tipoCruce,
                                           ArrayList<int[]> nuevaGen, ArrayList<Integer> costesGen,
                                           ArrayList<int[]> nuevaGenDCruceOut, ArrayList<Integer> costesGenDCOut, Random rand) {
        boolean[] marcados = new boolean[tamPobl]; // Inicializado aquí para mantener la lógica original

        nuevaGenDCruceOut.clear();
        costesGenDCOut.clear();
        for (int i = 0; i < tamPobl; i++) costesGenDCOut.add(0); // Inicialización de costesGenDC

        for (int i = 0; i < tamPobl / 2; i++) {
            //elegimos padres diferentes
            int c1 = rand.nextInt(0, tamPobl - 1);
            int c2;
            do {c2 = rand.nextInt(0, tamPobl - 1);
            } while (c1 == c2);

            int x = rand.nextInt(0, 100);
            //si se cruzan
            if (x < kProbCruce) {
                int[] h1 = nuevaGen.get(c1).clone();
                int[] h2 = nuevaGen.get(c2).clone();

                if (tipoCruce == 0)
                    utilities.cruceOx2(h1, h2);
                else
                    utilities.cruceMOC(h1, h2);

                nuevaGenDCruceOut.add(h1);
                marcados[nuevaGenDCruceOut.size() - 1] = true;

                nuevaGenDCruceOut.add(h2);
                marcados[nuevaGenDCruceOut.size() - 1] = true;

            } else { //si no se cruzan pasan tal cual
                nuevaGenDCruceOut.add(nuevaGen.get(c1).clone());
                costesGenDCOut.set(nuevaGenDCruceOut.size() - 1, costesGen.get(c1));

                nuevaGenDCruceOut.add(nuevaGen.get(c2).clone());
                costesGenDCOut.set(nuevaGenDCruceOut.size() - 1, costesGen.get(c2));
            }
        }
        return marcados;
    }


    /**
     * Aplica la mutación (intercambio 2-opt) a cada individuo con probabilidad kProbMuta.
     */
    private static void mutarPoblacion(int tamPobl, int tamCrom, double kProbMuta,
                                       ArrayList<int[]> nuevaGen, boolean[] marcados, Random rand) {
        for (int i = 0; i < tamPobl; i++) {
            double x = rand.nextFloat(0, 100);

            if (x < kProbMuta) {
                int pos1 = rand.nextInt(0, tamCrom - 1);
                int pos2;
                do {
                    pos2 = rand.nextInt(0, tamCrom - 1);
                } while (pos1 == pos2);
                // Mutamos intercambiando dos posiciones
                int temp = nuevaGen.get(i)[pos1];
                nuevaGen.get(i)[pos1] = nuevaGen.get(i)[pos2];
                nuevaGen.get(i)[pos2] = temp;
                marcados[i] = true;
            }
        }
    }


    /**
     * Calcula los costes de los individuos marcados (que fueron modificados).
     */
    private static int actualizarCostes(int tamPobl, int[][] flujos, int[][] localizaciones,
                                        ArrayList<int[]> nuevaGen, ArrayList<Integer> costesGen,
                                        int contadorE, boolean[] marcados) {
        // ACTUALIZACIÓN DE COSTES
        for (int i = 0; i < tamPobl; i++) {
            if (marcados[i]) {
                costesGen.set(i, utilities.calcularCosto(nuevaGen.get(i), flujos, localizaciones));
                contadorE++;
            }
        }
        return contadorE;
    }


    /**
     * Implementa la recuperación de los individuos élite que no sobrevivieron.
     */
    private static void reemplazoConTorneoDePerdedores(int elitismo, int kworst,
                                         ArrayList<int[]> mejorCromosoma, ArrayList<Integer> mejorCoste,
                                         ArrayList<int[]> nuevaGen, ArrayList<Integer> costesGen, Random rand) {
        boolean[] encElite = new boolean[elitismo];
        for (int i = 0; i < nuevaGen.size(); i++) {
            for (int j = 0; j < elitismo; j++) {
                if (arraysiguales(mejorCromosoma.get(j), nuevaGen.get(i))) {
                    encElite[j] = true;
                }
            }
        }

        //si un elite no esta, lo recuperamos sustituyendolo x un peor
        ArrayList<Integer> peores = new ArrayList<>();

        for (int i = 0; i < elitismo; i++) {
            if (!encElite[i]) {

                boolean enc2;
                ArrayList<Integer> elegidos = new ArrayList<>();
                for (int x = 0; x < kworst; x++) elegidos.add(-1);

                //elegimos kworst pos distintas
                for (int kk = 0; kk < kworst; kk++) {
                    int valor;
                    boolean enc;

                    do {
                        valor = rand.nextInt(0, nuevaGen.size() - 1);
                        enc = false;
                        for (int j = 0; j < kk; j++) { //evitamos repetidos
                            if (valor == elegidos.get(j)) {
                                enc = true;
                                break;
                            }
                        }
                        //evitamos pos ya sustituidas
                        enc2 = false;
                        for (int p : peores) {
                            if (valor == p) {
                                enc2 = true;
                                break;
                            }
                        }

                    } while (enc || enc2);

                    elegidos.set(kk, valor);
                }

                //seleccionamos el peor para sustituirlo
                int peor = utilities.posMayorCoste(elegidos, costesGen);
                peores.add(peor);
                //Recuperamos el elite
                nuevaGen.set(peor, mejorCromosoma.get(i).clone());
                costesGen.set(peor, mejorCoste.get(i));
            }
        }
    }

    /**
     * Función para comparar arrays
     */
    private static boolean arraysiguales(int[] a, int[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

}