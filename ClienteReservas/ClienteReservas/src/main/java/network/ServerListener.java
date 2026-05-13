package network;

import javafx.application.Platform;
import service.Response;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Escucha mensajes asincronos enviados por el servidor.
 */
public class ServerListener extends Thread {

    private final ServerConnection connection;
    private boolean running = true;

    /**
     * Crea un listener asociado a una conexion existente.
     *
     * @param connection conexion desde la cual se leeran respuestas
     */
    public ServerListener(ServerConnection connection) {
        this.connection = connection;
        setDaemon(true);
    }

    /**
     * Inicia el ciclo de escucha de objetos enviados por el servidor.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Object obj = connection.getObjectInput().readObject();

                if (obj instanceof Response) {
                    Response response = (Response) obj;
                    handleResponse(response);
                }

            } catch (Exception e) {
                System.out.println(
                        "Listener del servidor detenido: " 
                        + e.getMessage());
                running = false;
            }
        }
    }

    /**
     * Procesa una respuesta recibida y la enruta segun su tipo.
     *
     * @param response respuesta recibida desde el servidor
     */
    private void handleResponse(Response response) {

        String message = response.getMessage();
        if ("RESERVATION_DRAFT_EXPIRED".equals(message)
                || "AUDITORIUM_DRAFT_EXPIRED".equals(message)) {
            Platform.runLater(() -> {
                System.out.println("Broadcast recibido: " + message);
                ReservationNotificationHandler.notifyDraftExpired(response);
            });
            return;
        }

        ResponseStore.setResponse(response);
    }

    /**
     * Detiene el listener y marca el hilo para interrupcion.
     */
    public void stopListener() {
        running = false;
        interrupt();
    }
}
