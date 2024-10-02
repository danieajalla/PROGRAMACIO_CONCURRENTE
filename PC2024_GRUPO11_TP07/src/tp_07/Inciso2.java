package tp_07;

import java.util.concurrent.Semaphore;

public class Inciso2 {
	private static int X = 0;
    private static Semaphore semaforo1 = new Semaphore(1,true);
    private static Semaphore semaforo2 = new Semaphore(0,true);
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Tarea1()).start();
        new Thread(new Tarea2()).start();
        new Thread(new Tarea3()).start();
    }

    private static class Tarea1 implements Runnable {
        @Override
        public void run() {
        	while(true) {
        		 try {
                     semaforo2.acquire();
                     semaforo1.acquire();
                     X = 2*X;
                     System.out.println("Tarea 1: valor X = " +X);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 } finally {
                    semaforo1.release();
                }
        	}
        }
    }

    private static class Tarea2 implements Runnable {
        @Override
        public void run() {
        	 while(true) {
        		 try {
                     semaforo1.acquire();
                     X = X*X;
                    System.out.println("Tarea 2: valor X = " +X);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 }finally {
                    semaforo1.release();
                }
        	 }
        }
    }
    private static class Tarea3 implements Runnable {
        @Override
        public void run() {
        	while(true) {
        		 try {
                     semaforo1.acquire();
                     X = X + 3;
                     System.out.println("Tarea 3: valor X = " +X);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 } finally {
                    semaforo2.release();
                    semaforo1.release();
                }
        	}
        }
    }
}