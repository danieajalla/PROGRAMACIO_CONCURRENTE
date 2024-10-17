package practica;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Lavadero {
	private static final Semaphore lugar = new Semaphore(20,true);
	private static final Semaphore lavado = new Semaphore(4,true);
	private static final  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static int SaldoCuenta = 0;
	public static void main(String[] args) {
		scheduler.schedule(new llegadaAutos(), 0, TimeUnit.MILLISECONDS);
        scheduler.schedule(() -> {
            scheduler.shutdown();
        }, 10, TimeUnit.SECONDS);
	}
	static class llegadaAutos implements Runnable{

		@Override
		public void run() {
			new Thread(new Auto()).start();
			int tiempo = ThreadLocalRandom.current().nextInt(50,101);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
	}
	static class Auto implements Runnable {
		private final int id;
		private static int contador = 0;
		public Auto() {
			this.id = ++contador;
		}
		@Override
		public void run() {
			try {
				lugar.acquire();
				System.out.println("Auto "+id+", ocupo un lugar.");
				lavado.acquire();
				System.out.println("Auto "+id+", esta siendo lavando");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally{
				lugar.release();
			}
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(150,301));
				System.out.println("Auto "+id+", finaliza lavado");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				lavado.release();
			}
			SaldoCuenta += 500;
			System.out.println("Saldo Total: $ "+SaldoCuenta);
		}
		
	}
}
