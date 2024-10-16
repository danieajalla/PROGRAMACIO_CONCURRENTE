package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AeroPuerto {
	private static final ExecutorService executor = Executors.newFixedThreadPool(18);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private static final AtomicInteger atomic = new AtomicInteger(1);
    private static final Semaphore TerminalT3 = new Semaphore(4,true);
    private static final Semaphore TerminalT4 = new Semaphore(5,true);
    private static final Semaphore TerminalT5 = new Semaphore(3,true);
    public static void main(String[] args) {
    	scheduler.schedule(new Taxi(), 0, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 20, TimeUnit.SECONDS);
    }
    static class Taxi implements Runnable {

		@Override
		public void run() {
			int destino = ThreadLocalRandom.current().nextInt(3);
			int numeropasajeros = ThreadLocalRandom.current().nextInt(1,4);
			for(int i=0; i<numeropasajeros; i++) {
				int id = atomic.getAndIncrement();
				executor.submit(() -> ProcesandoPasajero(id, destino));
			}
			int tiempo = ThreadLocalRandom.current().nextInt(120, 181);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}
    }
    private static void ProcesandoPasajero(int pasajeroId, int destino) {
        Semaphore boleteria;
        String terminal;

        switch (destino) {
            case 0:
                boleteria = TerminalT3;
                terminal = "Terminal T3";
                break;
            case 1:
                boleteria = TerminalT4;
                terminal = "Terminal T4";
                break;
            case 2:
                boleteria = TerminalT5;
                terminal = "Terminal T5";
                break;
            default:
                throw new IllegalStateException("Destino desconocido: " + destino);
        }

        try {
            System.out.println("Pasajero " + pasajeroId + " llegó a la terminal " + terminal);
            boleteria.acquire();
            System.out.println("Pasajero " + pasajeroId + " realizando check-in en terminal " + terminal);
            Thread.sleep(ThreadLocalRandom.current().nextInt(180, 281)); 
            System.out.println("Pasajero " + pasajeroId + " completó el check-in en terminal " + terminal);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            boleteria.release();
        }
    }
}
