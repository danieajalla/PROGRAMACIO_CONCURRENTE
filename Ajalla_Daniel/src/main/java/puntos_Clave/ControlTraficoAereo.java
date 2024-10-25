package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlTraficoAereo {
    private static final Map<String, Semaphore> BOLETERIAS = new ConcurrentHashMap<>();
    private static final Semaphore PISTAS_ATERRIZAJE = new Semaphore(2, true);
    private static final Semaphore PISTAS_DESPEGUE = new Semaphore(3, true);
    private static final AtomicInteger idPasajero = new AtomicInteger(1);
    private static final AtomicInteger idAvion = new AtomicInteger(1);
    private static final ScheduledExecutorService taxis = Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService aviones = Executors.newScheduledThreadPool(1);
    private static final ExecutorService checkin = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(ControlTraficoAereo.class.getName());

    public static void main(String[] args) {
        BOLETERIAS.put("T1", new Semaphore(5, true));
        BOLETERIAS.put("T2", new Semaphore(6, true));
        BOLETERIAS.put("T3", new Semaphore(4, true));

        taxis.scheduleAtFixedRate(new LlegadaTaxi(), 0, ThreadLocalRandom.current().nextInt(100, 151), TimeUnit.MILLISECONDS);
        aviones.scheduleAtFixedRate(new LlegadaAvion(), 0, ThreadLocalRandom.current().nextInt(200, 301), TimeUnit.MILLISECONDS);
        aviones.scheduleAtFixedRate(new DespegueAvion(), 0, ThreadLocalRandom.current().nextInt(300, 401), TimeUnit.MILLISECONDS);
        
        taxis.schedule(() -> {
            taxis.shutdown();
            aviones.shutdown();
            checkin.shutdown();
        }, 2, TimeUnit.MINUTES);
    }

    static class LlegadaTaxi implements Runnable {
        @Override
        public void run() {
            int numPasajeros = ThreadLocalRandom.current().nextInt(1, 5);
            String[] terminales = {"T1", "T2", "T3"};
            String terminal = terminales[ThreadLocalRandom.current().nextInt(terminales.length)];

            for (int i = 0; i < numPasajeros; i++) {
                int id = idPasajero.getAndIncrement();
                System.out.println("Pasajero " + id + " llegó a la terminal " + terminal + ".");
                checkin.submit(() -> realizarCheckIn(id, terminal));
            }
        }
    }

    private static void realizarCheckIn(int id, String terminal) {
        try {
            BOLETERIAS.get(terminal).acquire();
            System.out.println("Pasajero " + id + " - inicia check-in en la terminal " + terminal + ".");
            Thread.sleep(ThreadLocalRandom.current().nextInt(150, 251));
            System.out.println("Pasajero " + id + " - finaliza check-in en la terminal " + terminal + ".");
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Pasajero " + id + " fue interrumpido durante el check-in en la terminal " + terminal + ".", e);
            Thread.currentThread().interrupt();
        } finally {
            BOLETERIAS.get(terminal).release();
        }
    }

    static class LlegadaAvion implements Runnable {
        @Override
        public void run() {
            int id = idAvion.getAndIncrement();
            try {
                PISTAS_ATERRIZAJE.acquire();
                System.out.println("Avión " + id + " - inicia aterrizaje.");
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 301));
                System.out.println("Avión " + id + " - completó aterrizaje.");
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Avión " + id + " fue interrumpido durante el aterrizaje.", e);
                Thread.currentThread().interrupt();
            } finally {
                PISTAS_ATERRIZAJE.release();
            }
        }
    }

    static class DespegueAvion implements Runnable {
        @Override
        public void run() {
            int id = idAvion.getAndIncrement();
            String[] terminales = {"T1", "T2", "T3"};
            String terminal = terminales[ThreadLocalRandom.current().nextInt(terminales.length)];
            try {
                PISTAS_DESPEGUE.acquire();
                System.out.println("Avión " + id + " - inicia despegue desde la terminal " + terminal + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(300, 401));
                System.out.println("Avión " + id + " - completó despegue desde la terminal " + terminal + ".");
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Avión " + id + " fue interrumpido durante el despegue desde la terminal " + terminal + ".", e);
                Thread.currentThread().interrupt();
            } finally {
                PISTAS_DESPEGUE.release();
            }
        }
    }
}
