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
    public long costoFinal;
    private long tiempoTotalMs;
    private int evaluaciones; // NUEVO: Para guardar las evaluaciones máximas
    private int generaciones;


    public Logs(String nombreAlgoritmo, String nombreArchivoDatos, long semilla,
                int elite, int kBest, int kWorst, String tipoCruce,
                double probMuta, double probCruce, int tamPoblacion,
                List<Integer> solFinal, long costoFinal, long tiempoTotalMs, int evaluaciones, int generaciones) {

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
        this.evaluaciones = evaluaciones;
        this.generaciones = generaciones;
    }

    public void registrarIntercambio(int pos1, int pos2, int nuevoCosto, boolean mejora) {
        String linea;
        if (mejora) {
            linea = String.format("Intercambio (%d, %d) -> Nueva solución con coste %d\n", pos1, pos2, nuevoCosto);
        } else {
            //Este caso aplica a Busqueda Tabu si acepta un movimiento no mejorador/peor
            linea = String.format("Intercambio (%d, %d) -> Peor solución (coste %d) — no aceptada\n", pos1, pos2, nuevoCosto);
        }
        this.logEvolucion.append(linea);
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
        sb.append("Evaluaciones Max: ").append(evaluaciones).append("\n");
        sb.append("Generaciones:     ").append(generaciones).append("\n");
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
        //Metodo que llaman BusquedaLocal y BusquedaTabu para grabar cada paso

    }

    @Override
    public void run() {
        construirYGuardarLog();
    }
}