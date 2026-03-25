package database;

import model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.AdminRequest;
import model.Contact;
import model.User;
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

    // Obtener administrador por id de usuario
    public static Admin getAdminByUserId(int idUser) {
        String sql = "SELECT id_admin, id_user, f_name, m_name, f_surname, m_surname, identity_card "
                + "FROM AUD_Administrators WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
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
            System.out.println("Error al obtener administrador por id_user: " + e.getMessage());
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
            psAdmin.setInt(1, admin.getIdUser());   
            psAdmin.setString(2, admin.getfName()); 
            psAdmin.setString(3, admin.getmName()); 
            psAdmin.setString(4, admin.getfSurname()); 
            psAdmin.setString(5, admin.getmSurname()); 
            psAdmin.setString(6, admin.getIdentityCard()); 

            psAdmin.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting administrator: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
            }
            return false;
        } finally {
            try {
                if (psAdmin != null) {
                    psAdmin.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    // Actualizar un administrador
    public static boolean updateAdmin(Admin admin) {
        // Validaciones previas
        if (Validator.isEmpty(admin.getfName()) || Validator.isEmpty(admin.getfSurname())) {
            System.out.println("Error: Nombre y apellido son obligatorios.");
            return false;
        }

        String sql = "UPDATE AUD_Administrators SET f_name = ?, m_name = ?, f_surname = ?, m_surname = ? WHERE id_admin = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getfName());
            ps.setString(2, admin.getmName());
            ps.setString(3, admin.getfSurname());
            ps.setString(4, admin.getmSurname());
            ps.setInt(5, admin.getIdAdmin());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar administrador: " + e.getMessage());
            return false;
        }
    }
    // Obtener admin completo con todos sus datos
     public static AdminRequest getFullAdminById(int idAdmin) {
        String sql = "SELECT a.id_admin, a.id_user, a.f_name, a.m_name, "
                + "a.f_surname, a.m_surname, a.identity_card, "
                + "u.username, u.password, "
                + "co.id_contact, co.type AS contact_type, co.contact_value "
                + "FROM AUD_Administrators a "
                + "INNER JOIN AUD_Users u ON a.id_user = u.id_user "
                + "LEFT JOIN AUD_CXA cxa ON a.id_admin = cxa.id_admin "
                + "LEFT JOIN AUD_Contacts co ON cxa.id_contact = co.id_contact "
                + "WHERE a.id_admin = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAdmin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                User user = new User(
                        rs.getInt("id_user"),
                        2, 
                        rs.getString("username"),
                        rs.getString("password")
                );

                Admin admin = new Admin(
                        rs.getInt("id_admin"),
                        rs.getInt("id_user"),
                        rs.getString("f_name"),
                        rs.getString("m_name"),
                        rs.getString("f_surname"),
                        rs.getString("m_surname"),
                        rs.getString("identity_card")
                );

                Contact contact = new Contact();
                contact.setIdContact(rs.getInt("id_contact"));
                contact.setType(rs.getString("contact_type"));
                contact.setContactValue(rs.getString("contact_value"));

                AdminRequest adminRequest = new AdminRequest();
                adminRequest.setUser(user);
                adminRequest.setAdmin(admin);
                adminRequest.setContact(contact);
                return adminRequest;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener admin completo: " + e.getMessage());
        }
        return null;
    }

// Eliminar admin en cascada (CXA + Contacts + Admin + User)
    public static boolean deleteAdminCascade(int idAdmin) {
        try (Connection conn = DBConnection.getConnection()) {

            // 1. Obtener id_contact antes de eliminar
            int idContact = -1;
            String getContact = "SELECT id_contact FROM AUD_CXA WHERE id_admin = ?";
            try (PreparedStatement ps = conn.prepareStatement(getContact)) {
                ps.setInt(1, idAdmin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idContact = rs.getInt("id_contact");
                }
            }

            // 2. Eliminar CXA 
            String deleteCXA = "DELETE FROM AUD_CXA WHERE id_admin = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteCXA)) {
                ps.setInt(1, idAdmin);
                ps.executeUpdate();
            }

            // 3. Eliminar contacto
            if (idContact != -1) {
                String deleteContact = "DELETE FROM AUD_Contacts WHERE id_contact = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteContact)) {
                    ps.setInt(1, idContact);
                    ps.executeUpdate();
                }
            }

            // 4. Eliminar admin
            String deleteAdmin = "DELETE FROM AUD_Administrators WHERE id_admin = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteAdmin)) {
                ps.setInt(1, idAdmin);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar admin en cascada: " + e.getMessage());
            return false;
        }
    }
}
