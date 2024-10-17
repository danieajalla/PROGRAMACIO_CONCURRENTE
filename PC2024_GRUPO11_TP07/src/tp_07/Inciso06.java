package tp_07;

import java.util.concurrent.Semaphore;

public class Inciso06 {
	static final Semaphore siguiente = new Semaphore(0,true);
	static final Semaphore aviso = new Semaphore(0,true);
	public static void main(String[] args) {
		Thread bar =new Thread(new Barbero());
		for(int i=1; i<=20; i++) {
			new Thread(new Cliente(i)).start();
		}
		
	    bar.setDaemon(true);
	    bar.start();
	}
	static class Barbero implements Runnable {
		@Override
		public void run() {
			Salon.RealizarCorte();
		}
		
	}
	static class Cliente implements Runnable {
		private static final Semaphore entrada = new Semaphore(1,true);
		private static final Semaphore salida = new Semaphore(1,true);
		private static final Semaphore puerta = new Semaphore(1,true);
		private final int id;
		public Cliente(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			boolean esperar = false;
			try {
				entrada.acquire();
				puerta.acquire();
				System.out.println("Cliente "+id+", ingresa a la Barberia_2024");
				puerta.release();
				esperar = Salon.Bancos(id);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				entrada.release();
			}
			if(esperar==true) {
				try {
					siguiente.acquire();
					System.out.print("Cliente "+id+", finaliza de ser atendido ");
					aviso.release();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			try {
				salida.acquire();
				puerta.acquire();
				System.out.println(", se retira de la Barberia_2024");
				puerta.release();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				salida.release();
			}
		}
		
	}
	static class Salon {
		private static final Semaphore sillas = new Semaphore(5,true);
		private static final Semaphore barbero = new Semaphore(0,true);
		private static final Semaphore atendido = new Semaphore(1,true);
		private volatile static boolean dormido = true;
		public static boolean Bancos(int id) {
			boolean esperar = false;
				try {
					atendido.acquire();
					if(sillas.tryAcquire()) {
						System.out.println("Cliente "+id+", ocupa una silla");
						if(dormido) {
							dormido = false;
							System.out.println("Cliente "+id+", despierta al Barbero");
						}
						barbero.release();
						esperar = true;
					}else {
						System.out.print("Sin silla, para el Cliente "+id);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					atendido.release();
				}
				return esperar;
		}
		public static void RealizarCorte() {
			while(true){
				try {
					atendido.acquire();
					if(barbero.availablePermits()==0) {
						System.out.println("Sin Cliente, Barbero se duerme");
						dormido = true;
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					atendido.release();
				}
				try {
					barbero.acquire();
					sillas.release();
					System.out.println("Barbero, atiende un Cliente");
					Thread.sleep(2000);
					siguiente.release();
					aviso.acquire();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}