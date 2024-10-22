package fuerza;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aeropuerto_Internacional {
	private static final Logger logger = Logger.getLogger(Ejercicio02.class.getName());
	private static final ExecutorService boleteriaT3 = Executors.newFixedThreadPool(4);
	private static final ExecutorService boleteriaT4 = Executors.newFixedThreadPool(5);
	private static final ExecutorService boleteriaT5 = Executors.newFixedThreadPool(3);
	private static final ScheduledExecutorService AeroPuerto = Executors.newScheduledThreadPool(3);
	public static void main(String[] args) {
		AeroPuerto.schedule(new LlegadaTaxis(), 0, TimeUnit.MILLISECONDS);
		AeroPuerto.schedule(() -> {AeroPuerto.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaTaxis implements Runnable {

		@Override
		public void run() {
			int numPasajeros = ThreadLocalRandom.current().nextInt(1, 5);  
            String[] terminales = {"T3", "T4", "T5"};
            String idTerminal = terminales[ThreadLocalRandom.current().nextInt(terminales.length)];  
            procesarPasajeros(idTerminal, numPasajeros);
            int tiempo = ThreadLocalRandom.current().nextInt(120, 181);
            AeroPuerto.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void procesarPasajeros(String idTerminal, int numPasajero) {
			for(int i=0; i<numPasajero; i++) {
				if(idTerminal == "T3") {
					boleteriaT3.submit(() -> Boleteria("T3"));
				}else if(idTerminal == "T4") {
					boleteriaT4.submit(() -> Boleteria("T4"));
				}else {
					boleteriaT5.submit(() -> Boleteria("T5"));
				}
			}
		}

		private void Boleteria(String idTerminal) {
			try {
				System.out.println("Pasajero - inicia el check-in en una Boleteria de la Terminal "+idTerminal+".");
				Thread.sleep(ThreadLocalRandom.current().nextInt(180,281));
				System.out.println("Pasajero - Termina el check-in en una Boleteria de la Terminal "+idTerminal+".");
			}  catch (InterruptedException e) {
				logger.log(Level.SEVERE,"Pasajero de la Terminal "+idTerminal+" fue interrupido durante el check-in.",e);
				Thread.currentThread().interrupt();
			}catch (Exception e) {
	            logger.log(Level.SEVERE, "Error inesperado en la Boleteria en la Terminal " + idTerminal + ".", e);
	        }
		}
	}
}
