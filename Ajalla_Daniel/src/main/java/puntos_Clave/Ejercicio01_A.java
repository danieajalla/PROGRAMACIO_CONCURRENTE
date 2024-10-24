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

public class Ejercicio01_A {
	private static final Semaphore galpon = new Semaphore(20,true);
	private static final Semaphore lavado = new Semaphore(4,true);
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final ScheduledExecutorService autos = Executors.newScheduledThreadPool(1);
	private static final ExecutorService turnoLavado = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(Ejercicio01_A.class.getName());
	private static final int CostoLavado = 500;
	private static final int[] CuentaLavado = {0};
	public static void main(String[] args) {
		autos.schedule(new LlegadaAuto(), 0, TimeUnit.MILLISECONDS);
		autos.schedule(() -> {autos.shutdown();turnoLavado.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaAuto implements Runnable {

		@Override
		public void run() {
			int idAuto = id.getAndIncrement();
			System.out.println("Auto "+idAuto+" llegó al Galpón.");
			turnoLavado.submit(() -> ProcesandoLavado(idAuto));
			int tiempo = ThreadLocalRandom.current().nextInt(50,101);
			autos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoLavado(int idAuto) {
			try {
				if(!galpon.tryAcquire()) {
					System.out.println("Auto "+idAuto+" - espera afuera del Galpón.");
					galpon.acquire();
				}
				System.out.println("Auto "+idAuto+" - ingreso al Galpón.");
				lavado.acquire();
				System.out.println("Auto "+idAuto+" - inicia lavado.");
				Thread.sleep(ThreadLocalRandom.current().nextInt(150,301));
				System.out.println("Auto "+idAuto+" - finaliza lavado.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Auto "+idAuto+" - fue interrumpido durante el proceso del lavado.", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error inesperado durante el proceso del lavado del Auto "+idAuto+".", e);
			}finally {
				galpon.release();
				lavado.release();
			}
			CuentaLavado[0] += CostoLavado;
			System.out.println("Saldo: $ "+CuentaLavado[0]+".");
		}
	}
}
