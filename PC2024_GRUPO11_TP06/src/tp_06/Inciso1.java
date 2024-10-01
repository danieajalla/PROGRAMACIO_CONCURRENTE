package tp_06;
import java.util.concurrent.Semaphore;

public class Inciso1 {
    private static final Semaphore entrada = new Semaphore(2,true);
    private static final Semaphore salida = new Semaphore(2,true);
    private static final Semaphore estacionamiento = new Semaphore(20,true);
    public static void main(String[] args) {
        for (int i = 1; i <= 100; i++) {
        	try {
        		entrada.acquire();
        		System.out.println("Auto n°" + i + " ingresando");
				new Thread(new AutoMovil(i)).start();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
            
        }
    }
    static class AutoMovil implements Runnable {
        private final int id;
        public AutoMovil(int id) {
            this.id = id;
        }
        @Override
        public void run() {
            try {
            	estacionamiento.acquire();
            	System.out.println("Auto n°" + id + " estacionado");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
            	entrada.release();
            }
            try{
                Thread.sleep((long) (Math.random() * 5));
                System.out.println("Auto n°" + id + " abandona el lugar");
            	salida.acquire();
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
            	estacionamiento.release();
            	System.out.println("Auto n°" + id + " saliendo");
            	salida.release();
            }
        }
    }
}