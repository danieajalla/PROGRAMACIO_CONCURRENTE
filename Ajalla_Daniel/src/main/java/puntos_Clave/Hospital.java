package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Hospital {
	private static final AtomicInteger idPacientes = new AtomicInteger(1);
	private static final Map<String, Semaphore> AREAS = new ConcurrentHashMap<>();
	private static final Map<String, int[]> TIEMPOS = new ConcurrentHashMap<>();
	private static final PriorityBlockingQueue<Paciente> PRIORIDAD = new PriorityBlockingQueue<>();
	private static final Logger logger = Logger.getLogger(Hospital.class.getName());
	private static final ScheduledExecutorService pacientes = Executors.newScheduledThreadPool(1);
	private static final ExecutorService turno = Executors.newCachedThreadPool();
	public static void main(String[] args) {
		AREAS.put("Área A", new Semaphore(3,true));
		AREAS.put("Área B", new Semaphore(4,true));
		AREAS.put("Área C", new Semaphore(2,true));
		TIEMPOS.put("Área A", new int[] {200,400});
		TIEMPOS.put("Área B", new int[] {300,500});
		TIEMPOS.put("Área C", new int[] {100,300});
		pacientes.scheduleAtFixedRate(new LlegadaPaciente(), 0, ThreadLocalRandom.current().nextInt(100, 201), TimeUnit.MILLISECONDS);
		pacientes.schedule(() -> {
            pacientes.shutdown();
        }, 2, TimeUnit.MINUTES);
	}
	static class LlegadaPaciente implements Runnable {

		@Override
		public void run() {
			int id = idPacientes.getAndIncrement();
			String[] area = {"Área A","Área B","Área C"};
			String sector = area[ThreadLocalRandom.current().nextInt(area.length)];
			int prioridad = ThreadLocalRandom.current().nextInt(1,4);
			PRIORIDAD.add(new Paciente(id,sector,prioridad));
			turno.submit(() -> ProcesandoPaciente());
		}

		private void ProcesandoPaciente() {
			Paciente paciente = PRIORIDAD.poll();
			int id = paciente.getId();
			String sector = paciente.getSector();
			int prioridad = paciente.getPrioridad();
			int[] tiempo = TIEMPOS.get(sector);
			try {
				AREAS.get(sector).acquire();
				System.out.println("Paciente "+id+", Prioridad: "+prioridad+", "+sector+" - inicia tratamiento.");
				Thread.sleep(ThreadLocalRandom.current().nextInt(tiempo[0],tiempo[1]+1));
				System.out.println("Paciente "+id+", Prioridad: "+prioridad+", "+sector+" - finaliza tratamiento.");
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Paciente "+id+", Prioridad: "+prioridad+", "+sector+" - fue interrumpido durante el tratamiento.", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error inesperado durante el tratamiento del Paciente "+id+".", e);
			}finally {
				AREAS.get(sector).release();
			}
		}
	}
	static class Paciente implements Comparable<Paciente>{
		private final int id;
		private final String sector;
		private final int prioridad;
		public Paciente(int id, String sector, int prioridad) {
			this.id = id;
			this.sector = sector;
			this.prioridad = prioridad;
		}

		@Override
		public int compareTo(Paciente otro) {
			return Integer.compare(this.prioridad, otro.prioridad);
		}

		public int getId() {
			return id;
		}

		public String getSector() {
			return sector;
		}

		public int getPrioridad() {
			return prioridad;
		}
	}
}
