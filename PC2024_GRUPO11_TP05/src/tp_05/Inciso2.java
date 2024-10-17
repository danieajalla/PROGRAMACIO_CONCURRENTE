package tp_05;


public class Inciso2 {
	public static void main(String[] args) {
		Estacionamiento estacion = new Auto(1);
		estacion.Estacionar(new Auto(1));
	}

}
abstract class Estacionamiento {
	Object[] lista = new Object[20];
	abstract void Estacionar(Auto auto);
	abstract void Entrada();
	abstract void Salida();
}
class Auto extends Estacionamiento implements Runnable{
	private final int id;
	public Auto(int id) {
		this.id = id;
	}
	@Override
	public void run() {
		System.out.println("Hola1");
	}
	@Override
	void Estacionar(Auto auto) {
		lista[0]=auto;
		
	}
	@Override
	void Entrada() {
		// TODO Auto-generated method stub
		
	}
	@Override
	void Salida() {
		// TODO Auto-generated method stub
		
	}
}