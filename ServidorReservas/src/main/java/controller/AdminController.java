package controller;

import model.Admin;
import model.User;
import model.Contact;
import service.Response;

import database.UserDAO;
import database.AdminDAO;
import database.ContactDAO;
import dto.AdminRequest;

import utils.Validator;

public class AdminController {

    // Obtener admin completo por id
    public static Response getAdmin(int idAdmin) {
        if (idAdmin <= 0) {
            return new Response(false, "El id del administrador es inválido", null);
        }

        AdminRequest adminRequest = AdminDAO.getFullAdminById(idAdmin);

        if (adminRequest == null) {
            return new Response(false, "Administrador no encontrado", null);
        }

        return new Response(true, "Administrador obtenido correctamente", adminRequest);
    }

    // Crear administrador completo (User + Admin + Contact + CXA)
    public static Response createAdmin(AdminRequest adminRequest) {

        System.out.println("DEBUG - Iniciando createAdmin");

        if (adminRequest == null) {
            return new Response(false, "La solicitud del administrador es obligatoria", null);
        }

        User user = adminRequest.getUser();
        Admin admin = adminRequest.getAdmin();
        Contact contact = adminRequest.getContact();

        if (user == null) {
            return new Response(false, "El usuario es obligatorio", null);
        }

        if (admin == null) {
            return new Response(false, "El administrador es obligatorio", null);
        }

        if (contact == null) {
            return new Response(false, "El contacto es obligatorio", null);
        }

        // 1. VALIDACIONES USER
        if (!Validator.isValidUsername(user.getUsername())) {
            return new Response(false, "Nombre de usuario inválido", null);
        }

        if (!Validator.isValidPassword(user.getPassword())) {
            return new Response(false, "Contraseña inválida", null);
        }

        if (UserDAO.getUserByUsername(user.getUsername()) != null) {
            return new Response(false, "El nombre de usuario ya está en uso", null);
        }

        // 2. VALIDACIONES ADMIN
        if (!Validator.isValidFName(admin.getfName())) {
            return new Response(false, "El campo primer nombre es obligatorio", null);
        }

        if (!Validator.isValidFSurname(admin.getfSurname())) {
            return new Response(false, "El campo primer apellido es obligatorio", null);
        }

        if (!Validator.isValidMSurname(admin.getmSurname())) {
            return new Response(false, "El campo segundo apellido es obligatorio", null);
        }

        if (!Validator.isValidIdentityCard(admin.getIdentityCard())) {
            return new Response(false, "Cédula inválida", null);
        }

        if (AdminDAO.getAdminByIdentityCard(admin.getIdentityCard()) != null) {
            return new Response(false, "La cédula ya está registrada como administrador", null);
        }
        // 3. VALIDACIONES CONTACTO
        if (Validator.isEmpty(contact.getType())) {
            return new Response(false, "Tipo de contacto requerido", null);
        }

        if (Validator.isEmpty(contact.getContactValue())) {
            return new Response(false, "Valor de contacto requerido", null);
        }

        if (!Validator.isValidContact(contact.getType(), contact.getContactValue())) {
            if ("PHONE".equalsIgnoreCase(contact.getType())) {
                return new Response(false, "El teléfono debe contener exactamente 8 dígitos numéricos", null);
            }
            if ("EMAIL".equalsIgnoreCase(contact.getType())) {
                return new Response(false, "El correo debe tener un formato válido (ejemplo: usuario@dominio.com)", null);
            }
            return new Response(false, "Valor de contacto inválido", null);
        }

        // 4. CREAR USER
        user.setIdRole(2); // Administrador
        boolean userInserted = UserDAO.insertUser(user);

        if (!userInserted) {
            return new Response(false, "No se pudo crear el usuario", null);
        }

        User createdUser = UserDAO.getUserByUsername(user.getUsername());

        if (createdUser == null) {
            return new Response(false, "No se pudo recuperar el usuario creado", null);
        }

        System.out.println("DEBUG - createdUser ID: " + createdUser.getIdUser());

        // 5. CREAR ADMIN
        admin.setIdUser(createdUser.getIdUser());

        boolean adminInserted = AdminDAO.insertAdmin(admin);

        if (!adminInserted) {
            return new Response(false, "No se pudo crear el administrador", null);
        }

        Admin createdAdmin = AdminDAO.getAdminByUserId(createdUser.getIdUser());

        if (createdAdmin == null) {
            return new Response(false, "No se pudo recuperar el administrador creado", null);
        }

        System.out.println("DEBUG - createdAdmin ID: " + createdAdmin.getIdAdmin());

        // 6. CREAR CONTACTO
        int idContact = ContactDAO.insertContact(contact);

        if (idContact == -1) {
            return new Response(false, "No se pudo crear el contacto", null);
        }

        contact.setIdContact(idContact);

        System.out.println("DEBUG - idContact: " + idContact);

        // 7. VINCULAR CONTACTO (CXA)
        boolean linked = ContactDAO.insertCXA(createdAdmin.getIdAdmin(), idContact);

        if (!linked) {
            return new Response(false, "No se pudo vincular el contacto al administrador", null);
        }

        // 8. ARMAR RESPUESTA
        AdminRequest responseData = new AdminRequest();
        responseData.setUser(createdUser);
        responseData.setAdmin(createdAdmin);
        responseData.setContact(contact);

        return new Response(true, "Administrador creado correctamente", responseData);
    }

