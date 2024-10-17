package practica;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Cafeteria {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ExecutorService baristas = Executors.newFixedThreadPool(4); 
    private static final AtomicInteger idClientes = new AtomicInteger(1);
    private static final ConcurrentLinkedQueue<Integer> baristasLibres = new ConcurrentLinkedQueue<>(Arrays.asList(1, 2, 3, 4));
    public static void main(String[] args) {
        scheduler.schedule(new LlegadaCliente(), 0, TimeUnit.MILLISECONDS);
        scheduler.schedule(() -> {
            scheduler.shutdown();
            baristas.shutdown();
        }, 1, TimeUnit.MINUTES);
    }

    static class LlegadaCliente implements Runnable {
        @Override
        public void run() {
            int idCliente = idClientes.getAndIncrement();
            int numeroPedidos = ThreadLocalRandom.current().nextInt(1, 4);
            List<String> pedidos = new ArrayList<>();
            System.out.print("Cliente " + idCliente + " llegó y pidió: ");
            for (int i = 1; i <= numeroPedidos; i++) {
                String pedido = GenerandoPedido();
                pedidos.add(pedido);
                System.out.print(pedido + ((numeroPedidos > i) ? ", " : ".\n"));
            }

            Cliente cliente = new Cliente(idCliente, pedidos);
            for (String pedido : pedidos) {
                baristas.submit(new Bebida(cliente.getId(), pedido));
            }

            int tiempo = ThreadLocalRandom.current().nextInt(100, 251);
            scheduler.schedule(this, tiempo, TimeUnit.MILLISECONDS);
        }

        private String GenerandoPedido() {
            List<String> bebidas = Arrays.asList("Café", "Té", "Chocolate Caliente");
            return bebidas.get(ThreadLocalRandom.current().nextInt(bebidas.size()));
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

    static class Bebida implements Runnable {
        private final int clienteId;
        private final String tipoBebida;

        public Bebida(int clienteId, String tipoBebida) {
            this.clienteId = clienteId;
            this.tipoBebida = tipoBebida;
        }

        @Override
        public void run() {
        	int id = baristasLibres.poll();
            try {
                int min, max;
                switch (tipoBebida) {
                    case "Café":
                        min = 300;
                        max = 500;
                        break;
                    case "Té":
                        min = 200;
                        max = 400;
                        break;
                    case "Chocolate Caliente":
                        min = 400;
                        max = 600;
                        break;
                    default:
                        throw new IllegalStateException("Pedido desconocido: " + tipoBebida);
                }
                System.out.println("Barista " + id + " preparando " + tipoBebida + " para Cliente " + clienteId + ".");
                Thread.sleep(ThreadLocalRandom.current().nextInt(min, max + 1));
                System.out.println("Bebida " + tipoBebida + " para el Cliente " + clienteId + " lista.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
            	baristasLibres.add(id);
            }
        }
    }
}