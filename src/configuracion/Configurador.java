package configuracion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Configurador {

    private ArrayList<String> archivos;
    private ArrayList<String> algoritmos;
    ArrayList<Integer> semilla;
    private String cruce;
    private int elite;
    private int kBestMGen;
    private int k;
    private int kWorstGen;
    private int kWorstEst;
    private int tamPoblacion;
    private int evaluaciones;
    private int tiempoMax;
    private int porPoblAle;
    private double probMutaMGen;
    private double probCruceMGen;

    public Configurador(String ruta) {
        // Inicializamos las listas
        archivos = new ArrayList<>();
        algoritmos = new ArrayList<>();
        semilla = new ArrayList<>();
        iteraciones = new ArrayList<>();
        evaluaciones = new ArrayList<>();

        String linea;
        try (FileReader f = new FileReader(ruta); BufferedReader b = new BufferedReader(f)) {
            while ((linea = b.readLine()) != null) {
                // Ignorar líneas vacías
                if (linea.trim().isEmpty()) continue;

                String[] split = linea.split("=");
                if (split.length < 2) continue; // Protección contra líneas mal formadas

                // Limpiamos espacios y posibles puntos y comas al final
                String clave = split[0].trim();
                String valor = split[1].replace(";", "").trim();

                switch (clave) {
                    case "Archivos":
                        String[] vArchivos = valor.split(" ");
                        for (String s : vArchivos) archivos.add(s);
                        break;

                    case "Algoritmos":
                        String[] vAlgoritmos = valor.split(" ");
                        for (String s : vAlgoritmos) algoritmos.add(s);
                        break;

                    case "Semillas":
                        String[] vsemillas = split[1].split(" ");
                        for (int i = 0; i < vsemillas.length; i++){
                            semilla.add(Integer.parseInt(vsemillas[i]));
                        }
                        break;

                    case "Cruce":
                        cruce = valor;
                        break;

                    case "Elite":
                        elite = Integer.parseInt(valor);
                        break;

                    case "kBestMGen":
                        kBestMGen = Integer.parseInt(valor);
                        break;

                    case "kBestEst":
                        String[] vkBestEst = valor.split(" ");
                        for (String s : vkBestEst) kBestEst.add(Integer.parseInt(s));
                        break;

                    case "K":
                        k = Integer.parseInt(valor);
                        break;

                    case "kWorstGen":
                        kWorstGen = Integer.parseInt(valor);
                        break;

                    case "kWorstEst":
                        kWorstEst = Integer.parseInt(valor);
                        break;

                    case "tamPoblacion":
                        tamPoblacion = Integer.parseInt(valor);
                        break;

                    case "evaluaciones":
                        String[] vEvaluaciones = valor.split(" ");
                        for (String s : vEvaluaciones) evaluaciones.add(Integer.parseInt(s));
                        break;

                    case "tiempoMax":
                        tiempoMax = Integer.parseInt(valor);
                        break;

                    case "porPoblAle":
                        porPoblAle = Integer.parseInt(valor);
                        break;

                    case "probMutaGEN":
                        probMutaGen = Double.parseDouble(valor);
                        break;

                    case "probCruceGEN":
                        probCruceGen = Double.parseDouble(valor);
                        break;

                    case "evaluacionesMax":
                        evaluacionesMax = Integer.parseInt(valor);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo de configuración: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato numérico en configuración: " + e.getMessage());
        }
    }

    public ArrayList<String> getArchivos() { return archivos; }
    public ArrayList<String> getAlgoritmos() { return algoritmos; }
    public ArrayList<Integer> getSemillas() {return semilla;}
    public String getCruce() { return cruce; }
    public int getElite() { return elite; }
    public int getKBestMGen() { return kBestMGen; }
    public int getK() { return k; }
    public int getKWorstGen() { return kWorstGen; }
    public int getKWorstEst() { return kWorstEst; }
    public int getTamPoblacion() { return tamPoblacion; }
    public int getEvaluaciones() { return evaluaciones; }
    public int getTiempoMax() { return tiempoMax; }
    public int getPorPoblAle() { return porPoblAle; }
    public double getProbMutaGen() { return probMutaGen; }
    public double getProbCruceGen() { return probCruceGen; }
    public double getProbMutaEst() { return probMutaEst; }
}