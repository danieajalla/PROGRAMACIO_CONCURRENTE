package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Ensamblado {
	private static final int smartphones = 100;
	private static final int T = 400;
	private static final Semaphore pantalla = new Semaphore(5,true);
	private static final Semaphore chip = new Semaphore(5,true);
	private static final Semaphore bateria = new Semaphore(4,true);
	private static final Semaphore carcasa = new Semaphore(4,true);
	private static final Semaphore camara = new Semaphore(3,true);
	private static final Semaphore antenas = new Semaphore(3,true);
	private static final Semaphore instalacionSO = new Semaphore(4,true);
	private static final ExecutorService estaciones = Executors.newFixedThreadPool(4);
    public static void main(String[] args) {
    	for(int id=1; id<=smartphones; id++) {
    		final int idSmart = id;
    		estaciones.submit(() -> { try{
    			System.out.println("Smartphone "+idSmart+" inicia el ensamblado.");
    			Fase1(idSmart);Fase2(idSmart);Fase3(idSmart);Fase4(idSmart);
    		} catch (InterruptedException e) {
    			System.out.println("Smartphone "+idSmart+" fue interrupido durante el ensamblado.");
                Thread.currentThread().interrupt();
            }
    		System.out.println("Smartphone "+idSmart+" Termina el ensamblado.");
    		});
    	}
    	estaciones.shutdown();
    }
	private static void Fase1(int idSmart) throws InterruptedException {
		if(!pantalla.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera una pantalla.");
			pantalla.acquire();
		}
		if(!chip.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera una chip.");
			chip.acquire();
		}
		System.out.println("Smartphone "+idSmart+" inicia Fase 1.");
		Thread.sleep(T);
		System.out.println("Smartphone "+idSmart+" completa Fase 1.");
		pantalla.release();
		chip.release();
	}
	private static void Fase2(int idSmart) throws InterruptedException {
		if(!bateria.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera una bateria.");
			bateria.acquire();
		}
		if(!carcasa.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera una carcasa.");
			carcasa.acquire();
		}
		System.out.println("Smartphone "+idSmart+" inicia Fase 2.");
		Thread.sleep(T*(2/3));
		System.out.println("Smartphone "+idSmart+" completa Fase 2.");
		bateria.release();
		carcasa.release();
	}
	private static void Fase3(int idSmart) throws InterruptedException {
		if(!camara.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera una camara.");
			camara.acquire();
		}
		if(!antenas.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera un antena.");
			antenas.acquire();
		}
		System.out.println("Smartphone "+idSmart+" inicia Fase 3.");
		Thread.sleep(T/2);
		System.out.println("Smartphone "+idSmart+" completa Fase 3.");
		camara.release();
		antenas.release();
	}
	private static void Fase4(int idSmart) throws InterruptedException {
		if(!instalacionSO.tryAcquire()) {
			System.out.println("Smartphone "+idSmart+" espera la Instalacion de Sistema Operativo.");
			instalacionSO.acquire();
		}
		System.out.println("Smartphone "+idSmart+" inicia Fase 4.");
		Thread.sleep(T);
		System.out.println("Smartphone "+idSmart+" completa Fase 4.");
		instalacionSO.release();
	}
}