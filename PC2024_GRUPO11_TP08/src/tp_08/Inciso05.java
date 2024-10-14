package tp_08;

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Inciso05 {
	 static BigInteger M = new BigInteger("1999");
	 public static void main(String[] args) {
		 long[] vector = new long[]{100477L, 105477L, 112986L, 100078L, 165987L, 142578L};
		 ExecutorService executor = Executors.newFixedThreadPool(2);
		 for(int i=0; i<vector.length; i++) {
			 Future<BigInteger> resultado = executor.submit(new TareaCalculo(vector[i]));
			 try {
				System.out.println("Resultado: " + resultado.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		 }
		 executor.shutdown();
	 }

	 private static class TareaCalculo implements Callable<BigInteger> {
	     private final long n;
	     public TareaCalculo(long n) {
	        this.n = n;
	     }
	     @Override
	     public BigInteger call() {
	         return compute(n);
	     }
	 }

	 private static BigInteger compute(long n) {
	     String s = "";
	     for (long i = 0; i < n; i++) {
	         s = s + n;
	     }
	     return new BigInteger(s.toString()).mod(M);
	 }
}
