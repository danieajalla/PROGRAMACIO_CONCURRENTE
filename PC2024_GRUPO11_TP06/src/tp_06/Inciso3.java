package tp_06;

import java.util.concurrent.Semaphore;

public class Inciso3 {
    private static final Semaphore cola = new Semaphore(50,true);
    private static Semaphore cabina = new Semaphore(1,true);
    private static int numerocabina=3;
	public static void main(String[] args) {
		for(int i=1; i<=numerocabina; i++) {
			new Thread(new Cabina(i)).start();
		}
	}
	static class Cabina implements Runnable{
		private final int id;
		static Semaphore semaforo = new Semaphore(1,true);
		static int cliente=0;
		private int contador;
		public Cabina(int id) {
			this.id = id;
			this.contador = 0;
		}

		@Override
		public void run() {
			try {
				if(semaforo.tryAcquire()) {
					System.out.println("'Cabina "+id+"' NO DISPONIBLE!!");
					Thread.sleep(15000);
					System.out.println("'Cabina "+id+"' DISPONIBLE!!");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			while(cola.tryAcquire()) {
				try {
					cabina.acquire();
					cliente++;
					contador = cliente;
				} catch (InterruptedException e) {
					 Thread.currentThread().interrupt();
				}finally {
					System.out.println("Cliente "+contador+" siendo atendido en la Cabina "+id);
					cabina.release();
				}
				try {
					Thread.sleep(((int) (Math.random()*3)+1)*1000);
					cabina.acquire();
					System.out.println("Cliente "+contador+" finaliza atencion");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}finally {
					cabina.release();
				}
			}
		}
	}
}
