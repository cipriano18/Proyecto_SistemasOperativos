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
 * Gestiona las operaciones de reservas de equipos confirmadas.
 */
public class EquipmentReservationService {

    /**
     * Solicita la actualizacion de una reserva de equipos.
     *
     * @param request datos actualizados de la reserva
     * @return respuesta del servidor
     */
    public static Response updateEquipmentReservation(
            EquipmentReservationRequest request) {
        
        return sendRequest("UPDATE_EQUIPMENT_RESERVATION", request);
    }

    /**
     * Consulta una reserva de equipos por su identificador.
     *
     * @param idReservation identificador de la reserva
     * @return respuesta del servidor
     */
    public static Response getReservationById(int idReservation) {
        return sendRequest("GET_RESERVATION_BY_ID", idReservation);
    }

    /**
     * Consulta las reservas de equipos asociadas a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response getReservationsByClientId(int idClient) {
        return sendRequest("GET_RESERVATIONS_BY_CLIENT_ID", idClient);
    }

    /**
     * Consulta las reservas de equipos de un mes especifico.
     *
     * @param month mes a consultar
     * @param year anio a consultar
     * @return respuesta del servidor
     */
    public static Response getEquipmentReservationsByMonth(
            int month, 
            int year) {
        
        int[] data = {month, year};
        return sendRequest("GET_EQUIPMENT_RESERVATIONS_BY_MONTH", data);
    }

    /**
     * Solicita la eliminacion de una reserva de equipos.
     *
     * @param request datos de la reserva a eliminar
     * @return respuesta del servidor
     */
    public static Response deleteReservationById(
            EquipmentReservationRequest request) {
        
        return sendRequest("DELETE_RESERVATION_BY_ID", request);
    }

    /**
     * Solicita la eliminacion de todas las reservas de un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response deleteReservationsByClientId(int idClient) {
        return sendRequest("DELETE_RESERVATIONS_BY_CLIENT_ID", idClient);
    }

    /**
     * Envia un comando de reservas de equipos y espera la respuesta.
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
