package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class FabricaRobots {
	private static final int NUM_ROBOTS = 60;
	private static final int T = 400;
	private static final Semaphore CABEZAS = new Semaphore(5,true);
	private static final Semaphore CUERPOS = new Semaphore(5,true);
	private static final Semaphore BRAZOS = new Semaphore(4,true);
	private static final Semaphore PIERNAS = new Semaphore(4,true);
	private static final Semaphore BATERIAS = new Semaphore(3,true);
	private static final Semaphore SOFTWARE_CONTROL = new Semaphore(4,true);
	private static final ExecutorService ESTACION = Executors.newFixedThreadPool(3);
	public static void main(String[] args) {
		for(int i=1; i<=NUM_ROBOTS; i++) {
			int idRobots = i;
			ESTACION.submit(() -> {
	            Ensamblado_Fase1(idRobots);
				Ensamblado_Fase2(idRobots);
				Ensamblado_Fase3(idRobots);
				Ensamblado_Fase4(idRobots);
				Ensamblado_Fase5(idRobots);
	        });
		}
		ESTACION.shutdown();
		try {
			if(!ESTACION.awaitTermination(1, TimeUnit.HOURS)) {
				ESTACION.shutdownNow();
			}
		} catch (InterruptedException e) {
			ESTACION.shutdownNow();
			Thread.currentThread().interrupt();
		}
		
	}
	private static void Ensamblado_Fase1(int idRobots) {
		try {
			AcquireRecursos(CABEZAS,CUERPOS);
			System.out.println("Robot "+idRobots+" - iniciando la Fase 1.");
			Thread.sleep(T);
			System.out.println("Robot "+idRobots+" - completa la Fase 1.");
		} catch (InterruptedException e) {
            System.out.println("Robot " + idRobots + " fue interrumpido durante la Fase 1.");
            Thread.currentThread().interrupt();
	    } catch (Exception e) {
	        System.out.println("Error inesperado en la Fase 1 del robot " + idRobots + ": " + e.getMessage());
	        e.printStackTrace();
	    }finally {
			ReleaseRecursos(CABEZAS, CUERPOS);
		}
	}
	private static void Ensamblado_Fase2(int idRobots) {
		try {
			AcquireRecursos(BRAZOS, BRAZOS);
			System.out.println("Robot "+idRobots+" - iniciando la Fase 2.");
			Thread.sleep(T/3);
			System.out.println("Robot "+idRobots+" - completa la Fase 2.");
		} catch (InterruptedException e) {
			System.out.println("Robot " + idRobots + " fue interrumpido durante la Fase 2.");
            Thread.currentThread().interrupt();
	    } catch (Exception e) {
	        System.out.println("Error inesperado en la Fase 2 del robot " + idRobots + ": " + e.getMessage());
	        e.printStackTrace();
	    }finally {
			ReleaseRecursos(BRAZOS, BRAZOS);
		}
	}
	private static void Ensamblado_Fase3(int idRobots) {
		try {
			AcquireRecursos(PIERNAS,PIERNAS);
			System.out.println("Robot "+idRobots+" - iniciando la Fase 3.");
			Thread.sleep(2*T/3);
			System.out.println("Robot "+idRobots+" - completa la Fase 3.");
		}  catch (InterruptedException e) {
			System.out.println("Robot " + idRobots + " fue interrumpido durante la Fase 3.");
            Thread.currentThread().interrupt();
	    } catch (Exception e) {
	        System.out.println("Error inesperado en la Fase 3 del robot " + idRobots + ": " + e.getMessage());
	        e.printStackTrace();
	    }finally {
			ReleaseRecursos(PIERNAS,PIERNAS);
		}
	}
	private static void Ensamblado_Fase4(int idRobots) {
		try {
			AcquireRecursos(BATERIAS);
			System.out.println("Robot "+idRobots+" - iniciando la Fase 4.");
			Thread.sleep(T);
			System.out.println("Robot "+idRobots+" - completa la Fase 4.");
		}  catch (InterruptedException e) {
			System.out.println("Robot " + idRobots + " fue interrumpido durante la Fase 4.");
            Thread.currentThread().interrupt();
	    } catch (Exception e) {
	        System.out.println("Error inesperado en la Fase 4 del robot " + idRobots + ": " + e.getMessage());
	        e.printStackTrace();
	    }finally {
			ReleaseRecursos(BATERIAS);
		}
	}
	private static void Ensamblado_Fase5(int idRobots) {
		try {
			AcquireRecursos(SOFTWARE_CONTROL);
			System.out.println("Robot "+idRobots+" - iniciando la Fase 5.");
			Thread.sleep(T/2);
			System.out.println("Robot "+idRobots+" - completa la Fase 5.");
		}  catch (InterruptedException e) {
			System.out.println("Robot " + idRobots + " fue interrumpido durante la Fase 5.");
            Thread.currentThread().interrupt();
	    } catch (Exception e) {
	        System.out.println("Error inesperado en la Fase 5 del robot " + idRobots + ": " + e.getMessage());
	        e.printStackTrace();
	    }finally {
			System.out.println("Robots "+idRobots+" - completo todas las Fases.");
			ReleaseRecursos(SOFTWARE_CONTROL);
		}
	}
	private static void AcquireRecursos(Semaphore...recursos) {
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
	private static void ReleaseRecursos(Semaphore...recursos) {
		for(Semaphore recurso: recursos) {
			recurso.release();
		}
	}
}