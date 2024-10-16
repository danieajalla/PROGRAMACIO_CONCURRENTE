package practica;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Delivery {
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
	private static final AtomicInteger idpedidos = new AtomicInteger(1);
	private static final Semaphore hamburguesa = new Semaphore(0,true);
	private static final Semaphore postre = new Semaphore(0,true);
	public static void main(String[] args) {
		new Thread(new EmpresaMD()).start();
		new Thread(new EmpresaP()).start();
		scheduler.schedule(new Pedidos(), 0, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 20, TimeUnit.SECONDS);
	}
	static class EmpresaMD implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(ThreadLocalRandom.current().nextInt(100,301));
					hamburguesa.release();
					System.out.println("Se a producido una Hamburguesa(MD)");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	static class EmpresaP implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(ThreadLocalRandom.current().nextInt(100,301));
					postre.release();
					System.out.println("Se a producido un Postre(P)");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	static class Pedidos implements Runnable {

		@Override
		public void run() {
			int id = idpedidos.getAndIncrement();
			String pedido = GenerandoPedido();
			System.out.println("Pedido " + id + " ha llegado: " + pedido);
			executor.submit(() -> ProcesandoPedido(id, pedido));
			int tiempo = ThreadLocalRandom.current().nextInt(150,251);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
		private void ProcesandoPedido(int id, String pedido) {
			try {
				if(pedido.contains("Hamburguesa")) {
					hamburguesa.acquire();
				}
				if(pedido.contains("Postre")) {
					postre.acquire();
				}
				System.out.println("Pedido "+id+" completado: "+pedido);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		private String GenerandoPedido() {
			List<String> productos = Arrays.asList("Hamburguesa","Postre","Hamburguesa y Postre");
			return productos.get(ThreadLocalRandom.current().nextInt(productos.size()));
		}
	}
	
}
