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
            System.out.println("1. Crear cliente");
            System.out.println("2. Eliminar cliente");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    crearCliente();
                    break;
                case 2:
                    eliminarCliente();
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

    private static void crearCliente() {

        try (
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            System.out.println("\n=========== CREANDO CLIENTE ===========");

            long timestamp = System.currentTimeMillis();

            User user = new User(
                    0,
                    3,
                    "reyner" + timestamp,
                    "Test1234"
            );

            Client client = new Client(
                    0,
                    0,
                    "Reyner",
                    "Rojas",
                    "Gutiérrez",
                    "Rojas",
                    "987654321"
            );

            Contact contact = new Contact(
                    0,
                    "EMAIL",
                    "reyner" + timestamp + "@gmail.com"
            );

            ClientRequest request = new ClientRequest(user, client, contact);

            System.out.println("\n=== DATOS ENVIADOS ===");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Nombre: " + client.getfName());
            System.out.println("Apellidos: " + client.getfSurname());
            System.out.println("Cédula: " + client.getIdentityCard());
            System.out.println("Contacto: " + contact.getType() + " - " + contact.getContactValue());

            out.writeObject("CREATE_CLIENT");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            System.out.println("\n=== RESPUESTA ===");

            if (response instanceof Response) {
                Response resp = (Response) response;

                System.out.println("Success: " + resp.isSuccess());
                System.out.println("Message: " + resp.getMessage());

                if (resp.getData() instanceof ClientRequest) {
                    ClientRequest data = (ClientRequest) resp.getData();

                    System.out.println("\n=== CLIENTE CREADO ===");
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

                    if (data.getContact() != null) {
                        System.out.println("Contacto: "
                                + data.getContact().getType() + " - "
                                + data.getContact().getContactValue());
                    }
                }

            } else {
                System.out.println("Respuesta inesperada: " + response);
            }

        } catch (Exception e) {
            System.out.println("Error al crear cliente:");
            e.printStackTrace();
        }
    }

    private static void eliminarCliente() {

        try (
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            System.out.println("\n=========== ELIMINANDO CLIENTE ===========");

            User user = new User(
                    11,
                    3,
                    null,
                    null
            );

            Client client = new Client(
                    10,
                    11,
                    "Reyner",
                    "Rojas",
                    "Gutiérrez",
                    "Rojas",
                    "987654321"
            );

            Contact contact = new Contact(
                    6,
                    "EMAIL",
                    "reyner@gmail.com"
            );

            ClientRequest request = new ClientRequest(user, client, contact);

            System.out.println("\n=== DATOS ENVIADOS ===");
            System.out.println("IdClient: " + client.getIdClient());
            System.out.println("IdUser: " + user.getIdUser());

            out.writeObject("DELETE_CLIENT");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            System.out.println("\n=== RESPUESTA ===");

            if (response instanceof Response) {
                Response resp = (Response) response;

                System.out.println("Success: " + resp.isSuccess());
                System.out.println("Message: " + resp.getMessage());

                if (resp.getData() instanceof ClientRequest) {
                    ClientRequest data = (ClientRequest) resp.getData();

                    System.out.println("\n=== CLIENTE ELIMINADO ===");
                    System.out.println("IdClient: " + data.getClient().getIdClient());
                    System.out.println("IdUser: " + data.getUser().getIdUser());
                }
            } else {
                System.out.println("Respuesta inesperada: " + response);
            }

        } catch (Exception e) {
            System.out.println("Error al eliminar cliente:");
            e.printStackTrace();
        }
    }
}