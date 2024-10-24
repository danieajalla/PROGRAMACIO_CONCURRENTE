package puntos_Clave;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Ejercicio02_B {
	private static final ScheduledExecutorService elemento = Executors.newScheduledThreadPool(3);
	private static final List<Integer> lista = new ArrayList<>();
	public static void main(String[] args) {
		elemento.scheduleAtFixedRate(new Elemento(), 0, 5, TimeUnit.SECONDS);
	}
	static class Elemento implements Runnable {

		@Override
		public void run() {
			lista.add(ThreadLocalRandom.current().nextInt(90,131));
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
