/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Role;

/**
 *
 * @author User
 */
public class RoleDAO {
    // Obtiene todos los roles
    public static List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT id_role, name FROM AUD_Roles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new Role(
                    rs.getInt("id_role"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            System.out.println(" Error al obtener roles: " + e.getMessage());
        }
        return roles;
    }

    // Obtiene un rol por su ID
    public static Role getRoleById(int idRole) {
        String sql = "SELECT id_role, name FROM AUD_Roles WHERE id_role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRole);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Role(
                    rs.getInt("id_role"),
                    rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.out.println(" Error al obtener rol: " + e.getMessage());
        }
        return null;
    }
}
