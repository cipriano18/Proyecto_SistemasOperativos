package service;

import dto.CalendarRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

public class CalendarService {

    public static Response getCalendarBlocks(int month, int year, int idClient) {
        CalendarRequest request = new CalendarRequest(month, year, idClient);
        return sendRequest("GET_CALENDAR_BLOCKS", request);
    }

    public static Response getAuditoriumCalendarBlocks(int month, int year, int idClient) {
        CalendarRequest request = new CalendarRequest(month, year, idClient);
        return sendRequest("GET_CALENDAR_AUDITORIUM", request);
    }

    public static Response enterReservationsView(int idClient) {
        return sendRequest("ENTER_RESERVATIONS_VIEW", idClient);
    }

    public static Response exitReservationsView() {
        return sendRequest("EXIT_RESERVATIONS_VIEW", null);
    }

    private static Response sendRequest(String command, Object data) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();

            connection.sendRequest(command, data);

            return ResponseStore.waitResponse();

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al comunicarse con el servidor: " + e.getMessage(), null);
        }
    }
}