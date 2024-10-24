package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ensamble {
	private static final Map<String, Semaphore> HERRAMIENTAS = new ConcurrentHashMap<>();
	private static final int NUM_COMPONENTE = 100;
	private static final int T = 400;
	private static final ExecutorService mesas = Executors.newFixedThreadPool(3);
	private static final Logger logger = Logger.getLogger(Ensamble.class.getName());
	public static void main(String[] args) {
		HERRAMIENTAS.put("destornillador", new Semaphore(2,true));
		HERRAMIENTAS.put("pinza", new Semaphore(4,true));
		HERRAMIENTAS.put("sargento", new Semaphore(4,true));
		for(int i=1; i<=NUM_COMPONENTE; i++) {
			int idDispositivo = i;
			mesas.submit(() -> {
				Ensamblado_Fase(idDispositivo,"destornillador y pinza", 1, new int[]{1,1}, T);
				Ensamblado_Fase(idDispositivo,"sargento", 2, new int[]{2}, T/2);
				Ensamblado_Fase(idDispositivo,"pinza", 3, new int[]{2}, 2*T);
			});
		}
		mesas.shutdown();
	}
	private static void Ensamblado_Fase(int idDispositivo, String herramienta, int numFase, int[] cajaHerramientas, int tiempo) {
		int[] cantidad = cajaHerramientas;
		try {
			ObtenerRecursos(herramienta,cantidad);
			System.out.println("Dispositivo "+idDispositivo+" - inicia ensamblado Fase "+numFase+".");
			Thread.sleep(tiempo);
			System.out.println("Dispositivo "+idDispositivo+" - finaliza ensamblado Fase "+numFase+".");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Dispositivo "+idDispositivo+" - fue interrumpido durante la Fase "+numFase+".", e);
			Thread.currentThread().interrupt();
		}catch (Exception e) {
			logger.log(Level.SEVERE, "Error inesperado en Fase "+numFase+" del Dispositivo "+idDispositivo+".", e);
		}finally {
			DevolverRecursos(herramienta,cantidad);
		}
	}
	private static void ObtenerRecursos(String recurso, int[] cantidad) {
		try {
			if(recurso.contains("destornillador y pinza")) {
				HERRAMIENTAS.get("destornillador").acquire(cantidad[0]);
				HERRAMIENTAS.get("pinza").acquire(cantidad[1]);
			}else {
				HERRAMIENTAS.get(recurso).acquire(cantidad[0]);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	private static void DevolverRecursos(String recurso, int[] cantidad) {
		if(recurso.contains("destornillador y pinza")) {
			HERRAMIENTAS.get("destornillador").release(cantidad[0]);
			HERRAMIENTAS.get("pinza").release(cantidad[1]);
		}else {
			HERRAMIENTAS.get(recurso).release(cantidad[0]);
		}
	}
}
