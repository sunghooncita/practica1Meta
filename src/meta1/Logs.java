package meta1;

import configuracion.Configurador;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Logs implements Runnable {
    private Random aleatorio;
    private Configurador configurador;
    private StringBuilder log;
    private CountDownLatch cdl;

    public Logs(Configurador configurador, StringBuilder log, CountDownLatch cdl, long semilla) {
        this.configurador =configurador;
        this.cdl = cdl;
        aleatorio = new Random(semilla);
        log = new StringBuilder();
    }

    public void run(){
        log.append("El coste de la sol inicial es X");
        long tiempoInicial = System.currentTimeMillis();

        log.append("Iteracion Y \n Coste mejor X \n Se acepta solucion generada cn coste XXX......");

        long tiempoFinal = System.currentTimeMillis();
        log.append("El costo final es X \n Duracion"+(tiempoFinal-tiempoInicial)/1000.0+" segundos");
        cdl.countDown();
    }

    public String getLogs(){
        return log.toString();
    }

}
