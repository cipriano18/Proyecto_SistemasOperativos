package service;

import dto.AdminRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
public class RegisterAdmin {

    public static Response register(AdminRequest request) {
        return sendRequest("CREATE_ADMIN", request);
    }

    private static Response sendRequest(String command, Object data) {
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