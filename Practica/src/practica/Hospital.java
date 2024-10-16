package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Hospital {
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ExecutorService medicos = Executors.newFixedThreadPool(5);
    private static final PriorityBlockingQueue<Paciente> pacientes = new PriorityBlockingQueue<>();
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaPaciente(), 0, TimeUnit.MILLISECONDS);
		for (int i = 0; i < 5; i++) {
			medicos.submit(new Medico(i));
	    }
		scheduler.schedule(() -> {
            scheduler.shutdown();
            medicos.shutdown();
        }, 1, TimeUnit.MINUTES);
	}
	static class Medico implements Runnable {
        private final int id;
        public Medico(int id) {
            this.id = id;
        }

        @Override
        public void run() {
        	while(!pacientes.isEmpty()) {
        		Paciente paciente = pacientes.poll();
                if (paciente != null) {
                    System.out.println("Medico " + id +" atendiendo a " + paciente);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
        	}
        }
    }
	static class LlegadaPaciente implements Runnable {

		@Override
		public void run() {
			int severidad = ThreadLocalRandom.current().nextInt(1,4);
			String leccion = GenerarPaciente(severidad);
			pacientes.add(new Paciente(leccion,severidad));
			int tiempo = ThreadLocalRandom.current().nextInt(50,151);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private String GenerarPaciente(int severidad) {
			return (severidad == 1)? "Grave":(severidad == 2)? "Moderada":"Leve";
		}
	}
	static class Paciente implements Comparable<Paciente> {

		private int prioridad;
	    private String leccion;
	    public Paciente(String leccion, int prioridad) {
	        this.prioridad = prioridad;
	        this.leccion = leccion;
	    }

	    public int getPrioridad() {
	        return prioridad;
	    }

	    @Override
	    public int compareTo(Paciente otro) {
	        return Integer.compare(this.prioridad, otro.prioridad);
	    }
	    
	    @Override
		public String toString() {
			return "Paciente [prioridad = " + prioridad + ", leccion = " + leccion + "]";
		}
	}
}