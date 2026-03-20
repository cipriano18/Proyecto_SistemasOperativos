package database;

import model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.Validator;

public class AdminDAO {

    // Obtener lista de administradores
    public static List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT id_admin, id_user, f_name, m_name, f_surname, m_surname, identity_card FROM AUD_Administrators ORDER BY id_admin ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                admins.add(new Admin(
                        rs.getInt("id_admin"),
                        rs.getInt("id_user"),
                        rs.getString("f_name"),
                        rs.getString("m_name"),
                        rs.getString("f_surname"),
                        rs.getString("m_surname"),
                        rs.getString("identity_card")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener lista de administradores: " + e.getMessage());
        }
        return admins;
    }

    // Obtener administrador por id
    public static Admin getAdminById(int idAdmin) {
        String sql = "SELECT id_admin, id_user, f_name, m_name, f_surname, m_surname, identity_card FROM AUD_Administrators WHERE id_admin = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("id_admin"),
                        rs.getInt("id_user"),
                        rs.getString("f_name"),
                        rs.getString("m_name"),
                        rs.getString("f_surname"),
                        rs.getString("m_surname"),
                        rs.getString("identity_card")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener administrador por ID: " + e.getMessage());
        }
        return null;
    }

    public static boolean insertAdmin(Admin admin) {
        Connection conn = null;
        PreparedStatement psAdmin = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insertar administrador
            String sqlAdmin = "INSERT INTO AUD_Administrators (id_user, f_name, m_name, f_surname, m_surname, identity_card) VALUES (?, ?, ?, ?, ?, ?)";
            psAdmin = conn.prepareStatement(sqlAdmin, PreparedStatement.RETURN_GENERATED_KEYS);
            psAdmin.setInt(1, admin.getIdUser());   // ya debe venir del User creado
            psAdmin.setString(2, admin.getFName());
            psAdmin.setString(3, admin.getMName());
            psAdmin.setString(4, admin.getFSurname());
            psAdmin.setString(5, admin.getMSurname());
            psAdmin.setString(6, admin.getIdentityCard());
            psAdmin.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting administrator: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { }
            return false;
        } finally {
            try {
                if (psAdmin != null) psAdmin.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) { }
        }
    }

    // Actualizar un administrador
    public static boolean updateAdmin(Admin admin) {
        // Validaciones previas
        if (Validator.isEmpty(admin.getFName()) || Validator.isEmpty(admin.getFSurname())) {
            System.out.println("Error: Nombre y apellido son obligatorios.");
            return false;
        }

        String sql = "UPDATE AUD_Administrators SET f_name = ?, m_name = ?, f_surname = ?, m_surname = ? WHERE id_admin = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getFName());
            ps.setString(2, admin.getMName());
            ps.setString(3, admin.getFSurname());
            ps.setString(4, admin.getMSurname());
            ps.setInt(5, admin.getIdAdmin());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar administrador: " + e.getMessage());
            return false;
        }
    }

    // "Eliminar" administrador (soft delete/inactivar usuario)
    public static boolean softDeleteAdmin(int idAdmin) {
        String sql = "UPDATE AUD_Users SET status = 'I' WHERE id_user = (SELECT id_user FROM AUD_Administrators WHERE id_admin = ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al inactivar administrador: " + e.getMessage());
            return false;
        }
    }
}
