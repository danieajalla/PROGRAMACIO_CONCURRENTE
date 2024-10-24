package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ejercicio02_C {
	private static final Map<String, Semaphore> HERRAMIENTAS = new ConcurrentHashMap<>();
	private static final int NUM_COMPONENTE = 100;
	private static final int T = 400;
	private static final ExecutorService mesas = Executors.newFixedThreadPool(3);
	private static final Logger logger = Logger.getLogger(Ejercicio02_C.class.getName());
	public static void main(String[] args) {
		HERRAMIENTAS.put("pinza", new Semaphore(4,true));
		HERRAMIENTAS.put("destornillador", new Semaphore(2,true));
		HERRAMIENTAS.put("sargento", new Semaphore(4,true));
		for(int i=1; i<=NUM_COMPONENTE; i++) {
			int idDispositivo = i;
			mesas.submit(() -> {
				Ensamblado_Fase1(idDispositivo);
				Ensamblado_Fase2(idDispositivo);
				Ensamblado_Fase3(idDispositivo);
			});
		}
		mesas.shutdown();
	}
	private static void Ensamblado_Fase1(int idDispositivo) {
		int[] cantidad = {1,1,0};
		try {
			ObtenerRecursos("destornillador y pinza",cantidad);
			System.out.println("Dispositivo "+idDispositivo+" - inicia ensamblado Fase 1.");
			Thread.sleep(T);
			System.out.println("Dispositivo "+idDispositivo+" - finaliza ensamblado Fase 1.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Dispositivo "+idDispositivo+" - fue interrumpido durante la Fase 1.", e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en fase 1 del Dispositivo "+idDispositivo+".", e);
		}finally {
			DevolverRecursos("destornillador y pinza",cantidad);
		}
	}
	private static void Ensamblado_Fase2(int idDispositivo) {
		int[] cantidad = {0,0,2};
		try {
			ObtenerRecursos("sargento",cantidad);
			System.out.println("Dispositivo "+idDispositivo+" - inicia ensamblado Fase 2.");
			Thread.sleep(T/2);
			System.out.println("Dispositivo "+idDispositivo+" - finaliza ensamblado Fase 2.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Dispositivo "+idDispositivo+" - fue interrumpido durante la Fase 2.", e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en fase 2 del Dispositivo "+idDispositivo+".", e);
		}finally {
			DevolverRecursos("sargento",cantidad);
		}
	}
	private static void Ensamblado_Fase3(int idDispositivo) {
		int[] cantidad = {0,2,0};
		try {
			ObtenerRecursos("pinza",cantidad);
			System.out.println("Dispositivo "+idDispositivo+" - inicia ensamblado Fase 3.");
			Thread.sleep(2*T);
			System.out.println("Dispositivo "+idDispositivo+" - finaliza ensamblado Fase 3.");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Dispositivo "+idDispositivo+" - fue interrumpido durante la Fase 3.", e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en fase 3 del Dispositivo "+idDispositivo+".", e);
		}finally {
			DevolverRecursos("pinza",cantidad);
		}
	}
	private static void ObtenerRecursos(String recurso, int[] cantidad) {
		try {
			if(recurso.contains("destornillador")) {
				HERRAMIENTAS.get("destornillador").acquire(cantidad[0]);
			}
			if(recurso.contains("pinza")) {
				HERRAMIENTAS.get("pinza").acquire(cantidad[1]);
			}
			if(recurso.contains("sargento")) {
				HERRAMIENTAS.get("sargento").acquire(cantidad[2]);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	private static void DevolverRecursos(String recurso, int[] cantidad) {
		if(recurso.contains("destornillador")) {
			HERRAMIENTAS.get("destornillador").release(cantidad[0]);
		}
		if(recurso.contains("pinza")) {
			HERRAMIENTAS.get("pinza").release(cantidad[1]);
		}
		if(recurso.contains("sargento")) {
			HERRAMIENTAS.get("sargento").release(cantidad[2]);
		}
	}
}
