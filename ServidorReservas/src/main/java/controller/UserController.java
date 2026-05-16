/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.AdminDAO;
import database.ClientDAO;
import database.ContactDAO;
import model.User;
import database.UserDAO;
import model.Admin;
import dto.AdminRequest;
import model.Client;
import dto.ClientRequest;
import model.Contact;
import service.Response;
import utils.Validator;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con usuarios.
 *
 * <p>
 * Permite validar el inicio de sesión y retornar los datos correspondientes
 * según el rol del usuario.
 * </p>
 *
 * @author Cipriano
 */
public class UserController {

    /**
     * Valida el inicio de sesión de un usuario.
     *
     * <p>
     * Según el rol detectado, retorna la información correspondiente del Super
     * Administrador, Administrador o Cliente.
     * </p>
     *
     * @param user usuario con las credenciales de acceso
     * @return respuesta con el resultado del inicio de sesión
     */
    public static Response login(User user) {

        if (user == null) {
            return new Response(
                    false,
                    "La solicitud de login es obligatoria",
                    null
            );
        }

        if (!Validator.isValidUsername(user.getUsername())
                || !Validator.isValidPassword(
                        user.getPassword()
                )) {

            return new Response(
                    false,
                    "Credenciales incorrectas",
                    null
            );
        }

        User loggedUser
                = UserDAO.validateLogin(
                        user.getUsername(),
                        user.getPassword()
                );

        if (loggedUser == null) {
            return new Response(
                    false,
                    "Credenciales incorrectas",
                    null
            );
        }

        System.out.println(
                "Usuario validado: "
                + loggedUser
        );

        System.out.println(
                "Rol detectado: "
                + loggedUser.getIdRole()
        );

        /*
         * SUPER ADMIN
         */
        if (loggedUser.getIdRole() == 1) {

            AdminRequest adminRequest
                    = new AdminRequest();

            adminRequest.setUser(loggedUser);

            adminRequest.setAdmin(null);

            adminRequest.setContact(null);

            return new Response(
                    true,
                    "Login correcto - SUPER ADMIN",
                    adminRequest
            );
        }

        /*
         * ADMIN
         */
        if (loggedUser.getIdRole() == 2) {

            Admin adminDB
                    = AdminDAO.getAdminByUserId(
                            loggedUser.getIdUser()
                    );

            if (adminDB == null) {
                return new Response(
                        false,
                        "No se encontró el administrador",
                        null
                );
            }

            Contact contactDB
                    = ContactDAO.getContactByAdminId(
                            adminDB.getIdAdmin()
                    );

            AdminRequest adminRequest
                    = new AdminRequest();

            adminRequest.setUser(loggedUser);

            adminRequest.setAdmin(adminDB);

            adminRequest.setContact(contactDB);

            return new Response(
                    true,
                    "Login correcto - ADMIN",
                    adminRequest
            );
        }

        /*
         * CLIENTE
         */
        if (loggedUser.getIdRole() == 3) {

            Client clientDB
                    = ClientDAO.getClientByUserId(
                            loggedUser.getIdUser()
                    );

            if (clientDB == null) {
                return new Response(
                        false,
                        "No se encontró el cliente",
                        null
                );
            }

            Contact contactDB
                    = ContactDAO.getContactByClientId(
                            clientDB.getIdClient()
                    );

            ClientRequest clientRequest
                    = new ClientRequest();

            clientRequest.setUser(loggedUser);

            clientRequest.setClient(clientDB);

            clientRequest.setContact(contactDB);

            return new Response(
                    true,
                    "Login correcto - CLIENTE",
                    clientRequest
            );
        }

        return new Response(
                false,
                "Rol no permitido",
                null
        );
    }
}
