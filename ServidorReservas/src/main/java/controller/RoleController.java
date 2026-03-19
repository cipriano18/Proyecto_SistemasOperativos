/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.RoleDAO;
import java.util.List;
import model.Role;

/**
 *
 * @author User
 */
public class RoleController {

    // Obtiene todos los roles disponibles
    public static List<Role> getAllRoles() {
        return RoleDAO.getAllRoles();
    }

    // Obtiene un rol por su ID
    public static Role getRole(int idRole) {
        Role role = RoleDAO.getRoleById(idRole);
        if (role == null) {
            System.out.println("ERROR:Rol no encontrado");
        }
        return role;
    }
}
