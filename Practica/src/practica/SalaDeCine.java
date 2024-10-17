package practica;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SalaDeCine {
    private static final int CAPACIDAD = 50;
    private static final Semaphore asientos = new Semaphore(CAPACIDAD, true);
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final AtomicInteger idEspectadores = new AtomicInteger(1);

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new LlegadaEspectador(), 0, 100, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 5, TimeUnit.MINUTES);
    }

    static class LlegadaEspectador implements Runnable {
        @Override
        public void run() {
            int id = idEspectadores.getAndIncrement();
            System.out.println("Espectador " + id + " llegó.");
            executor.submit(new Espectador(id));
        }
    }

    static class Espectador implements Runnable {
        private final int id;

        public Espectador(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                asientos.acquire();
                int asientoAsignado = CAPACIDAD - asientos.availablePermits();
                System.out.println("Espectador " + id + " asignado al asiento " + asientoAsignado + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 2) * 60 * 60 * 1000); 
                System.out.println("Espectador " + id + " libera el asiento " + asientoAsignado + ".");
                asientos.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
