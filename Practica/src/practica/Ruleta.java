package practica;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Ruleta {
	private static final int lanzamiento = 40;
	private static final int numeros[] = new int[2];
	private static final int Dilay = 4;
	private static final ScheduledExecutorService ruleta = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		ruleta.scheduleAtFixedRate(Ruleta::LanzamientoRuleta, 0, Dilay, TimeUnit.SECONDS);
	}
	private static void LanzamientoRuleta() {
		if(numeros[0] >= lanzamiento) {
			int media = numeros[1]/numeros[0];
			System.out.println("Resultado Media Final: "+media+".");
			ruleta.shutdown();
			return;
		}
		int numero = ThreadLocalRandom.current().nextInt(1,37);
		numeros[1] += numero;
		numeros[0]++;
		System.out.println("Numero en la ruleta: "+numero+".");
		if(numero == 18) {
			System.out.println("¡Número afortunado!");
		}
	}
}