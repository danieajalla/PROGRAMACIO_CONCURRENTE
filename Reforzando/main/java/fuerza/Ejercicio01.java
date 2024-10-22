package fuerza;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Ejercicio01 {
	private static final int NUM_LANZAMIENTO = 25;
	private static final int TIEMPO = 5;
	private static final int[] CANTIDAD = new int[4];
	private static final ScheduledExecutorService MONEDA = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		MONEDA.scheduleAtFixedRate(Ejercicio01::LanzadoMoneda, 0, TIEMPO, TimeUnit.SECONDS);
	}
	private static void LanzadoMoneda() {
		if(CANTIDAD[0] >= NUM_LANZAMIENTO) {
			System.out.println("Resultado Total de Caras - CARA 1: "+CANTIDAD[1]+", CARA 2: "+CANTIDAD[2]+", CARA 3: "+CANTIDAD[3]);
			MONEDA.shutdown();
			return;
		}
		int cara = ThreadLocalRandom.current().nextInt(1,4);
		CANTIDAD[cara]++;
		System.out.println(" - Resultado => CARA "+cara);
		if(cara == 1) {
			CANTIDAD[0]++;
			if(CANTIDAD[0] == 3) {
				System.out.println("¡Tres Cara 1 seguidas!");
				CANTIDAD[0]--;
			}
		}else {
			CANTIDAD[0] = 0;
		}
	}
}
