package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Representa una conexion de sockets con el servidor principal.
 */
public class ServerConnection {

    //private static final String HOST = "10.35.142.88";
    private static final String HOST = "172.17.44.224";
    private static final int PORT = 10000;

    private Socket socket;
    private ObjectOutputStream objectOutput;
    private ObjectInputStream objectInput;

    private ServerListener listener;

    /**
     * Abre la conexion, inicializa los flujos y arranca el listener.
     *
     * @throws IOException si ocurre un error al conectar con el servidor
     */
    public ServerConnection() throws IOException {
        socket = new Socket(HOST, PORT);

        objectOutput = new ObjectOutputStream(socket.getOutputStream());
        objectOutput.flush();

        objectInput = new ObjectInputStream(socket.getInputStream());

        listener = new ServerListener(this);
        listener.start();

        System.out.println("Conectado al servidor en " + HOST + ":" + PORT);
    }

    public ObjectOutputStream getObjectOutput() {
        return objectOutput;
    }

    public ObjectInputStream getObjectInput() {
        return objectInput;
    }

    /**
     * Envia un comando y su carga asociada al servidor.
     *
     * @param command comando a enviar
     * @param data datos asociados al comando
     * @throws IOException si ocurre un error al escribir en el socket
     */
   public void sendRequest(String command, Object data) throws IOException {
    synchronized (objectOutput) {
        objectOutput.writeObject(command);
        objectOutput.flush();

        objectOutput.writeObject(data);
        objectOutput.flush();

        objectOutput.reset();
    }
}

    /**
     * Cierra la conexion, el listener y los flujos asociados.
     */
    public void close() {
        try {
            if (listener != null) {
                listener.stopListener();
            }
        } catch (Exception e) {
            System.out.println("Error al detener listener: " + e.getMessage());
        }

        try {
            if (objectOutput != null) {
                objectOutput.close();
            }
        } catch (IOException e) {
            System.out.println(
                    "Error al cerrar ObjectOutputStream: " 
                    + e.getMessage());
        }

        try {
            if (objectInput != null) {
                objectInput.close();
            }
        } catch (IOException e) {
            System.out.println(
                    "Error al cerrar ObjectInputStream: " 
                    + e.getMessage());
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

    /**
     * Indica si el socket permanece conectado y abierto.
     *
     * @return {@code true} si la conexion sigue activa
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
