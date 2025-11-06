package algoritmos.modelos;
import java.util.Arrays;
import java.util.Random;

/**
 * Representa un individuo (solución) para el Problema de Asignación Cuadrática (QAP).
 * El constructor maneja la inicialización única de las matrices de flujo y distancia.
 */
public class Individuo {

    private int[] genoma; // Permutación: genoma[departamento_i] = ubicacion_j
    private double fitness; // Almacenará el Coste Total (valor a MINIMIZAR)

    /**
     * Constructor de conveniencia: Asume que los datos globales (F y D) ya están inicializados.
     * Ideal para crear individuos descendientes (hijos de cruce/mutación).
     */
    public Individuo(int[] genoma) {
        this.genoma = genoma;
    }


    private double calcularFitness(int[] genoma, int[][] flujos, int[][] distancias) {
        double costo = 0.0;
        int n = genoma.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // El cast (double) asegura que la multiplicación se maneje correctamente
                costo += (double) flujos[i][j] * distancias[genoma[i]][genoma[j]];
            }
        }
        return costo;// Este valor es el Coste Total (a MINIMIZAR)
    }


    public double getFitness() {
        return fitness;
    }

    public int[] getGenoma() {
        return genoma;
    }


    // ... Métodos equals y hashCode (necesarios para el elitismo)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Individuo other = (Individuo) obj;
        return Arrays.equals(this.genoma, other.genoma);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genoma);
    }
}