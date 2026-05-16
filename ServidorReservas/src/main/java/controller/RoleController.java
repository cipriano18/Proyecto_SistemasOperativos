/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.RoleDAO;
import java.util.List;
import model.Role;

/**
 * Controlador encargado de gestionar los roles del sistema.
 *
 * <p>
 * Permite obtener los roles disponibles y consultar un rol específico por su
 * identificador.
 * </p>
 *
 * @author Cipriano
 */
public class RoleController {

    /**
     * Obtiene todos los roles disponibles.
     *
     * @return lista de roles
     */
    public static List<Role> getAllRoles() {

        return RoleDAO.getAllRoles();
    }

    /**
     * Obtiene un rol por su identificador.
     *
     * @param idRole identificador del rol
     * @return rol encontrado
     */
    public static Role getRole(int idRole) {

        Role role = RoleDAO.getRoleById(idRole);

        if (role == null) {

            System.out.println(
                    "ERROR: Rol no encontrado"
            );
        }

        return role;
    }
}
