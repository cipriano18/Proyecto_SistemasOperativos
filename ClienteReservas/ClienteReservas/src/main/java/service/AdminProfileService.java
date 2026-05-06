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
 *
 * @author Cipriano
 */
public class AdminProfileService {

    public static Response update(AdminRequest request) {
        return send("UPDATE_ADMIN", request);
    }

    public static Response delete(int idAdmin) {
        return send("DELETE_ADMIN", idAdmin);
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