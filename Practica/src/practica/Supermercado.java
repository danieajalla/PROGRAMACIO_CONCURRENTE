package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Supermercado {
	private static final AtomicInteger atomic = new AtomicInteger(1);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ExecutorService caja = Executors.newFixedThreadPool(5);
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaCliente(), 0, TimeUnit.MILLISECONDS);
		 scheduler.schedule(() -> {
	            scheduler.shutdown();
	            caja.shutdown();
	        }, 1, TimeUnit.MINUTES);
	}
	static class LlegadaCliente implements Runnable {

		@Override
		public void run() {
			int idCliente = atomic.getAndIncrement();
			int tiempo = ThreadLocalRandom.current().nextInt(100,201);
			int cantidadArticulo = ThreadLocalRandom.current().nextInt(1,21);
			System.out.println("Cliente "+idCliente+" llego con "+cantidadArticulo+" articulos");
			caja.submit(new Cliente(idCliente, cantidadArticulo));
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
		
	}
	static class Cliente implements Runnable {
		private final int idCliente;
		private final int cantidadArticulo;
		public Cliente(int idCliente, int cantidadArticulo) {
			this.idCliente = idCliente;
			this.cantidadArticulo = cantidadArticulo;
		}

		@Override
		public void run() {
			System.out.println("Cliente "+idCliente+" atendido en la caja ");
			try {
				Thread.sleep(50*cantidadArticulo);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			System.out.println("Cliente "+idCliente+" finalizo pago de sus articulos ");
		}
	}
}