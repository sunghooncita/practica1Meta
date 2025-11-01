package algoritmos.modelos;

import java.util.Arrays;

public class Individuo {
    private int[] cromosoma;
    private double fitness;


    public Individuo(int[] cromosoma) {
        this.cromosoma = cromosoma.clone();
        this.fitness = Double.MAX_VALUE; // Inicialmente, fitness muy malo (minimización).
    }


    public void evaluar() {
        // *** Lógica de evaluación de tu problema (Práctica 1) ***
        // Simulación: Fitness es la suma de los genes (debería ser tu función de coste real)
        double sum = 0;
        for (int gene : cromosoma) {
            sum += gene;
        }
        // El fitness real debe ser calculado por tu función de coste
        this.fitness = Math.random() * 1000;
    }

    public Individuo clonar() {
        Individuo clon = new Individuo(this.cromosoma);
        clon.fitness = this.fitness;
        return clon;
    }

    public double getFitness() {
        return fitness;
    }

    public int[] getCromosoma() {
        return cromosoma;
    }

    public void setCromosoma(int[] nuevoCromosoma) {
        this.cromosoma = nuevoCromosoma;
    }

    @Override
    public boolean equals(Object obj) {
        // Método para comparar si dos individuos son iguales (se usa en el elitismo).
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Individuo that = (Individuo) obj;
        // La igualdad se basa en el contenido del cromosoma y el fitness.
        return Double.compare(that.fitness, fitness) == 0 && Arrays.equals(cromosoma, that.cromosoma);
    }
}