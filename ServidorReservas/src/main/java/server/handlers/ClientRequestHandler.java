/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.ClientController;
import model.ClientRequest;
import model.Response;

/**
 *
 * @author Cipriano
 */
public class ClientRequestHandler {

    public static Response handle(String command, Object obj) {
        ClientRequest request = (ClientRequest) obj;

        switch (command.toUpperCase()) {
            case "CREATE_CLIENT":
                return ClientController.createClient(request);

            case "UPDATE_CLIENT":
                return ClientController.updateClient(request);

            case "DELETE_CLIENT":
                return ClientController.deleteClient(request);

            default:
                return new Response(false, "Comando de cliente no reconocido", null);
        }
    }
}
