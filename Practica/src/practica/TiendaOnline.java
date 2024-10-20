package practica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TiendaOnline {
	private static final int NUM_PEDIDOS = 50;
	private static final int T = 500;
	private static final Semaphore TrabajadoresProceso = new Semaphore(3,true);
	private static final Semaphore TrabajadoresEmpaquetado = new Semaphore(4,true);
	private static final Semaphore TrabajadoresEnvio = new Semaphore(2,true);
	private static final ExecutorService Estaciones = Executors.newFixedThreadPool(3);
	public static void main(String[] args) {
		for (int i = 1; i <= NUM_PEDIDOS; i++) {
            int idPedido = i;
            Estaciones.submit(() -> {FaseProcesamiento(idPedido);FaseEmpaquetado(idPedido);FaseEnvio(idPedido);});
		}
		Estaciones.shutdown();
	}
	private static void FaseProcesamiento(int idPedido) {
        try {
            TrabajadoresProceso.acquire();
            System.out.println("Pedido " + idPedido + " en Fase de Procesamiento del Pedido.");
            Thread.sleep(T);
        } catch (InterruptedException e) {
            System.out.println("Pedido " + idPedido + " fue interrumpido durante la Fase de Procesamiento del Pedido.");
            Thread.currentThread().interrupt();
        } finally {
            TrabajadoresProceso.release();
        }
    }

    private static void FaseEmpaquetado(int idPedido) {
        try {
        	TrabajadoresEmpaquetado.acquire();
            System.out.println("Pedido " + idPedido + " en Fase de Empaquetado.");
            Thread.sleep(2*T);
        } catch (InterruptedException e) {
            System.out.println("Pedido " + idPedido + " fue interrumpido durante la Fase de Empaquetado.");
            Thread.currentThread().interrupt();
        } finally {
        	TrabajadoresEmpaquetado.release();
        }
    }

    private static void FaseEnvio(int idPedido) {
        try {
        	TrabajadoresEnvio.acquire();
            System.out.println("Pedido " + idPedido + " en Fase de Envío.");
            Thread.sleep(T);
        } catch (InterruptedException e) {
            System.out.println("Pedido " + idPedido + " fue interrumpido durante la Fase de Envío.");
            Thread.currentThread().interrupt();
        } finally {
        	TrabajadoresEnvio.release();
            System.out.println("Pedido " + idPedido + " ha completado todas las fases.");
        }
    }

}
