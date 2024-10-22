package fuerza;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Juego {
	private static final int NUM_LANZAMIENTO = 30;
	private static final int TIEMPO = 3;
	private static final int[] CANTIDAD = new int[25];
	private static final ScheduledExecutorService dados = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		dados.scheduleAtFixedRate(new LanzamientoDados(), 0, TIEMPO, TimeUnit.SECONDS);
	}
	static class LanzamientoDados implements Runnable {

		@Override
		public void run() {
			if(CANTIDAD[0] >= NUM_LANZAMIENTO) {
				System.out.println("Cantidad Total de cada suma: ");
				for(int i=4; i<=24; i++) {
					System.out.println(" Numero "+i+" se obtuvo "+CANTIDAD[i]+" veces.");
				}
				dados.shutdown();
				return;
			}
			int dado1 = ThreadLocalRandom.current().nextInt(1,7);
			int dado2 = ThreadLocalRandom.current().nextInt(1,7);
			int dado3 = ThreadLocalRandom.current().nextInt(1,7);
			int dado4 = ThreadLocalRandom.current().nextInt(1,7);
			int suma = dado1 + dado2 + dado3 + dado4;
			CANTIDAD[suma]++;
			CANTIDAD[0]++;
			System.out.println("Resultado: Dado_1 = "+dado1+", Dado_2 = "+dado2+", Dado_3 = "+dado3+", Dado_4 = "+dado4+".");
			if(suma == 24) {
				System.out.println("¡Suma perfecta!");
			}
		}
	}
}
