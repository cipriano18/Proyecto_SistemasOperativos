/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.ClientDAO;
import database.ContactDAO;
import database.UserDAO;
import java.util.List;
import model.Client;
import model.ClientRequest;
import model.Contact;
import model.Response;
import model.User;
import utils.Validator;

/**
 *
 * @author Cipriano
 */
public class ClientController {

// Crear cliente completo (User + Client + Contact + CXC)
    public static Response createClient(ClientRequest clientRequest) {

        System.out.println("DEBUG - Iniciando createClient");

        List<String> errors = Validator.validateClientRequest(clientRequest);
        if (!errors.isEmpty()) {
            return new Response(false, String.join(" | ", errors), null);
        }

        User user = clientRequest.getUser();
        Client client = clientRequest.getClient();
        Contact contact = clientRequest.getContact();

        if (UserDAO.getUserByUsername(user.getUsername()) != null) {
            return new Response(false, "El nombre de usuario ya está en uso", null);
        }

        user.setIdRole(3);
        boolean userInserted = UserDAO.insertUser(user);

        if (!userInserted) {
            return new Response(false, "No se pudo crear el usuario", null);
        }

        User createdUser = UserDAO.getUserByUsername(user.getUsername());
        if (createdUser == null) {
            return new Response(false, "No se pudo recuperar el usuario creado", null);
        }

        client.setIdUser(createdUser.getIdUser());

        boolean clientInserted = ClientDAO.createClient(client);
        if (!clientInserted) {
            return new Response(false, "No se pudo crear el cliente", null);
        }

        Client createdClient = ClientDAO.getClientByUserId(createdUser.getIdUser());
        if (createdClient == null) {
            return new Response(false, "No se pudo recuperar el cliente creado", null);
        }

        int idContact = ContactDAO.insertContact(contact);
        if (idContact == -1) {
            return new Response(false, "No se pudo crear el contacto", null);
        }

        boolean linked = ContactDAO.insertCXC(createdClient.getIdClient(), idContact);
        if (!linked) {
            return new Response(false, "No se pudo vincular el contacto al cliente", null);
        }

        // armar objeto completo de respuesta
        ClientRequest responseData = new ClientRequest();
        responseData.setUser(createdUser);
        responseData.setClient(createdClient);
        responseData.setContact(contact);

        return new Response(true, "Cliente creado correctamente", responseData);
    }

// Actualizar cliente existente
    public static Response updateClient(ClientRequest clientRequest) {

        System.out.println("DEBUG - Iniciando updateClient");

        if (clientRequest == null) {
            return new Response(false, "La solicitud del cliente es obligatoria", null);
        }

        User user = clientRequest.getUser();
        Client client = clientRequest.getClient();
        Contact contact = clientRequest.getContact();

        if (user == null) {
            return new Response(false, "El usuario es obligatorio", null);
        }

        if (client == null) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        if (!Validator.isValidFName(client.getfName())) {
            return new Response(false, "El campo primer nombre es obligatorio", null);
        }

        if (!Validator.isValidFSurname(client.getfSurname())) {
            return new Response(false, "El campo primer apellido es obligatorio", null);
        }

        if (!Validator.isValidMSurname(client.getmSurname())) {
            return new Response(false, "El campo segundo apellido es obligatorio", null);
        }

        if (!Validator.isValidIdentityCard(client.getIdentityCard())) {
            return new Response(false, "Cédula inválida", null);
        }

        if (!Validator.isValidUsername(user.getUsername())) {
            return new Response(false, "Nombre de usuario inválido", null);
        }

        if (contact != null) {
            if (!Validator.isValidContact(contact.getType(), contact.getContactValue())) {
                if ("PHONE".equalsIgnoreCase(contact.getType())) {
                    return new Response(false, "El teléfono debe contener exactamente 8 dígitos numéricos", null);
                }
                if ("EMAIL".equalsIgnoreCase(contact.getType())) {
                    return new Response(false, "El correo debe tener un formato válido (ejemplo: usuario@dominio.com)", null);
                }
                return new Response(false, "Valor de contacto inválido", null);
            }

            boolean contactUpdated = ContactDAO.updateContact(contact);
            if (!contactUpdated) {
                return new Response(false, "No se pudo actualizar el contacto", null);
            }
        }

        user.setIdRole(3);
        boolean userUpdated = UserDAO.updateUser(user);
        if (!userUpdated) {
            return new Response(false, "No se pudo actualizar el usuario", null);
        }

        boolean clientUpdated = ClientDAO.updateClient(client);
        if (!clientUpdated) {
            return new Response(false, "No se pudo actualizar el cliente", null);
        }

        ClientRequest responseData = new ClientRequest();
        responseData.setUser(user);
        responseData.setClient(client);
        responseData.setContact(contact);

        return new Response(true, "Cliente actualizado correctamente", responseData);
    }

    public static String deleteClient(int idClient, int idUser) {

        // 1. Eliminar contacto (CXC + AUD_Contacts)
        boolean contactDeleted = ContactDAO.deleteContactByClientId(idClient);
        if (!contactDeleted) {
            return "ERROR:No se pudo eliminar el contacto del cliente";
        }

        // 2. Eliminar cliente
        boolean clientDeleted = ClientDAO.deleteClientCascade(idClient);
        if (!clientDeleted) {
            return "ERROR:No se pudo eliminar el cliente";
        }

        // 3. Eliminar usuario
        boolean userDeleted = UserDAO.deleteUser(idUser);
        if (!userDeleted) {
            return "ERROR:No se pudo eliminar el usuario";
        }

        return "SUCCESS:Cliente eliminado correctamente";
    }
    //obtener datos de un cliente 

    public static ClientRequest getClient(int idClient) {
        ClientRequest clientRequest = ClientDAO.getFullClientById(idClient);
        if (clientRequest == null) {
            System.out.println("ERROR:Cliente no encontrado");
        }
        return clientRequest;
    }
}
