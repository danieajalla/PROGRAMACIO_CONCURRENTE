package puntos_Clave;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Ejercicio01_C {
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		scheduler.scheduleAtFixedRate(new Lanzamiento(),0, 2, TimeUnit.SECONDS);
		scheduler.schedule(() -> {scheduler.shutdown();System.out.println("Test de la máquina de dados ha finalizado.");}, 1, TimeUnit.MINUTES);
	}
	static class Lanzamiento implements Runnable {

		@Override
		public void run() {
			int[] dados = new int[6];
	        for (int i = 0; i < dados.length; i++) {
	            dados[i] = ThreadLocalRandom.current().nextInt(1,7);
	        }

	        System.out.println("Dados lanzados: " + Arrays.toString(dados));

	        int suma = Arrays.stream(dados).sum();
	        if (suma % 2 == 0) {
	            System.out.println("La suma de los dados es par: " + suma);
	        } else {
	            System.out.println("La suma de los dados es impar: " + suma);
	        }

	        Arrays.sort(dados);
	        if (Arrays.equals(dados, new int[]{1, 2, 3, 4, 5, 6})) {
	            System.out.println("SE HA PRODUCIDO UNA ESCALERA!!!");
	        }
		}
	}
}