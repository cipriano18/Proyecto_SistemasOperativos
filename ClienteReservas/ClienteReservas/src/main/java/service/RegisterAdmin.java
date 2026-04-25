/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dto.AdminRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
public class RegisterAdmin {

    public static Response register(AdminRequest request) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject("CREATE_ADMIN");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            } else {
                return new Response(false, "Respuesta inesperada del servidor", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al conectar con el servidor: " + e.getMessage(), null);
        }
    }
}
