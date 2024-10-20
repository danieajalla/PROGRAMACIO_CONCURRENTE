package practica;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class JuegoMoneda {
	private static final int NLanzamiento = 25;
	private static final int[] resultado = {0,0};
	private static final ScheduledExecutorService lanza = Executors.newScheduledThreadPool(1);
    public static void main(String[] args) throws InterruptedException {
    	lanza.scheduleAtFixedRate(new Lanzado(),0, 4, TimeUnit.SECONDS);
		lanza.schedule(() -> {lanza.shutdown();System.out.println("Test de la máquina de dados ha finalizado.");}, 2, TimeUnit.MINUTES);
    }
    static class Lanzado implements Runnable {
    	
		@Override
		public void run() {
			if(NLanzamiento == resultado[1]) {
	    		int media = resultado[0]/resultado[1];
	    		System.out.println("Resultado de la Media: "+media+".");
	    		return;
	    	}
	    	int numero = ThreadLocalRandom.current().nextInt(1,21);
	    	resultado[0]+=numero;
	    	resultado[1]++;
	    	if(18<=numero) {
	    		System.out.println("Numero obtenido: "+numero+" ¡Lanzamiento afortunado!");
	    	}else {
	    		System.out.println("Numero obtenido: "+numero+".");
	    	}
		}
    }
}