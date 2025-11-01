package algoritmos;


import algoritmos.modelos.Individuo;
import algoritmos.modelos.Poblacion;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Clase que implementa el Algoritmo Genético AlgGen15.
 * Las lógicas de Generación y Operadores se han integrado para minimizar clases.
 */
public class AlgGen15 {

    // --- Parámetros de Configuración (COMO ANTES) ---
    private static final int TAMANO_POBLACION = 100;
    private static final double PROPORCION_ALEATORIA = 0.80;
    private static final int TCL_GREEDY = 5;

    private static final int TAMANO_ELITE = 2; // E=1 o E=2
    private static final int K_BEST_SELECCION = 3; // kBest=2 o kBest=3
    private static final int K_WORST_REEMPLAZO = 3; // kWorst=3

    private static final double PROBABILIDAD_CRUCE = 0.70;
    private static final double PROBABILIDAD_MUTACION = 0.10;

    private static final int MAX_EVALUACIONES = 50000;
    private static final long MAX_TIEMPO_MS = 60 * 1000;

    private int evaluacionesActuales = 0;
    private long tiempoInicio = 0;
    private final Random rand = new Random();

    // Asumimos el tamaño de cromosoma para los operadores
    private static final int TAMANO_CROMOSOMA = 50;

    // I. MÉTODO PRINCIPAL DE EVOLUCIÓN (Sin cambios respecto al plan)

    public Individuo ejecutar() {
        tiempoInicio = System.currentTimeMillis();

        Poblacion poblacionActual = inicializarPoblacion();
        evaluacionesActuales += TAMANO_POBLACION;

        Individuo mejorGlobal = poblacionActual.getMejorIndividuo();
        int generacion = 0;

        while (!criterioDeParadaAlcanzado()) {
            generacion++;

            // A. FASE DE ELITISMO
            Individuo[] elite = obtenerMejores(poblacionActual, TAMANO_ELITE);

            // B. FASE DE SELECCIÓN (Torneo Binario)
            Poblacion padres = seleccionarPadres(poblacionActual, K_BEST_SELECCION);

            // C. FASE DE CRUCE y MUTACIÓN
            Poblacion descendencia = generarDescendencia(padres, PROBABILIDAD_CRUCE, PROBABILIDAD_MUTACION);

            // D. FASE DE EVALUACIÓN
            descendencia.evaluar();
            evaluacionesActuales += TAMANO_POBLACION;

            // E. FASE DE REEMPLAZAMIENTO (Población completa + Elitismo de Torneo)
            poblacionActual = reemplazarPoblacion(descendencia, elite, K_WORST_REEMPLAZO);

            // Actualizar mejor global
            Individuo mejorActual = poblacionActual.getMejorIndividuo();
            if (mejorActual.getFitness() < mejorGlobal.getFitness()) {
                mejorGlobal = mejorActual.clonar();
            }

            // Seguimiento
            System.out.println("Gen " + generacion + ": Mejor Fitness = " + mejorGlobal.getFitness() +
                    ", Evals = " + evaluacionesActuales);
        }

        System.out.println(" Algoritmo Genético Finalizado.");
        return mejorGlobal;
    }

    // 2. LÓGICA DE INICIALIZACIÓN (GeneradorIndividuos integrado)

    private Poblacion inicializarPoblacion() {
        Poblacion poblacion = new Poblacion(TAMANO_POBLACION);

        int numAleatorios = (int) Math.round(TAMANO_POBLACION * PROPORCION_ALEATORIA);
        int numGreedy = TAMANO_POBLACION - numAleatorios;

        System.out.println("🚀 Inicializando: " + numAleatorios + " Aleatorios, " + numGreedy + " Greedy (TCL=" + TCL_GREEDY + ")");

        // 80% Aleatorio
        for (int i = 0; i < numAleatorios; i++) {
            Individuo ind = generarAleatorio();
            poblacion.agregarIndividuo(ind);
        }

        // 20% Greedy Aleatorizado (TCL=5)
        for (int i = 0; i < numGreedy; i++) {
            Individuo ind = generarGreedyAleatorizado(TCL_GREEDY);
            poblacion.agregarIndividuo(ind);
        }

        // La evaluación debe ser llamada por el constructor o por el método 'evaluar' de Población
        // Lo incluimos aquí por si los métodos de generación no evalúan
        poblacion.evaluar();
        return poblacion;
    }

    private Individuo generarAleatorio() {
        // Implementación simple aleatoria para el esqueleto
        int[] cromosoma = new int[TAMANO_CROMOSOMA];
        for (int i = 0; i < TAMANO_CROMOSOMA; i++) {
            cromosoma[i] = rand.nextInt(100); // Genera un número aleatorio
        }
        return new Individuo(cromosoma);
    }

    private Individuo generarGreedyAleatorizado(int tcl) {
        // *** Implementación de tu Práctica 1 para el Greedy Aleatorizado (GRASP/Metaheurística) ***
        // Este método debe usar la lista de candidatos de tamaño 'tcl' (5)
        // Simulación: Genera un individuo y lo evalúa para simular la construcción de una buena solución
        Individuo ind = generarAleatorio(); // Usa el método real de tu práctica 1 aquí
        ind.evaluar(); // Para simular que es una solución "mejor"
        return ind;
    }

