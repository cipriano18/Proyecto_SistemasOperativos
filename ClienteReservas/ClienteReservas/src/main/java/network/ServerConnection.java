/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    private Socket socket;
    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;

    public ServerConnection() throws IOException {
        socket = new Socket(HOST, PORT);

        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectOutput.flush();

        objectInput = new ObjectInputStream(socket.getInputStream());

        System.out.println("Conectado al servidor en " + HOST + ":" + PORT);
    }

    public ObjectOutputStream getObjectOutput() {
        return objectOutput;
    }

    public ObjectInputStream getObjectInput() {
        return objectInput;
    }

    public void close() {
        try {
            if (objectOutput != null) {
                objectOutput.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar ObjectOutputStream: " + e.getMessage());
        }

        try {
            if (objectInput != null) {
                objectInput.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar ObjectInputStream: " + e.getMessage());
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar socket: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}