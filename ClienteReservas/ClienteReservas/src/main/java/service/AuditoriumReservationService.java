package service;

import dto.AuditoriumDraftRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
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

            connection.sendRequest(action, data);

            return ResponseStore.waitResponse();

        } catch (Exception e) {
            e.printStackTrace();

            return new Response(
                    false,
                    "Error al conectar con el servidor: " + e.getMessage(),
                    null
            );
        }
    }
}