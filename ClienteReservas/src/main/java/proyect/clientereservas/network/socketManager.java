package proyect.clientereservas.network;

import java.io.IOException;

public class SocketManager {

    private static SocketManager instance;
    private ServerConnection connection;

    private SocketManager() {}

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void connect() throws IOException {
        if (connection == null || !connection.isConnected()) {
            connection = new ServerConnection();
        }
    }

    public ServerConnection getConnection() {
        if (connection == null || !connection.isConnected()) {
            throw new IllegalStateException("No hay conexión activa. Llama connect() primero.");
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }
}