    // 3. LÓGICA DE OPERADORES (OperadoresGA integrado)

    // --- SELECCIÓN ---
    private Poblacion seleccionarPadres(Poblacion poblacion, int kBest) {
        Poblacion padres = new Poblacion(TAMANO_POBLACION);
        for (int i = 0; i < TAMANO_POBLACION; i++) {
            Individuo padre = torneo(poblacion, kBest);
            padres.agregarIndividuo(padre);
        }
        return padres;
    }

    private Individuo torneo(Poblacion poblacion, int k) {
        List<Individuo> competidores = new java.util.ArrayList<>();
        int tamanoPoblacion = poblacion.getTamano();

        // Selecciona k individuos al azar
        for (int i = 0; i < k; i++) {
            int index = rand.nextInt(tamanoPoblacion);
            competidores.add(poblacion.getIndividuo(index));
        }

        // Devuelve el mejor (menor fitness)
        return Collections.min(competidores, Comparator.comparingDouble(Individuo::getFitness));
    }

    // --- CRUCE Y MUTACIÓN ---
    private Poblacion generarDescendencia(Poblacion padres, double pCruce, double pMutacion) {
        Poblacion descendencia = new Poblacion(TAMANO_POBLACION);

        for (int i = 0; i < padres.getTamano(); i += 2) {
            Individuo padre1 = padres.getIndividuo(i);
            Individuo padre2 = padres.getIndividuo(i + 1);

            Individuo[] hijos;
            if (rand.nextDouble() < pCruce) {
                if ((i / 2) % 2 == 0) {
                    hijos = cruceOX2(padre1, padre2);
                } else {
                    hijos = cruceMOC(padre1, padre2);
                }
            } else {
                hijos = new Individuo[] {padre1.clonar(), padre2.clonar()};
            }

            for (Individuo hijo : hijos) {
                if (rand.nextDouble() < pMutacion) {
                    mutacion2Opt(hijo);
                }
                descendencia.agregarIndividuo(hijo);
            }
        }
        return descendencia;
    }

    // *Implementaciones de Cruce y Mutación (solo esquemas)*

    private Individuo[] cruceOX2(Individuo p1, Individuo p2) {
        // *** Lógica de Cruce de Orden OX2 (Práctica 1) ***
        return new Individuo[] {p1.clonar(), p2.clonar()}; // Retorna clones por simplicidad de esquema
    }

    private Individuo[] cruceMOC(Individuo p1, Individuo p2) {
        // *** Lógica de Cruce MOC ***
        return new Individuo[] {p1.clonar(), p2.clonar()}; // Retorna clones por simplicidad de esquema
    }

    private void mutacion2Opt(Individuo ind) {
        // *** Lógica de Mutación 2-opt (Práctica 1) ***
        int i1 = rand.nextInt(TAMANO_CROMOSOMA);
        int i2 = rand.nextInt(TAMANO_CROMOSOMA);
        while (i1 == i2) i2 = rand.nextInt(TAMANO_CROMOSOMA);

        int[] c = ind.getCromosoma();
        int temp = c[i1];
        c[i1] = c[i2];
        c[i2] = temp;
    }

    // --- ELITISMO Y REEMPLAZO ---
    private Individuo[] obtenerMejores(Poblacion poblacion, int E) {
        // Devuelve los E mejores individuos clonados
        List<Individuo> copia = new java.util.ArrayList<>(poblacion.getTamano());
        for (int i = 0; i < poblacion.getTamano(); i++) {
            copia.add(poblacion.getIndividuo(i).clonar());
        }
        copia.sort(Comparator.comparingDouble(Individuo::getFitness));
        return copia.subList(0, Math.min(E, copia.size())).toArray(new Individuo[0]);
    }

    private Individuo torneoPerdedores(Poblacion poblacion, int k) {
        // Selecciona k individuos y devuelve el peor (mayor fitness)
        List<Individuo> competidores = new java.util.ArrayList<>();
        int tamanoPoblacion = poblacion.getTamano();

        for (int i = 0; i < k; i++) {
            int index = rand.nextInt(tamanoPoblacion);
            competidores.add(poblacion.getIndividuo(index));
        }

        // Devuelve el peor (máximo fitness, asumiendo minimización)
        return Collections.max(competidores, Comparator.comparingDouble(Individuo::getFitness));
    }

    private Poblacion reemplazarPoblacion(Poblacion descendencia, Individuo[] elite, int kWorst) {
        Poblacion nuevaPoblacion = descendencia;

        for (Individuo e : elite) {
            if (!nuevaPoblacion.contiene(e)) {
                // Elitismo de Torneo: El élite reemplaza al peor de un torneo de perdedores
                Individuo peorDelTorneo = torneoPerdedores(nuevaPoblacion, kWorst);
                nuevaPoblacion.reemplazar(peorDelTorneo, e);
            }
        }

        return nuevaPoblacion;
    }

    // --- CONDICIÓN DE PARADA ---
    private boolean criterioDeParadaAlcanzado() {
        boolean maxEval = evaluacionesActuales >= MAX_EVALUACIONES;
        boolean maxTiempo = (System.currentTimeMillis() - tiempoInicio) >= MAX_TIEMPO_MS;

        return maxEval || maxTiempo;
    }

}