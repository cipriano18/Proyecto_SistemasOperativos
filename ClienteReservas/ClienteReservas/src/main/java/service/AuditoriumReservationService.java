package service;

import dto.AuditoriumDraftRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import network.ServerConnection;
import network.SocketManager;

public class AuditoriumReservationService {

    public static Response getAuditoriumReservationById(int idReservation) {
        return sendRequest("GET_AUDITORIUM_RESERVATION_BY_ID", idReservation);
    }

    public static Response getAuditoriumReservationsByClientId(int idClient) {
        return sendRequest("GET_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID", idClient);
    }

    public static Response getAuditoriumReservationsByMonth(int month, int year) {
        int[] data = {month, year};
        return sendRequest("GET_AUDITORIUM_RESERVATIONS_BY_MONTH", data);
    }

    public static Response deleteAuditoriumReservationById(AuditoriumDraftRequest request) {
        return sendRequest("DELETE_AUDITORIUM_RESERVATION_BY_ID", request);
    }

    public static Response deleteAuditoriumReservationsByClientId(int idClient) {
        return sendRequest("DELETE_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID", idClient);
    }

    private static Response sendRequest(String action, Object data) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject(action);
            out.flush();

            out.writeObject(data);
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
}
