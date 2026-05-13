package service;

import java.sql.Date;
import model.Equipment;
import model.Reservation;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona las operaciones relacionadas con equipos del sistema.
 */
public class EquipmentService {

    /**
     * Solicita la creacion de un nuevo equipo.
     *
     * @param equipment equipo a registrar
     * @return respuesta del servidor
     */
    public static Response createEquipment(Equipment equipment) {
        return sendRequest("CREATE_EQUIPMENT", equipment);
    }

    /**
     * Solicita la actualizacion de un equipo existente.
     *
     * @param equipment equipo con los datos actualizados
     * @return respuesta del servidor
     */
    public static Response updateEquipment(Equipment equipment) {
        return sendRequest("UPDATE_EQUIPMENT", equipment);
    }

    /**
     * Consulta un equipo por su identificador.
     *
     * @param idEquipment identificador del equipo
     * @return respuesta del servidor
     */
    public static Response getEquipment(int idEquipment) {
        return sendRequest("GET_EQUIPMENT", idEquipment);
    }

    /**
     * Solicita la lista completa de equipos registrados.
     *
     * @return respuesta del servidor
     */
    public static Response getAllEquipment() {
        return sendRequest("GET_ALL_EQUIPMENT", null);
    }

    /**
     * Solicita la eliminacion de un equipo.
     *
     * @param equipment equipo a eliminar
     * @return respuesta del servidor
     */
    public static Response deleteEquipment(Equipment equipment) {
        return sendRequest("DELETE_EQUIPMENT", equipment);
    }

    /**
     * Consulta los equipos disponibles para una fecha y bloque concretos.
     *
     * @param date fecha reservada
     * @param section bloque horario consultado
     * @return respuesta del servidor
     */
    public static Response getAvailableEquipmentByDateAndSection(
            Date date, 
            int section) {

        Reservation reservation = new Reservation();
        reservation.setReservationDate(date);
        reservation.setIdSection(section);

        return sendRequest("GET_AVAILABLE_EQUIPMENT", reservation);
    }

    /**
     * Envia un comando relacionado con equipos y espera la respuesta.
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
                    "Error al conectar con el servidor: " + e.getMessage(),
                    null);
        }
    }
}
