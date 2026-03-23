package proyect.clientereservas.network;

import java.io.IOException;

public class socketManager {

    private static socketManager instance;
    private ServerConnection connection;

    private socketManager() {}

    public static socketManager getInstance() {
        if (instance == null) {
            instance = new socketManager();
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