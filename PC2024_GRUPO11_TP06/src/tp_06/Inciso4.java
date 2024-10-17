package tp_06;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Inciso4 {
	private static final AtomicInteger idClientes = new AtomicInteger(1);
	private static final Semaphore caja = new Semaphore(3,true);
	private static final Semaphore carritos = new Semaphore(15,true);
	public static void main(String[] args) {
		new Thread(new LlegadaCliente()).start();
	}
	static class LlegadaCliente implements Runnable {

		@Override
		public void run() {
			int id = idClientes.getAndIncrement();
			new Thread(() -> ProcesandoCompra(id)).start();
			try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(300, 501));
                new Thread(this).start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
		}

		private void ProcesandoCompra(int id) {
			try {
				carritos.acquire();
				System.out.println("Cliente "+id+" entró al Súper y tomó un carrito.");
				System.out.println("Cliente "+id+" está comprando");
				Thread.sleep(ThreadLocalRandom.current().nextInt(4000, 7001));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
            try {
            	if (!caja.tryAcquire()) {
            	    System.out.println("Cliente " + id + " está esperando un cajero.");
            	    caja.acquire();
            	}
                System.out.println("Cliente "+id +" esta pagando en la Caja.");
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4001));
                System.out.println("Cliente "+id +" abandona el Súper.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
            	caja.release();
            	carritos.release();
            }
		}
	}
}