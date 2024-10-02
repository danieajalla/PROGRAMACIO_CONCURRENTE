package tp_07;

import java.util.concurrent.Semaphore;

public class Inciso3 {
	static  Semaphore semaforo = new Semaphore(1,true);
	static Semaphore semaforo2 = new Semaphore(1,true);
	private static int NCUADRANTE=4;
	public static void main(String[] args) {
		for(int i=1; i<=NCUADRANTE; i++) {
			new Thread(new Cuadrante(i)).start();
		}
	}
	private static class Cuadrante implements Runnable{
		 int id;
		public Cuadrante(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			if(id==1) {
				try {
					semaforo.acquire();
					semaforo2.acquire();
					System.out.println("Auto va hacia el norte");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					semaforo.release();
					semaforo2.release();
				}
			}
			else if(id==2) {
				try {
					semaforo.acquire();
					semaforo2.acquire();
					System.out.println("Auto va hacia el oeste");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					semaforo.release();
					semaforo2.release();
				}
			}
			else if(id==3) {
				try {
					semaforo.acquire();
					semaforo2.acquire();
					System.out.println("Auto va hacia el sur");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					semaforo.release();
					semaforo2.release();
				}
			}
			else if(id==4) {
				try {
					semaforo.acquire();
					semaforo2.acquire();
					System.out.println("Auto va hacia el este");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					semaforo.release();
					semaforo2.release();
				}
			}
		}
		
	}
}