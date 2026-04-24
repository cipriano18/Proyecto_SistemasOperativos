/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.UserController;
import model.Response;
import model.User;

/**
 *
 * @author Cipriano
 */
public class UserRequestHandler {
     public static Response handle(String command, Object obj) {
        User user = (User) obj;

        switch (command.toUpperCase()) {
            case "LOGIN":
                return UserController.login(user);

            default:
                return new Response(false, "Comando de usuario no reconocido", null);
        }
    }
}
