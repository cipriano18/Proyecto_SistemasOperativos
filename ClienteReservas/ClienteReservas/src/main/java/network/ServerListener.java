package network;

import javafx.application.Platform;
import service.Response;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author cipriano
 */
public class ServerListener extends Thread {

    private final ServerConnection connection;
    private boolean running = true;

    public ServerListener(ServerConnection connection) {
        this.connection = connection;
        setDaemon(true);
    }

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
                System.out.println("Listener del servidor detenido: " + e.getMessage());
                running = false;
            }
        }
    }

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

    public void stopListener() {
        running = false;
        interrupt();
    }
}
