package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Delevery {
	private static final Map<String, Semaphore> PRODUCTOS = new ConcurrentHashMap<>();
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final ScheduledExecutorService pedidos = Executors.newScheduledThreadPool(1);
	private static final ExecutorService turnoPedido = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(Ejercicio01_A.class.getName());
	public static void main(String[] args) {
		PRODUCTOS.put("MD", new Semaphore(1,true));
		PRODUCTOS.put("P", new Semaphore(1,true));
		pedidos.schedule(new LlegadaPedido(), 0, TimeUnit.MILLISECONDS);
		pedidos.schedule(() -> {pedidos.shutdown();turnoPedido.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaPedido implements Runnable {

		@Override
		public void run() {
			int idPedido = id.getAndIncrement();
			int eleccion = ThreadLocalRandom.current().nextInt(3);
			GenerandoPedido(eleccion, idPedido);
			int tiempo = ThreadLocalRandom.current().nextInt(150,201);
			pedidos.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void GenerandoPedido(int eleccion, int idPedido) {
			String[] productos = {"MD","P","MD y P"};
			System.out.println("Llegó un nuevo pedido: "+productos[eleccion]+".");
			if(eleccion == 2) {
				turnoPedido.submit(() -> ProcesandoPedido(idPedido, productos[0]));
				turnoPedido.submit(() -> ProcesandoPedido(idPedido, productos[1]));
			} else {
				turnoPedido.submit(() -> ProcesandoPedido(idPedido, productos[eleccion]));
			}
		}

		private static void ProcesandoPedido(int idPedido, String pedido) {
			try {
				ObtenerRecursos(pedido);
				Thread.sleep(ThreadLocalRandom.current().nextInt(100,301));
				System.out.println("Empresa - produce un '"+pedido+"'.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Personal delevery "+idPedido+" - fue interrumpido durante el proceso del pedido.", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error inesperado durante el proceso del pedido "+pedido+".", e);
			} finally {
				DevolverRecursos(pedido);
			}
		}
		private static void ObtenerRecursos(String recurso) {
			try {
				PRODUCTOS.get(recurso).acquire();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		private static void DevolverRecursos(String recurso) {
			PRODUCTOS.get(recurso).release();
		}
	}
}
