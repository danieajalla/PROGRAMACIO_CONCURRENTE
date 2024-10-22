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

public class Estacionamiento {
	private static final Semaphore CAPACIDAD = new Semaphore(25,true);
	private static final ScheduledExecutorService autos = Executors.newScheduledThreadPool(1);
	private static final Semaphore ParqueoyDesparqueo = new Semaphore(5,true);
	private static final int CostoParqueo = 400;
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final int[] Cuenta = {0};
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(Estacionamiento.class.getName());
	public static void main(String[] args) {
		autos.schedule(new LlegadaAuto(), 0, TimeUnit.MILLISECONDS);
		autos.schedule(() -> {autos.shutdown();executor.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaAuto implements Runnable {

		@Override
		public void run() {
			int idAuto = id.getAndIncrement();
			System.out.println("Auto "+idAuto+" llegó al Estacion.");
			executor.submit(() -> ProcesandoAuto(idAuto));
			int tiempo = ThreadLocalRandom.current().nextInt(60,121);
			autos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoAuto(int idAuto) {
			try {
				if(!CAPACIDAD.tryAcquire()) {
					System.out.println("Auto "+idAuto+" - espera afuera del Estacion.");
					CAPACIDAD.acquire();
				}
				System.out.println("Auto "+idAuto+" - consigue ingresar al Estacion.");
				Parqueo(idAuto);
				Thread.sleep(ThreadLocalRandom.current().nextInt(180,351));
				Desparqueo(idAuto);
				System.out.println("Auto "+idAuto+" - se retira del Estacion.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Auto "+idAuto+" fue interruptido durante el proceso", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error inesperado durante el proceso de estacionamiento del Auto "+idAuto+".", e);
			}finally {
				Cuenta[0] += CostoParqueo;
				System.out.println("Saldo: $ "+Cuenta[0]+".");
			}
		}

		private void Desparqueo(int idAuto) {
			try {
				ParqueoyDesparqueo.acquire();
				System.out.println("Auto "+idAuto+" - inicia Desparqueo.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Auto "+idAuto+" fue interruptido durante el Desparqueo", e);
				Thread.currentThread().interrupt();
			}finally {
				ParqueoyDesparqueo.release();
			}
		}

		private void Parqueo(int idAuto) {
			try {
				ParqueoyDesparqueo.acquire();
				System.out.println("Auto "+idAuto+" - inicia el Parqueo.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Auto "+idAuto+" fue interruptido durante el Parqueo", e);
				Thread.currentThread().interrupt();
			}finally {
				ParqueoyDesparqueo.release();
			}
		}
	}
}
