package tp_07;

import java.util.concurrent.Semaphore;

public class Inciso06 {
	static final int NUMEROSILLAS = 15;
	static final Semaphore SILLAS = new Semaphore(NUMEROSILLAS,true);
	static final Semaphore SALA = new Semaphore(1,true);
	static final Semaphore BARBERO = new Semaphore(0,true);
	static final Semaphore ENTYSAL = new Semaphore(1,true);
	static final Semaphore LOOCK = new Semaphore(0,true);
	public static void main(String[] args) {
		new Thread(new SalaBarbero()).start();
		for(int i=0; i<20; i++) {
			new Thread(new SalaEspera()).start();
		}
	}
	static class SalaEspera implements Runnable{
		
		@Override
		public void run() {
			try {
				ENTYSAL.acquire();
				System.out.println("El Cliente, ingresa al local");
				if(SILLAS.tryAcquire(1)) {
					try {
						System.out.println("El Cliente, tomá una silla");
					}finally {
						ENTYSAL.release();
					}
					try {
						SALA.acquire();
						if(BARBERO.tryAcquire(0)) {
							try {
								System.out.println("Un Cliente despierta al Barbero");
							}finally {
								BARBERO.release();
							}
						}
						LOOCK.acquire();
						System.out.println("Cliente, termina de ser atendido");
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}finally {
						SALA.release();
					}
				}else {
					try{
						System.out.println("El Cliente, se retira por que no encontro silla disponible");
					}finally {
						ENTYSAL.release();
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			try {
				ENTYSAL.acquire();
				System.out.println("El Cliente, se retira del local");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				ENTYSAL.release();
			}
		}
	}
	static class SalaBarbero implements Runnable{

		@Override
		public void run() {
			while(true) {
				if(SILLAS.availablePermits()<NUMEROSILLAS) {
					try {
						System.out.println("Barbero esta atendiendo al Cliente");
					}finally {
						SILLAS.release();
						LOOCK.release();
					}
				}else {
					try {
						System.out.println("no hay Clientes, el Barbero se duerme");
						BARBERO.acquire();
					}catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}
}