package service;

import dto.AuditoriumDraftRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import network.ServerConnection;
import network.SocketManager;

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
