package practica;
import java.util.concurrent.*;

public class PlantaEnsamblado {
    private static final int T = 400; // Tiempo base para las fases
    private static final int NUM_COMPONENTES = 100;
    private static final Semaphore pinzas = new Semaphore(4, true);
    private static final Semaphore destornilladores = new Semaphore(2, true);
    private static final Semaphore sargentos = new Semaphore(4, true);
    private static final ExecutorService executor = Executors.newFixedThreadPool(3); // 3 mesas

    public static void main(String[] args) {
        for (int i = 1; i <= NUM_COMPONENTES; i++) {
            int idComponente = i;
            executor.submit(() -> {
                try {
                    ensamblarFase1(idComponente);
                    ensamblarFase2(idComponente);
                    ensamblarFase3(idComponente);
                } catch (InterruptedException e) {
                    System.out.println("Componente " + idComponente + " fue interrumpido durante el ensamblaje.");
                    Thread.currentThread().interrupt();
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void ensamblarFase1(int idComponente) throws InterruptedException {
        destornilladores.acquire();
        pinzas.acquire();
        System.out.println("Componente " + idComponente + " inicia Fase 1.");
        Thread.sleep(T);
        System.out.println("Componente " + idComponente + " completa Fase 1.");
        destornilladores.release();
        pinzas.release();
    }

    private static void ensamblarFase2(int idComponente) throws InterruptedException {
        sargentos.acquire(2);
        System.out.println("Componente " + idComponente + " inicia Fase 2.");
        Thread.sleep(T / 2);
        System.out.println("Componente " + idComponente + " completa Fase 2.");
        sargentos.release(2);
    }

    private static void ensamblarFase3(int idComponente) throws InterruptedException {
        pinzas.acquire(2);
        System.out.println("Componente " + idComponente + " inicia Fase 3.");
        Thread.sleep(2 * T);
        System.out.println("Componente " + idComponente + " completa Fase 3.");
        pinzas.release(2);
        System.out.println("Componente " + idComponente + " ensamblado completamente.");
    }
}