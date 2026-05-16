/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.ClientController;
import dto.ClientRequest;
import service.Response;

/**
 * Manejador encargado de procesar las solicitudes relacionadas con clientes.
 *
 * <p>
 * Recibe comandos provenientes del servidor y delega las operaciones
 * correspondientes al {@code ClientController}.
 * </p>
 *
 * @author Cipriano
 */
public class ClientRequestHandler {

    /**
     * Procesa un comando relacionado con clientes.
     *
     * @param command comando recibido
     * @param obj objeto asociado al comando
     * @return respuesta del proceso
     */
    public static Response handle(
            String command,
            Object obj
    ) {

        ClientRequest request
                = (ClientRequest) obj;

        switch (command.toUpperCase()) {

            case "CREATE_CLIENT":

                return ClientController.createClient(
                        request
                );

            case "UPDATE_CLIENT":

                return ClientController.updateClient(
                        request
                );

            case "DELETE_CLIENT":

                return ClientController.deleteClient(
                        request
                );

            default:

                return new Response(
                        false,
                        "Comando de cliente "
                        + "no reconocido",
                        null
                );
        }
    }
}
