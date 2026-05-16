/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import service.Response;
import server.ClientHandler;
import server.Server;

/**
 * Manejador encargado de procesar las solicitudes
 * relacionadas con conexiones del servidor.
 *
 * <p>
 * Permite gestionar acciones como el cierre
 * de conexión de clientes.
 * </p>
 *
 * @author Cipriano
 */
public class ConnectionRequestHandler {

    /**
     * Procesa un comando relacionado con conexiones.
     *
     * @param command comando recibido
     * @param clientHandler cliente asociado a la conexión
     * @return respuesta del proceso
     */
    public static Response handle(
            String command,
            ClientHandler clientHandler
    ) {

        switch (command.toUpperCase()) {

            case "LOGOUT":

            case "CLOSE_CONNECTION":

                System.out.println(
                        "---- CLOSE_CONNECTION ----"
                );

                System.out.println(
                        "Cliente solicitó cerrar conexión: "
                        + clientHandler.getClientAddress()
                );

                Server.clients.remove(clientHandler);

                return new Response(
                        true,
                        "Conexión cerrada correctamente",
                        null
                );

            default:

                return new Response(
                        false,
                        "Comando de conexión "
                        + "no reconocido",
                        null
                );
        }
    }
}