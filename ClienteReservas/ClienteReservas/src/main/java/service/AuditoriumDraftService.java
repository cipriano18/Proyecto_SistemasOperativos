package service;

import dto.AuditoriumDraftRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
public class AuditoriumDraftService {

    public static Response startAuditoriumDraft(AuditoriumDraftRequest request) {
        return sendRequest("START_AUDITORIUM_DRAFT", request);
    }

    public static Response updateAuditoriumDraft(AuditoriumDraftRequest request) {
        return sendRequest("UPDATE_AUDITORIUM_DRAFT", request);
    }

    public static Response getAuditoriumDraftByClientId(int idClient) {
        return sendRequest("GET_AUDITORIUM_DRAFT_BY_CLIENT_ID", idClient);
    }

    public static Response discardAuditoriumDraft(AuditoriumDraftRequest request) {
        return sendRequest("DISCARD_AUDITORIUM_DRAFT", request);
    }

    public static Response confirmAuditoriumDraft(AuditoriumDraftRequest request) {
        return sendRequest("CONFIRM_AUDITORIUM_DRAFT", request);
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