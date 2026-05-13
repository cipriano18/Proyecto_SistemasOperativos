/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import java.io.IOException;

/**
 * Administra una unica conexion activa con el servidor.
 */
public class SocketManager {

    private static SocketManager instance;
    private ServerConnection connection;

    /**
     * Crea el administrador de sockets.
     */
    private SocketManager() {
    }

    /**
     * Obtiene la instancia compartida del administrador de sockets.
     *
     * @return instancia unica de {@code SocketManager}
     */
    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    /**
     * Abre la conexion con el servidor si no existe una activa.
     *
     * @throws IOException si ocurre un error al conectar con el servidor
     */
    public void connect() throws IOException {
        if (connection == null || !connection.isConnected()) {
            connection = new ServerConnection();
        }
    }

    /**
     * Devuelve la conexion activa con el servidor.
     *
     * @return conexion activa
     * @throws IllegalStateException si no existe una conexion activa
     */
    public ServerConnection getConnection() {
        if (connection == null || !connection.isConnected()) {
            throw new IllegalStateException(
                    "No hay conexión activa. Llama a connect() primero.");
        }
        return connection;
    }

    /**
     * Cierra la conexion activa y libera la referencia almacenada.
     */
    public void disconnect() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * Indica si existe una conexion activa con el servidor.
     *
     * @return {@code true} si la conexion esta activa
     */
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }
}
