package tp_05;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Clase que representa una tarea que realiza una solicitud HTTP GET a una URL específica,
 * parsea el contenido HTML utilizando Jsoup y extrae elementos específicos.
 *
 * @author [Ajalla_Daniel]
 */
public class Tarea implements Runnable {
	static Clock clock = Clock.systemDefaultZone();
    /**
     * Identificador de la tarea.
     */
    private int id;
    public static long suma = 0;
    /**
     * Arreglo de noticias.
     */
    private String[] noticias;
    /**
     * Constructor para la Tarea.
     *
     * @param id     Identificador de la tarea.
     * @param noticias Arreglo de noticias.
     */
    public Tarea(int id, String[] noticias) {
        this.id = id;
        this.noticias = noticias;
    }
    /**
     * Método que ejecuta la tarea. Realiza una solicitud HTTP GET a la URL específica,
     * parsea el contenido HTML utilizando Jsoup y extrae elementos específicos.
     */
    @Override
    public void run() {
        synchronized (noticias) {
        	long tiempo =clock.millis();
        	// Construye la URL para la solicitud HTTP GET
            String url = "https://eltribunodejujuy.com" + noticias[id];
            // Realiza la solicitud HTTP GET utilizando HttpClient
            try {
                HttpClient cliente = HttpClient.newHttpClient();
                HttpRequest solicitud = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
                String nota = respuesta.body();
             // Parsea el contenido HTML utilizando Jsoup
                Document documento = Jsoup.parse(nota);
             // Extrae elementos específicos del documento
                Elements elementos = documento.select("div[amp-access=mostrarNota]");
             // Imprime los elementos extraídos
                System.out.println("-------------------------------------------------");
                System.out.println();
                System.out.println(elementos);
            } catch (IOException | InterruptedException e) {
            	// Maneja excepciones
                System.err.println("Error: " + e.getMessage());
            } finally {
            	// Mide el tiempo de ejecución de cada tarea
            	long sumas = (clock.millis()-tiempo);
            	System.out.println();
                System.out.println("El Tiempo de ejecución es de " + (int) sumas + " ms.");
                suma += sumas;
            }
        }
    }
}
