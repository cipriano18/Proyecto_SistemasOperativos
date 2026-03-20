/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author User
 */
public class  Server extends Thread {

    // Lista de clientes conectados
    public static Vector<ClientHandler> clients = new Vector<>();
    private static final int PORT = 8000;

    public static void startServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println(" Servidor activo en puerto " + PORT);
            System.out.println("Esperando conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(" Cliente conectado: " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            }

        } catch (IOException e) {
            System.out.println(" Error al iniciar servidor: " + e.getMessage());
        }
    }

}
