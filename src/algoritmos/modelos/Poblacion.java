package algoritmos.modelos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Poblacion {
    private List<Individuo> individuos;
    private final int tamanoMax;

    public Poblacion(int tamanoMax) {
        this.tamanoMax = tamanoMax;
        this.individuos = new ArrayList<>(tamanoMax);
    }

    // --- Métodos Requeridos ---

    public void agregarIndividuo(Individuo ind) {
        if (individuos.size() < tamanoMax) {
            individuos.add(ind);
        }
    }

    public Individuo getIndividuo(int index) {
        return individuos.get(index);
    }

    public int getTamano() {
        return individuos.size();
    }

    public Individuo getMejorIndividuo() {
        if (individuos.isEmpty()) return null;
        return Collections.min(individuos, Comparator.comparingDouble(Individuo::getFitness));
    }

    public void evaluar() {
        for (Individuo ind : individuos) {
            ind.evaluar();
        }
    }

    public boolean contiene(Individuo ind) {
        // Verifica si existe un individuo con el mismo cromosoma y fitness
        return individuos.stream().anyMatch(i -> i.equals(ind));
    }

    public void reemplazar(Individuo peor, Individuo elite) {
        int index = -1;
        // Busca el índice del individuo a reemplazar (el peor)
        for (int i = 0; i < individuos.size(); i++) {
            if (individuos.get(i).equals(peor)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            // Reemplaza al peor con una copia del élite
            individuos.set(index, elite.clonar());
        }
    }
}
