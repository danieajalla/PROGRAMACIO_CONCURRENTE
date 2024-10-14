package tp_08;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Inciso04 {
	private static final String DIRECTORIO = "C:/Users/danie/OneDrive/Escritorio/PC/Archivo"; 
    private static Set<String> archivosExistentes = new HashSet<>();

    public static void main(String[] args) {
        ScheduledExecutorService servicio = Executors.newScheduledThreadPool(1);
        Runnable tarea = new Runnable() {
            @Override
            public void run() {
                chequearNuevosArchivos();
            }
        };

        cargarArchivosExistentes();
        servicio.scheduleAtFixedRate(tarea, 0, 5, TimeUnit.SECONDS);
    }

    private static void cargarArchivosExistentes() {
        File directorio = new File(DIRECTORIO);
        if (directorio.exists() && directorio.isDirectory()) {

            File[] archivos = directorio.listFiles();

            System.out.println("Contenido de la carpeta:");
            for (File archivo : archivos) {
                if (archivo.isFile()) {
                    System.out.println("Archivo: " + archivo.getName());
                } else if (archivo.isDirectory()) {
                    System.out.println("Carpeta: " + archivo.getName());
                }
                archivosExistentes.add(archivo.getName());
            }
        }
    }

    private static void chequearNuevosArchivos() {
        File directorio = new File(DIRECTORIO);
        if (directorio.exists() && directorio.isDirectory()) {
            for (File archivo : directorio.listFiles()) {
                if (!archivosExistentes.contains(archivo.getName())) {
                    archivosExistentes.add(archivo.getName());
                    System.out.println("Nuevo archivo [" + archivo.getName() + "], con tamaño [" + archivo.length() + " bytes]");
                }
            }
        }
    }
}
