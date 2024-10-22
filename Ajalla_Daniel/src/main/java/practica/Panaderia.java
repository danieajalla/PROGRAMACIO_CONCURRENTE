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


public class Panaderia {
	private static final Semaphore PN = new Semaphore(1,true);
	private static final Semaphore PS = new Semaphore(1,true);
	private static final Logger logger = Logger.getLogger(Estacionamiento.class.getName());
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final ScheduledExecutorService pedidos = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		pedidos.schedule(new LlegadaPedidos(), 0, TimeUnit.MILLISECONDS);
		pedidos.schedule(() -> {pedidos.shutdown();executor.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaPedidos implements Runnable {

		@Override
		public void run() {
			int idPersona = id.getAndIncrement();
			String[] producto = {"PN","PS","PN y PS"};
			String pedido = producto[ThreadLocalRandom.current().nextInt(producto.length)];
			System.out.println("Clente "+idPersona+" llegó y pidió: "+pedido+".");
			executor.submit(() -> ProcesandoProducto(idPersona, pedido));
			int tiempo = ThreadLocalRandom.current().nextInt(120,221);
			pedidos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoProducto(int idPersona, String pedido) {
	    	if (pedido.contains("PN")) {
	        	try {
		            if (!PN.tryAcquire()) {
		                System.out.println("Cliente " + idPersona + " - espera su pedido pan 'PN'.");
		                PN.acquire();
		            }
		            Thread.sleep(ThreadLocalRandom.current().nextInt(80, 201));
		            System.out.println("PN - produce un nuevo 'PN'.");
	        	} catch (InterruptedException e) {
	 		        logger.log(Level.SEVERE, "Interrupción durante el proceso del pedido de pan " + idPersona, e);
	 		        Thread.currentThread().interrupt();
	        	} catch (Exception e) {
	 		        logger.log(Level.SEVERE,"Error inesperado en el proceso del pedido de pan " + idPersona + ".", e);
	 		    }finally {
	 		    	PN.release();
	 		    }
	        }

	        if (pedido.contains("PS")) {
	        	try {
		            if (!PS.tryAcquire()) {
		                System.out.println("Cliente " + idPersona + " - espera su pedido pan 'PS'.");
		                PS.acquire();
		            }
		            Thread.sleep(ThreadLocalRandom.current().nextInt(80, 201));
		            System.out.println("PS - produce un nuevo 'PS'.");
	        	} catch (InterruptedException e) {
	 		        logger.log(Level.SEVERE, "Interrupción durante el proceso del pedido de pastel " + idPersona, e);
	 		        Thread.currentThread().interrupt();
	        	} catch (Exception e) {
	 		        logger.log(Level.SEVERE,"Error inesperado en el proceso del pedido de pastel " + idPersona + ".", e);
	 		    }finally {
	 		    	PS.release();
	 		    }
	        }
	        System.out.println("Pedido: "+pedido+" - completado del Cliente "+idPersona+".");
		}
	}
}
