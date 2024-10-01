package TP04;

public class Inciso2 {

	public static void main(String[] args) {
		Mostrador mostrador = new Mostrador();
		Producto[] producto = {new Producto("Bizcocho",0),new Producto("Factura",0)};
		Horno[] horno = {new Horno(false,producto),new Horno(true,producto)};
		Productor productor = new Productor(horno,producto);
		productor.start();
		int j = 1;
		while(true) {
			Consumidor consumidor = new Consumidor(j,producto,mostrador);
			System.out.println("El cliente"+j+" llega a la Panaderia...");
			consumidor.start();
			try {
				Thread.sleep(((int) Math.random()*701)+800);
				j++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		

}
class Productor extends Thread {
	private Producto[] producto;
	public Productor(Horno[] hornos, Producto[] producto) {
		for(int i=0; i<hornos.length; i++) {
			hornos[i].start();
		}
		this.producto = producto;
	}
	public void run() {
		while(true) {
			synchronized(producto) {
				try {
					if(producto[0].getNproducto()==0 || producto[1].getNproducto()==0) {
						System.out.println();
						System.out.println("______MOSTRADOR______ \n Bizcocho " 
						+ producto[0].getNproducto() + "\n Factura " 
							+ producto[1].getNproducto());
						System.out.println();
						producto.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
class Consumidor extends Thread{
	private final int id;
	private Producto[] producto;
	private Mostrador mostrador;
	public Consumidor(int id, Producto[] producto, Mostrador mostrador) {
		super();
		this.id = id;
		this.producto = producto;
		this.mostrador = mostrador;
	}
	public void run() {
		mostrador.Turno(id, producto);
	}	
}
class Horno extends Thread{
	private boolean libre;
	private Producto[] producto;
	public Horno(boolean libre, Producto[] producto) {
		this.libre = libre;
		this.producto = producto;
	}
	public void run() {
		while(true) {
			synchronized(producto){
				if(Math.abs(producto[0].getNproducto()-producto[1].getNproducto())>=1) {
					if(producto[0].getNproducto()>producto[1].getNproducto()) {
						libre=true;
					}
					else {
						libre=false;
					}
				}
			}
			try {
				if(libre==true) {
					sleep((int) ((Math.random()*301)+1000));
					synchronized(producto) {
						producto[1].setNproducto(1);
						System.out.println("¡¡¡ 1 Factura listo !!!");
						producto.notifyAll();
					}
					
				}else {
					sleep((int) ((Math.random()*201)+400));
					synchronized(producto) {
						producto[0].setNproducto(1);
						System.out.println("¡¡¡ 1 Bizcocho listo !!!");
						producto.notifyAll();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
class Mostrador {
	public Mostrador() {
		
	}
	public void Turno(int id,Producto[] producto) {
		System.out.println("El Cliente"+id+" en fila...");
		synchronized(this) {
			System.out.println("El Cliente"+id+" espera que su pedido este en el mostrador...");
			try {
				synchronized(producto) {
					while(producto[0].getNproducto()==0 || producto[1].getNproducto()==0) {
						producto.wait();
					}
					System.out.println("El Cliente"+id+" esta comprando...");
					Thread.sleep(((int) Math.random()*201)+200);
					producto[0].setNproducto(-1);
					producto[1].setNproducto(-1);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Cliente"+id+" finaliza su compra...");
		}
	}
}
class Producto {
	private String producto;
	private int Nproducto;
	public Producto(String producto, int nproducto) {
		super();
		this.producto = producto;
		this.Nproducto = nproducto;
	}
	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
	public int getNproducto() {
		return Nproducto;
	}
	public void setNproducto(int nproducto) {
		this.Nproducto += nproducto;
	}
}

