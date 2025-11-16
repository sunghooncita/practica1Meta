package algoritmos;
import java.util.ArrayList;
import java.util.Random;

import meta1.utilities;

public class AlgGen15 {

    private static final Random RAND = new Random();

    public static double AlgGen(int tamPobl, int tamCrom, int evaluacionesMax,
                                int[][] flujos, int[][] localizaciones,
                                ArrayList<Integer> mejorSolIn,
                                int tiempoMax, int kbest, int kworst,
                                double kProbMuta, double kProbCruce,
                                int elitismo,
                                int porPoblAle, int tipoCruce, int k) {

        int iteraciones = 0;


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
                cromosomas.set(i,utilities.generarSolucionAleatoria(tamCrom, RAND));
            } else {
                cromosomas.set(i,AlgGA15.algoritmoGreedyAleatorio(flujos, localizaciones, k, RAND));
            }
            costes.set(i, utilities.calcularCosto(cromosomas.get(i), flujos, localizaciones));
        }

        int contadorE = tamPobl; //contador de las evaluaciones realizadas

        long inicio = 0;                // instante en el que empieza el bucle
        double tiempoTranscurrido = 0;

        // BUCLE PRINCIPAL
        while (contadorE < evaluacionesMax && tiempoTranscurrido  < tiempoMax * 1000) {
            if (iteraciones == 0) {
                inicio = System.currentTimeMillis();
            }
            iteraciones++;


            // ELITISMO
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

            // SELECCIÓN POR TORNEO k-best
            for (int kk = 0; kk < tamPobl; kk++) {
                ArrayList<Integer> elegidos = new ArrayList<>();
                for (int i = 0; i < kbest; i++) elegidos.add(-1);
                //elegimos kbest individuos sin repetir
                for (int i = 0; i < kbest; i++) {
                    boolean enc;
                    int valor;
                    do {
                        valor = RAND.nextInt(0, tamPobl - 1);
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

            // PADRES SELECCIONADOS
            for (int i = 0; i < tamPobl; i++) {
                int p = posi.get(i);
                nuevaGen.set(i, cromosomas.get(p).clone());
                costesGen.set(i, costes.get(p));
            }

            // CRUCE
            boolean[] marcados = new boolean[tamPobl];
            nuevaGenDCruce.clear();
            costesGenDC.clear();
            for (int i = 0; i < tamPobl; i++) costesGenDC.add(0);

            for (int i = 0; i < tamPobl / 2; i++) {
                //elegimos padres diferentes
                int c1 = RAND.nextInt(0, tamPobl - 1);
                int c2;
                do {c2 = RAND.nextInt(0, tamPobl - 1);
                } while (c1 == c2);

                int x = RAND.nextInt(0, 100);
                //si se cruzan
                if (x < kProbCruce) {
                    int[] h1 = nuevaGen.get(c1).clone();
                    int[] h2 = nuevaGen.get(c2).clone();

                    if (tipoCruce == 0)
                        utilities.cruceOx2(h1, h2);
                    else
                        utilities.cruceMOC(h1, h2);

                    nuevaGenDCruce.add(h1);
                    marcados[nuevaGenDCruce.size() - 1] = true;

                    nuevaGenDCruce.add(h2);
                    marcados[nuevaGenDCruce.size() - 1] = true;

                } else { //si no se cruzan pasan tal cual
                    nuevaGenDCruce.add(nuevaGen.get(c1).clone());
                    costesGenDC.set(nuevaGenDCruce.size() - 1, costesGen.get(c1));

                    nuevaGenDCruce.add(nuevaGen.get(c2).clone());
                    costesGenDC.set(nuevaGenDCruce.size() - 1, costesGen.get(c2));
                }
            }

            //actualizamos la gen
            nuevaGen.clear();
            nuevaGen.addAll(nuevaGenDCruce);
            costesGen = new ArrayList<>(costesGenDC);

            // MUTACIÓN
            for (int i = 0; i < tamPobl; i++) {
                double x = RAND.nextFloat(0, 100);

                if (x < kProbMuta) {
                    int pos1 = RAND.nextInt(0, tamCrom - 1);
                    int pos2;
                    do {
                        pos2 = RAND.nextInt(0, tamCrom - 1);
                    } while (pos1 == pos2);
                    // Mutamos intercambiando dos posiciones
                    int temp = nuevaGen.get(i)[pos1];
                    nuevaGen.get(i)[pos1] = nuevaGen.get(i)[pos2];
                    nuevaGen.get(i)[pos2] = temp;
                    marcados[i] = true;
                }
            }

            // ACTUALIZACIÓN DE COSTES
            for (int i = 0; i < tamPobl; i++) {
                if (marcados[i]) {
                    costesGen.set(i, utilities.calcularCosto(nuevaGen.get(i), flujos, localizaciones));
                    contadorE++;
                }
            }

            // MANTENER ELITISMO
            boolean[] encElite = new boolean[elitismo];
            for (int i = 0; i < nuevaGen.size(); i++) {
                for (int j = 0; j < elitismo; j++) {
                    if (arrayEquals(mejorCromosoma.get(j), nuevaGen.get(i))) {
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
                            valor = RAND.nextInt(0, tamPobl - 1);
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

            costes = new ArrayList<>(costesGen);
            cromosomas.clear();
            cromosomas.addAll(nuevaGen);

            tiempoTranscurrido = System.currentTimeMillis() - inicio;

        }

        int[] best = mejorCromosoma.get(0);
        mejorSolIn.clear();
        for (int v : best) mejorSolIn.add(v);

        if (repetidos(best)) System.out.println("Repetidos");

        System.out.println("Total Evaluaciones:" + contadorE);
        System.out.println(" Total Iteraciones:" + iteraciones);

        return mejorCoste.get(0);
    }


    //función para comparar arrays
    private static boolean arrayEquals(int[] a, int[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    private static boolean repetidos(int[] genoma) {
        int N = genoma.length;
        boolean[] visto = new boolean[N];

        for (int valor : genoma) {
            if (valor < 0 || valor >= N) {
                return true;
            }
            if (visto[valor]) {
                return true; // ¡Duplicado encontrado!
            }
            visto[valor] = true;
        }
        return false;
    }
}