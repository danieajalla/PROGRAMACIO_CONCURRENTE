package fuerza;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MegaLimpito {
	private static final Semaphore galpon = new Semaphore(20,true);
	private static final ExecutorService lavando = Executors.newCachedThreadPool();
	private static final ScheduledExecutorService autos = Executors.newScheduledThreadPool(1);
	private static final Semaphore lavadero = new Semaphore(4,true);
	private static final int[] CuentaLavado = {0};
	private static final int CostoLavado = 500;
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final Logger logger = Logger.getLogger(MegaLimpito.class.getName());
	public static void main(String[] args) {
		autos.schedule(new LlegadaAuto(), 0, TimeUnit.MILLISECONDS);
		autos.schedule(() -> {autos.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaAuto implements Runnable {

		@Override
		public void run() {
			int idAuto = id.getAndIncrement();
			System.out.println("Auto "+idAuto+" llegó al galpón.");
			lavando.submit(() -> ProcesandoAuto(idAuto));
			int tiempo = ThreadLocalRandom.current().nextInt(50,101);
			autos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoAuto(int idAuto) {
			try {
				if(!galpon.tryAcquire()) {
					System.out.println("Auto "+idAuto+" espera afuera del Galpón.");
					galpon.acquire();
				}
				System.out.println("Auto "+idAuto+" consigue ingresar al Galpón.");
				lavadero.acquire();
				System.out.println("Auto "+idAuto+" inicia el lavado.");
				Thread.sleep(ThreadLocalRandom.current().nextInt(150,301));
				System.out.println("Auto "+idAuto+" finaliza el lavado.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE,"Auto "+idAuto+" fue interrumpido durante el proceso lavado.", e);
				Thread.currentThread().interrupt();
			}catch (Exception e) {
				logger.log(Level.SEVERE,"Error inesperado del proceso de lavado " + idAuto + ".", e);
			}finally {
				galpon.release();
				lavadero.release();
			}
			CuentaLavado[0] += CostoLavado;
			System.out.println("Saldo Total del Lavadero : $ "+CuentaLavado[0]+".");
		}
	}
}
