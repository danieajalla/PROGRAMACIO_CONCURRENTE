package tp_08;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Inciso02 {
	private static final List<String> listaFechas = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:S");
    private static final ScheduledExecutorService servicio = Executors.newScheduledThreadPool(2);
    public static void main(String[] args) {
        Runnable tarea1 = new Runnable() {
            @Override
            public void run() {
            	try {
            		String fechaActual = LocalDateTime.now().format(formatter);
                    synchronized (listaFechas) {
                        listaFechas.add(fechaActual);
                    }
                    System.out.println("Tarea1: Fecha y hora almacenada: " + fechaActual);
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        };
        Runnable tarea2 = new Runnable() {
            @Override
            public void run() {
            	try {
            		String ultimaFecha;
                    synchronized (listaFechas) {
                        if (listaFechas.isEmpty()) {
                            return;
                        }
                        ultimaFecha = listaFechas.get(listaFechas.size() - 1);
                    }
                    int milisegundos = Integer.parseInt(ultimaFecha.split(":")[2]);
                    if (esPrimo(milisegundos)) {
                        escribirArchivo("Primos.txt", milisegundos);
                    } else {
                        escribirArchivo("NoPrimos.txt", milisegundos);
                    }
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        };
        servicio.scheduleAtFixedRate(tarea1, 2, 2, TimeUnit.SECONDS);
        servicio.scheduleAtFixedRate(tarea2, 2, 2, TimeUnit.SECONDS);
    }
    private static boolean esPrimo(int numero) {
        if (numero <= 1) return false;
        for (int i = 2; i <= Math.sqrt(numero); i++) {
            if (numero % i == 0) return false;
        }
        return true;
    }
    private static void escribirArchivo(String nombreArchivo, int milisegundos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            writer.write(milisegundos + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}