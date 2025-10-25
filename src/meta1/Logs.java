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
    private int[] solucionInicial;
    private int costoInicial;
    private long semilla;
    public long tiempoTotalMs;
    public int[] solucionFinal;
    public int costoFinal;
    public StringBuilder logEvolucion = new StringBuilder(); // Contiene el historial de intercambio

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
        this.solucionInicial = (solInicial != null) ? solInicial.clone() : null;
        this.costoInicial = costoInicial;

        // Asignación de los resultados finales (que serán actualizados por Main para BL/BT)
        this.solucionFinal = (solFinal != null) ? solFinal.clone() : null;
        this.costoFinal = costoFinal;
        this.tiempoTotalMs = tiempoTotalMs;

        log = new StringBuilder();
    }

    //Metodo que llaman BusquedaLocal y BusquedaTabu para grabar cada paso
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

    // Construye el contenido final del archivo de log
    public void construirLog() {

        log.append("Parámetros del algoritmo\n")
                .append("------------------------\n")
                .append("Algoritmo: ").append(nombreAlgoritmo).append("\n")
                .append("Dataset: ").append(nombreArchivo).append("\n")
                .append("Semilla: ").append(semilla).append("\n");

        log.append("\nSolución inicial y su coste\n")
                .append("---------------------------\n");

        //Verificamos si es nulo para evitar error al imprimir
        if (solucionInicial != null) {
            log.append(Arrays.toString(solucionInicial)).append("\n")
                    .append("Coste: ").append(costoInicial).append("\n");
        } else {
            log.append("(Solución inicial no registrada)\n");
        }

        log.append("\nEvolución de la búsqueda\n")
                .append("------------------------\n");

        if (logEvolucion.length() > 0) {
            log.append(logEvolucion.toString()); //Insertamos los cambios registrados
        } else {
            log.append("(Algoritmo no iterativo)\n");
        }

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
