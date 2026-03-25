package controller;

import model.Admin;
import model.User;
import model.Contact;

import database.UserDAO;
import database.AdminDAO;
import database.ContactDAO;
import model.AdminRequest;

import utils.Validator;

public class AdminController {

    // Crear administrador completo (User + Admin + Contact + CXA)
    public static String createAdmin(AdminRequest adminRequest) {

        User user = adminRequest.getUser();
        Admin admin = adminRequest.getAdmin();
        Contact contact = adminRequest.getContact();

        System.out.println("DEBUG - Iniciando createAdmin");

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

        // 2. VALIDACIONES ADMIN
        if (!Validator.isValidFName(admin.getfName())) {
            return "ERROR:El campo primer nombre es obligatorio";
        }

        if (!Validator.isValidFSurname(admin.getfSurname())) {
            return "ERROR:El campo primer apellido es obligatorio";
        }

        if (!Validator.isValidMSurname(admin.getfSurname())) {
            return "ERROR:El campo segundo apellido es obligatorio";
        }

        if (!Validator.isValidIdentityCard(admin.getIdentityCard())) {
            return "ERROR:Cédula inválida";
        }

        // 3. VALIDACIONES CONTACTO
        if (Validator.isEmpty(contact.getType())) {
            return "ERROR:Tipo de contacto requerido";
        }

        if (Validator.isEmpty(contact.getContactValue())) {
            return "ERROR:Valor de contacto requerido";
        }
        if (!Validator.isValidContact(contact.getType(), contact.getContactValue())) {
            if ("PHONE".equals(contact.getType())) {
                return "ERROR:El teléfono debe contener exactamente 8 dígitos numéricos";
            }
            if ("EMAIL".equals(contact.getType())) {
                return "ERROR:El correo debe tener un formato válido (ejemplo: usuario@dominio.com)";
            }
            return "ERROR:Valor de contacto inválido";
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

        // 5. CREAR ADMIN
        admin.setIdUser(createdUser.getIdUser());

        boolean adminInserted = AdminDAO.insertAdmin(admin);

        if (!adminInserted) {
            return "ERROR:No se pudo crear el administrador";
        }

        // Obtener admin correctamente (por id_user)
        Admin createdAdmin = AdminDAO.getAdminByUserId(createdUser.getIdUser());

        if (createdAdmin == null) {
            return "ERROR:No se pudo recuperar el administrador creado";
        }

        System.out.println("DEBUG - createdAdmin ID: " + createdAdmin.getIdAdmin());

        // 6. CREAR CONTACTO
        int idContact = ContactDAO.insertContact(contact);

        if (idContact == -1) {
            return "ERROR:No se pudo crear el contacto";
        }

        System.out.println("DEBUG - idContact: " + idContact);

        // 7. VINCULAR CONTACTO (CXA)
        boolean linked = ContactDAO.insertCXA(createdAdmin.getIdAdmin(), idContact);

        if (!linked) {
            return "ERROR:No se pudo vincular el contacto al administrador";
        }

        return "SUCCESS:Administrador creado correctamente";
    }

    public static String updateAdmin(AdminRequest adminRequest) {

        User user = adminRequest.getUser();
        Admin admin = adminRequest.getAdmin();
        Contact contact = adminRequest.getContact();

        System.out.println("DEBUG - Iniciando updateAdmin");

        //  VALIDAR EXISTENCIA
        Admin existingAdmin = AdminDAO.getAdminById(admin.getIdAdmin());

        if (existingAdmin == null) {
            return "ERROR:Administrador no encontrado";
        }
        if (user != null) {

            if (!Validator.isValidUsername(user.getUsername())) {
                return "ERROR:Nombre de usuario inválido";
            }

            //  VALIDAR DUPLICADO (
            User existingUser = UserDAO.getUserByUsername(user.getUsername());

            if (existingUser != null && existingUser.getIdUser() != user.getIdUser()) {
                return "ERROR:El nombre de usuario ya está en uso";
            }

            boolean userUpdated = UserDAO.updateUser(user);

            if (!userUpdated) {
                return "ERROR:No se pudo actualizar el usuario";
            }
        }
        // 3. UPDATE ADMIN
        if (!Validator.isValidFName(admin.getfName())) {
            return "ERROR:El campo primer nombre es obligatorio";
        }

        if (!Validator.isValidFSurname(admin.getfSurname())) {
            return "ERROR:El campo primer apellido es obligatorio";
        }

        if (!Validator.isValidMSurname(admin.getmSurname())) {
            return "ERROR:El campo segundo apellido es obligatorio";
        }

        boolean adminUpdated = AdminDAO.updateAdmin(admin);

        if (!adminUpdated) {
            return "ERROR:No se pudo actualizar el administrador";
        }

        if (contact != null) {

            if (Validator.isEmpty(contact.getType())) {
                return "ERROR:Tipo de contacto requerido";
            }

            if (Validator.isEmpty(contact.getContactValue())) {
                return "ERROR:Valor de contacto requerido";
            }
            // 4. UPDATE CONTACT (correo / teléfono)
            if (!Validator.isValidContact(contact.getType(), contact.getContactValue())) {
                if ("PHONE".equals(contact.getType())) {
                    return "ERROR:El teléfono debe contener exactamente 8 dígitos numéricos";
                }
                if ("EMAIL".equals(contact.getType())) {
                    return "ERROR:El correo debe tener un formato válido (ejemplo: usuario@dominio.com)";
                }
                return "ERROR:Valor de contacto inválido";
            }

            boolean contactUpdated = ContactDAO.updateContact(contact);

            if (!contactUpdated) {
                return "ERROR:No se pudo actualizar el contacto";
            }
        }

        return "SUCCESS:Administrador actualizado correctamente";
    }
    // Obtener admin completo por id
     public static AdminRequest getAdmin(int idAdmin) {
        AdminRequest adminRequest = AdminDAO.getFullAdminById(idAdmin);
        if (adminRequest == null) {
            System.out.println("ERROR:Administrador no encontrado");
        }
        return adminRequest;
    }

    // Eliminar admin en cascada
    public static String deleteAdmin(int idAdmin, int idUser) {

        // 1. Eliminar CXA + Contacto + Admin
        boolean adminDeleted = AdminDAO.deleteAdminCascade(idAdmin);
        if (!adminDeleted) {
            return "ERROR:No se pudo eliminar el administrador";
        }

        // 2. Eliminar usuario
        boolean userDeleted = UserDAO.deleteUser(idUser);
        if (!userDeleted) {
            return "ERROR:No se pudo eliminar el usuario";
        }

        return "SUCCESS:Administrador eliminado correctamente";
    }
}
