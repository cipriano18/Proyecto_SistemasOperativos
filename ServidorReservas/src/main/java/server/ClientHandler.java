package server;

import controller.AdminController;
import controller.RoleController;
import controller.UserController;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.AdminRequest;
import model.Role;
import model.User;

public class ClientHandler extends Thread {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objectOutput;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            objectInput  = new ObjectInputStream(socket.getInputStream());
            objectOutput.flush();
            input  = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
    e.printStackTrace(); 
}
    }

    // Retorna la dirección del cliente conectado
    public String getClientAddress() {
        return socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            System.out.println("Hilo iniciado para: " + getClientAddress());
            while (true) {
                // 1. Lee comando
                String command = input.readUTF();
                System.out.println("Petición de " + getClientAddress() + ": " + command);

                // 2. Lee objeto
                Object obj = objectInput.readObject();

                // 3. Procesa y responde en el mismo hilo
                processRequest(command, obj);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + getClientAddress());
        } catch (ClassNotFoundException e) {
            System.out.println("Error al leer objeto: " + e.getMessage());
        } finally {
            Server.clients.remove(this);
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void processRequest(String command, Object obj) {
        try {
            switch (command.toUpperCase()) {

                // ── USUARIOS ─────────────────────────────────────────
                case "CREATE_USER":
                    output.writeUTF(UserController.createUser((User) obj));
                    output.flush();
                    break;

                case "UPDATE_USER":
                    output.writeUTF(UserController.updateUser((User) obj));
                    output.flush();
                    break;

                case "GET_USER":
                    User found = UserController.getUser(((User) obj).getIdUser());
                    objectOutput.writeObject(found);
                    objectOutput.flush();
                    break;

                // ── ROLES ─────────────────────────────────────────────
                case "GET_ROLES":
                    List<Role> roles = RoleController.getAllRoles();
                    objectOutput.writeObject(roles);
                    objectOutput.flush();
                    break;

                case "GET_ROLE":
                    Role role = RoleController.getRole(((Role) obj).getIdRole());
                    objectOutput.writeObject(role);
                    objectOutput.flush();
                    break;

                // ── ADMINISTRADORES ───────────────────────────────────
                case "CREATE_ADMIN":
                    output.writeUTF(AdminController.createAdmin((AdminRequest) obj));
                    System.out.print("hola " + AdminRequest.class);
                    System.out.println("Objeto recibido: " + obj);
                    output.flush();
                    break;
                case "UPDATE_ADMIN":
                    output.writeUTF(AdminController.updateAdmin((AdminRequest) obj));
                    System.out.println("UPDATE_ADMIN recibido: " + obj);
                    output.flush();
                    break;
                case "LOGIN":

                    if (obj instanceof User) {
                        User u = (User) obj;

                        System.out.println("=== OBJETO LOGIN RECIBIDO ===");
                        System.out.println("Username: " + u.getUsername());
                        System.out.println("Password: " + u.getPassword());
                        System.out.println("Role: " + u.getIdRole());

                        // Ahora sí validamos
                        Object resp = UserController.login(u);
                      System.out.println("objet: " +resp.toString());
                        if (resp instanceof String) {
                            output.writeUTF((String) resp);
                            output.flush();
                        } else {
                            output.writeUTF("SUCCESS:Login correcto");
                            output.flush();
                            objectOutput.writeObject(resp);
                            objectOutput.flush();
                        }
                    }
                    break;
                default:
                    output.writeUTF("ERROR:Comando no reconocido");
                    output.flush();
            }
        } catch (IOException e) {
            System.out.println("Error al responder: " + e.getMessage());
        }
    }
}
