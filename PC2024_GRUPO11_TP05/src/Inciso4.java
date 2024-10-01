package TP05;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class Inciso4 {
	private static Clock clock = Clock.systemDefaultZone();
	private static final int VEINTE = 20;
	private static final int QUINCE = 15;
	public static void main(String[] args) {
		long tiempo =clock.millis();
		List<Thread>listo = new ArrayList<Thread>();
		int[][] Matriz_A = Inicializar_Matriz(VEINTE,QUINCE);
		int[][] Matriz_B = Inicializar_Matriz(QUINCE,VEINTE);
		int[][] Matriz_C = new int[VEINTE][VEINTE];
		System.out.println("___________Matriz_A___________");
		Mostrar(Matriz_A,Matriz_A[0].length);
		System.out.println("___________Matriz_B___________");
		Mostrar(Matriz_B,Matriz_B[0].length);
		System.out.println();
		for(int columna=0; columna<VEINTE; columna++) {
			for(int fila=0; fila<VEINTE; fila++) {
				Thread Vector = new Thread(new CalculoConcurrente(Matriz_A, Matriz_B, Matriz_C,columna,fila));
				listo.add(Vector);
				Vector.start();
			}
		}
		for(Thread vector : listo) {
			try {
				vector.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("___________Matriz_C___________");
		Mostrar(Matriz_C,Matriz_C[0].length);
		System.out.println("El Tiempo de ejecución es de " + (int) (clock.millis()-tiempo) + " ms.");
	}
	private static void Mostrar(int[][] matriz, int tamaño) {
		System.out.println();
		for(int columna=0; columna<matriz.length; columna++) {
			for(int fila=0; fila<matriz[0].length; fila++) {
				int dato = matriz[columna][fila];
				System.out.printf("| %2d ", dato);
			}
			System.out.println("|");
		}
		System.out.println();
	}
	private static int[][] Inicializar_Matriz(int filas, int columnas){
		int[][] Matriz_Helper = new int[filas][columnas];
		for(int columna=0; columna<columnas; columna++) {
			for(int fila=0; fila<filas; fila++) {
				Matriz_Helper[fila][columna] = (int)(Math.random()*11)+5;
			}
		}
		return Matriz_Helper;
	}
	
}
class CalculoConcurrente extends Operaciones implements Runnable{
	private int[][] Matriz_A;
	private int[][] Matriz_B;
	private int[][] Matriz_C;
	private int columna;
	private int fila;
	public CalculoConcurrente(int[][] matriz_A, int[][] matriz_B, int[][] matriz_C, int columna, int fila) {
		this.Matriz_A = matriz_A;
		this.Matriz_B = matriz_B;
		this.Matriz_C = matriz_C;
		this.columna = columna;
		this.fila = fila;
	}

	@Override
	public void run() {
		int suma = 0;
		for(int pos=0; pos<Matriz_B.length; pos++) {
			suma += ((Matriz_A[fila][pos])*(Matriz_B[pos][columna]));
		}
		suma = (int) SumRootN(suma);
		synchronized(Matriz_C) {
			Matriz_C[columna][fila] = suma;
		}
	}
	
}
class Operaciones{
	public static double SumRootN(int root) {
		double result = 0;
		for(int i=0; i<10000000; i++) {
			result += Math.exp(Math.log(i)/root);
		}
		return result;
	}
}