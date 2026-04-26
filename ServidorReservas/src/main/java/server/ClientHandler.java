package server;

import controller.AuditoriumDraftController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import server.handlers.AdminRequestHandler;
import server.handlers.AuditoriumDraftRequestHandler;
import service.Response;
import server.handlers.ClientRequestHandler;
import server.handlers.UserRequestHandler;
import server.handlers.ConnectionRequestHandler;
import server.handlers.EquipmentRequestHandler;
import server.handlers.ReservationDraftRequestHandler;
import server.handlers.ReservationRequestHandler;
import server.handlers.AuditoriumReservationRequestHandler;

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
            Response resp;

            switch (command.toUpperCase()) {
                // GESTION DE CLIENTES
                case "CREATE_CLIENT":
                case "UPDATE_CLIENT":
                case "DELETE_CLIENT":
                    resp = ClientRequestHandler.handle(command, obj);
                    break;
                    
                // GESTION DE ADMINS    
                case "CREATE_ADMIN":
                case "UPDATE_ADMIN":
                case "DELETE_ADMIN":
                    resp = AdminRequestHandler.handle(command, obj);
                    break;
                    
                // GESTION DE RESERVAS DE EQUIPO TEMPORALES   
                case "START_EQUIPMENT_DRAFT":
                case "UPDATE_EQUIPMENT_DRAFT":
                case "GET_EQUIPMENT_DRAFT_BY_ID":
                case "GET_EQUIPMENT_DRAFT_BY_CLIENT_ID":
                case "DISCARD_EQUIPMENT_DRAFT":
                case "CONFIRM_EQUIPMENT_DRAFT":
                case "GET_CALENDAR_BLOCKS":
                    resp = ReservationDraftRequestHandler.handle(command, obj);
                    break;
                    
                // GESTION DE RESERVAS DE AUDITORIO TEMPORALES     
                case "START_AUDITORIUM_DRAFT":
                case "UPDATE_AUDITORIUM_DRAFT":
                case "GET_AUDITORIUM_DRAFT_BY_CLIENT_ID":
                case "DISCARD_AUDITORIUM_DRAFT":
                case "CONFIRM_AUDITORIUM_DRAFT":
                case "GET_CALENDAR_AUDITORIUM":    
                    resp = AuditoriumDraftRequestHandler.handle(command, obj);
                    break;
                    
                // GESTION DE RESERVAS DE EQUIPO
                case "UPDATE_EQUIPMENT_RESERVATION":
                case "GET_RESERVATION_BY_ID":
                case "GET_RESERVATIONS_BY_CLIENT_ID":
                case "DELETE_RESERVATION_BY_ID":
                case "DELETE_RESERVATIONS_BY_CLIENT_ID":
                    resp = ReservationRequestHandler.handle(command, obj);
                    break;
                    
                // GESTION DE RESERVAS DE AUDITORIO            
                case "GET_AUDITORIUM_RESERVATION_BY_ID":
                case "GET_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID":
                case "DELETE_AUDITORIUM_RESERVATION_BY_ID":
                case "DELETE_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID":
                    resp = AuditoriumReservationRequestHandler.handle(command, obj);
                    break;  
                    
                // GESTION DE EQUIPOS  
                case "CREATE_EQUIPMENT":
                case "UPDATE_EQUIPMENT":
                case "GET_EQUIPMENT":
                case "GET_ALL_EQUIPMENT":
                case "DELETE_EQUIPMENT":
                case "GET_AVAILABLE_EQUIPMENT":    
                    resp = EquipmentRequestHandler.handle(command, obj);
                    break;
                    
                // VISTAS DE RESERVACION    
                case "ENTER_RESERVATIONS_VIEW":
                    Integer idClient = (Integer) obj;

                    this.loggedClientId = idClient;
                    this.viewingReservations = true;
                    resp = new Response(true, "Cliente registrado en vista de reservas", null);

                    logResponse(resp);
                    sendResponse(resp);
                    return;

                case "EXIT_RESERVATIONS_VIEW":
                    this.viewingReservations = false;

                    resp = new Response(true, "Cliente salió de vista de reservas", null);

                    logResponse(resp);
                    sendResponse(resp);
                    return;
                 
                // SESION     
                case "LOGIN":
                    resp = UserRequestHandler.handle(command, obj);
                    break;    
                case "LOGOUT":
                case "CLOSE_CONNECTION":
                    resp = ConnectionRequestHandler.handle(command, this);

                    logResponse(resp);
                    sendResponse(resp);

                    return;
                default:
                    resp = new Response(false, "Comando no reconocido", null);
                    break;
            }

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
