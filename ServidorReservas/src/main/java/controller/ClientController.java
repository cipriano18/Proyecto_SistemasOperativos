/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.ClientDAO;
import database.ContactDAO;
import database.UserDAO;
import model.Client;
import model.ClientRequest;
import model.Contact;
import model.User;
import utils.Validator;

/**
 *
 * @author Cipriano
 */
public class ClientController {
 
// Crear cliente completo (User + Client + Contact + CXC)
public static String createClient(ClientRequest clientRequest) {

    User user = clientRequest.getUser();
    Client client = clientRequest.getClient();
    Contact contact = clientRequest.getContact();

    System.out.println("DEBUG - Iniciando createClient");

    // 1. VALIDACIONES USER
    if (!Validator.isValidUsername(user.getUsername())) {
        return "ERROR:Nombre de usuario inválido";
    }

    if (!Validator.isValidPassword(user.getPassword())) {
        return "ERROR:Contraseña inválida";
    }

    // Verificar si ya existe
    if (UserDAO.getUserByUsername(user.getUsername()) != null) {
        return "ERROR:El nombre de usuario ya está en uso";
    }

    // 2. VALIDACIONES CLIENT
    if (!Validator.isValidFName(client.getfName())) {
        return "ERROR:El campo primer nombre es obligatorio";
    }

    if (!Validator.isValidFSurname(client.getfSurname())) {
        return "ERROR:El campo primer apellido es obligatorio";
    }

    if (!Validator.isValidMSurname(client.getmSurname())) {
        return "ERROR:El campo segundo apellido es obligatorio";
    }

    if (!Validator.isValidIdentityCard(client.getIdentityCard())) {
        return "ERROR:Cédula inválida";
    }

    if (client.getIdType() <= 0) {
        return "ERROR:Tipo de cliente inválido";
    }

    // 3. VALIDACIONES CONTACTO
    if (Validator.isEmpty(contact.getType())) {
        return "ERROR:Tipo de contacto requerido";
    }

    if (Validator.isEmpty(contact.getContactValue())) {
        return "ERROR:Valor de contacto requerido";
    }

    if ("EMAIL".equals(contact.getType())) {
        if (!Validator.isValidEmail(contact.getContactValue())) {
            return "ERROR:El correo debe tener un formato válido (ejemplo: usuario@dominio.com)";
        }
    }

    if ("PHONE".equals(contact.getType())) {
        if (!Validator.isValidPhone(contact.getContactValue())) {
            return "ERROR:El número de teléfono debe contener exactamente 8 dígitos";
        }
    }
    boolean userInserted = UserDAO.insertUser(user);

    if (!userInserted) {
        return "ERROR:No se pudo crear el usuario";
    }

    // Obtener usuario creado
    User createdUser = UserDAO.getUserByUsername(user.getUsername());

    if (createdUser == null) {
        return "ERROR:No se pudo recuperar el usuario creado";
    }

    System.out.println("DEBUG - createdUser ID: " + createdUser.getIdUser());

    // 5. CREAR CLIENT
    client.setIdUser(createdUser.getIdUser());

    boolean clientInserted = ClientDAO.createClient(client);

    if (!clientInserted) {
        return "ERROR:No se pudo crear el cliente";
    }

    // Obtener cliente correctamente
    Client createdClient = ClientDAO.getClientByUserId(createdUser.getIdUser());

    if (createdClient == null) {
        return "ERROR:No se pudo recuperar el cliente creado";
    }

    System.out.println("DEBUG - createdClient ID: " + createdClient.getIdClient());

    // 6. CREAR CONTACTO
    int idContact = ContactDAO.insertContact(contact);

    if (idContact == -1) {
        return "ERROR:No se pudo crear el contacto";
    }

    System.out.println("DEBUG - idContact: " + idContact);

    // 7. VINCULAR CONTACTO (CXC)
    boolean linked = ContactDAO.insertCXC(createdClient.getIdClient(), idContact);

    if (!linked) {
        return "ERROR:No se pudo vincular el contacto al cliente";
    }

    return "SUCCESS:Cliente creado correctamente";
}

}