package server;

import controller.ClientController;
import controller.UserController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.ClientRequest;
import model.Response;
import model.User;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objectOutput;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.flush();
            objectInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error al inicializar streams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getClientAddress() {
        return socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            System.out.println("Hilo iniciado para: " + getClientAddress());

            while (true) {
                String command = (String) objectInput.readObject();
                Object obj = objectInput.readObject();

                System.out.println("\n==============================");
                System.out.println("Petición recibida de: " + getClientAddress());
                System.out.println("Comando: " + command);
                System.out.println("Tipo de objeto: " + (obj != null ? obj.getClass().getName() : "null"));
                System.out.println("Contenido del objeto: " + obj);
                System.out.println("==============================\n");

                processRequest(command, obj);
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + getClientAddress());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al leer objeto: " + e.getMessage());
        } finally {
            Server.clients.remove(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar socket: " + e.getMessage());
            }
        }
    }

    private void processRequest(String command, Object obj) {
        try {
            switch (command.toUpperCase()) {

                case "CREATE_CLIENT": {
                    ClientRequest request = (ClientRequest) obj;

                    System.out.println("---- CREATE_CLIENT ----");
                    System.out.println("Objeto recibido: " + request);

                    Response resp = ClientController.createClient(request);

                    System.out.println("Success: " + resp.isSuccess());
                    System.out.println("Mensaje: " + resp.getMessage());
                    System.out.println("Data: " + resp.getData());

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "UPDATE_CLIENT": {
                    ClientRequest request = (ClientRequest) obj;

                    System.out.println("---- UPDATE_CLIENT ----");
                    System.out.println("Objeto recibido: " + request);

                    Response resp = ClientController.updateClient(request);

                    System.out.println("Success: " + resp.isSuccess());
                    System.out.println("Mensaje: " + resp.getMessage());
                    System.out.println("Data: " + resp.getData());

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "DELETE_CLIENT": {
                    ClientRequest request = (ClientRequest) obj;

                    System.out.println("---- DELETE_CLIENT ----");
                    System.out.println("Objeto recibido: " + request);

                    Response resp = ClientController.deleteClient(request);

                    System.out.println("Success: " + resp.isSuccess());
                    System.out.println("Mensaje: " + resp.getMessage());
                    System.out.println("Data: " + resp.getData());

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "LOGIN": {
                    User user = (User) obj;

                    System.out.println("---- LOGIN ----");
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Password: " + user.getPassword());
                    System.out.println("IdRole recibido: " + user.getIdRole());

                    Response resp = UserController.login(user);

                    System.out.println("Success: " + resp.isSuccess());
                    System.out.println("Mensaje: " + resp.getMessage());
                    System.out.println("Data: " + resp.getData());

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                default: {
                    objectOutput.writeObject(
                            new Response(false, "Comando no reconocido", null)
                    );
                    objectOutput.flush();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error procesando petición: " + e.getMessage());
            e.printStackTrace();

            try {
                objectOutput.writeObject(
                        new Response(false, "Error interno del servidor: " + e.getMessage(), null)
                );
                objectOutput.flush();
            } catch (IOException ex) {
                System.out.println("No se pudo enviar respuesta de error al cliente");
                ex.printStackTrace();
            }
        }
    }
}