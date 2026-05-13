/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.User;
import network.ResponseStore;
import network.ServerConnection;
import network.SocketManager;

/**
 * Gestiona la autenticacion de usuarios contra el servidor.
 */
public class AuthService {

    /**
     * Intenta autenticar un usuario con sus credenciales.
     *
     * @param username nombre de usuario
     * @param password contrasena del usuario
     * @return respuesta del servidor o una respuesta de error local
     */
    public static Response login(String username, String password) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            connection.sendRequest("LOGIN", user);

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
