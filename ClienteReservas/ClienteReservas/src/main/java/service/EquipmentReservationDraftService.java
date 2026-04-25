/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.EquipmentReservationDraftRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import network.ServerConnection;
import network.SocketManager;

public class EquipmentReservationDraftService {


    public static Response startEquipmentDraft(EquipmentReservationDraftRequest request) {
        return sendRequest("START_EQUIPMENT_DRAFT", request);
    }

    public static Response updateEquipmentDraft(EquipmentReservationDraftRequest request) {
        return sendRequest("UPDATE_EQUIPMENT_DRAFT", request);
    }

    public static Response getEquipmentDraftById(int idDraft) {
        return sendRequest("GET_EQUIPMENT_DRAFT_BY_ID", idDraft);
    }

    public static Response getEquipmentDraftByClientId(int idClient) {
        return sendRequest("GET_EQUIPMENT_DRAFT_BY_CLIENT_ID", idClient);
    }

    public static Response discardEquipmentDraft(int idDraft, int idClient) {
        int[] data = {idDraft, idClient};
        return sendRequest("DISCARD_EQUIPMENT_DRAFT", data);
    }

    public static Response confirmEquipmentDraft(int idDraft, int idClient) {
        int[] data = {idDraft, idClient};
        return sendRequest("CONFIRM_EQUIPMENT_DRAFT", data);
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
