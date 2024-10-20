package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class CocinaRestaurante {
	private static final int TotalPlatos = 100;
	private static final int T = 300;
	private static final Semaphore Cuchillos = new Semaphore(3,true);
	private static final Semaphore TablaCortar = new Semaphore(4,true);
	private static final Semaphore Sartenes = new Semaphore(5,true);
	private static final Semaphore Espatulas = new Semaphore(2,true);
	private static final Semaphore PlatosServir = new Semaphore(4,true);
	private static final ExecutorService Estaciones = Executors.newFixedThreadPool(3);
	public static void main(String[] args) {
		for(int id=1; id<= TotalPlatos; id++) {
			int idPlato = id;
			Estaciones.submit(() -> {
				Fases("Preparacion",idPlato,() -> Preparacion(idPlato,T));
				Fases("Coccion",idPlato,() -> Coccion(idPlato,T*2));
				Fases("Decoracion",idPlato,() -> Decoracion(idPlato,T/2));
				Fases("Servido",idPlato,() -> Servido(idPlato,T));
			});
		}
		Estaciones.shutdown();
	}
	private static void Fases(String nombreFase,int idPlato, Runnable fase) {
		System.out.println("Plato "+idPlato+" inicia "+nombreFase+".");
		fase.run();
		System.out.println("Plato "+idPlato+" completa "+nombreFase+".");
	}
	private static void Servido(int idPlato, int tiempo) {
		try {
			if(!PlatosServir.tryAcquire()) {
				System.out.println("Plato " + idPlato + " espera un plato.");
				PlatosServir.acquire();
			}
			System.out.println("Plato " + idPlato + " en Fase de Servido.");
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			System.out.println("Plato " + idPlato + " fue interrumpido durante el Servido.");
			Thread.currentThread().interrupt();
		}finally {
			PlatosServir.release();
		}
	}
	private static void Decoracion(int idPlato, int tiempo) {
		try {
			if(!Espatulas.tryAcquire()) {
				System.out.println("Plato " + idPlato + " espera un espatula.");
				Espatulas.acquire();
			}
			System.out.println("Plato " + idPlato + " en Fase de Decoracion.");
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			System.out.println("Plato " + idPlato + " fue interrumpido durante la Decoracion.");
			Thread.currentThread().interrupt();
		}finally{
			Espatulas.release();
		}
	}
	private static void Coccion(int idPlato, int tiempo) {
		try {
			if(!Sartenes.tryAcquire()) {
				System.out.println("Plato " + idPlato + " espera un sarten.");
				Sartenes.acquire();
			}
			System.out.println("Plato " + idPlato + " en Fase de Coccion.");
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			System.out.println("Plato " + idPlato + " fue interrumpido durante la Coccion.");
			Thread.currentThread().interrupt();
		}finally {
			Sartenes.release();
		}
	}
	private static void Preparacion(int idPlato, int tiempo) {
		try {
			if(!Cuchillos.tryAcquire()) {
				System.out.println("Plato " + idPlato + " espera un cuchillo.");
				Cuchillos.acquire();
			}
			if(!TablaCortar.tryAcquire()) {
				System.out.println("Plato " + idPlato + " espera una tabla de cortar.");
				TablaCortar.acquire();
			}
			System.out.println("Plato " + idPlato + " en Fase de Preparacion.");
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			System.out.println("Plato " + idPlato + " fue interrumpido durante la Preparacion.");
			Thread.currentThread().interrupt();
		}finally {
			Cuchillos.release();
			TablaCortar.release();
		}
	}
}