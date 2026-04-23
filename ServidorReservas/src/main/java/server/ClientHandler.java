package server;

import controller.ClientController;
import controller.ReservationDraftController;
import controller.UserController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.CalendarRequest;
import model.ClientRequest;
import model.EquipmentReservationDraftRequest;
import model.Response;
import model.User;

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

            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar socket: " + e.getMessage());
            }
        }
    }

    private void processRequest(String command, Object obj) {
        try {
            Response resp;

            switch (command.toUpperCase()) {

                case "CREATE_CLIENT": {
                    ClientRequest request = (ClientRequest) obj;

                    System.out.println("---- CREATE_CLIENT ----");
                    System.out.println("Objeto recibido: " + request);

                    resp = ClientController.createClient(request);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "UPDATE_CLIENT": {
                    ClientRequest request = (ClientRequest) obj;

                    System.out.println("---- UPDATE_CLIENT ----");
                    System.out.println("Objeto recibido: " + request);

                    resp = ClientController.updateClient(request);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "DELETE_CLIENT": {
                    ClientRequest request = (ClientRequest) obj;

                    System.out.println("---- DELETE_CLIENT ----");
                    System.out.println("Objeto recibido: " + request);

                    resp = ClientController.deleteClient(request);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "LOGIN": {
                    User user = (User) obj;

                    System.out.println("---- LOGIN ----");
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Password: " + user.getPassword());
                    System.out.println("IdRole recibido: " + user.getIdRole());

                    resp = UserController.login(user);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "START_EQUIPMENT_DRAFT": {
                    EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                    System.out.println("---- START_EQUIPMENT_DRAFT ----");
                    System.out.println("Objeto recibido: " + request);

                    resp = ReservationDraftController.startEquipmentDraft(request);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "UPDATE_EQUIPMENT_DRAFT": {
                    EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                    System.out.println("---- UPDATE_EQUIPMENT_DRAFT ----");
                    System.out.println("Objeto recibido: " + request);

                    resp = ReservationDraftController.updateEquipmentDraft(request);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "GET_EQUIPMENT_DRAFT_BY_ID": {
                    Integer idDraft = (Integer) obj;

                    System.out.println("---- GET_EQUIPMENT_DRAFT_BY_ID ----");
                    System.out.println("IdDraft recibido: " + idDraft);

                    resp = ReservationDraftController.getEquipmentDraftById(idDraft);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "GET_EQUIPMENT_DRAFT_BY_CLIENT_ID": {
                    Integer idClient = (Integer) obj;

                    System.out.println("---- GET_EQUIPMENT_DRAFT_BY_CLIENT_ID ----");
                    System.out.println("IdClient recibido: " + idClient);

                    resp = ReservationDraftController.getEquipmentDraftByClientId(idClient);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "ENTER_RESERVATIONS_VIEW": {
                    Integer idClient = (Integer) obj;

                    this.loggedClientId = idClient;
                    this.viewingReservations = true;

                    System.out.println("---- ENTER_RESERVATIONS_VIEW ----");
                    System.out.println("Cliente entró a reservas");
                    System.out.println("IdClient: " + this.loggedClientId);
                    System.out.println("ViewingReservations: " + this.viewingReservations);

                    resp = new Response(true, "Cliente registrado en vista de reservas", null);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "EXIT_RESERVATIONS_VIEW": {
                    this.viewingReservations = false;

                    System.out.println("---- EXIT_RESERVATIONS_VIEW ----");
                    System.out.println("Cliente salió de reservas");
                    System.out.println("IdClient: " + this.loggedClientId);
                    System.out.println("ViewingReservations: " + this.viewingReservations);

                    resp = new Response(true, "Cliente salió de vista de reservas", null);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "DISCARD_EQUIPMENT_DRAFT": {
                    EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                    System.out.println("---- DISCARD_EQUIPMENT_DRAFT ----");
                    System.out.println("Objeto recibido: " + request);

                    resp = ReservationDraftController.discardEquipmentDraft(
                            request.getIdDraft(),
                            request.getIdClient()
                    );

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "CONFIRM_EQUIPMENT_DRAFT": {
                    EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                    System.out.println("---- CONFIRM_EQUIPMENT_DRAFT ----");
                    System.out.println("Objeto recibido: " + request);
                    System.out.println("IdDraft: " + request.getIdDraft());
                    System.out.println("IdClient: " + request.getIdClient());

                    resp = ReservationDraftController.confirmEquipmentDraft(
                            request.getIdDraft(),
                            request.getIdClient()
                    );

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                case "GET_CALENDAR_BLOCKS": {
                    CalendarRequest request = (CalendarRequest) obj;

                    System.out.println("---- GET_CALENDAR_BLOCKS ----");
                    System.out.println("Objeto recibido: " + request);
                    System.out.println("Mes: " + request.getMonth());
                    System.out.println("Año: " + request.getYear());

                    resp = ReservationDraftController.getCalendarBlocks(
                            request.getMonth(),
                            request.getYear()
                    );

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }

                default: {
                    resp = new Response(false, "Comando no reconocido", null);

                    logResponse(resp);
                    sendResponse(resp);
                    break;
                }
            }

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
