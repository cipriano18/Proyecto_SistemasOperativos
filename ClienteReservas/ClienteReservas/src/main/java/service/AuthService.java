/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.Response;
import model.User;
import network.ServerConnection;
import network.SocketManager;

public class AuthService {
     public static Response login(String username, String password) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            out.writeObject("LOGIN");
            out.flush();

            out.writeObject(user);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            } else {
                return new Response(false, "Respuesta inesperada del servidor", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
