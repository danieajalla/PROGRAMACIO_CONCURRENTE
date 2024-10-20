package tp_08;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Inciso03 {
    private static final int NUM_CAJAS = 3;
    private static final int NUM_CLIENTES = 50;
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUM_CAJAS);

    public static void main(String[] args) {
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            final int clienteId = i;
            executor.submit(() -> atenderCliente(clienteId));
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
    }

    private static void atenderCliente(int clienteId) {
        try {
            int tiempoAtencion = ThreadLocalRandom.current().nextInt(1, 4) * 1000;
            System.out.println("Cliente " + clienteId + " está siendo atendido.");
            Thread.sleep(tiempoAtencion);
            System.out.println("Cliente " + clienteId + " finaliza de ser atendido (duración: " + (tiempoAtencion / 1000) + " segundos).");
        } catch (InterruptedException e) {
            System.out.println("Cliente " + clienteId + " fue interrumpido mientras era atendido.");
            Thread.currentThread().interrupt();
        }
    }
}