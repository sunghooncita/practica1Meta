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

    // Parámetros
    private int elite;
    private int kBest;
    private int kWorst;
    private String tipoCruce;
    private double probMuta;
    private double probCruce;
    private int tamPoblacion;

    // Resultados
    private List<Integer> solucionFinal;
    private double costoFinal;
    private long tiempoTotalMs;


    public Logs(String nombreAlgoritmo, String nombreArchivoDatos, long semilla,
                int elite, int kBest, int kWorst, String tipoCruce,
                double probMuta, double probCruce, int tamPoblacion,
                List<Integer> solFinal, double costoFinal, long tiempoTotalMs) {

        this.nombreAlgoritmo = nombreAlgoritmo;
        this.nombreArchivoDatos = nombreArchivoDatos;
        this.semilla = semilla;
        this.elite = elite;
        this.kBest = kBest;
        this.kWorst = kWorst;
        this.tipoCruce = tipoCruce;
        this.probMuta = probMuta;
        this.probCruce = probCruce;
        this.tamPoblacion = tamPoblacion;
        this.solucionFinal = solFinal;
        this.costoFinal = costoFinal;
        this.tiempoTotalMs = tiempoTotalMs;
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
        sb.append("kBest:          ").append(kBest).append("\n");
        sb.append("kWorst:         ").append(kWorst).append("\n");
        sb.append("Cruce:          ").append(tipoCruce).append("\n");
        sb.append("Elite:          ").append(elite).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Costo Final:    ").append(costoFinal).append("\n");
        sb.append("Tiempo (ms):    ").append(tiempoTotalMs).append("\n");
        sb.append("Solución:       ").append(solucionFinal != null ? solucionFinal.toString() : "null").append("\n");
        sb.append("========================================\n");

        guardarArchivo(sb.toString());
    }

    private void guardarArchivo(String contenido) {
        File dir = new File("logs");
        if (!dir.exists()) dir.mkdirs();

        // Quitamos la extensión .txt del nombre original para que quede limpio
        String nombreArchivoLimpio = nombreArchivoDatos.replace(".txt", "").replace(".dat", "");

        // NOMBRE FORMATEADO SIN NANOTIME
        // Formato: Archivo_Algoritmo_EliteX_kBX_kWX_Cruce.log
        String nombreFichero = String.format("logs/%s_%s_E%d_kB%d_kW%d_%s_S%d.log",
                nombreArchivoLimpio,
                nombreAlgoritmo,
                elite,
                kBest,
                kWorst,
                tipoCruce,
                semilla);

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