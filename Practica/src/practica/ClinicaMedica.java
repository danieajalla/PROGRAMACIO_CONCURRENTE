package practica;
import java.util.concurrent.*;

public class ClinicaMedica {
    private static final int T = 200; // Tiempo base en milisegundos
    private static final int NUM_PACIENTES = 50;
    private static final Semaphore recepcionistas = new Semaphore(2, true);
    private static final Semaphore medicos = new Semaphore(3, true);
    private static final Semaphore equiposPruebas = new Semaphore(4, true);
    private static final Semaphore equiposResultados = new Semaphore(2, true);
    private static final ExecutorService consultorios = Executors.newFixedThreadPool(3); // 3 consultorios

    public static void main(String[] args) {
        for (int i = 1; i <= NUM_PACIENTES; i++) {
            int idPaciente = i;
            consultorios.submit(() -> {
                try {
                    ejecutarFase("Registro", idPaciente, T, () -> FaseRegistro(idPaciente));
                    ejecutarFase("Consulta", idPaciente, 2 * T, () -> FaseConsulta(idPaciente));
                    ejecutarFase("Pruebas", idPaciente, T / 2, () -> FasePruebas(idPaciente));
                    ejecutarFase("Resultados", idPaciente, T, () -> FaseResultados(idPaciente));
                } catch (InterruptedException e) {
                    System.out.println("Paciente " + idPaciente + " fue interrumpido durante la atención.");
                    Thread.currentThread().interrupt();
                }
            });
        }
        consultorios.shutdown();
    }

    private static void ejecutarFase(String nombreFase, int idPaciente, int tiempo, Runnable fase) throws InterruptedException {
        System.out.println("Paciente " + idPaciente + " inicia " + nombreFase + ".");
        fase.run();
        Thread.sleep(tiempo);
        System.out.println("Paciente " + idPaciente + " completa " + nombreFase + ".");
    }

    private static void FaseRegistro(int idPaciente) {
        try {
            recepcionistas.acquire();
            System.out.println("Paciente " + idPaciente + " en Fase de Registro.");
        } catch (InterruptedException e) {
            System.out.println("Paciente " + idPaciente + " fue interrumpido durante la Fase de Registro.");
            Thread.currentThread().interrupt();
        } finally {
            recepcionistas.release();
        }
    }

    private static void FaseConsulta(int idPaciente) {
        try {
            medicos.acquire();
            System.out.println("Paciente " + idPaciente + " en Fase de Consulta.");
        } catch (InterruptedException e) {
            System.out.println("Paciente " + idPaciente + " fue interrumpido durante la Fase de Consulta.");
            Thread.currentThread().interrupt();
        } finally {
            medicos.release();
        }
    }

    private static void FasePruebas(int idPaciente) {
        try {
            equiposPruebas.acquire();
            System.out.println("Paciente " + idPaciente + " en Fase de Pruebas.");
        } catch (InterruptedException e) {
            System.out.println("Paciente " + idPaciente + " fue interrumpido durante la Fase de Pruebas.");
            Thread.currentThread().interrupt();
        } finally {
            equiposPruebas.release();
        }
    }

    private static void FaseResultados(int idPaciente) {
        try {
            equiposResultados.acquire();
            System.out.println("Paciente " + idPaciente + " en Fase de Resultados.");
        } catch (InterruptedException e) {
            System.out.println("Paciente " + idPaciente + " fue interrumpido durante la Fase de Resultados.");
            Thread.currentThread().interrupt();
        } finally {
            equiposResultados.release();
            System.out.println("Paciente " + idPaciente + " ha completado todas las fases.");
        }
    }
}