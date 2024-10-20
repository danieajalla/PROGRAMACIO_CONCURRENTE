package practica;
import java.util.concurrent.*;

public class RestauranteAltaGama {
    private static final int T = 300; // Tiempo base en milisegundos
    private static final int NUM_PLATOS = 100;
    private static final Semaphore cuchillos = new Semaphore(3, true);
    private static final Semaphore tablas = new Semaphore(4, true);
    private static final Semaphore sartenes = new Semaphore(5, true);
    private static final Semaphore espatulas = new Semaphore(2, true);
    private static final Semaphore platos = new Semaphore(4, true);
    private static final ExecutorService estaciones = Executors.newFixedThreadPool(3); // 3 estaciones de trabajo

    public static void main(String[] args) {
        for (int i = 1; i <= NUM_PLATOS; i++) {
            int idPlato = i;
            estaciones.submit(() -> {
                try {
                    ejecutarFase("Preparación", idPlato, T, () -> FasePreparacion(idPlato));
                    ejecutarFase("Cocción", idPlato, 2 * T, () -> FaseCoccion(idPlato));
                    ejecutarFase("Decoración", idPlato, T / 2, () -> FaseDecoracion(idPlato));
                    ejecutarFase("Servido", idPlato, T, () -> FaseServido(idPlato));
                } catch (InterruptedException e) {
                    System.out.println("Plato " + idPlato + " fue interrumpido durante el Proceso.");
                    Thread.currentThread().interrupt();
                }
            });
        }
        estaciones.shutdown();
    }

    private static void ejecutarFase(String nombreFase, int idPlato, int tiempo, Runnable fase) throws InterruptedException {
        System.out.println("Plato " + idPlato + " inicia " + nombreFase + ".");
        fase.run();
        Thread.sleep(tiempo);
        System.out.println("Plato " + idPlato + " completa " + nombreFase + ".");
    }

    private static void FasePreparacion(int idPlato) {
        try {
            cuchillos.acquire();
            tablas.acquire();
            System.out.println("Plato " + idPlato + " en Fase de Preparación.");
        } catch (InterruptedException e) {
            System.out.println("Plato " + idPlato + " fue interrumpido durante la Fase de Preparación.");
            Thread.currentThread().interrupt();
        } finally {
            cuchillos.release();
            tablas.release();
        }
    }

    private static void FaseCoccion(int idPlato) {
        try {
            sartenes.acquire();
            System.out.println("Plato " + idPlato + " en Fase de Cocción.");
        } catch (InterruptedException e) {
            System.out.println("Plato " + idPlato + " fue interrumpido durante la Fase de Cocción.");
            Thread.currentThread().interrupt();
        } finally {
            sartenes.release();
        }
    }

    private static void FaseDecoracion(int idPlato) {
        try {
            espatulas.acquire();
            System.out.println("Plato " + idPlato + " en Fase de Decoración.");
        } catch (InterruptedException e) {
            System.out.println("Plato " + idPlato + " fue interrumpido durante la Fase de Decoración.");
            Thread.currentThread().interrupt();
        } finally {
            espatulas.release();
        }
    }

    private static void FaseServido(int idPlato) {
        try {
            platos.acquire();
            System.out.println("Plato " + idPlato + " en Fase de Servido.");
        } catch (InterruptedException e) {
            System.out.println("Plato " + idPlato + " fue interrumpido durante la Fase de Servido.");
            Thread.currentThread().interrupt();
        } finally {
            platos.release();
            System.out.println("Plato " + idPlato + " está listo para servir.");
        }
    }
}