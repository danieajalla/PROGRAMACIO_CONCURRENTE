package puntos_Clave;

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

public class Emergencias {
	private static final AtomicInteger id = new AtomicInteger(1);
	private static final Semaphore medico = new Semaphore(3,true);
	private static final ScheduledExecutorService pacientes = Executors.newScheduledThreadPool(1);
	private static final ExecutorService turnoAtencion = Executors.newCachedThreadPool();
	private static final Logger logger = Logger.getLogger(Emergencias.class.getName());
	private static final PriorityBlockingQueue<Paciente> colaPacientes = new PriorityBlockingQueue<>();
	public static void main(String[] args) {
		pacientes.schedule(new LlegadaPaciente(), 0, TimeUnit.MILLISECONDS);
		pacientes.schedule(() -> {pacientes.shutdown(); turnoAtencion.shutdown();}, 2, TimeUnit.MINUTES);
	}
	static class LlegadaPaciente implements Runnable {

		@Override
		public void run() {
			int idPaciente = id.getAndIncrement();
			int severe = ThreadLocalRandom.current().nextInt(1,4);
			colaPacientes.add(new Paciente(idPaciente,severe));
			turnoAtencion.submit(() -> ProcesandoPaciente());
			int tiempo = ThreadLocalRandom.current().nextInt(50,101);
			pacientes.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoPaciente() {
			Paciente paciente = colaPacientes.poll();
			try {
				if(!medico.tryAcquire()) {
					medico.acquire();
				}
				System.out.println("Paciente "+paciente.getId()+" - esta siendo atendido.");
				Thread.sleep(ThreadLocalRandom.current().nextInt(150,301));
				System.out.println("Paciente "+paciente.getId()+" - finaliza atencion medico.");
			}  catch (InterruptedException e) {
				logger.log(Level.SEVERE, "Paciente "+paciente.getId()+" - fue interrumpido durante el proceso de atencion.", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error inesperado durante el proceso de atencion del Paciente "+paciente.getId()+".", e);
			}finally {
				medico.release();
			}
		}
	}
	static class Paciente implements Comparable<Paciente> {
	    private int prioridad;
	    private int id;
		public Paciente(int id,int prioridad) {
	    	this.id = id;
	        this.prioridad = prioridad;
	    }

	    public int getPrioridad() {
	        return prioridad;
	    }
	    public int getId() {
			return id;
		}

	    @Override
	    public int compareTo(Paciente otro) {
	        return Integer.compare(this.prioridad, otro.prioridad);
	    }
	}


}
