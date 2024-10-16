package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Estadio {
	private static final Semaphore sala1 = new Semaphore(6,true);
	private static final Semaphore sala2 = new Semaphore(5,true);
	private static final Semaphore sala3 = new Semaphore(6,true);
	private static final Semaphore pasillo1 = new Semaphore(5,true);
	private static final Semaphore pasillo2 = new Semaphore(5,true);
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	public static void main(String[] args) {
		 for (int i = 1; i <= 100; i++) {
	            executor.submit(new Deportista(i));
	        }

	        executor.shutdown();
	}
	static class Deportista implements Runnable {
		private final int id;
		public Deportista(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			try {
				sala1.acquire();
				realizarTest("SALA 1");
				pasillo1.acquire();
				System.out.println("Persona " + id + "paso al pasillo 1");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				sala1.release();
			}
			try {
				sala2.acquire();
				pasillo1.release();
				realizarTest("SALA 2");
				pasillo2.acquire();
				System.out.println("Persona " + id + "paso al pasillo 2");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				sala2.release();
			}
			try {
				sala3.acquire();
				pasillo2.release();
				realizarTest("SALA 3");
				System.out.println("Persona " + id + "Termino de hacer los Test, ingreso al Estadio.");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				sala3.release();
			}
		}
		private void realizarTest(String nombreSala) throws InterruptedException {
            System.out.println("Persona " + id + " realizando test en " + nombreSala + ".");
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
        }
	}
}