package practica;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class JuegosMonedas {
	private static final int lanzamientos = 20;
	private static int contador = 0;
	private static int cara = 0;
	private static int cruz = 0;
	private static final ScheduledExecutorService lanzado = Executors.newScheduledThreadPool(1);
	public static void main(String[] args) {
		lanzado.scheduleAtFixedRate(() -> Lanzando(), 0, 3, TimeUnit.SECONDS);
		lanzado.schedule(() -> {lanzado.shutdown();}, 1, TimeUnit.MINUTES);
	}
	private static void Lanzando() {
		int resultado = ThreadLocalRandom.current().nextInt(0,2);
		if(resultado == 0) {
			cara++;
			contador++;
			System.out.println("¡Cara!");
			if(contador == 5) {
				System.out.println("¡5 Caras seguidas!");
				contador = 0;
			}
		}else {
			cruz++;
			contador = 0;
			System.out.println("¡Cruz!");
		}
		if((cara+cruz)==lanzamientos) {
			System.out.println("Cara : "+cara+" Cruz: "+cruz);
		}
	}

}
