package practica;
import java.util.concurrent.*;
import java.util.*;

public class Biblioteca {
    private static final Map<String, Semaphore> libros = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ExecutorService prestamos = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        libros.put("Harry Potter", new Semaphore(3, true));
        libros.put("El Señor de los Anillos", new Semaphore(2, true));
        libros.put("El Hobbit", new Semaphore(4, true));

        scheduler.scheduleAtFixedRate(new LlegadaUsuario(), 0, 50, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            prestamos.shutdown();
        }, 1, TimeUnit.MINUTES);
    }

    static class LlegadaUsuario implements Runnable {
        @Override
        public void run() {
            String[] nombresLibros = {"Harry Potter", "El Señor de los Anillos", "El Hobbit"};
            String libroSolicitado = nombresLibros[ThreadLocalRandom.current().nextInt(nombresLibros.length)];
            prestamos.submit(new Usuario(libroSolicitado));
        }
    }

    static class Usuario implements Runnable {
        private final String libro;

        public Usuario(String libro) {
            this.libro = libro;
        }

        @Override
        public void run() {
            try {
                System.out.println("Usuario solicitó el libro: " + libro);
                Semaphore copias = libros.get(libro);

                if (copias.tryAcquire()) {
                    System.out.println("Libro \"" + libro + "\" prestado al usuario.");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001)); 
                    System.out.println("Libro \"" + libro + "\" devuelto.");
                    copias.release();
                } else {
                    System.out.println("Libro \"" + libro + "\" no disponible, usuario en espera.");
                    copias.acquire();
                    System.out.println("Libro \"" + libro + "\" prestado al usuario después de la espera.");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10001)); 
                    System.out.println("Libro \"" + libro + "\" devuelto.");
                    copias.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}