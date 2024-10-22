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


public class Ejercicio02 {
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final ExecutorService nuevopedido = Executors.newCachedThreadPool();
	private static final Semaphore libroB = new Semaphore(1,true);
	private static final Semaphore libroR = new Semaphore(1,true);
	private static final ScheduledExecutorService pedidos = Executors.newScheduledThreadPool(1);
	private static final Logger logger = Logger.getLogger(Ejercicio02.class.getName());
	public static void main(String[] args) {
		pedidos.schedule(new LlegadaPedidos(), 0, TimeUnit.MILLISECONDS);
		pedidos.schedule(() -> {pedidos.shutdown();nuevopedido.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaPedidos implements Runnable {

		@Override
		public void run() {
			int idLibro = id.getAndIncrement();
			String[] libros = {"B","R","B y R"};
			String pedido = libros[ThreadLocalRandom.current().nextInt(libros.length)];
			System.out.println("Persona "+idLibro+" llegó y realizó un pedido: Libro "+pedido+".");
			nuevopedido.submit(() -> manejarPedido(idLibro,pedido));
			int tiempo = ThreadLocalRandom.current().nextInt(100,201);
			pedidos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
		private void manejarPedido(int idLibro, String pedido) {
	    	if (pedido.contains("B")) {
	        	try {
		            if (!libroB.tryAcquire()) {
		                System.out.println("Persona " + idLibro + " - espera su pedido del Libro B.");
		                libroB.acquire();
		            }
		            Thread.sleep(ThreadLocalRandom.current().nextInt(80, 201));
		            System.out.println("Proveedor - produce un nuevo Libro 'B'.");
	        	} catch (InterruptedException e) {
	 		        logger.log(Level.SEVERE, "Interrupción durante el proceso del pedido para Libro " + idLibro, e);
	 		        Thread.currentThread().interrupt();
	        	} catch (Exception e) {
	 		        logger.log(Level.SEVERE,"Error inesperado en el proceso del pedido de Libro " + idLibro + ".", e);
	 		    }finally {
	 		    	libroB.release();
	 		    }
	        }

	        if (pedido.contains("R")) {
	        	try {
		            if (!libroR.tryAcquire()) {
		                System.out.println("Persona " + idLibro + " - espera su pedido del Libro R.");
		                libroR.acquire();
		            }
		            Thread.sleep(ThreadLocalRandom.current().nextInt(80, 201));
		            System.out.println("Proveedor - produce un nuevo Libro 'R'.");
	        	} catch (InterruptedException e) {
	 		        logger.log(Level.SEVERE, "Interrupción durante el proceso del pedido para Libro " + idLibro, e);
	 		        Thread.currentThread().interrupt();
	        	} catch (Exception e) {
	 		        logger.log(Level.SEVERE,"Error inesperado en el proceso del pedido de Libro " + idLibro + ".", e);
	 		    }finally {
	 		    	libroR.release();
	 		    }
	        }
	        System.out.println("Pedido: libro "+pedido+" - completado Persona "+idLibro+".");
		}
	}
}