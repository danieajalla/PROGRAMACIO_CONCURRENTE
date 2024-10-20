package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class EnsambladoJuguetes {
	private static final int Juguetes = 50;
	private static final int T = 300;
	private static final Semaphore martillo = new Semaphore(3,true);
	private static final Semaphore clavos = new Semaphore(3,true);
	private static final Semaphore pinceles = new Semaphore(2,true);
	private static final Semaphore pinturas = new Semaphore(2,true);
	private static final Semaphore destornilladores = new Semaphore(3,true);
	private static final Semaphore tornillos = new Semaphore(3,true);
	private static final ExecutorService estaciones = Executors.newFixedThreadPool(4);
	public static void main(String[] args) {
		for (int i=1; i<=Juguetes; i++) {
			int idJuguete = i;
			estaciones.submit(() -> { Fase1(idJuguete,T);Fase2(idJuguete,T/3);Fase3(idJuguete,2*T);});
		}
		estaciones.shutdown();
	}
	private static void Fase3(int idJuguete, int tiempo) {
		try {
			if(!destornilladores.tryAcquire()) {
				System.out.println("Juguete "+idJuguete+" espera un destornillador.");
				destornilladores.acquire();
			}
			if(!tornillos.tryAcquire()) {
				System.out.println("Juguete "+idJuguete+" espera un tornillo.");
				tornillos.acquire();
			}
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 3.");
			Thread.sleep(tiempo);
			System.out.println("Juguete "+idJuguete+" completa la Fase 3.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrumpido durante la Fase 3.");
			Thread.currentThread().interrupt();
		}finally {
			destornilladores.release();
			tornillos.release();
		}
	}
	private static void Fase2(int idJuguete, int tiempo) {
		try {
			if(!pinceles.tryAcquire()) {
				System.out.println("Juguete "+idJuguete+" espera un pincel.");
				pinceles.acquire();
			}
			if(!pinturas.tryAcquire()) {
				System.out.println("Juguete "+idJuguete+" espera una pintura.");
				pinturas.acquire();
			}
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 2.");
			Thread.sleep(tiempo);
			System.out.println("Juguete "+idJuguete+" completa la Fase 2.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrumpido durante la Fase 2.");
			Thread.currentThread().interrupt();
		}finally {
			pinceles.release();
			pinturas.release();
		}
	}
	private static void Fase1(int idJuguete, int tiempo) {
		try {
			if(!martillo.tryAcquire()) {
				System.out.println("Juguete "+idJuguete+" espera un martillo.");
				martillo.acquire();
			}
			if(!clavos.tryAcquire()) {
				System.out.println("Juguete "+idJuguete+" espera un clavo.");
				clavos.acquire();
			}
			System.out.println("Juguete "+idJuguete+" iniciando la Fase 1.");
			Thread.sleep(tiempo);
			System.out.println("Juguete "+idJuguete+" completa la Fase 1.");
		} catch (InterruptedException e) {
			System.out.println("Juguete "+idJuguete+" fue interrumpido durante la Fase 1.");
			Thread.currentThread().interrupt();
		}finally {
			martillo.release();
			clavos.release();
		}
	}

}
