package parcial2024;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio2 {
	private static final int tiempo = 5/2;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final Logger logger = Logger.getLogger(Ejercicio2.class.getName());
	public static void main(String[] args) {
		scheduler.scheduleAtFixedRate(new GenerandoPunto(), 0, tiempo, TimeUnit.SECONDS);
	}
	static class GenerandoPunto implements Runnable {

		@Override
		public void run() {
			double x = ThreadLocalRandom.current().nextInt(-5,6);
			double y = 1 / (1+x*x);
			System.out.println("Puntos generado: ("+x+","+y+").");
			escribirArchivo("Puntos.txt",x, y);
		}
		private void escribirArchivo(String nombreArchivo, double X, double Y) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
	            writer.write("("+X+","+Y+") \n");
	        } catch (IOException e) {
	        	logger.log(Level.SEVERE, "Error inesperado durante el guardado del Archivo.", e);
	            Thread.currentThread().interrupt();
	        }
		}
	}
}
