package service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.CalendarRequest;
import model.Response;
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
            return new Response(false, "Error al obtener el calendario: " + e.getMessage(), null);
        }
    }
}