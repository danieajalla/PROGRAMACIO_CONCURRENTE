package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Estacionamiento {
	private static final int CAPACIDAD = 30;
    private static final Semaphore espacios = new Semaphore(CAPACIDAD, true);
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final AtomicInteger idVehiculo = new AtomicInteger(1);
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaVehiculo(), 0, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 5, TimeUnit.MINUTES);
	}
	static class LlegadaVehiculo implements Runnable {

		@Override
		public void run() {
			int id = idVehiculo.getAndIncrement();
			System.out.println("Vehiculo "+id+" llegó.");
			executor.submit(new Vehiculo(id));
			int tiempo = ThreadLocalRandom.current().nextInt(100,301);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
	}
	static class Vehiculo implements Runnable {
		private final int idVehiculo;
		public Vehiculo(int id) {
			this.idVehiculo = id;
		}
		@Override
		public void run() {
			try {
                if(!espacios.tryAcquire()) {
                	System.out.println("Vehiculo " +idVehiculo+ " está esperando un espacio.");
                	espacios.acquire();
                }
                int asientoAsignado = CAPACIDAD - espacios.availablePermits();
                System.out.println("Vehiculo " + idVehiculo + " asignado al espacio " + asientoAsignado + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(3, 6)* 60 * 1000); 
                System.out.println("Vehiculo " + idVehiculo + " libera el espacio " + asientoAsignado + ".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
            	espacios.release();
            }
		}
		
	}
}