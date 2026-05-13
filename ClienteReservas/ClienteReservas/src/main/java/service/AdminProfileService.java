/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.AdminRequest;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona las operaciones sobre el perfil de administrador.
 */
public class AdminProfileService {

    /**
     * Solicita la actualizacion del perfil de un administrador.
     *
     * @param request datos actualizados del administrador
     * @return respuesta del servidor
     */
    public static Response update(AdminRequest request) {
        return send("UPDATE_ADMIN", request);
    }

    /**
     * Solicita la eliminacion del perfil de un administrador.
     *
     * @param idAdmin identificador del administrador a eliminar
     * @return respuesta del servidor
     */
    public static Response delete(int idAdmin) {
        return send("DELETE_ADMIN", idAdmin);
    }

    /**
     * Envia un comando de perfil de administrador y espera la respuesta.
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
