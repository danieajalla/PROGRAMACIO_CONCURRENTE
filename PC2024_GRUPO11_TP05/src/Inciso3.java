package TP05;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/**
 * @author [Ajalla_Daniel]
 */
public class Inciso3 {
	public static void main(String[] args) {
		 // URL del sitio web a extraer noticias
        String url = "https://eltribunodejujuy.com/seccion/policiales";
        // Número máximo de noticias a extraer
        int maxNoticias = 10;
        
        try {
            // Conecta a la URL y obtiene el documento HTML
            Document documento = Jsoup.connect(url).get();
            
            // Selecciona los enlaces de noticias con la clase "nota__media-degrade"
            Elements enlaces = documento.select("a.nota__media-degrade");
            
            // Crea un arreglo para almacenar los enlaces de noticias
            String[] noticias = new String[Math.min(enlaces.size(), maxNoticias)];
            
            // Itera sobre los enlaces y almacena en el arreglo
            for (int i = 0; i < noticias.length; i++) {
                noticias[i] = enlaces.get(i).attr("href");
            }
            // Crea una lista para almacenar los hilos
            List<Thread> listo = new ArrayList<>();
            // Registra el tiempo inicial de ejecución
            // Crea y ejecuta hilos para procesar las noticias en paralelo
            for (int i = 0; i < maxNoticias; i++) {
                // Crea un hilo con la tarea de procesar una noticia
                Thread tarea = new Thread(new Tarea(i, noticias));
                listo.add(tarea);
                tarea.start(); // Inicia el hilo
            }
            // Espera a que todos los hilos finalicen
            for (Thread tarea : listo) {
	            try {
	            		tarea.join();
	            } catch (InterruptedException e) {
	                // Maneja la excepción de interrupción
	                e.printStackTrace();
	            }
            }
         // Imprime el tiempo de ejecución
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("El Tiempo Total de ejecución es de " + (int) Tarea.suma + " ms.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
	    
}