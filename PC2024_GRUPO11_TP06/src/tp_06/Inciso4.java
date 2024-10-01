package tp_06;

import java.util.concurrent.Semaphore;

public class Inciso4 {
	private static final Semaphore carrito = new Semaphore(15,true);
	private static final Semaphore caja = new Semaphore(3,true);
	public static void main(String[] args) {
		int j = 0;
		while(true) {
			try {
				new Thread(new Cliente(j+1)).start();
				j++;
				Thread.sleep((int) (Math.random()*201)+300);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	static class Cliente implements Runnable{
		private final int id;
		public Cliente(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			try {
				carrito.acquire();
				System.out.println("Cliente "+id+" entró al Súper y tomó un carrito");
				Thread.sleep(((int) (Math.random()*4)+4)*1000);
				System.out.println("Cliente "+id+" está comprando");
				caja.acquire();
				Thread.sleep(((int) (Math.random()*3)+2)*1000);
				System.out.println("Cliente "+id+" está pagando en la caja");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				caja.release();
				carrito.release();
				System.out.println("Cliente "+id+" abandona el Súper");
			}
		}
		
	}
}
