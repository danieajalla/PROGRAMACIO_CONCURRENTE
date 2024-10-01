package TP04;

public class Inciso3 {

	public static void main(String[] args) {
		Zoologico zoologico = new Zoologico();
		int j = 1;
		while(true) {
			new Persona(j, zoologico).start();
			try {
				Thread.sleep(((int)(Math.random()*101)+100));
				j++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
class Persona extends Thread{
	 int id;
	final Zoologico zoologico;
	public Persona(int id,Zoologico zoologico) {
		this.id = id;
		this.zoologico = zoologico;
	}
	public void run() {
		zoologico.Entrar(id);
		zoologico.Permanece(id);
		zoologico.Salir(id);
	}
}
class Zoologico{	
	public Zoologico() {
	}
	public void Entrar(int id) {
		System.out.println("Persona n°"+id+" se derige a la Entrada...");
		synchronized(this) {
			try {
				System.out.println("Persona n°"+id+" ocupa pasillo");
				Thread.sleep(50);
				System.out.println("Persona n°"+id+" libera pasillo");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Persona n°"+id+" ingreso al Zoologico");
	}
	public void Permanece(int id) {
		int tiempo = ((int)(Math.random()*301)+400);
		try {
			System.out.println("Persona n°"+id+" permanese en el Zoologico durante "+ tiempo+"ms.");
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	public void Salir(int id) {
		System.out.println("Persona n°"+id+" se derige a la Salida...");
		synchronized(this) {
			try {
				System.out.println("Persona n°"+id+" ocupa pasillo");
				Thread.sleep(50);
				System.out.println("Persona n°"+id+" libera pasillo");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Persona n°"+id+" salio del Zoologico");
	}
	
}
