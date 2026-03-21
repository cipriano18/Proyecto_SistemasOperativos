/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.AdminDAO;
import database.ContactDAO;
import model.User;
import database.UserDAO;
import model.Admin;
import model.AdminRequest;
import model.Contact;
import model.Status;
import utils.Validator;

/**
 *
 * @author User
 */
public class UserController {
   
 // Crea un nuevo usuario 
    public static String createUser(User user) {
        if (!Validator.isValidUsername(user.getUsername())) {
            return "ERROR:Nombre de usuario inválido";
        }
        if (!Validator.isValidPassword(user.getPassword())) {
            return "ERROR:Contraseña inválida (mínimo 8 caracteres, 1 mayúscula, 1 número)";
        }

        User existing = UserDAO.getUserByUsername(user.getUsername());
        if (existing != null) {
            return "ERROR:El nombre de usuario ya está en uso";
        }

        user.setStatus(Status.ACTIVE.getCode());
        boolean insertado = UserDAO.insertUser(user);
        if (insertado) {
            return "SUCCESS:Usuario creado correctamente";
        }
        return "ERROR:No se pudo crear el usuario";
    }

    // Obtiene un usuario por su ID
    public static User getUser(int idUser) {
        User user = UserDAO.getUserById(idUser);
        if (user == null) {
            System.out.println("ERROR:Usuario no encontrado");
        }
        return user;
    }

    // Actualiza username, password y status de un usuario
    public static String updateUser(User user) {
        if (!Validator.isValidUsername(user.getUsername())) {
            return "ERROR:Nombre de usuario inválido";
        }
        if (!Validator.isValidPassword(user.getPassword())) {
            return "ERROR:Contraseña inválida (mínimo 8 caracteres, 1 mayúscula, 1 número)";
        }

        boolean actualizado = UserDAO.updateUser(user);
        if (actualizado) {
            return "SUCCESS:Usuario actualizado correctamente";
        }
        return "ERROR:No se pudo actualizar el usuario";
    }

    public static Object login(User user) {
        if ( !Validator.isValidUsername(user.getUsername())
            || !Validator.isValidPassword(user.getPassword())) {
        return "ERROR: Credenciales incorrectas";
    }

        User loggedUser = UserDAO.validateLogin(user.getUsername(), user.getPassword());
        System.out.println("Usuario validado: " + loggedUser);
        if (loggedUser == null) {
            return "ERROR: Credenciales incorrectas `pipiu";
        }

        // Si es admin
        if (loggedUser.getIdRole() == 2) {
            Admin adminDB = AdminDAO.getAdminByUserId(loggedUser.getIdUser());
            if (adminDB == null) {
                return "ERROR: No se encontró el administrador";
            }

            Contact contactDB = ContactDAO.getContactByAdminId(adminDB.getIdAdmin());

            AdminRequest response = new AdminRequest();
            response.setUser(loggedUser);
            response.setAdmin(adminDB);
            response.setContact(contactDB); 
            return response;
        }

        return loggedUser;
    }
}
