package practica;
import java.util.concurrent.*;
import java.util.*;

public class Capicua {
    private static final List<Integer> ListaNumeros = Collections.synchronizedList(new ArrayList<>());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        scheduler.scheduleAtFixedRate(new AgregarElemento(), 0, 5, TimeUnit.SECONDS);

        scheduler.schedule(() -> scheduler.shutdown(), 1, TimeUnit.MINUTES);
    }

    static class AgregarElemento implements Runnable {
        @Override
        public void run() {
            int numero = ThreadLocalRandom.current().nextInt(90, 131);
            ListaNumeros.add(numero);
            procesarCalculos();
        }

        private void procesarCalculos() {
            List<Integer> lista = new ArrayList<>(ListaNumeros); 
            int suma = lista.stream().mapToInt(Integer::intValue).sum();
            double promedio = lista.stream().mapToInt(Integer::intValue).average().orElse(0);

            System.out.print("Números: ");
            lista.forEach(n -> System.out.print(n + " "));
            System.out.println("\nSuma Actual: " + suma + " | Promedio Actual: " + promedio + " | Suma Capicúa: " + (esCapicua(suma) ? "SI" : "NO"));
        }

        private boolean esCapicua(int numero) {
            String str = String.valueOf(numero);
            int longitud = str.length();
            for (int i = 0; i < longitud / 2; i++) {
                if (str.charAt(i) != str.charAt(longitud - i - 1)) {
                    return false;
                }
            }
            return true;
        }
    }
}
