package service;

import dto.ClientRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona el registro de nuevos clientes en el sistema.
 */
public class RegisterClient {

    /**
     * Solicita la creacion de un nuevo cliente.
     *
     * @param request datos del cliente a registrar
     * @return respuesta del servidor
     */
    public static Response register(ClientRequest request) {
        return sendRequest("CREATE_CLIENT", request);
    }

    /**
     * Envia un comando de registro y espera la respuesta.
     *
     * @param command comando a ejecutar
     * @param data datos asociados al comando
     * @return respuesta del servidor o una respuesta de error local
     */
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

            return new Response(
                    false,
                    "Error al conectar con el servidor: " + e.getMessage()
                    ,null);
        }
    }
}
