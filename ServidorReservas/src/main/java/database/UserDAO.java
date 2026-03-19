/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class UserDAO {

    // Obtiene un usuario por su ID
    public static User getUserById(int idUser) {
        String sql = "SELECT id_user, id_role, username, password, status FROM AUD_Users WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id_user"),
                        rs.getInt("id_role"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println(" Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }
// Inserta un nuevo usuario, retorna true si se insertó correctamente

    public static boolean insertUser(User user) {
        String sql = "INSERT INTO AUD_Users (id_role, username, password, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getIdRole());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getStatus());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(" Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // Obtiene un usuario por su nombre de usuario
    public static User getUserByUsername(String username) {
        String sql = "SELECT id_user, id_role, username, password, status FROM AUD_Users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id_user"),
                        rs.getInt("id_role"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuario por username: " + e.getMessage());
        }
        return null;
    }

    // Actualiza username, password y status de un usuario existente
    public static boolean updateUser(User user) {
        String sql = "UPDATE AUD_Users SET username = ?, password = ?, status = ? WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getStatus());
            ps.setInt(4, user.getIdUser());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(" Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
}
