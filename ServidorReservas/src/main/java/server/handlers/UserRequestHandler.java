/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.UserController;
import service.Response;
import model.User;

/**
 * Manejador encargado de procesar las solicitudes relacionadas con usuarios.
 *
 * <p>
 * Recibe comandos provenientes del servidor y delega las operaciones
 * correspondientes al {@code UserController}.
 * </p>
 *
 * @author Cipriano
 */
public class UserRequestHandler {

    /**
     * Procesa un comando relacionado con usuarios.
     *
     * @param command comando recibido
     * @param obj objeto asociado al comando
     * @return respuesta del proceso
     */
    public static Response handle(
            String command,
            Object obj
    ) {

        User user
                = (User) obj;

        switch (command.toUpperCase()) {

            case "LOGIN":

                return UserController.login(
                        user
                );

            default:

                return new Response(
                        false,
                        "Comando de usuario "
                        + "no reconocido",
                        null
                );
        }
    }
}
