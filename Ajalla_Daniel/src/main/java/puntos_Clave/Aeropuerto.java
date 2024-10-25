package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aeropuerto {
	private static final Logger logger = Logger.getLogger(Aeropuerto.class.getName());
	private static final Map<String, Semaphore> BOLETERIAS = new ConcurrentHashMap<>();
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final int[] cantidadPasajeros = {0,0,0};
	private static final ScheduledExecutorService AeroPuerto = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		BOLETERIAS.put("T3", new Semaphore(4,true));
		BOLETERIAS.put("T4", new Semaphore(5,true));
		BOLETERIAS.put("T5", new Semaphore(3,true));
		AeroPuerto.scheduleAtFixedRate(new LlegadaTaxis(), 0,ThreadLocalRandom.current().nextInt(120, 181), TimeUnit.MILLISECONDS);
		AeroPuerto.schedule(() -> {AeroPuerto.shutdown(); executor.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaTaxis implements Runnable {

		@Override
		public void run() {
			int numPasajeros = ThreadLocalRandom.current().nextInt(1, 5);  
            String[] terminales = {"T3", "T4", "T5"};
            int destino = ThreadLocalRandom.current().nextInt(terminales.length);
            String idTerminal = terminales[destino];  
            for(int i=0; i<numPasajeros; i++) {
            	executor.submit(() -> ProcesarPasajero(destino,idTerminal));
            }
		}

		private void ProcesarPasajero(int destino, String idTerminal) {
			cantidadPasajeros[destino]++;
			int idPasajero = cantidadPasajeros[destino];
			try {
				ObtenerRecursos(idTerminal);
				System.out.println("Pasajero "+idPasajero+" - inicia check-in, Boleteria \""+idTerminal+"\".");
				Thread.sleep(ThreadLocalRandom.current().nextInt(180,281));
				System.out.println("Pasajero "+idPasajero+" - finaliza check-in, Boleteria \""+idTerminal+"\".");
			}  catch (InterruptedException e) {
				logger.log(Level.SEVERE,"Pasajero en Terminal "+idTerminal+" fue interrupido durante el check-in.",e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
	            logger.log(Level.SEVERE, "Error inesperado en Boleteria del Terminal " + idTerminal + ".", e);
	        } finally {
				DevolverRecursos(idTerminal);
			}
		}
		private static void ObtenerRecursos(String boleteria) {
			try {
				BOLETERIAS.get(boleteria).acquire();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		private static void DevolverRecursos(String boleteria) {
			BOLETERIAS.get(boleteria).release();
		}
	}
}
