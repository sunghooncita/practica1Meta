package meta1;

import configuracion.Configurador;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.Arrays;


public class Logs implements Runnable {

    private Configurador configurador;
    private StringBuilder log;
    private CountDownLatch cdl;
    private String nombreAlgoritmo;
    private String nombreArchivo;
    private long semilla;
    public long tiempoTotalMs;

    // Usamos el StringBuilder directamente para que los algoritmos puedan añadir contenido.
    // Esto es un enfoque común para el logging inter-clase asíncrono.
    public StringBuilder logEvolucion = new StringBuilder();

    private int[] solucionInicial;
    private int costoInicial;
    public int[] solucionFinal;
    public int costoFinal;

    public Logs(Configurador configurador, String nombreAlgoritmo, String nombreArchivo, CountDownLatch cdl,
                long semilla,
                int[] solInicial, int costoInicial,
                int[] solFinal, int costoFinal,
                long tiempoTotalMs) {

        this.configurador = configurador;
        this.nombreAlgoritmo = nombreAlgoritmo;
        this.nombreArchivo = nombreArchivo;
        this.cdl = cdl;
        this.semilla = semilla;

        // 🚨 CORRECCIÓN: Evitar NullPointerException usando una verificación ternaria
        this.solucionInicial = (solInicial != null) ? solInicial.clone() : null;
        this.costoInicial = costoInicial;
        this.solucionFinal = (solFinal != null) ? solFinal.clone() : null;
        this.costoFinal = costoFinal;
        this.tiempoTotalMs = tiempoTotalMs;

        log = new StringBuilder();
    }

    // 🚨 Este método debe ser llamado desde BusquedaLocal/BusquedaTabu
    public void registrarIntercambio(int pos1, int pos2, int nuevoCosto, boolean mejora) {
        String linea;
        if (mejora) {
            linea = String.format("Intercambio (%d, %d) -> Nueva solución con coste %d\n", pos1, pos2, nuevoCosto);
        } else {
            linea = String.format("Intercambio (%d, %d) -> Peor solución (coste %d) — no aceptada\n", pos1, pos2, nuevoCosto);
        }
        this.logEvolucion.append(linea);
    }


    public void construirLog() {

        // 1. Parámetros del algoritmo
        log.append("Parámetros del algoritmo\n")
                .append("------------------------\n")
                .append("Algoritmo: ").append(nombreAlgoritmo).append("\n")
                .append("Dataset: ").append(nombreArchivo).append("\n")
                .append("Semilla: ").append(semilla).append("\n");

        // 2. Solución inicial y su coste
        log.append("\nSolución inicial y su coste\n")
                .append("---------------------------\n");
        // Verificamos si es nulo para evitar error al imprimir
        if (solucionInicial != null) {
            log.append(Arrays.toString(solucionInicial)).append("\n")
                    .append("Coste: ").append(costoInicial).append("\n");
        } else {
            log.append("(Solución inicial no registrada)\n");
        }

        // 3. Evolución de la búsqueda (Por cada cambio)
        log.append("\nEvolución de la búsqueda\n")
                .append("------------------------\n");

        if (logEvolucion.length() > 0) {
            log.append(logEvolucion.toString()); // Insertamos los cambios registrados
        } else {
            log.append("(Algoritmo no iterativo o no se registraron intercambios)\n");
        }

        // 4. Solución final, su coste y el tiempo.
        log.append("\nSolución final\n")
                .append("--------------\n");
        if (solucionFinal != null) {
            log.append(Arrays.toString(solucionFinal)).append("\n")
                    .append("Coste final: ").append(costoFinal).append("\n")
                    .append("Duración: ").append(tiempoTotalMs / 1000.0).append(" segundos\n");
        } else {
            log.append("(Solución final no registrada)\n");
        }
    }


    private void guardarLog() throws IOException {
        File dir = new File("logs");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String ruta = "logs/" + nombreArchivo + "_" + nombreAlgoritmo + "_semilla" + semilla + ".log";
        try (FileWriter fw = new FileWriter(ruta); PrintWriter pw = new PrintWriter(fw)) {
            pw.print(log.toString());
        }
    }

    public void run(){
        try {
            construirLog();
            guardarLog();
        } catch (IOException e) {
            System.err.println("Error durante la ejecución del log: " + e.getMessage());
        } finally {
            cdl.countDown();
        }
    }
}
