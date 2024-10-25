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

public class Automotriz {
	private static final Logger logger = Logger.getLogger(Automotriz.class.getName());
	private static final Map<String, Semaphore> HERRAMIENTAS = new ConcurrentHashMap<>();
	private static final ScheduledExecutorService automovil = Executors.newScheduledThreadPool(1);
	private static final ExecutorService movil = Executors.newFixedThreadPool(5);
	private static final AtomicInteger idMovil = new AtomicInteger(1);
	private static final int T = 400;
	public static void main(String[] args) {
		HERRAMIENTAS.put("motor", new Semaphore(5,true));
		HERRAMIENTAS.put("chasis", new Semaphore(8,true));
		HERRAMIENTAS.put("puerta", new Semaphore(10,true));
		HERRAMIENTAS.put("rueda", new Semaphore(20,true));
		HERRAMIENTAS.put("ventana", new Semaphore(10,true));
		HERRAMIENTAS.put("bateria", new Semaphore(5,true));
		HERRAMIENTAS.put("asiento", new Semaphore(20,true));
		HERRAMIENTAS.put("volante", new Semaphore(5,true));
		automovil.scheduleAtFixedRate(new LlegadaMovil(), 0, ThreadLocalRandom.current().nextInt(100, 151), TimeUnit.MILLISECONDS);
		automovil.schedule(() -> {automovil.shutdown();movil.shutdown();}, 2, TimeUnit.SECONDS);
	}
	static class LlegadaMovil implements Runnable {

		@Override
		public void run() {
			int id = idMovil.getAndIncrement();
			System.out.println("Componente "+id+" - llegó a la planta.");
			movil.submit(() -> {
				Ensamblado(new String[] {"motor","chasis"},id,T,new int[] {1,2},1);
				Ensamblado(new String[] {"puerta","rueda"},id,3*T/2,new int[] {3,4},2);
				Ensamblado(new String[] {"ventana","bateria"},id,2*T,new int[] {2,1},3);
				Ensamblado(new String[] {"asiento","volante"},id,5*T/2,new int[] {4,1},4);
			});
		}

		private void Ensamblado(String[] herramienta, int id, int tiempo, int[] cantidad, int numFase) {
			String[] herramients = herramienta;
			int[] control = cantidad;
			try {
				ObtenerR(herramients,control,id);
				System.out.println("Componente "+id+" - inicia Fase "+numFase+".");
				Thread.sleep(tiempo);
				System.out.println("Componente "+id+" - completó Fase "+numFase+".");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Componente "+id+" - fue interrumpido durante la Fase "+numFase+".", e);
				Thread.currentThread().interrupt();
			}finally {
				DevolverR(herramients,control);
			}
		}

		private void ObtenerR(String[] herramients, int[] control, int id) {
			for(int i=0; i<control.length; i++) {
				try {
					HERRAMIENTAS.get(herramients[i]).acquire(control[i]);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Componente "+id+" - fue interrumpido durante la obtencion de recursos.", e);
					Thread.currentThread().interrupt();
				}
			}
		}

		private void DevolverR(String[] herramients, int[] control) {
			for(int i=0; i<control.length; i++) {
				HERRAMIENTAS.get(herramients[i]).release(control[i]);
			}
		}
	}
}
