package configuracion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Configurador {
    ArrayList<String> archivos;
    ArrayList<String> algoritmos;
    Integer semilla;
    Integer iteraciones;
    Integer K;
    Integer tenenciaTabu;
    Double oscilacionEstrategica;
    Double estancamiento;

    public Configurador(String ruta){
        archivos = new ArrayList<>();
        algoritmos = new ArrayList<>();
        String linea;
        FileReader f= null;
        try {
            f = new FileReader(ruta);
            BufferedReader b= new BufferedReader(f);
            while((linea=b.readLine())!=null){
                String[] split = linea.split("=");
                switch (split[0]){
                    case "Archivos":
                        String[] v = split[1].split(" ");
                        for (int i = 0; i < v.length; i++){
                            archivos.add(v[i]);
                        }
                        break;
                    case "Semillas":
                        semilla= Integer.parseInt(split[1]);

                        break;
                    case "Algoritmos":
                        String[] valgoritmos = split[1].split(" ");
                        for (int i = 0; i < valgoritmos.length; i++){
                            algoritmos.add(valgoritmos[i]);
                        }
                        break;
                    case "iteraciones":
                        iteraciones = Integer.parseInt(split[1]);
                        break;
                    case "K":
                        K = Integer.parseInt(split[1]);
                        break;
                    case "tenenciaTabu":
                        tenenciaTabu = Integer.parseInt(split[1]);
                        break;
                    case "oscilacionEstrategica":
                            oscilacionEstrategica = Double.parseDouble(split[1]);
                        break;
                    case "estancamiento":
                        estancamiento = Double.parseDouble(split[1]);
                        break;
                }
            }
        }catch(IOException e){
            System.out.println(e);
        }


    }

    public ArrayList<String> getArchivos() {
        return archivos;
    }
    public ArrayList<String> getAlgoritmos() {
        return algoritmos;
    }

    public Integer getSemillas() {
        return semilla;
    }

    public Integer getIteraciones() {
        return iteraciones;
    }

    public Integer getTenenciaTabu() {
        return tenenciaTabu;
    }

    public Double getOscilacion() {
        return oscilacionEstrategica;
    }

    public Double getEstancamiento() {
        return estancamiento;
    }

    public int getK() {
        return K;
    }
}
