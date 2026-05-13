/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.EquipmentReservationDraftRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona el ciclo de vida de los drafts de reserva de equipos.
 */
public class EquipmentReservationDraftService {

    /**
     * Inicia un nuevo draft de reserva de equipos.
     *
     * @param request datos iniciales del draft
     * @return respuesta del servidor
     */
    public static Response startEquipmentDraft(
            EquipmentReservationDraftRequest request) {
        
        return sendRequest("START_EQUIPMENT_DRAFT", request);
    }

    /**
     * Actualiza un draft de reserva de equipos existente.
     *
     * @param request datos actualizados del draft
     * @return respuesta del servidor
     */
    public static Response updateEquipmentDraft(
            EquipmentReservationDraftRequest request) {
        
        return sendRequest("UPDATE_EQUIPMENT_DRAFT", request);
    }

    /**
     * Consulta un draft de equipos por su identificador.
     *
     * @param idDraft identificador del draft
     * @return respuesta del servidor
     */
    public static Response getEquipmentDraftById(int idDraft) {
        return sendRequest("GET_EQUIPMENT_DRAFT_BY_ID", idDraft);
    }

    /**
     * Consulta el draft de equipos asociado a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response getEquipmentDraftByClientId(int idClient) {
        return sendRequest("GET_EQUIPMENT_DRAFT_BY_CLIENT_ID", idClient);
    }

    /**
     * Descarta un draft de equipos existente.
     *
     * @param idDraft identificador del draft
     * @param idClient identificador del cliente asociado
     * @return respuesta del servidor
     */
    public static Response discardEquipmentDraft(int idDraft, int idClient) {
        int[] data = {idDraft, idClient};
        return sendRequest("DISCARD_EQUIPMENT_DRAFT", data);
    }

    /**
     * Confirma definitivamente un draft de equipos.
     *
     * @param idDraft identificador del draft
     * @param idClient identificador del cliente asociado
     * @return respuesta del servidor
     */
    public static Response confirmEquipmentDraft(int idDraft, int idClient) {
        EquipmentReservationDraftRequest request 
                = new EquipmentReservationDraftRequest();
        
        request.setIdDraft(idDraft);
        request.setIdClient(idClient);

        return sendRequest("CONFIRM_EQUIPMENT_DRAFT", request);
    }

    /**
     * Envia un comando de drafts de equipos y espera la respuesta.
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
                    "Error al conectar con el servidor: " 
                    + e.getMessage()
                    , null);
        }
    }
}
