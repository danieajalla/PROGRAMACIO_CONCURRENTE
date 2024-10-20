package practica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurante {
	private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final AtomicInteger idCliente = new AtomicInteger(1);
	public static void main(String[] args) {
		scheduler.schedule(new LlegadaCliente(), 0, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> {
            scheduler.shutdown();
            executor.shutdown();
        }, 5, TimeUnit.MINUTES);
	}
	static class LlegadaCliente implements Runnable{

		@Override
		public void run() {
			int id = idCliente.getAndIncrement();
			List<String> pedidos = new ArrayList<>();
			int cantidadpedidos = ThreadLocalRandom.current().nextInt(1,4);
			for(int i=0; i<cantidadpedidos; i++) {
				String pedido = GenerandoPedido();
				pedidos.add(pedido);
			}
			Cliente cliente = new Cliente(id, pedidos);
			for(String plato: pedidos) {
				executor.submit(new Platillos(cliente.getId(),plato));
			}
			int tiempo = ThreadLocalRandom.current().nextInt(100,251);
			scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
		}

		private String GenerandoPedido() {
			List<String> pedidos = Arrays.asList("Entrante", "Plato Principal", "Postre");
			return pedidos.get(ThreadLocalRandom.current().nextInt(pedidos.size()));
		}
	}
	static class Platillos implements Runnable {
		private final int id;
		private final String plato;
		public Platillos(int id, String plato) {
			this.id = id;
			this.plato = plato;
		}

		@Override
		public void run() {
			try {
                int min, max;
                switch (plato) {
                    case "Entrante":
                        min = 500;
                        max = 700;
                        break;
                    case "Plato Principal":
                        min = 800;
                        max = 1200;
                        break;
                    case "Postre":
                        min = 400;
                        max = 600;
                        break;
                    default:
                        throw new IllegalStateException("Pedido desconocido: " + plato);
                }
                System.out.println("Chef preparando " + plato + " para Cliente " + id + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(min, max + 1));
                System.out.println("Plato " + plato + " para el Cliente " + id + " lista.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
		}
	}
	static class Cliente {
		private final int id;
		private final List<String> pedidos;
		public Cliente(int id, List<String> pedidos) {
			this.id = id;
			this.pedidos = pedidos;
		}
		public int getId() {
			return id;
		}
		public List<String> getPedidos() {
			return pedidos;
		}
	}
}
