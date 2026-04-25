package service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import dto.CalendarRequest;
import dto.EquipmentReservationDraftRequest;
import network.ServerConnection;
import network.SocketManager;

public class CalendarService {

    public static Response getCalendarBlocks(int month, int year) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            CalendarRequest request = new CalendarRequest(month, year);

            out.writeObject("GET_CALENDAR_BLOCKS");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            }

            return new Response(false, "Respuesta inesperada del servidor", null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Response enterReservationsView(int idClient) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject("ENTER_RESERVATIONS_VIEW");
            out.flush();

            out.writeObject(idClient);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            }

            return new Response(false, "Respuesta inesperada del servidor", null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Response exitReservationsView() {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject("EXIT_RESERVATIONS_VIEW");
            out.flush();

            out.writeObject(null);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            }

            return new Response(false, "Respuesta inesperada del servidor", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al salir de vista de reservas", null);
        }
    }
    
    public static Response startEquipmentDraft(EquipmentReservationDraftRequest request) {
    try {
        SocketManager socketManager = SocketManager.getInstance();

        if (!socketManager.isConnected()) {
            socketManager.connect();
        }

        ServerConnection connection = socketManager.getConnection();
        ObjectOutputStream out = connection.getObjectOutput();
        ObjectInputStream in = connection.getObjectInput();

        // 1. Enviar comando
        out.writeObject("START_EQUIPMENT_DRAFT");
        out.flush();

        // 2. Enviar objeto
        out.writeObject(request);
        out.flush();

        // 3. Leer respuesta
        Object response = in.readObject();

        if (response instanceof Response) {
            return (Response) response;
        }

        return new Response(false, "Respuesta inesperada del servidor", null);

    } catch (Exception e) {
        e.printStackTrace();
        return new Response(false, "Error al iniciar draft", null);
    }
}
}
