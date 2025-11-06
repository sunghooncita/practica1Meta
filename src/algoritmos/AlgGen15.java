package algoritmos;
import algoritmos.modelos.Individuo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AlgGen15 {

    private static final Random RANDOM = new Random();

    // 1. SELECCIÓN (Torneo Binario kBest)
    public static List<Individuo> seleccion(List<Individuo> poblacion, int kBest) {
        List<Individuo> descendientes = new ArrayList<>();
        for (int i = 0; i < poblacion.size(); i++) {
            descendientes.add(torneoBinarioKBest(poblacion, kBest));
        }
        return descendientes;
    }

    // 2. CRUCE
    public static List<Individuo> cruce(List<Individuo> descendientes, double Pc) {
        List<Individuo> hijos = new ArrayList<>();
        Collections.shuffle(descendientes);
        for (int i = 0; i < descendientes.size() - 1; i += 2) {
            Individuo p1 = descendientes.get(i);
            Individuo p2 = descendientes.get(i + 1);
            if (RANDOM.nextDouble() < Pc) {
                hijos.add(OX2(p1, p2));
                hijos.add(OX2(p2, p1));
            } else {
                hijos.add(p1);
                hijos.add(p2);
            }
        }
        return hijos;
    }

    // 3. MUTACIÓN
    public static List<Individuo> mutacion(List<Individuo> poblacion, double Pm) {
        for (int i = 0; i < poblacion.size(); i++) {
            if (RANDOM.nextDouble() < Pm) {
                poblacion.set(i, intercambio2Opt(poblacion.get(i)));
            }
        }
        return poblacion;
    }

    // 4. REEMPLAZAMIENTO
    public static List<Individuo> reemplazamiento(List<Individuo> nuevaPob, Individuo mejorAnterior, int kWorst) {
        // Copia para asegurar que el listado pueda ser modificado
        List<Individuo> poblacionFinal = new ArrayList<>(nuevaPob);

        // Si el élite no sobrevive, reemplaza al peor de un torneo de perdedores
        if (!poblacionFinal.contains(mejorAnterior)) {
            Individuo peorTorneo = torneoPerdedores(poblacionFinal, kWorst);
            poblacionFinal.remove(peorTorneo);
            poblacionFinal.add(mejorAnterior);
        }
        return poblacionFinal;
    }

    public static Individuo torneoBinarioKBest(List<Individuo> poblacion, int kBest) {
        // ... Lógica para encontrar el MEJOR (menor fitness) de kBest competidores ...
        Individuo mejor = poblacion.get(RANDOM.nextInt(poblacion.size())); // Placeholder
        for (int i = 1; i < kBest; i++) {
            Individuo competidor = poblacion.get(RANDOM.nextInt(poblacion.size()));
            if (competidor.getFitness() < mejor.getFitness()) {
                mejor = competidor;
            }
        }
        return mejor;
    }

    public static Individuo torneoPerdedores(List<Individuo> poblacion, int kWorst) {
        // ... Lógica para encontrar el PEOR (mayor fitness) de kWorst competidores ...
        Individuo peor = poblacion.get(RANDOM.nextInt(poblacion.size())); // Placeholder
        for (int i = 1; i < kWorst; i++) {
            Individuo competidor = poblacion.get(RANDOM.nextInt(poblacion.size()));
            if (competidor.getFitness() > peor.getFitness()) {
                peor = competidor;
            }
        }
        return peor;
    }

    public static Individuo intercambio2Opt(Individuo p) {
        int[] genoma = Arrays.copyOf(p.getGenoma(), p.getGenoma().length);
        int n = genoma.length;
        int pos1 = RANDOM.nextInt(n);
        int pos2 = RANDOM.nextInt(n);
        while (pos1 == pos2) {
            pos2 = RANDOM.nextInt(n);
        }
        // Intercambio
        int temp = genoma[pos1];
        genoma[pos1] = genoma[pos2];
        genoma[pos2] = temp;

        return new Individuo(genoma);
    }

    public static Individuo OX2(Individuo p1, Individuo p2) {
        int n = p1.getGenoma().length;
        int[] genomaHijo = new int[n];
        Arrays.fill(genomaHijo, -1); // Marcar huecos
        int punto1 = RANDOM.nextInt(n);
        int punto2 = RANDOM.nextInt(n);
        int inicio = Math.min(punto1, punto2);
        int fin = Math.max(punto1, punto2);
        for (int i = inicio; i <= fin; i++) {
            genomaHijo[i] = p1.getGenoma()[i];
        }

        return new Individuo(genomaHijo);
    }

}