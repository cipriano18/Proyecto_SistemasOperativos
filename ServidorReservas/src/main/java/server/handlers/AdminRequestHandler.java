/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.AdminController;
import dto.AdminRequest;
import service.Response;

/**
 * Manejador encargado de procesar las solicitudes relacionadas con
 * administradores.
 *
 * <p>
 * Recibe comandos provenientes del servidor y delega las operaciones
 * correspondientes al {@code AdminController}.
 * </p>
 *
 * @author Cipriano
 */
public class AdminRequestHandler {

    /**
     * Procesa un comando relacionado con administradores.
     *
     * @param command comando recibido
     * @param obj objeto asociado al comando
     * @return respuesta del proceso
     */
    public static Response handle(
            String command,
            Object obj
    ) {

        switch (command.toUpperCase()) {

            case "CREATE_ADMIN": {

                AdminRequest request
                        = (AdminRequest) obj;

                return AdminController.createAdmin(
                        request
                );
            }

            case "UPDATE_ADMIN": {

                AdminRequest request
                        = (AdminRequest) obj;

                return AdminController.updateAdmin(
                        request
                );
            }

            case "DELETE_ADMIN": {

                int idAdmin = (int) obj;

                System.out.println(
                        "id admin piupiu "
                        + idAdmin
                );

                return AdminController.deleteAdmin(
                        idAdmin
                );
            }

            default:

                return new Response(
                        false,
                        "Comando de administrador "
                        + "no reconocido",
                        null
                );
        }
    }
}
