package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Gestiona el servidor principal de conexiones.
 */
public class Server {

    public static Vector<ClientHandler> clients =
            new Vector<>();

    private static final int PORT = 10000;

    private static ServerSocket serverSocket;

    private static boolean running = false;

    /**
     * Inicia el servidor y espera conexiones.
     */
    public static void startServer() {

        if (running) {
            return;
        }

        running = true;

        try {

            serverSocket =
                    new ServerSocket(PORT);

            System.out.println(
                    "Servidor activo en puerto "
                    + PORT
            );

            System.out.println(
                    "Esperando conexiones..."
            );

            while (running) {

                try {

                    Socket socket =
                            serverSocket.accept();

                    System.out.println(
                            "Cliente conectado: "
                            + socket.getInetAddress()
                    );

                    ClientHandler handler =
                            new ClientHandler(socket);

                    clients.add(handler);

                    handler.start();

                } catch (IOException e) {
                    if (running) {

                        System.out.println(
                                "Error aceptando "
                                + "conexión: "
                                + e.getMessage()
                        );
                    }
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error al iniciar servidor: "
                    + e.getMessage()
            );

        } finally {

            stopServer();
        }
    }

    /**
     * Detiene el servidor y cierra conexiones.
     */
    public static void stopServer() {

        running = false;

        System.out.println(
                "Apagando servidor..."
        );

        try {

            // Cierra el socket principal
            if (
                    serverSocket != null
                    && !serverSocket.isClosed()
            ) {

                serverSocket.close();
            }

            // Cierra todos los clientes
            for (ClientHandler client : clients) {

                try {

                    client.closeConnection();

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            clients.clear();

        } catch (IOException e) {

            System.out.println(
                    "Error al cerrar servidor: "
                    + e.getMessage()
            );
        }

        System.out.println(
                "Servidor detenido."
        );
    }
}