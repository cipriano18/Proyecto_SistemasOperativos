/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import java.io.IOException;

public class SocketManager {

    private static SocketManager instance;
    private ServerConnection connection;

    private SocketManager() {
    }

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
            throw new IllegalStateException("No hay conexión activa. Llama a connect() primero.");
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
