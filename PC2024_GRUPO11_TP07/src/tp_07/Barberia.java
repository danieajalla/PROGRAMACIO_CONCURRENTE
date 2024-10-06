package tp_07;

import java.util.concurrent.Semaphore;

public class Barberia {
	private static final Semaphore atendido = new Semaphore(0,true);
	public static void main(String[] args) {
		Salon salon = new Salon();
		Runnable barbero = new Barbero(salon,atendido);
		new Thread(barbero).start();
		for(int i=1; i<=10; i++) {
			new Thread(new Cliente(salon,i,atendido)).start();
		}
	}

}
class Barbero implements Runnable{
	private final Salon salon;
	private static Semaphore atendido;
	public Barbero(Salon salon, Semaphore atendido) {
		this.salon = salon;
		Barbero.atendido = atendido;
	}
	@Override
	public void run() {
		while(true) {
			try {
				salon.SalaCorte();
				Thread.sleep(2000);
			}catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}finally {
				atendido.release();
			}
		}
		
	}
	
}
class Cliente implements Runnable{
	private static final Semaphore entrada = new Semaphore(1,true);
	private static final Semaphore salida = new Semaphore(1,true);
	private static final Semaphore puerta = new Semaphore(1,true);
	private final Salon salon;
	private final int id;
	private static Semaphore atendido;
	public Cliente(Salon salon, int id, Semaphore atendido) {
		this.salon = salon;
		this.id = id;
		Cliente.atendido = atendido;
	}
	@Override
	public void run() {
		boolean salir=false;
		try {
			entrada.acquire();
			puerta.acquire();
			System.out.println("Cliente "+id+", ingeresa a la Barberia");
			salir = salon.SalaEspera(id);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}finally {
			puerta.release();
			entrada.release();
		}
		try {
			if(salir==true) {
				atendido.acquire();
				System.out.println("Cliente "+id+", finaliza corte");
			}
			salida.acquire();
			puerta.acquire();
			System.out.println("Cliente "+id+" se retira de la Barberia");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}finally {
			puerta.release();
			salida.release();
		}
	}
}
class Salon{
	private static final Semaphore sillas = new Semaphore(5,true);
	private static final Semaphore barbero = new Semaphore(0,true);
	private static boolean durmiendo = true;
	public boolean SalaEspera(int id) {
		if(sillas.tryAcquire()) {
			System.out.println("Cliente "+id+", ocupa una silla");
			if(durmiendo==true) {
				System.out.println("Cliente "+id+", despierta al Barbero");
				durmiendo = false;
			}
			barbero.release();
			return true;
		}else {
			System.out.print("No hay silla, ");
			return false;
		}
	}
	public void SalaCorte() {
		try {
			if(!barbero.tryAcquire()) {
				System.out.println("No hay Cliente, Barbero se duerme");
				durmiendo = true;
				barbero.acquire();
			}
			durmiendo = false;
			System.out.println("Barbero, esta atendiendo");
			sillas.release();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}