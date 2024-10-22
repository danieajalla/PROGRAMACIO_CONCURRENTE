package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio01 {
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final Semaphore CAPACIDAD = new Semaphore(30,true);
	private static final int CostoEstacion = 300;
	private static final int[] cuenta = {0};
	private static final ExecutorService estacion = Executors.newFixedThreadPool(5);
	private static final ScheduledExecutorService autos = Executors.newScheduledThreadPool(1);
	private static final Logger logger = Logger.getLogger(Ejercicio01.class.getName());
	public static void main(String[] args) {
		autos.schedule(new LlegadaAuto(), 0, TimeUnit.MILLISECONDS);
		autos.schedule(() -> {autos.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaAuto implements Runnable {

		@Override
		public void run() {
			int idAuto = id.getAndIncrement();
			System.out.println("Auto "+idAuto+" llegó al Estacionamiento.");
			estacion.submit(() -> ProcesandoAuto(idAuto));
			int tiempo = ThreadLocalRandom.current().nextInt(40,91);
			autos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoAuto(int idAuto) {
			try {
				if(!CAPACIDAD.tryAcquire()) {
					System.out.println("Auto "+idAuto+" espera afuera del estacion.");
					CAPACIDAD.acquire();
				}
				System.out.println("Auto "+idAuto+" - inicia estacionamiento.");
				Thread.sleep(ThreadLocalRandom.current().nextInt(120,251));
				System.out.println("Auto "+idAuto+" - finaliza estacionamiento.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Auto "+idAuto+" fue interrumpido durante el proceso del estacionamiento.", e);
				Thread.currentThread().interrupt();
			}catch (Exception e) {
				logger.log(Level.SEVERE,"Error inesperado del proceso del estacionamiento del Auto " + idAuto + ".", e);
			}finally {
				CAPACIDAD.release();
			}
			cuenta[0] += CostoEstacion;
			System.out.println("Saldo: $"+cuenta[0]+".");
		}
	}
}
