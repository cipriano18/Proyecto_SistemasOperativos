package server;

import controller.AdminController;
import controller.ReservationController;
import controller.RoleController;
import controller.UserController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.AdminRequest;
import model.EquipmentReservationRequest;
import model.Role;
import model.RXE;
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

                case "CREATE_USER": {
                    User user = (User) obj;

                    System.out.println("---- CREATE_USER ----");
                    System.out.println("IdUser: " + user.getIdUser());
                    System.out.println("IdRole: " + user.getIdRole());
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Password: " + user.getPassword());

                    String resp = UserController.createUser(user);
                    System.out.println("Respuesta: " + resp);

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "UPDATE_USER": {
                    User user = (User) obj;

                    System.out.println("---- UPDATE_USER ----");
                    System.out.println("IdUser: " + user.getIdUser());
                    System.out.println("IdRole: " + user.getIdRole());
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Password: " + user.getPassword());

                    String resp = UserController.updateUser(user);
                    System.out.println("Respuesta: " + resp);

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "GET_USER": {
                    User req = (User) obj;

                    System.out.println("---- GET_USER ----");
                    System.out.println("IdUser solicitado: " + req.getIdUser());

                    User found = UserController.getUser(req.getIdUser());

                    System.out.println("Usuario encontrado: " + found);

                    objectOutput.writeObject(found);
                    objectOutput.flush();
                    break;
                }

                case "GET_ROLES": {
                    System.out.println("---- GET_ROLES ----");

                    List<Role> roles = RoleController.getAllRoles();

                    System.out.println("Cantidad de roles enviados: " + roles.size());
                    System.out.println("Roles: " + roles);

                    objectOutput.writeObject(roles);
                    objectOutput.flush();
                    break;
                }

                case "GET_ROLE": {
                    Role req = (Role) obj;

                    System.out.println("---- GET_ROLE ----");
                    System.out.println("IdRole solicitado: " + req.getIdRole());

                    Role role = RoleController.getRole(req.getIdRole());

                    System.out.println("Rol encontrado: " + role);

                    objectOutput.writeObject(role);
                    objectOutput.flush();
                    break;
                }

                case "CREATE_ADMIN": {
                    AdminRequest adminRequest = (AdminRequest) obj;

                    System.out.println("---- CREATE_ADMIN ----");
                    System.out.println("Objeto recibido: " + adminRequest);

                    String resp = AdminController.createAdmin(adminRequest);
                    System.out.println("Respuesta: " + resp);

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "UPDATE_ADMIN": {
                    AdminRequest adminRequest = (AdminRequest) obj;

                    System.out.println("---- UPDATE_ADMIN ----");
                    System.out.println("Objeto recibido: " + adminRequest);

                    String resp = AdminController.updateAdmin(adminRequest);
                    System.out.println("Respuesta: " + resp);

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "CREATE_EQUIPMENT_RESERVATION": {
                    EquipmentReservationRequest request = (EquipmentReservationRequest) obj;

                    System.out.println("---- CREATE_EQUIPMENT_RESERVATION ----");

                    if (request.getReservation() != null) {
                        System.out.println("Reserva:");
                        System.out.println("  IdClient: " + request.getReservation().getIdClient());
                        System.out.println("  IdSection: " + request.getReservation().getIdSection());
                        System.out.println("  ReservationDate: " + request.getReservation().getReservationDate());
                    } else {
                        System.out.println("Reserva: null");
                    }

                    if (request.getEquipmentList() != null) {
                        System.out.println("Equipos recibidos: " + request.getEquipmentList().size());
                        for (RXE item : request.getEquipmentList()) {
                            System.out.println("  --- Equipo ---");
                            System.out.println("  IdRXE: " + item.getIdRxe());
                            System.out.println("  IdReservation: " + item.getIdReservation());
                            System.out.println("  IdEquipment: " + item.getIdEquipment());
                            System.out.println("  Quantity: " + item.getQuantity());
                        }
                    } else {
                        System.out.println("Lista de equipos: null");
                    }

                    String resp = ReservationController.createEquipmentReservation(request);
                    System.out.println("Respuesta: " + resp);

                    objectOutput.writeObject(resp);
                    objectOutput.flush();
                    break;
                }

                case "LOGIN": {
                    if (obj instanceof User) {
                        User u = (User) obj;

                        System.out.println("---- LOGIN ----");
                        System.out.println("Username: " + u.getUsername());
                        System.out.println("Password: " + u.getPassword());
                        System.out.println("IdRole: " + u.getIdRole());

                        Object resp = UserController.login(u);

                        System.out.println("Respuesta login: " + resp);

                        if (resp instanceof String) {
                            objectOutput.writeObject(resp);
                            objectOutput.flush();
                        } else {
                            objectOutput.writeObject("SUCCESS:Login correcto");
                            objectOutput.flush();

                            objectOutput.writeObject(resp);
                            objectOutput.flush();
                        }
                    }
                    break;
                }

                default: {
                    System.out.println("---- COMANDO NO RECONOCIDO ----");
                    System.out.println("Comando recibido: " + command);

                    objectOutput.writeObject("ERROR:Comando no reconocido");
                    objectOutput.flush();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al responder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}