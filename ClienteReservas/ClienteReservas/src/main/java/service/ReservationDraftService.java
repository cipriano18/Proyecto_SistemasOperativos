/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.EquipmentReservationDraftRequest;
import network.ServerConnection;
import network.SocketManager;
import network.ResponseStore;

/**
 * Provee operaciones puntuales sobre drafts de reserva.
 */
public class ReservationDraftService {

    /**
     * Solicita descartar un draft de reserva de equipos.
     *
     * @param request datos del draft a descartar
     * @return respuesta del servidor
     */
    public static Response discardEquipmentDraft(
            EquipmentReservationDraftRequest request) {
        
        return send("DISCARD_EQUIPMENT_DRAFT", request);
    }

    /**
     * Envia un comando relacionado con drafts y espera la respuesta.
     *
     * @param command comando a ejecutar
     * @param data datos asociados al comando
     * @return respuesta del servidor o una respuesta de error local
     */
    private static Response send(String command, Object data) {
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
                    "Error al conectar con el servidor: " 
                    + e.getMessage(),
                    null);
        }
    }
}