    // Actualizar administrador existente
    public static Response updateAdmin(AdminRequest adminRequest) {

        System.out.println("DEBUG - Iniciando updateAdmin");

        if (adminRequest == null) {
            return new Response(false, "La solicitud del administrador es obligatoria", null);
        }

        User user = adminRequest.getUser();
        Admin admin = adminRequest.getAdmin();
        Contact contact = adminRequest.getContact();

        if (user == null) {
            return new Response(false, "El usuario es obligatorio", null);
        }

        if (admin == null) {
            return new Response(false, "El administrador es obligatorio", null);
        }

        Admin existingAdmin = AdminDAO.getAdminById(admin.getIdAdmin());
        if (existingAdmin == null) {
            return new Response(false, "Administrador no encontrado", null);
        }

        if (!Validator.isValidUsername(user.getUsername())) {
            return new Response(false, "Nombre de usuario inválido", null);
        }

        User existingUser = UserDAO.getUserByUsername(user.getUsername());
        if (existingUser != null && existingUser.getIdUser() != user.getIdUser()) {
            return new Response(false, "El nombre de usuario ya está en uso", null);
        }

        if (!Validator.isValidFName(admin.getfName())) {
            return new Response(false, "El campo primer nombre es obligatorio", null);
        }

        if (!Validator.isValidFSurname(admin.getfSurname())) {
            return new Response(false, "El campo primer apellido es obligatorio", null);
        }

        if (!Validator.isValidMSurname(admin.getmSurname())) {
            return new Response(false, "El campo segundo apellido es obligatorio", null);
        }

        if (!Validator.isValidIdentityCard(admin.getIdentityCard())) {
            return new Response(false, "Cédula inválida", null);
        }

        if (contact != null) {
            if (Validator.isEmpty(contact.getType())) {
                return new Response(false, "Tipo de contacto requerido", null);
            }

            if (Validator.isEmpty(contact.getContactValue())) {
                return new Response(false, "Valor de contacto requerido", null);
            }

            if (!Validator.isValidContact(contact.getType(), contact.getContactValue())) {
                if ("PHONE".equalsIgnoreCase(contact.getType())) {
                    return new Response(false, "El teléfono debe contener exactamente 8 dígitos numéricos", null);
                }
                if ("EMAIL".equalsIgnoreCase(contact.getType())) {
                    return new Response(false, "El correo debe tener un formato válido (ejemplo: usuario@dominio.com)", null);
                }
                return new Response(false, "Valor de contacto inválido", null);
            }
        }

        user.setIdRole(2);

        boolean userUpdated = UserDAO.updateUser(user);
        if (!userUpdated) {
            return new Response(false, "No se pudo actualizar el usuario", null);
        }

        boolean adminUpdated = AdminDAO.updateAdmin(admin);
        if (!adminUpdated) {
            return new Response(false, "No se pudo actualizar el administrador", null);
        }

        if (contact != null) {
            boolean contactUpdated = ContactDAO.updateContact(contact);
            if (!contactUpdated) {
                return new Response(false, "No se pudo actualizar el contacto", null);
            }
        }

        AdminRequest responseData = new AdminRequest();
        responseData.setUser(user);
        responseData.setAdmin(admin);
        responseData.setContact(contact);

        return new Response(true, "Administrador actualizado correctamente", responseData);
    }

    // Eliminar admin completo en cascada
    public static Response deleteAdmin(int idAdmin) {
        if (idAdmin <= 0) {
            return new Response(false, "El id del administrador es inválido", null);
        }

        AdminRequest existingAdmin = AdminDAO.getFullAdminById(idAdmin);
        if (existingAdmin == null) {
            return new Response(false, "Administrador no encontrado", null);
        }

        boolean adminDeleted = AdminDAO.deleteAdminCascade(idAdmin);

        if (!adminDeleted) {
            return new Response(false, "No se pudo eliminar el administrador", null);
        }

        return new Response(true, "Administrador eliminado correctamente", existingAdmin);
    }
}