package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    public static Vector<ClientHandler> clients = new Vector<>();
    private static final int PORT = 8000;

    private static ServerSocket serverSocket;
    private static boolean running = false;

    // ─────────────────────────────────────────────
    public static void startServer() {
        if (running) return;

        running = true;

        try {
            serverSocket = new ServerSocket(PORT);

            System.out.println("Servidor activo en puerto " + PORT);
            System.out.println("Esperando conexiones...");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();

                    System.out.println("Cliente conectado: " + socket.getInetAddress());

                    ClientHandler handler = new ClientHandler(socket);
                    clients.add(handler);
                    handler.start();

                } catch (IOException e) {
                    // Esto pasa cuando cerramos el serverSocket
                    if (running) {
                        System.out.println("Error aceptando conexión: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error al iniciar servidor: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    // ─────────────────────────────────────────────
    public static void stopServer() {
        running = false;

        System.out.println("Apagando servidor...");

        try {
            // Cerrar server socket (esto rompe el accept)
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // Cerrar todos los clientes
            for (ClientHandler client : clients) {
                try {
                    client.closeConnection(); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            clients.clear();

        } catch (IOException e) {
            System.out.println("Error al cerrar servidor: " + e.getMessage());
        }

        System.out.println("Servidor detenido.");
    }
}