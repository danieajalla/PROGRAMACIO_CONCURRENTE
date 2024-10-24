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


public class Ejercicio02_A {
	private static final Semaphore MD = new Semaphore(1,true);
	private static final Semaphore P = new Semaphore(1,true);
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final ScheduledExecutorService pedidos = Executors.newScheduledThreadPool(1);
	private static final ExecutorService turnoPedido = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(Ejercicio01_A.class.getName());
	public static void main(String[] args) {
		pedidos.schedule(new LlegadaPedido(), 0, TimeUnit.MILLISECONDS);
		pedidos.schedule(() -> {pedidos.shutdown();turnoPedido.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaPedido implements Runnable {

		@Override
		public void run() {
			int idPedido = id.getAndIncrement();
			String[] productos = {"MD","P","MD y P"};
			String pedido = productos[ThreadLocalRandom.current().nextInt(productos.length)];
			System.out.println("Llegó un nuevo pedido: "+pedido+".");
			turnoPedido.submit(() -> ProcesandoPedido(idPedido, pedido));
			int tiempo = ThreadLocalRandom.current().nextInt(150,201);
			pedidos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoPedido(int idPedido, String pedido) {
			if(pedido.contains("MD")) {
				try {
					if(!MD.tryAcquire()) {
						System.out.println("Personal delevery "+idPedido+" - espera pedido: Hamburguesa 'MD'.");
						MD.acquire();
					}
					Thread.sleep(ThreadLocalRandom.current().nextInt(100,301));
					System.out.println("Empresa(MD) - produce una Hamburguesa 'MD'.");
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Personal delevery "+idPedido+" - fue interrumpido durante el proceso del pedido.", e);
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error inesperado durante el proceso del pedido "+idPedido+".", e);
				}finally {
					MD.release();
				}
			}
			if(pedido.contains("P")) {
				try {
					if(!P.tryAcquire()) {
						System.out.println("Personal delevery "+idPedido+" - espera pedido: Postre 'P'.");
						P.acquire();
					}
					Thread.sleep(ThreadLocalRandom.current().nextInt(100,301));
					System.out.println("Empresa(P) - produce un Postre 'P'.");
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Personal delevery "+idPedido+" - fue interrumpido durante el proceso del pedido.", e);
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error inesperado durante el proceso del pedido "+idPedido+".", e);
				}finally {
					P.release();
				}
			}
		}
	}
}