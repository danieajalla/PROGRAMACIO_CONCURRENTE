package practica;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Biblioteca_2 {
	private static final ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final AtomicInteger idLector = new AtomicInteger(1);
    private static final ConcurrentHashMap<String, Semaphore> libros = new ConcurrentHashMap<>();

	public static void main(String[] args) {
		libros.put("Libro_A", new Semaphore(1,true));
		libros.put("Libro_B", new Semaphore(1,true));
		libros.put("Libro_C", new Semaphore(1,true));
		libros.put("Libro_D", new Semaphore(1,true));
		libros.put("Libro_E", new Semaphore(1,true));
		scheduler.schedule(new LlegadaLectores(), 0, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 5, TimeUnit.MINUTES);
		
	}
	static class LlegadaLectores implements Runnable {

		@Override
		public void run() {
			int idlector = idLector.getAndIncrement();
			String libro = GenerandoLibro();
			System.out.println("Lector "+idlector+" llegó.");
			executor.submit(new Lector(idlector,libro));
			int tiempo = ThreadLocalRandom.current().nextInt(100,251);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private String GenerandoLibro() {
			List<String> libro = Arrays.asList("Libro_A", "Libro_B", "Libro_C", "Libro_D", "Libro_E");
			return libro.get(ThreadLocalRandom.current().nextInt(libro.size()));
		}
	}
	static class Lector implements Runnable {
		private final int idlector;
		private final String libro;
		public Lector(int idlector, String libro) {
			this.idlector = idlector;
			this.libro = libro;
		}

		@Override
		public void run() {
			try {
				System.out.println("Lector "+idlector+" asignado al libro: " + libro);
                Semaphore ocupacion = libros.get(libro);
                if (ocupacion.tryAcquire()) {
                	System.out.println("Lector " + idlector + " comienza a leer el Libro \""+libro+"\".");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1001)); 
                    System.out.println("Lector " + idlector + " finaliza lectura del Libro \""+libro+"\".");
                    ocupacion.release();
                } else {
                    System.out.println("Libro \"" + libro + "\" no disponible, Lector "+idlector+" espera.");
                    ocupacion.acquire();
                    System.out.println("Lector " + idlector + " comienza a leer el Libro \""+libro+"\".");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1001)); 
                    System.out.println("Lector " + idlector + " finaliza lectura del Libro \""+libro+"\".");
                    ocupacion.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
		}
	}
}