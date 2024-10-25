package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntregaPaquetes {
	private static final Map<String, Semaphore> HERRAMIENTAS = new ConcurrentHashMap<>();
	private static final ExecutorService Empresa = Executors.newFixedThreadPool(5);
	private static final Logger logger = Logger.getLogger(EntregaPaquetes.class.getName());
	private static final int T = 200;
	private static final int lote = 100;
	public static void main(String[] args) {
		HERRAMIENTAS.put("FURGONETAS", new Semaphore(10,true));
		HERRAMIENTAS.put("CAMIONES", new Semaphore(5,true));
		HERRAMIENTAS.put("EMPLEADOS", new Semaphore(20,true));
		for(int id=1; id<=lote; id++) {
			int idDato = id;
			Empresa.submit(() -> {
				Ensamblado(new String[] {"FURGONETAS","EMPLEADOS"},idDato,T,new int[] {1,2},1);
				Ensamblado(new String[] {"CAMIONES", "EMPLEADOS"},idDato,3*T/2,new int[] {1,1},2);
				Ensamblado(new String[] {"EMPLEADOS"},idDato,2*T,new int[] {3},3);
			});
		}
		Empresa.shutdown();
	}
	private static void Ensamblado(String[] herramientas, int idDato, int tiempo, int[] recuso, int numFase) {
		int[] recursos = recuso;
		try {
			ObtenerR(herramientas,recursos);
			System.out.println("Paquete "+idDato+" - inicia Fase "+numFase+".");
			Thread.sleep(tiempo);
			System.out.println("Paquete"+idDato+" - completada Fase "+numFase+".");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Paquete "+idDato+" - fue interrumpido durante la Fase "+numFase+".", e);
			Thread.currentThread().interrupt();
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Error inesperado el proceso de entrega del paquete "+idDato+".", e);
		}finally {
			DevolverR(herramientas,recursos);
		}
	}
	private static void ObtenerR(String[] herramientas, int[] recursos) {
		try {
			for(int i=0; i<herramientas.length; i++) {
				HERRAMIENTAS.get(herramientas[i]).acquire(recursos[i]);
			}
		}catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} 
	}
	private static void DevolverR(String[] herramientas, int[] recursos) {
		for(int i=0; i<herramientas.length; i++) {
			HERRAMIENTAS.get(herramientas[i]).release(recursos[i]);
		}
	}
}
