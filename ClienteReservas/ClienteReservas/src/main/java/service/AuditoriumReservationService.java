package service;

import dto.AuditoriumDraftRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona las operaciones de reservas de auditorio confirmadas.
 */
public class AuditoriumReservationService {

    /**
     * Consulta una reserva de auditorio por su identificador.
     *
     * @param idReservation identificador de la reserva
     * @return respuesta del servidor
     */
    public static Response getAuditoriumReservationById(int idReservation) {
        return sendRequest("GET_AUDITORIUM_RESERVATION_BY_ID", idReservation);
    }

    /**
     * Consulta las reservas de auditorio asociadas a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response getAuditoriumReservationsByClientId(int idClient) {
        return sendRequest(
                "GET_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID",
                idClient);
    }

    /**
     * Consulta las reservas de auditorio de un mes especifico.
     *
     * @param month mes a consultar
     * @param year anio a consultar
     * @return respuesta del servidor
     */
    public static Response getAuditoriumReservationsByMonth(
            int month, 
            int year) {
        
        int[] data = {month, year};
        return sendRequest("GET_AUDITORIUM_RESERVATIONS_BY_MONTH", data);
    }

    /**
     * Solicita la eliminacion de una reserva de auditorio.
     *
     * @param request datos de la reserva a eliminar
     * @return respuesta del servidor
     */
    public static Response deleteAuditoriumReservationById(
            AuditoriumDraftRequest request) {
        
        return sendRequest("DELETE_AUDITORIUM_RESERVATION_BY_ID", request);
    }

    /**
     * Solicita la eliminacion de todas las reservas de auditorio de un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response deleteAuditoriumReservationsByClientId(
            int idClient) {
        
        return sendRequest(
                "DELETE_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID", 
                idClient);
    }

    /**
     * Envia un comando de reservas de auditorio y espera la respuesta.
     *
     * @param action comando a ejecutar
     * @param data datos asociados al comando
     * @return respuesta del servidor o una respuesta de error local
     */
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
                    null);
        }
    }
}
