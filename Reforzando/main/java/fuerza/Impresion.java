package fuerza;
import java.util.concurrent.Semaphore;

public class Impresion {
    private static final Semaphore semA = new Semaphore(0);
    private static final Semaphore semB = new Semaphore(1);
    private static final Semaphore semC = new Semaphore(0);

    public static void main(String[] args) {
        Thread hiloA = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    semA.acquire();
                    System.out.print("A");
                    semB.release();
                }
                System.out.println();
                for (int i = 0; i < 10; i++) {
                    semB.acquire();
                    System.out.print("A");
                    semC.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread hiloB = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    semB.acquire();
                    System.out.print("B");
                    semC.release();
                }
                for (int i = 0; i < 10; i++) {
                    semA.acquire();
                    System.out.print("B");
                    semB.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread hiloC = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    semC.acquire();
                    System.out.print("C");
                    semA.release();
                }
                for (int i = 0; i < 10; i++) {
                    semC.acquire();
                    System.out.print("C");
                    semA.release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        hiloA.start();
        hiloB.start();
        hiloC.start();
    }
}
