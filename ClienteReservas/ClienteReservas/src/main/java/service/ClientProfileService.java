package service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.ClientRequest;
import model.Response;
import network.ServerConnection;
import network.SocketManager;

public class ClientProfileService {

    public static Response update(ClientRequest request) {
        return send("UPDATE_CLIENT", request);
    }

    public static Response delete(ClientRequest request) {
        return send("DELETE_CLIENT", request);
    }

    private static Response send(String command, ClientRequest request) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject(command);
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
            return null;
        }
    }
}
