package service;

import dto.AuditoriumDraftRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona el ciclo de vida de los drafts de reserva de auditorio.
 */
public class AuditoriumDraftService {

    /**
     * Inicia un nuevo draft de reserva de auditorio.
     *
     * @param request datos iniciales del draft
     * @return respuesta del servidor
     */
    public static Response startAuditoriumDraft(
            AuditoriumDraftRequest request) {
        
        return sendRequest("START_AUDITORIUM_DRAFT", request);
    }

    /**
     * Actualiza un draft de auditorio existente.
     *
     * @param request datos actualizados del draft
     * @return respuesta del servidor
     */
    public static Response updateAuditoriumDraft(
            AuditoriumDraftRequest request) {
        
        return sendRequest("UPDATE_AUDITORIUM_DRAFT", request);
    }

    /**
     * Consulta el draft de auditorio asociado a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del servidor
     */
    public static Response getAuditoriumDraftByClientId(int idClient) {
        return sendRequest("GET_AUDITORIUM_DRAFT_BY_CLIENT_ID", idClient);
    }

    /**
     * Descarta un draft de auditorio existente.
     *
     * @param request datos del draft a descartar
     * @return respuesta del servidor
     */
    public static Response discardAuditoriumDraft(
            AuditoriumDraftRequest request) {
        
        return sendRequest("DISCARD_AUDITORIUM_DRAFT", request);
    }

    /**
     * Confirma definitivamente un draft de auditorio.
     *
     * @param request datos del draft a confirmar
     * @return respuesta del servidor
     */
    public static Response confirmAuditoriumDraft(
            AuditoriumDraftRequest request) {
        
        return sendRequest("CONFIRM_AUDITORIUM_DRAFT", request);
    }

    /**
     * Envia un comando de drafts de auditorio y espera la respuesta.
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
