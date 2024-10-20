package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class FabricaAutos {
	private static final int Juguetes = 50;
	private static final int T = 400;
	private static final Semaphore ruedas = new Semaphore(6,true);
	private static final Semaphore chasis = new Semaphore(3,true);
	private static final Semaphore motores = new Semaphore(2,true);
	private static final Semaphore puertas = new Semaphore(4,true);
	private static final Semaphore ventanas = new Semaphore(4,true);
	private static final Semaphore pinturas = new Semaphore(3,true);
	private static final Semaphore baterias = new Semaphore(2,true);
	private static final ExecutorService estaciones = Executors.newFixedThreadPool(3);
    public static void main(String[] args) {
    	for(int id=1; id<=Juguetes; id++) {
    		final int idJuguete = id;
    		estaciones.submit(() -> { System.out.println("Juguete "+idJuguete+" inicia el ensamblado.");
			EnsambladoFase1(idJuguete);EnsambladoFase2(idJuguete);EnsambladoFase3(idJuguete);EnsambladoFase4(idJuguete);EnsambladoFase5(idJuguete);
    		System.out.println("Juguete "+idJuguete+" Termina el ensamblado.");
    		});
    	}
    	estaciones.shutdown();
    }
	private static void EnsambladoFase1(int idJuguete) {
		try {
			acquireResources(ruedas, chasis);
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 1.");
			Thread.sleep(T);
			System.out.println("Juguete "+idJuguete+" completa la Fase 1.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrupido durante la Fase 1.");
			Thread.currentThread().interrupt();
		}finally {
			releaseResources(ruedas, chasis);
		}
	}
	private static void EnsambladoFase2(int idJuguete) {
		try {
			acquireResources(motores);
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 2.");
			Thread.sleep(T/2);
			System.out.println("Juguete "+idJuguete+" completa la Fase 2.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrupido durante la Fase 2.");
			Thread.currentThread().interrupt();
		}finally {
			releaseResources(motores);
		}
	}
	private static void EnsambladoFase3(int idJuguete) {
		try {
			acquireResources(puertas, ventanas);
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 3.");
			Thread.sleep(2*T/3);
			System.out.println("Juguete "+idJuguete+" completa la Fase 3.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrupido durante la Fase 3.");
			Thread.currentThread().interrupt();
		}finally {
			releaseResources(puertas, ventanas);
		}
	}
	private static void EnsambladoFase4(int idJuguete) {
		try {
			acquireResources(pinturas);
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 4.");
			Thread.sleep(T);
			System.out.println("Juguete "+idJuguete+" completa la Fase 4.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrupido durante la Fase 4.");
			Thread.currentThread().interrupt();
		}finally {
			releaseResources(pinturas);
		}	
	}
	private static void EnsambladoFase5(int idJuguete) {
		try {
			acquireResources(baterias);
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 5.");
			Thread.sleep(T/2);
			System.out.println("Juguete "+idJuguete+" completa la Fase 5.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrupido durante la Fase 5.");
			Thread.currentThread().interrupt();
		}finally {
			releaseResources(baterias);
		}
	}
	private static void acquireResources(Semaphore... resources) throws InterruptedException {
	    for (Semaphore resource : resources) {
	        if (!resource.tryAcquire()) {
	            resource.acquire();
	        }
	    }
	}
	private static void releaseResources(Semaphore... resources) {
	    for (Semaphore resource : resources) {
	        resource.release();
	    }
	}
}