package service;

import java.sql.Date;
import model.Equipment;
import model.Reservation;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;
/**
 *
 * @author cipriano
 */
public class EquipmentService {

    public static Response createEquipment(Equipment equipment) {
        return sendRequest("CREATE_EQUIPMENT", equipment);
    }

    public static Response updateEquipment(Equipment equipment) {
        return sendRequest("UPDATE_EQUIPMENT", equipment);
    }

    public static Response getEquipment(int idEquipment) {
        return sendRequest("GET_EQUIPMENT", idEquipment);
    }

    public static Response getAllEquipment() {
        return sendRequest("GET_ALL_EQUIPMENT", null);
    }

    public static Response deleteEquipment(Equipment equipment) {
        return sendRequest("DELETE_EQUIPMENT", equipment);
    }

    public static Response getAvailableEquipmentByDateAndSection(Date date, int section) {

        Reservation reservation = new Reservation();
        reservation.setReservationDate(date);
        reservation.setIdSection(section);

        return sendRequest("GET_AVAILABLE_EQUIPMENT", reservation);
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

            return new Response(
                    false,
                    "Error al conectar con el servidor: " + e.getMessage(),
                    null
            );
        }
    }
}