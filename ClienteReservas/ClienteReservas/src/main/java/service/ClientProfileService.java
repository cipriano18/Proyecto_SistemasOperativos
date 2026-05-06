package service;

import model.Client;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author cipriano
 */
public class ClientProfileService {

    public static Response update(Client client) {
        return send("UPDATE_CLIENT", client);
    }

    public static Response getClient(int idClient) {
        return send("GET_CLIENT", idClient);
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