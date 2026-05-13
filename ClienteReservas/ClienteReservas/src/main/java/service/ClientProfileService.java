package service;

import dto.ClientRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona las operaciones sobre el perfil de cliente.
 */
public class ClientProfileService {

    /**
     * Solicita la actualizacion del perfil de un cliente.
     *
     * @param request datos actualizados del cliente
     * @return respuesta del servidor
     */
    public static Response update(ClientRequest request) {
        return send("UPDATE_CLIENT", request);
    }

    /**
     * Solicita la eliminacion del perfil de un cliente.
     *
     * @param request datos del cliente a eliminar
     * @return respuesta del servidor
     */
    public static Response delete(ClientRequest request) {
        return send("DELETE_CLIENT", request);
    }

    /**
     * Envia un comando de perfil de cliente y espera la respuesta.
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
                    "Error al conectar con el servidor: " + e.getMessage(),
                    null);
        }
    }
}
