package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Banco_2 {
	private static final int numeroClientes = 30;
	private static final ExecutorService executor = Executors.newFixedThreadPool(4);
	public static void main(String[] args) {
		System.out.println("Simulación del Banco ha comenzado.");
		for(int i=1; i<=numeroClientes; i++) {
			System.out.println("Cliente "+i+" llegó al Banco.");
			executor.submit(new Transaccion(i));
		}
		executor.shutdown();
		try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Simulación del Banco ha finalizado.");
	}
	static class Transaccion implements Runnable {
		private final int idCliente;
		public Transaccion(int i) {
			this.idCliente = i;
		}

		@Override
		public void run() {
			int transaccion = ThreadLocalRandom.current().nextInt(1,4);
			String realizar = (transaccion==1)? "Depósito":(transaccion==2)? "Retiro":"Consulta de Saldo";
			try {
				System.out.println("Cliente "+idCliente+" comienza a realizar: "+realizar+".");
				Thread.sleep(ThreadLocalRandom.current().nextInt(1,4)*1000);
				System.out.println("Cajero finaliza \""+realizar+"\" del Cliente "+idCliente+".");
			} catch (InterruptedException e) {
				System.out.println("Cliente "+idCliente+" fue interrupido antes de finalizar el "+realizar+".");
				Thread.currentThread().interrupt();
			}
		}
	}
}