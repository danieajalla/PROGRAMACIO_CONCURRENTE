package puntos_Clave;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MaquinaRuleta {
    private static final String[] colores = {"rojo", "negro", "verde"};
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static final int[] suma = {0};
    public static void main(String[] args) {
        executor.scheduleAtFixedRate(MaquinaRuleta::simularRuleta, 0, 3, TimeUnit.SECONDS);
    }

    private static void simularRuleta() {
        int numero = ThreadLocalRandom.current().nextInt(0,37);
        suma[0] += numero;
        System.out.print("Número: "+numero+" - Color: "+colores[ThreadLocalRandom.current().nextInt(colores.length)]);
        System.out.println(" - Suma: "+suma[0]);
        if (suma[0] == 100) {
            System.out.println("¡JACKPOT!");
            executor.shutdown();
            return;
        } else if(suma[0] > 100) {
        	suma[0] = 0;
        	System.out.println("-------------------------------------------");
        }
    }
}
