package meta1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Logs implements Runnable {

    private String nombreAlgoritmo;
    private String nombreArchivoDatos;
    private long semilla;

    // Parámetros de Algoritmo Generacional
    private int elite;
    private int kBest;
    private int kWorst;
    private String tipoCruce;
    private int tamPoblacion;

    // Parámetros de Algoritmo Memético (Nuevos)
    private Integer evaluacionesBT; // Frecuencia de la BT (cada X evaluaciones)
    private Integer iteracionesBT; // Profundidad de la BT (número de iteraciones)


    // Resultados
    private List<Integer> solucionFinal;
    public long costoFinal;
    private long tiempoTotalMs;
    private int evaluaciones;
    private int generaciones;


    // Constructor Modificado para incluir los parámetros de la BT
    public Logs(String nombreAlgoritmo, String nombreArchivoDatos, long semilla,
                int elite, int kBest, int kWorst, String tipoCruce, int tamPoblacion,
                List<Integer> solFinal, long costoFinal, long tiempoTotalMs, int evaluaciones, int generaciones,
                // Nuevos parámetros específicos para Memético
                Integer evaluacionesBT, Integer iteracionesBT) {

        this.nombreAlgoritmo = nombreAlgoritmo;
        this.nombreArchivoDatos = nombreArchivoDatos;
        this.semilla = semilla;
        this.elite = elite;
        this.kBest = kBest;
        this.kWorst = kWorst;
        this.tipoCruce = tipoCruce;
        this.tamPoblacion = tamPoblacion;
        this.solucionFinal = solFinal;
        this.costoFinal = costoFinal;
        this.tiempoTotalMs = tiempoTotalMs;
        this.evaluaciones = evaluaciones;
        this.generaciones = generaciones;

        // Inicialización de los nuevos parámetros
        this.evaluacionesBT = evaluacionesBT;
        this.iteracionesBT = iteracionesBT;
    }


    public void construirYGuardarLog() {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append(" RESUMEN DE EJECUCIÓN \n");
        sb.append("========================================\n");
        sb.append("Algoritmo:      ").append(nombreAlgoritmo).append("\n");
        sb.append("Archivo:        ").append(nombreArchivoDatos).append("\n");
        sb.append("Semilla:        ").append(semilla).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Población:      ").append(tamPoblacion).append("\n");
        sb.append("kBest:          ").append(kBest).append("\n");
        sb.append("kWorst:         ").append(kWorst).append("\n");
        sb.append("Elite:          ").append(elite).append("\n");
        sb.append("Cruce:          ").append(tipoCruce).append("\n");
        sb.append("----------------------------------------\n");

        // Sección específica para el Algoritmo Memético
        if (nombreAlgoritmo.equals("Memetico")) {
            sb.append("Evaluaciones BT: ").append(evaluacionesBT).append(" evaluaciones\n");
            sb.append("Iteraciones BT: ").append(iteracionesBT).append(" iteraciones (Profundidad)\n");
            sb.append("----------------------------------------\n");
        }

        sb.append("RESULTADOS FINALES:\n");
        sb.append("Evaluaciones Max: ").append(evaluaciones).append("\n");
        sb.append("Generaciones:     ").append(generaciones).append("\n");
        sb.append("Tiempo (ms):    ").append(tiempoTotalMs).append("\n");
        sb.append("Costo Final:    ").append(costoFinal).append("\n");
        sb.append("Solución:       ").append(solucionFinal != null ? solucionFinal.toString() : "null").append("\n");
        sb.append("========================================\n");

        guardarArchivo(sb.toString());
    }

    private void guardarArchivo(String contenido) {
        File dir = new File("logs");
        if (!dir.exists()) dir.mkdirs();

        String nombreArchivoLimpio = nombreArchivoDatos.replace(".txt", "").replace(".dat", "");

        String nombreFichero;

        if (nombreAlgoritmo.equals("Memetico")) {
            // Formato para el Memético (incluye los parámetros BT para diferenciar las 9 ejecuciones)
            // Ejemplo: Archivo_Memetico_Lanz1000_It10_S1.log
            nombreFichero = String.format("logs/%s_%s_Lanz%d_It%d_S%d.log",
                    nombreArchivoLimpio,
                    nombreAlgoritmo,
                    evaluacionesBT,
                    iteracionesBT,
                    semilla);
        } else {
            // Formato para el Generacional (Original)
            // Formato: Archivo_Generacional_E1_kB2_kW3_OX2_S1.log
            nombreFichero = String.format("logs/%s_%s_E%d_kB%d_kW%d_%s_S%d.log",
                    nombreArchivoLimpio,
                    nombreAlgoritmo,
                    elite,
                    kBest,
                    kWorst,
                    tipoCruce,
                    semilla);
        }


        try (FileWriter fw = new FileWriter(nombreFichero); PrintWriter pw = new PrintWriter(fw)) {
            pw.print(contenido);
        } catch (IOException e) {
            System.err.println("Error guardando log: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        construirYGuardarLog();
    }
}