package parcial2024;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio1 {
	private static final int NUM_CABALLO = 6;
	private static final int[] contador = {0,5};
	private static final ExecutorService salida = Executors.newFixedThreadPool(6);
	private static final ExecutorService caballo = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(Ejercicio1.class.getName());
	private static final Semaphore espera = new Semaphore(0,true);
	public static void main(String[] args) {
		for(int i=1; i<=NUM_CABALLO; i++) {
			int id = i;
			salida.submit(new SalidaCaballo(id));
		}
		salida.shutdown();
	}
	static class SalidaCaballo implements Runnable {
		private final int idCaballo;
		public SalidaCaballo(int id) {
			this.idCaballo = id;
		}

		@Override
		public void run() {
			System.out.println("Caballo "+idCaballo+" - inicia carrera.");
			for(int i=1; i<=contador[1]; i++) {
				int id = i;
				caballo.submit(() -> VueltaCaballo(idCaballo,id));
				try {
					espera.acquire();
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Caballo "+idCaballo+" - fue interrumpido durante la espera.", e);
				Thread.currentThread().interrupt();
				}
			}
			caballo.shutdown();
		}

		private void VueltaCaballo(int idCaballo, int i) {
			try {
				System.out.println("Caballo "+idCaballo+" - inicia carrera "+i+"° vuelta.");
				int tiempo = ThreadLocalRandom.current().nextInt(2000,4001);
				Thread.sleep(tiempo);
				contador[0]++;
				System.out.println("Caballo "+idCaballo+" - finaliza carrera en "+contador[0]+"° posicion, tiempo: "+tiempo+"ms.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Caballo "+idCaballo+" - fue interrumpido durante la carrera.", e);
				Thread.currentThread().interrupt();
			}finally {
				if(contador[0] == NUM_CABALLO) {
					contador[0]=0;
					espera.release(NUM_CABALLO);
				}
			}
		}
	}
}
