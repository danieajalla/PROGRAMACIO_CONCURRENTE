package practica;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Aeropuerto_Internacional {
	private static final AtomicInteger atomic = new AtomicInteger(1);
	private static final ExecutorService terminal = Executors.newCachedThreadPool();
	private static final ExecutorService terminalT3 = Executors.newFixedThreadPool(4);
	private static final ExecutorService terminalT4 = Executors.newFixedThreadPool(5);
	private static final ExecutorService terminalT5 = Executors.newFixedThreadPool(3);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaTaxi(), 0, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            terminal.shutdown();
        }, 20, TimeUnit.SECONDS);
	}
	static class LlegadaTaxi implements Runnable {

		@Override
		public void run() {
			int numeroPasajeros = ThreadLocalRandom.current().nextInt(1,5);
			String destino = GenerandoDestino();
			terminal.submit(() -> ProcesandoDestino(numeroPasajeros,destino));
			int tiempo = ThreadLocalRandom.current().nextInt(120,181);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private void ProcesandoDestino(int id, String destino) {
			switch(destino) {
			case "Terminal T3":
				for(int i=1; i<=id; i++) {
					int idpasajero = atomic.getAndIncrement();
					terminalT3.submit(new Boleteria(idpasajero,destino));
				}
				break;
			case "Terminal T4":
				for(int i=1; i<=id; i++) {
					int idpasajero = atomic.getAndIncrement();
					terminalT4.submit(new Boleteria(idpasajero,destino));
				}
				break;
			case "Terminal T5":
				for(int i=1; i<=id; i++) {
					int idpasajero = atomic.getAndIncrement();
					terminalT5.submit(new Boleteria(idpasajero,destino));
				}
				break;
			}
		}

		private String GenerandoDestino() {
			List<String> destino = Arrays.asList("Terminal T3", "Terminal T4", "Terminal T5");
			return destino.get(ThreadLocalRandom.current().nextInt(destino.size()));
		}
	}
	static class Boleteria implements Runnable {
		private final int pasajeroId;
		private final String destino;
		public Boleteria(int i, String destino) {
			this.pasajeroId = i;
			this.destino = destino;
		}

		@Override
		public void run() {
			 try {
		            System.out.println("Pasajero " + pasajeroId + " llegó a la " + destino);
		            System.out.println("Pasajero " + pasajeroId + " realizando check-in en " + destino);
		            Thread.sleep(ThreadLocalRandom.current().nextInt(180, 281)); 
		            System.out.println("Pasajero " + pasajeroId + " completó el check-in en " + destino);
		     } catch (InterruptedException e) {
		    	 System.out.println("Pasajero " + pasajeroId + " fue interrumpido mientras hacia el check-in en "+destino+".");
		            Thread.currentThread().interrupt();
		     } 
		}
		
	}
}
