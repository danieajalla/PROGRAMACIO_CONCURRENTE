package practica;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Banco {
	private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final AtomicInteger idCliente = new AtomicInteger(1);
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaCliente(), 0, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 5, TimeUnit.MINUTES);
	}
	static class LlegadaCliente implements Runnable {

		@Override
		public void run() {
			int id = idCliente.getAndIncrement();
			int numerotransaccion = ThreadLocalRandom.current().nextInt(1,4);
			String transaccion = GenerandoTransaccion(numerotransaccion);
			System.out.println("Cliente "+id+" llegó y solicitó "+transaccion+".");
			executor.submit(new Cajero(id,transaccion));
			int tiempo = ThreadLocalRandom.current().nextInt(100,301);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private String GenerandoTransaccion(int numerotransaccion) {
			List<String> transaccion = Arrays.asList("Depósito", "Retiro", "Consulta de Saldo");
			return transaccion.get(ThreadLocalRandom.current().nextInt(transaccion.size()));
		}
	}
	static class Cajero implements Runnable {
		private final int id;
		private final String transaccion;
		public Cajero(int id, String transaccion) {
			this.id = id;
			this.transaccion = transaccion;
		}

		@Override
		public void run() {
			try {
                int min, max;
                switch (transaccion) {
                    case "Depósito":
                        min = 500;
                        max = 1000;
                        break;
                    case "Retiro":
                        min = 700;
                        max = 1200;
                        break;
                    case "Consulta de Saldo":
                        min = 300;
                        max = 700;
                        break;
                    default:
                        throw new IllegalStateException("Transaccion desconocido: " + transaccion);
                }
                System.out.println("Cajero Procesando " + transaccion + " para Cliente " + id + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(min, max + 1));
                System.out.println("Cliente " + id + " completó "+transaccion+".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
		}
	}
}
