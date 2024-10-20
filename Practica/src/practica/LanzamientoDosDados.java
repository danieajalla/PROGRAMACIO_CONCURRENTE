package practica;
import java.util.concurrent.*;
import java.util.concurrent.ThreadLocalRandom;

public class LanzamientoDosDados {
    private static final int NUM_LANZAMIENTOS = 30;
    private static final int DELAY = 3; // segundos
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static final int[] sumas = new int[25]; // Indice 2 a 24

    public static void main(String[] args) {
        executor.scheduleAtFixedRate(LanzamientoDosDados::lanzarDados, 0, DELAY, TimeUnit.SECONDS);
    }

    private static void lanzarDados() {
        if (sumas[0] >= NUM_LANZAMIENTOS) {
            System.out.println("Resultados finales:");
            for (int i = 2; i <= 24; i++) {
                System.out.println("Suma " + i + ": " + sumas[i] + " veces");
            }
            executor.shutdown();
            return;
        }

        int dado1 = ThreadLocalRandom.current().nextInt(1, 13);
        int dado2 = ThreadLocalRandom.current().nextInt(1, 13);
        int suma = dado1 + dado2;
        sumas[suma]++;
        sumas[0]++; // Contador de lanzamientos

        System.out.println("Lanzamiento " + sumas[0] + ": " + dado1 + " + " + dado2 + " = " + suma);
        if (suma == 12) {
            System.out.println("¡Suma mágica!");
        }
    }
}
