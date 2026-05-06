/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.EquipmentReservationRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
public class EquipmentReservationService {

    public static Response updateEquipmentReservation(EquipmentReservationRequest request) {
        return sendRequest("UPDATE_EQUIPMENT_RESERVATION", request);
    }

    public static Response getReservationById(int idReservation) {
        return sendRequest("GET_RESERVATION_BY_ID", idReservation);
    }

    public static Response getReservationsByClientId(int idClient) {
        return sendRequest("GET_RESERVATIONS_BY_CLIENT_ID", idClient);
    }

    public static Response getEquipmentReservationsByMonth(int month, int year) {
        int[] data = {month, year};
        return sendRequest("GET_EQUIPMENT_RESERVATIONS_BY_MONTH", data);
    }

    public static Response deleteReservationById(EquipmentReservationRequest request) {
        return sendRequest("DELETE_RESERVATION_BY_ID", request);
    }

    public static Response deleteReservationsByClientId(int idClient) {
        return sendRequest("DELETE_RESERVATIONS_BY_CLIENT_ID", idClient);
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