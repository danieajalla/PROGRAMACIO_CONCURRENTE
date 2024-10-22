package fuerza;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Fabrica {
	private static final int NUM_BICICLETAS = 50;
	private static final int T = 400;
	private static final Semaphore cuadros = new Semaphore(3,true);
	private static final Semaphore horquillas = new Semaphore(3,true);
	private static final Semaphore ruedas = new Semaphore(4,true);
	private static final Semaphore manillares = new Semaphore(4,true);
	private static final Semaphore sillines = new Semaphore(3,true);
	private static final Semaphore frenos = new Semaphore(3,true);
	private static final Semaphore cadenas = new Semaphore(2,true);
	private static final ExecutorService estacion = Executors.newFixedThreadPool(3);
	private static final Logger logger = Logger.getLogger(Fabrica.class.getName());
	public static void main(String[] args) {
		for(int id=1; id<=NUM_BICICLETAS; id++) {
			int idBici = id;
			estacion.submit(() -> {
				Ensamblado_Fase1(idBici);
				Ensamblado_Fase2(idBici);
				Ensamblado_Fase3(idBici);
				Ensamblado_Fase4(idBici);
				Ensamblado_Fase5(idBici);
			});
		}
		estacion.shutdown();
	}
	private static void Ensamblado_Fase1(int idBici) {
		try {
			ObtenerRecursos(cuadros,horquillas);
			System.out.println("Bicicleta "+idBici+" - inicia el ensamblado de la Fase 1.");
			Thread.sleep(T);
			System.out.println("Bicicleta "+idBici+" - finaliza el ensamblado de la Fase 1.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Bicicleta "+idBici+" fue interrupido durante la Fase 1.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en la Fase 1 de la Bicicleta " + idBici + ".", e);
		}finally {
			DevolverRecursos(cuadros,horquillas);
		}
	}
	private static void Ensamblado_Fase2(int idBici) {
		try {
			ObtenerRecursos(ruedas,ruedas);
			System.out.println("Bicicleta "+idBici+" - inicia el ensamblado de la Fase 2.");
			Thread.sleep(T/2);
			System.out.println("Bicicleta "+idBici+" - finaliza el ensamblado de la Fase 2.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Bicicleta "+idBici+" fue interrupido durante la Fase 2.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en la Fase 2 de la Bicicleta " + idBici + ".", e);
		}finally {
			DevolverRecursos(ruedas,ruedas);
		}
	}
	private static void Ensamblado_Fase3(int idBici) {
		try {
			ObtenerRecursos(manillares,sillines);
			System.out.println("Bicicleta "+idBici+" - inicia el ensamblado de la Fase 3.");
			Thread.sleep(2*T/3);
			System.out.println("Bicicleta "+idBici+" - finaliza el ensamblado de la Fase 3.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Bicicleta "+idBici+" fue interrupido durante la Fase 3.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en la Fase 3 de la Bicicleta " + idBici + ".", e);
		}finally {
			DevolverRecursos(manillares,sillines);
		}
	}
	private static void Ensamblado_Fase4(int idBici) {
		try {
			ObtenerRecursos(frenos);
			System.out.println("Bicicleta "+idBici+" - inicia el ensamblado de la Fase 4.");
			Thread.sleep(T/4);
			System.out.println("Bicicleta "+idBici+" - finaliza el ensamblado de la Fase 4.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Bicicleta "+idBici+" fue interrupido durante la Fase 4.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en la Fase 4 de la Bicicleta " + idBici + ".", e);
		}finally {
			DevolverRecursos(frenos);
		}
	}
	private static void Ensamblado_Fase5(int idBici) {
		try {
			ObtenerRecursos(cadenas);
			System.out.println("Bicicleta "+idBici+" - inicia el ensamblado de la Fase 5.");
			Thread.sleep(T);
			System.out.println("Bicicleta "+idBici+" - finaliza el ensamblado de la Fase 5.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Bicicleta "+idBici+" fue interrupido durante la Fase 5.", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en la Fase 5 de la Bicicleta " + idBici + ".", e);
		}finally {
			DevolverRecursos(cadenas);
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
