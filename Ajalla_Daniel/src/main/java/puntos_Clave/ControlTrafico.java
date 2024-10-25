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

public class ControlTrafico {
	private static final AtomicInteger idTren = new AtomicInteger(1);
	private static final ScheduledExecutorService trenes = Executors.newScheduledThreadPool(1);
	private static final ExecutorService tren = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(ControlTrafico.class.getName());
	private static final Map<String, Semaphore> red = new ConcurrentHashMap<>();
	private static final Map<String, Semaphore> estacions = new ConcurrentHashMap<>();
	public static void main(String[] args) {
		red.put("plataforma", new Semaphore(3,true));
		red.put("empleado", new Semaphore(6,true));
		estacions.put("E1", new Semaphore(4,true));
		estacions.put("E2", new Semaphore(4,true));
		estacions.put("E3", new Semaphore(4,true));
		trenes.scheduleAtFixedRate(new LlegadaTren(), 0, ThreadLocalRandom.current().nextInt(100,201), TimeUnit.MILLISECONDS);
		trenes.schedule(() -> {trenes.shutdown();tren.shutdown();}, 2, TimeUnit.SECONDS);
	}
	static class LlegadaTren implements Runnable {

		@Override
		public void run() {
			int id = idTren.getAndIncrement();
			String[] estaciones = {"E1","E2","E3"};
			String estacion = estaciones[ThreadLocalRandom.current().nextInt(estaciones.length)];
			System.out.println("Tren "+id+" - llegó al estacion "+estacion+".");
			tren.submit(() -> {
				Etapa(new String[] {"plataforma","empleado"},id,300,new int[] {1,2},1,estacion);
				Etapa(new String[] {"plataforma","empleado"},id,400,new int[] {1,2},2,estacion);
				Etapa(new String[] {"plataforma","empleado"},id,200,new int[] {1,1},3,estacion);
			});
		}

		private void Etapa(String[] herramienta, int id, int tiempo, int[] cantidad, int numFase, String estacion) {
			String[] recurso = herramienta;
			int[] producto = cantidad;
			try {
				estacions.get(estacion).acquire();
				ObtenerR(recurso, producto,id);
				System.out.println("Tren "+id+" - inicia Fase "+numFase+".");
				Thread.sleep(tiempo);
				System.out.println("Tren "+id+" - completa Fase "+numFase+".");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Tren "+id+" - fue interrumpido durante la Fase "+numFase+".", e);
				Thread.currentThread().interrupt();
			}finally {
				estacions.get(estacion).release();
				DevolverR(recurso, producto);
			}
		}

		private void DevolverR(String[] recurso, int[] producto) {
			for(int i=0; i<recurso.length; i++) {
				red.get(recurso[i]).release(producto[i]);
			}
		}

		private void ObtenerR(String[] recurso, int[] producto, int id) {
			for(int i=0; i<recurso.length; i++) {
				try {
					red.get(recurso[i]).acquire(producto[i]);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Componente "+id+" - fue interrumpido durante la obtencion de recursos.", e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
