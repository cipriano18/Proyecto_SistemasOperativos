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
import model.AdminRequest;
import model.Client;
import model.ClientRequest;
import model.Contact;
import model.Response;
import utils.Validator;

/**
 *
 * @author User
 */
public class UserController {
    //funcion para login
    public static Response login(User user) {

        if (user == null) {
            return new Response(false, "La solicitud de login es obligatoria", null);
        }

        if (!Validator.isValidUsername(user.getUsername())
                || !Validator.isValidPassword(user.getPassword())) {
            return new Response(false, "Credenciales incorrectas", null);
        }

        User loggedUser = UserDAO.validateLogin(user.getUsername(), user.getPassword());

        if (loggedUser == null) {
            return new Response(false, "Credenciales incorrectas", null);
        }

        System.out.println("Usuario validado: " + loggedUser);
        System.out.println("Rol detectado: " + loggedUser.getIdRole());

        // ADMIN
        if (loggedUser.getIdRole() == 2) {

            Admin adminDB = AdminDAO.getAdminByUserId(loggedUser.getIdUser());
            if (adminDB == null) {
                return new Response(false, "No se encontró el administrador", null);
            }

            Contact contactDB = ContactDAO.getContactByAdminId(adminDB.getIdAdmin());

            AdminRequest adminRequest = new AdminRequest();
            adminRequest.setUser(loggedUser);
            adminRequest.setAdmin(adminDB);
            adminRequest.setContact(contactDB);

            return new Response(true, "Login correcto - ADMIN", adminRequest);
        }
        // CLIENTE
        if (loggedUser.getIdRole() == 3) {

            Client clientDB = ClientDAO.getClientByUserId(loggedUser.getIdUser());
            if (clientDB == null) {
                return new Response(false, "No se encontró el cliente", null);
            }

            Contact contactDB = ContactDAO.getContactByClientId(clientDB.getIdClient());

            ClientRequest clientRequest = new ClientRequest();
            clientRequest.setUser(loggedUser);
            clientRequest.setClient(clientDB);
            clientRequest.setContact(contactDB);

            return new Response(true, "Login correcto - CLIENTE", clientRequest);
        }

        return new Response(false, "Rol no permitido", null);
    }
}
