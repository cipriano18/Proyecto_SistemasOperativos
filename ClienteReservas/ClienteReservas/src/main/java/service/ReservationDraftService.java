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
 *
 * @author Cipriano
 */

public class ReservationDraftService {

    public static Response discardEquipmentDraft(EquipmentReservationDraftRequest request) {
        return send("DISCARD_EQUIPMENT_DRAFT", request);
    }

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
            return new Response(false, "Error al conectar con el servidor: " + e.getMessage(), null);
        }
    }
}