package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import model.Client;
import model.ClientRequest;
import model.Contact;
import model.Response;
import model.User;

public class TestClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n==============================");
            System.out.println("         TEST CLIENT");
            System.out.println("==============================");
            System.out.println("1. Crear cliente Reyner");
            System.out.println("2. Login Reyner");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    crearClienteReyner();
                    break;

                case 2:
                    loginReyner();
                    break;

                case 3:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida");
                    break;
            }

        } while (opcion != 3);

        sc.close();
    }

    // =========================
    // CREAR CLIENTE REYNER
    // =========================
    private static void crearClienteReyner() {

        try (
                Socket socket = new Socket(HOST, PORT); ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream in = new ObjectInputStream(socket.getInputStream());) {

            System.out.println("\n=========== CREANDO REYNER ===========");
            System.out.println("Conectado al servidor");

            User user = new User();
            user.setUsername("reyner" + System.currentTimeMillis());
            user.setPassword("Test1234");

            Client client = new Client(
                    0,
                    1,
                    "Reyner",
                    "Rojas",
                    "Gutiérrez",
                    "Rojas",
                    "987654321"
            );

            Contact contact = new Contact();
            contact.setType("EMAIL");
            contact.setContactValue("reynerrojas@gmail.com");

            ClientRequest request = new ClientRequest();
            request.setUser(user);
            request.setClient(client);
            request.setContact(contact);

            System.out.println("\n=== DATOS ENVIADOS ===");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            System.out.println("IdType: " + client.getIdType());
            System.out.println("Nombre: " + client.getfName() + " " + client.getmName());
            System.out.println("Apellidos: " + client.getfSurname() + " " + client.getmSurname());
            System.out.println("Cédula: " + client.getIdentityCard());
            System.out.println("Contacto: " + contact.getType() + " - " + contact.getContactValue());

            out.writeObject("CREATE_CLIENT");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            System.out.println("\n=== RESPUESTA DEL SERVIDOR ===");

            if (response instanceof Response) {
                Response resp = (Response) response;

                System.out.println("Success: " + resp.isSuccess());
                System.out.println("Message: " + resp.getMessage());

                if (resp.getData() instanceof ClientRequest) {
                    ClientRequest data = (ClientRequest) resp.getData();

                    System.out.println("\n=== CLIENTE CREADO ===");
                    System.out.println("IdClient: " + data.getClient().getIdClient());
                    System.out.println("IdUser: " + data.getClient().getIdUser());
                    System.out.println("IdType: " + data.getClient().getIdType());
                    System.out.println("Nombre completo: "
                            + data.getClient().getfName() + " "
                            + data.getClient().getmName() + " "
                            + data.getClient().getfSurname() + " "
                            + data.getClient().getmSurname());
                    System.out.println("Cédula: " + data.getClient().getIdentityCard());

                    System.out.println("\n=== DATOS RELACIONADOS ===");
                    System.out.println("Username: " + data.getUser().getUsername());
                    System.out.println("Password: " + data.getUser().getPassword());
                    System.out.println("Contacto: "
                            + data.getContact().getType() + " - "
                            + data.getContact().getContactValue());
                }

            } else {
                System.out.println("Respuesta inesperada: " + response);
            }

        } catch (Exception e) {
            System.out.println("Error al crear cliente Reyner:");
            e.printStackTrace();
        }
    }

    // =========================
    // LOGIN REYNER
    // =========================
    private static void loginReyner() {

        try (
                Socket socket = new Socket(HOST, PORT); ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream in = new ObjectInputStream(socket.getInputStream());) {

            System.out.println("\n=========== LOGIN REYNER ===========");
            System.out.println("Conectado al servidor");

            User user = new User();
            user.setUsername("reyner1776481559841");
            user.setPassword("Test1234");

            System.out.println("\n=== DATOS ENVIADOS ===");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());

            out.writeObject("LOGIN");
            out.flush();

            out.writeObject(user);
            out.flush();

            Object response = in.readObject();

            System.out.println("\n=== RESPUESTA DEL SERVIDOR ===");

            if (response instanceof Response) {
                Response resp = (Response) response;

                System.out.println("Success: " + resp.isSuccess());
                System.out.println("Message: " + resp.getMessage());

                if (resp.getData() instanceof ClientRequest) {
                    ClientRequest data = (ClientRequest) resp.getData();

                    System.out.println("\n=== LOGIN CLIENTE ===");
                    System.out.println("IdClient: " + data.getClient().getIdClient());
                    System.out.println("IdUser: " + data.getClient().getIdUser());
                    System.out.println("Nombre completo: "
                            + data.getClient().getfName() + " "
                            + data.getClient().getmName() + " "
                            + data.getClient().getfSurname() + " "
                            + data.getClient().getmSurname());
                    System.out.println("Cédula: " + data.getClient().getIdentityCard());

                    System.out.println("\n=== DATOS RELACIONADOS ===");
                    System.out.println("Username: " + data.getUser().getUsername());
                    System.out.println("Password: " + data.getUser().getPassword());
                    System.out.println("Contacto: "
                            + data.getContact().getType() + " - "
                            + data.getContact().getContactValue());
                } else if (resp.getData() != null) {
                    System.out.println("Data: " + resp.getData());
                }

            } else {
                System.out.println("Respuesta inesperada: " + response);
            }

        } catch (Exception e) {
            System.out.println("Error al hacer login:");
            e.printStackTrace();
        }
    }
}
