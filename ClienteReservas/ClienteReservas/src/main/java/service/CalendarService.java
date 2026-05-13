package service;

import dto.CalendarRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Centraliza las operaciones relacionadas con calendarios y bloques.
 */
public class CalendarService {

    /**
     * Solicita los bloques de calendario para reservas de equipos.
     *
     * @param month mes a consultar
     * @param year anio a consultar
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response getCalendarBlocks(
            int month, 
            int year, 
            int idClient) {
        
        CalendarRequest request = new CalendarRequest(month, year, idClient);
        return sendRequest("GET_CALENDAR_BLOCKS", request);
    }

    /**
     * Solicita los bloques de calendario para reservas de auditorio.
     *
     * @param month mes a consultar
     * @param year anio a consultar
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response getAuditoriumCalendarBlocks(
            int month, 
            int year, 
            int idClient) {
        
        CalendarRequest request = new CalendarRequest(month, year, idClient);
        return sendRequest("GET_CALENDAR_AUDITORIUM", request);
    }

    /**
     * Notifica al servidor que el cliente entro a la vista de reservas.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response enterReservationsView(int idClient) {
        return sendRequest("ENTER_RESERVATIONS_VIEW", idClient);
    }

    /**
     * Notifica al servidor que el cliente salio de la vista de reservas.
     *
     * @return respuesta del servidor
     */
    public static Response exitReservationsView() {
        return sendRequest("EXIT_RESERVATIONS_VIEW", null);
    }

    /**
     * Envia un comando de calendario al servidor y espera su respuesta.
     *
     * @param command comando a ejecutar
     * @param data datos asociados al comando
     * @return respuesta del servidor o una respuesta de error local
     */
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
            return new Response(
                    false, 
                    "Error al comunicarse con el servidor: " 
                    + e.getMessage()
                    , null);
        }
    }
}
