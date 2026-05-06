package service;

import dto.ClientRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
public class ClientProfileService {

    public static Response update(ClientRequest request) {
        return send("UPDATE_CLIENT", request);
    }

    public static Response delete(ClientRequest request) {
        return send("DELETE_CLIENT", request);
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

            return new Response(
                    false,
                    "Error al conectar con el servidor: " + e.getMessage(),
                    null
            );
        }
    }
}