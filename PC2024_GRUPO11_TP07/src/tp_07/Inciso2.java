package tp_07;

import java.util.concurrent.Semaphore;

public class Inciso2 {
	private static int A = 50;
    private static int B = 150;
    private static Semaphore semaforo = new Semaphore(1,true);
    private static Semaphore semaforo1 = new Semaphore(1,true);
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Tarea1());
        Thread t2 = new Thread(new Tarea2());
        t1.start();
        t2.start();
        System.out.println("Resultado final: A = " + A + ", B = " + B);
    }

    private static class Tarea1 implements Runnable {
        @Override
        public void run() {
        	while(true) {
        		 try {
                     semaforo.acquire();
                     A = A+100;
                     System.out.println("Tarea 1: variable1 = " +A);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 } finally {
                    semaforo.release();
                }
            	 try {
                     semaforo1.acquire();
                     B = B-100;
                     System.out.println("Tarea 1: variable2 = " +B);
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
                     semaforo.acquire();
                    A =(int) 1.21* A;
                    System.out.println("Tarea 2: variable1 = " +A);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 }finally {
                    semaforo.release();
                }
            	 try {
                     semaforo1.acquire();
                     B =(int) 0.5 * B;
                     System.out.println("Tarea 2: variable2 = " +B);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 } finally {
                    semaforo1.release();
                }
        	 }
        }
    }
}