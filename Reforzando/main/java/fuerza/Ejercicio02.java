package fuerza;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio02 {
	private static final Logger logger = Logger.getLogger(Ejercicio02.class.getName());
	private static final int NUM_COMPUTADORAS = 40;
	private static final int TIEMPO = 400;
	private static final Semaphore placasBases = new Semaphore(4,true);
	private static final Semaphore procesadores = new Semaphore(4,true);
	private static final Semaphore memoriaRAM = new Semaphore(3,true);
	private static final Semaphore discosduros = new Semaphore(3,true);
	private static final Semaphore tarjetagrafica = new Semaphore(2,true);
	private static final Semaphore instalacionesSO = new Semaphore(3,true);
	private static final ExecutorService estacion = Executors.newFixedThreadPool(3);
	public static void main(String[] args) {
		for(int i=1; i<=NUM_COMPUTADORAS; i++) {
			int idComputadora = i;
			estacion.submit(() -> {
				Ensamble_Fase1(idComputadora);
				Ensamble_Fase2(idComputadora);
				Ensamble_Fase3(idComputadora);
				Ensamble_Fase4(idComputadora);
				Ensamble_Fase5(idComputadora);
			});
		}
		estacion.shutdown();
	}
	private static void Ensamble_Fase1(int idComputadora) {
		try {
			ObtenerRecursos(placasBases,procesadores);
			System.out.println("Computadora "+idComputadora+" - inicia Fase 1.");
			Thread.sleep(TIEMPO);
			System.out.println("Computadora "+idComputadora+" - finaliza Fase 1.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,"Computadora "+idComputadora+" fue interrupido durante la Fase 1.",e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en la Fase 1 del robot " + idComputadora + ".", e);
        }finally {
			DevolverRecursos(placasBases,procesadores);
		}
	}
	private static void Ensamble_Fase2(int idComputadora) {
		try {
			ObtenerRecursos(memoriaRAM);
			System.out.println("Computadora "+idComputadora+" - inicia Fase 2.");
			Thread.sleep(TIEMPO/2);
			System.out.println("Computadora "+idComputadora+" - finaliza Fase 2.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,"Computadora "+idComputadora+" fue interrupido durante la Fase 2.",e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en la Fase 2 de la Computadora " + idComputadora + ".", e);
        }finally {
			DevolverRecursos(memoriaRAM);
		}
	}
	private static void Ensamble_Fase3(int idComputadora) {
		try {
			ObtenerRecursos(discosduros);
			System.out.println("Computadora "+idComputadora+" - inicia Fase 3.");
			Thread.sleep(TIEMPO/3);
			System.out.println("Computadora "+idComputadora+" - finaliza Fase 3.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,"Computadora "+idComputadora+" fue interrupido durante la Fase 3.",e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en la Fase 3 de la Computadora " + idComputadora + ".", e);
        }finally {
			DevolverRecursos(discosduros);
		}
	}
	private static void Ensamble_Fase4(int idComputadora) {
		try {
			ObtenerRecursos(tarjetagrafica);
			System.out.println("Computadora "+idComputadora+" - inicia Fase 4.");
			Thread.sleep(2*TIEMPO/3);
			System.out.println("Computadora "+idComputadora+" - finaliza Fase 4.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,"Computadora "+idComputadora+" fue interrupido durante la Fase 4.",e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en la Fase 4 de la Computadora " + idComputadora + ".", e);
        }finally {
			DevolverRecursos(tarjetagrafica);
		}
	}
	private static void Ensamble_Fase5(int idComputadora) {
		try {
			ObtenerRecursos(instalacionesSO);
			System.out.println("Computadora "+idComputadora+" - inicia Fase 5.");
			Thread.sleep(TIEMPO);
			System.out.println("Computadora "+idComputadora+" - finaliza Fase 5.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,"Computadora "+idComputadora+" fue interrupido durante la Fase 5.",e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado en la Fase 5 de la Computadora " + idComputadora + ".", e);
        }finally {
			DevolverRecursos(instalacionesSO);
		}
	}
	private static void ObtenerRecursos(Semaphore...recursos) {
		for(Semaphore recurso: recursos) {
			if(!recurso.tryAcquire()) {
				try {
					recurso.acquire();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		
	}
	private static void DevolverRecursos(Semaphore...recursos) {
		for(Semaphore recurso: recursos) {
			recurso.release();
		}
	}
}
