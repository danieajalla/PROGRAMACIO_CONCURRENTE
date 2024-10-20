package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Lavanderia {
	private static final Semaphore maquina = new Semaphore(6,true);
	private static final ExecutorService executor = Executors.newFixedThreadPool(6);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final AtomicInteger idCliente = new AtomicInteger(1);
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaCliente(), 0, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> {scheduler.shutdown();executor.shutdown();}, 1, TimeUnit.MINUTES);
	}
	static class LlegadaCliente implements Runnable {

		@Override
		public void run() {
			int id = idCliente.getAndIncrement();
			System.out.println("Cliente " + id + " llegó a la lavandería.");
			executor.submit(new Lavadero(id));
			int tiempo = ThreadLocalRandom.current().nextInt(100,301);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
	}
	static class Lavadero implements Runnable {
		private final int id;
		public Lavadero(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			try {
                if (!maquina.tryAcquire()) {
                    System.out.println("Cliente " + id + " está esperando una máquina disponible.");
                    maquina.acquire();
                }
                int maquinaAsignada = 6 - maquina.availablePermits();
                System.out.println("Cliente " + id + " comienza a usar la máquina " + maquinaAsignada + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 21) * 1000);
                System.out.println("Cliente " + id + " terminó de usar la máquina " + maquinaAsignada + " y la libera.");
            } catch (InterruptedException e) {
                System.out.println("Cliente " + id + " fue interrumpido mientras usaba la máquina.");
                Thread.currentThread().interrupt();
            } finally {
                maquina.release();
            }
		}
		
	}
}