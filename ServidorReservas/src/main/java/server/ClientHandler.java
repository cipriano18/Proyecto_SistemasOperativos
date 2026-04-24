package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.Response;
import server.handlers.ClientRequestHandler;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objectOutput;

    private boolean viewingReservations = false;
    private int loggedClientId = 0;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        try {
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.flush();
            objectInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error al inicializar streams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isViewingReservations() {
        return viewingReservations;
    }

    public int getLoggedClientId() {
        return loggedClientId;
    }

    public void send(Response resp) throws IOException {
        objectOutput.writeObject(resp);
        objectOutput.flush();
    }

    private void sendResponse(Response resp) throws IOException {
        send(resp);
    }

    public String getClientAddress() {
        return socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            System.out.println("Hilo iniciado para: " + getClientAddress());

            while (true) {
                Object commandObj = objectInput.readObject();

                if (!(commandObj instanceof String)) {
                    System.out.println("Error: se esperaba un comando String y se recibió: "
                            + (commandObj != null ? commandObj.getClass().getName() : "null"));

                    sendResponse(new Response(false, "Protocolo inválido", null));
                    continue;
                }

                String command = (String) commandObj;
                Object obj = objectInput.readObject();

                System.out.println("\n==============================");
                System.out.println("Petición recibida de: " + getClientAddress());
                System.out.println("Comando: " + command);
                System.out.println("Tipo de objeto: " + (obj != null ? obj.getClass().getName() : "null"));
                System.out.println("Contenido del objeto: " + obj);
                System.out.println("==============================\n");

                processRequest(command, obj);
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + getClientAddress());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al leer objeto: " + e.getMessage());
        } finally {
            Server.clients.remove(this);
            closeConnection();
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(String command, Object obj) {
        try {
            Response resp = ClientRequestHandler.handle(command, obj);

            logResponse(resp);
            sendResponse(resp);

        } catch (ClassCastException e) {
            System.out.println("Error de tipo al procesar petición: " + e.getMessage());
            e.printStackTrace();

            try {
                sendResponse(new Response(false, "Tipo de objeto inválido para el comando recibido", null));
            } catch (IOException ex) {
                System.out.println("No se pudo enviar respuesta de error al cliente");
                ex.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Error procesando petición: " + e.getMessage());
            e.printStackTrace();

            try {
                sendResponse(new Response(false, "Error interno del servidor: " + e.getMessage(), null));
            } catch (IOException ex) {
                System.out.println("No se pudo enviar respuesta de error al cliente");
                ex.printStackTrace();
            }
        }
    }

    private void logResponse(Response resp) {
        System.out.println("Success: " + resp.isSuccess());
        System.out.println("Mensaje: " + resp.getMessage());
        System.out.println("Data: " + resp.getData());
    }
}