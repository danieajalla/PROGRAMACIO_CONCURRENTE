package tp_07;
import java.util.concurrent.Semaphore;

public class Ejercicio3 {
	static Semaphore semaforoNorte, semaforoSur, semaforoEste, semaforoOeste;
	static Auto N, S, E, O;
	
	public static void main(String[] args) {
		semaforoNorte = new Semaphore(1);
		semaforoSur = new Semaphore(1);
		semaforoEste = new Semaphore(0);
		semaforoOeste = new Semaphore(0);

		N = new Auto("norte", semaforoNorte, semaforoEste );
		S = new Auto("sur"  , semaforoSur  , semaforoOeste);
		E = new Auto("este" , semaforoEste , semaforoSur  );
		O = new Auto("oeste", semaforoOeste, semaforoNorte);
		
		N.start();
		S.start();
		E.start();
		O.start();
	}
	
	public static class Auto extends Thread{
		String direccion;
		Semaphore semaforoDireccion, semaforoIzquierda;
		
		public Auto(String direccion, Semaphore semaforoDireccion, Semaphore semaforoIzquierda) {
			super();
			this.direccion = direccion;
			this.semaforoDireccion = semaforoDireccion;
			this.semaforoIzquierda = semaforoIzquierda;
		}
		
		public void run() {
			try {
				semaforoDireccion.acquire();
				System.out.println("El auto que se mueve en dirección "+direccion+" comienza a avanzar.");
				sleep(1000);
				System.out.println("El auto que se mueve en dirección "+direccion+" cruzó la intersección.");
				semaforoIzquierda.release();
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
}
