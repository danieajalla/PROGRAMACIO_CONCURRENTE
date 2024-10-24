package puntos_Clave;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Ejercicio01_B {
	private static final Logger logger = Logger.getLogger(Ejercicio01_B.class.getName());
	private static final Semaphore boleteriaT3 = new Semaphore(4,true);
	private static final Semaphore boleteriaT4 = new Semaphore(5,true);
	private static final Semaphore boleteriaT5 = new Semaphore(3,true);
	private static final AtomicInteger idT3 = new AtomicInteger(1);
	private static final AtomicInteger idT4 = new AtomicInteger(1);
	private static final AtomicInteger idT5 = new AtomicInteger(1);
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final ScheduledExecutorService AeroPuerto = Executors.newScheduledThreadPool(1);
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
            for(int i=0; i<numPasajeros; i++) {
            	executor.submit(() -> procesarPasajeros(idTerminal));
            }
            int tiempo = ThreadLocalRandom.current().nextInt(120, 181);
            AeroPuerto.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void procesarPasajeros(String idTerminal) {
			int idPasajero = 0;
			switch(idTerminal) {
			case "T3":
				idPasajero = idT3.getAndIncrement();
				try {
					boleteriaT3.acquire();
					System.out.println("Pasajero "+idPasajero+" - inicia check-in, Boleteria \""+idTerminal+"\".");
					Thread.sleep(ThreadLocalRandom.current().nextInt(180,281));
					System.out.println("Pasajero "+idPasajero+" - finaliza check-in, Boleteria \""+idTerminal+"\".");
				}  catch (InterruptedException e) {
					logger.log(Level.SEVERE,"Pasajero en Terminal "+idTerminal+" fue interrupido durante el check-in.",e);
					Thread.currentThread().interrupt();
				}catch (Exception e) {
		            logger.log(Level.SEVERE, "Error inesperado en Boleteria del Terminal " + idTerminal + ".", e);
		        }finally {
		        	boleteriaT3.release();
		        }
				break;
			case "T4":
				idPasajero = idT4.getAndIncrement();
				try {
					boleteriaT4.acquire();
					System.out.println("Pasajero "+idPasajero+" - inicia check-in, Boleteria \""+idTerminal+"\".");
					Thread.sleep(ThreadLocalRandom.current().nextInt(180,281));
					System.out.println("Pasajero "+idPasajero+" - finaliza check-in, Boleteria \""+idTerminal+"\".");
				}  catch (InterruptedException e) {
					logger.log(Level.SEVERE,"Pasajero en Terminal "+idTerminal+" fue interrupido durante el check-in.",e);
					Thread.currentThread().interrupt();
				}catch (Exception e) {
		            logger.log(Level.SEVERE, "Error inesperado en Boleteria del Terminal " + idTerminal + ".", e);
		        }finally {
		        	boleteriaT4.release();
		        }
				break;
			case "T5":
				idPasajero = idT5.getAndIncrement();
				try {
					boleteriaT5.acquire();
					System.out.println("Pasajero "+idPasajero+" - inicia check-in, Boleteria \""+idTerminal+"\".");
					Thread.sleep(ThreadLocalRandom.current().nextInt(180,281));
					System.out.println("Pasajero "+idPasajero+" - finaliza check-in, Boleteria \""+idTerminal+"\".");
				}  catch (InterruptedException e) {
					logger.log(Level.SEVERE,"Pasajero en Terminal "+idTerminal+" fue interrupido durante el check-in.",e);
					Thread.currentThread().interrupt();
				}catch (Exception e) {
		            logger.log(Level.SEVERE, "Error inesperado en Boleteria del Terminal " + idTerminal + ".", e);
		        }finally {
		        	boleteriaT5.release();
		        }
				break;
			default:
				throw new IllegalStateException("Terminal desconocido: " + idTerminal);
			}
		}

	}
}
