/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import controller.RoleController;
import controller.UserController;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import model.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import model.Role;

/**
 *
 * @author User
 */
public class ClientHandler extends Thread {

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objectOutput;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            objectInput = new ObjectInputStream(socket.getInputStream());
            objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.flush();
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error al crear flujos: " + e.getMessage());
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
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private void processRequest(String command, Object obj) {
        try {
            switch (command.toUpperCase()) {

                case "CREATE_USER":
                    String result = UserController.createUser((User) obj);
                    output.writeUTF(result);
                    output.flush();
                    break;

                case "UPDATE_USER":
                    String resultUpdate = UserController.updateUser((User) obj);
                    output.writeUTF(resultUpdate);
                    output.flush();
                    break;

                case "GET_USER":
                    User found = UserController.getUser(((User) obj).getIdUser());
                    objectOutput.writeObject(found);
                    objectOutput.flush();
                    break;

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

                default:
                    output.writeUTF("ERROR:Comando no reconocido");
                    output.flush();
            }
        } catch (IOException e) {
            System.out.println("Error al responder: " + e.getMessage());
        }
    }
}
