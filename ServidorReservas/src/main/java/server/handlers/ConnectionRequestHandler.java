/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import service.Response;
import server.ClientHandler;
import server.Server;

/**
 *
 * @author Cipriano
 */
public class ConnectionRequestHandler {

    public static Response handle(String command, ClientHandler clientHandler) {

        switch (command.toUpperCase()) {

            case "LOGOUT":
            case "CLOSE_CONNECTION":
                System.out.println("---- CLOSE_CONNECTION ----");
                System.out.println("Cliente solicitó cerrar conexión: " + clientHandler.getClientAddress());

                Server.clients.remove(clientHandler);

                return new Response(true, "Conexión cerrada correctamente", null);

            default:
                return new Response(false, "Comando de conexión no reconocido", null);
        }
    }

}
