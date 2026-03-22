package proyect.clientereservas.network;

import java.io.*;
import java.net.Socket;

public class ServerConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;

    public ServerConnection() throws IOException {
        socket = new Socket(HOST, PORT);

        output       = new DataOutputStream(socket.getOutputStream());
        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectOutput.flush();

        input       = new DataInputStream(socket.getInputStream());
        objectInput = new ObjectInputStream(socket.getInputStream());

        System.out.println("Conectado al servidor en " + HOST + ":" + PORT);
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public DataInputStream getInput() {
        return input;
    }

    public ObjectOutputStream getObjectOutput() {
        return objectOutput;
    }

    public ObjectInputStream getObjectInput() {
        return objectInput;
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}