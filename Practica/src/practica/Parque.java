package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Parque {
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ExecutorService atracciones = Executors.newFixedThreadPool(3); 
    private static final AtomicInteger idVisitante = new AtomicInteger(1);
    private static final Semaphore montañaRusa = new Semaphore(5, true);
    private static final Semaphore tirolesa = new Semaphore(3, true);
    private static final Semaphore casaDelTerror = new Semaphore(4, true);

    public static void main(String[] args) {
        scheduler.scheduleAtFixedRate(new LlegadaVisitante(), 0, 100, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            atracciones.shutdown();
        }, 1, TimeUnit.MINUTES);
    }

    static class LlegadaVisitante implements Runnable {
        @Override
        public void run() {
            int id = idVisitante.getAndIncrement();
            String[] atraccionesDisponibles = {"Montaña Rusa", "Tirolesa", "Casa del Terror"};
            String atraccionElegida = atraccionesDisponibles[ThreadLocalRandom.current().nextInt(atraccionesDisponibles.length)];

            System.out.println("Visitante " + id + " llegó y eligió la " + atraccionElegida + ".");
            atracciones.submit(new Visitante(id, atraccionElegida));
        }
    }

    static class Visitante implements Runnable {
        private final int id;
        private final String atraccion;

        public Visitante(int id, String atraccion) {
            this.id = id;
            this.atraccion = atraccion;
        }

        @Override
        public void run() {
            try {
                Semaphore semaforoAtraccion;
                int tiempoMin, tiempoMax;

                switch (atraccion) {
                    case "Montaña Rusa":
                        semaforoAtraccion = montañaRusa;
                        tiempoMin = 1000;
                        tiempoMax = 2000;
                        break;
                    case "Tirolesa":
                        semaforoAtraccion = tirolesa;
                        tiempoMin = 500;
                        tiempoMax = 1000;
                        break;
                    case "Casa del Terror":
                        semaforoAtraccion = casaDelTerror;
                        tiempoMin = 800;
                        tiempoMax = 1500;
                        break;
                    default:
                        throw new IllegalStateException("Atracción desconocida: " + atraccion);
                }

                semaforoAtraccion.acquire();
                System.out.println("Visitante " + id + " está en la " + atraccion + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(tiempoMin, tiempoMax + 1));
                System.out.println("Visitante " + id + " terminó la " + atraccion + " y salió.");
                semaforoAtraccion.release();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
