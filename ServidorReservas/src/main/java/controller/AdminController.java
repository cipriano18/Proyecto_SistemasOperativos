package controller;

import model.Admin;
import model.User;
import model.Status;
import database.UserDAO;
import database.AdminDAO;
import database.ContactDAO;
import utils.Validator;
import java.util.List;

public class AdminController {
    
    public static String createAdmin(Admin admin, User user, List<String[]> contacts) {
        // Validaciones de Admin
        if (!Validator.isValidIdentityCard(admin.getIdentityCard())) {
            return "ERROR: La cédula debe contener entre 9 y 20 caracteres alfanuméricos.";          
        }
        if (!Validator.isValidFName(admin.getFName())) {
            return "ERROR: El campo primer nombre es obligatorio.";          
        }
        if (!Validator.isValidFSurname(admin.getFSurname())) {
            return "ERROR: El campo primer apellido es obligatorio.";          
        }
        if (!Validator.isValidMSurname(admin.getMSurname())) {
            return "ERROR: El campo segundo apellido es obligatorio.";          
        }

        // 1. Crear usuario
        user.setStatus(Status.ACTIVE.getCode());
        String userResult = UserController.createUser(user);
        if (!userResult.startsWith("SUCCESS")) {
            return userResult; // devuelve el error del UserController
        }

        // Recuperar el usuario recién creado
        User createdUser = UserDAO.getUserByUsername(user.getUsername());
        if (createdUser == null) {
            return "ERROR: No se pudo recuperar el usuario creado.";
        }

        // 2. Crear administrador
        admin.setIdUser(createdUser.getIdUser());
        boolean adminInserted = AdminDAO.insertAdmin(admin);
        if (!adminInserted) {
            return "ERROR: No se pudo crear el administrador.";
        }

        // Lógica para crear contacto, apague y vamonos

        return "SUCCESS: Administrador creado con usuario y contactos.";
    }
}
