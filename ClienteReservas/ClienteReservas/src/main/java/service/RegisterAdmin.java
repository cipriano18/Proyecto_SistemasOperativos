package service;

import dto.AdminRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona el registro de nuevos administradores en el sistema.
 */
public class RegisterAdmin {

    /**
     * Solicita la creacion de un nuevo administrador.
     *
     * @param request datos del administrador a registrar
     * @return respuesta del servidor
     */
    public static Response register(AdminRequest request) {
        return sendRequest("CREATE_ADMIN", request);
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
                    "Error al conectar con el servidor: " 
                    + e.getMessage()
                    , null);
        }
    }
}
