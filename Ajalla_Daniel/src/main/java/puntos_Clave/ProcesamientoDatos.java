package puntos_Clave;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesamientoDatos {
	private static final Map<String, Semaphore> HERRAMIENTAS = new ConcurrentHashMap<>();
	private static final ExecutorService estacion = Executors.newFixedThreadPool(4);
	private static final Logger logger = Logger.getLogger(ProcesamientoDatos.class.getName());
	private static final int T = 300;
	private static final int lote = 100;
	public static void main(String[] args) {
		HERRAMIENTAS.put("CPU", new Semaphore(6,true));
		HERRAMIENTAS.put("MEMORIA", new Semaphore(10,true));
		HERRAMIENTAS.put("DISCO", new Semaphore(4,true));
		HERRAMIENTAS.put("RED", new Semaphore(3,true));
		for(int id=1; id<=lote; id++) {
			int idDato = id;
			estacion.submit(() -> {
				Ensamblado(new String[] {"CPU","MEMORIA"},idDato,T,new int[] {1,2},1);
				Ensamblado(new String[] {"DISCO"},idDato,2*T,new int[] {2},2);
				Ensamblado(new String[] {"RED","CPU"},idDato,3*T/2,new int[] {1,1},3);
				Ensamblado(new String[] {"MEMORIA", "RED"},idDato,3*T,new int[] {3,1},4);
			});
		}
		estacion.shutdown();
	}
	private static void Ensamblado(String[] herramientas, int idDato, int tiempo, int[] recuso, int numFase) {
		int[] recursos = recuso;
		try {
			ObtenerR(herramientas,recursos);
			System.out.println("Procesamiento de Dato "+idDato+" - inicia Fase "+numFase+".");
			Thread.sleep(tiempo);
			System.out.println("Procesamiento de Dato "+idDato+" - completada Fase "+numFase+".");
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Procesamiento de Dato "+idDato+" - fue interrumpido durante la Fase "+numFase+".", e);
			Thread.currentThread().interrupt();
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Error inesperado del procesamiento de Dato "+idDato+".", e);
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
