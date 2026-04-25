package database;

import model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.AdminRequest;
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
    // Obtener administrador por Cedula
    public static Admin getAdminByIdentityCard(String identityCard) {
        String sql = "SELECT id_admin, id_user, f_name, m_name, f_surname, m_surname, identity_card "
                + "FROM AUD_Administrators WHERE identity_card = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, identityCard);
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
            System.out.println("Error al buscar administrador por cédula: " + e.getMessage());
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
        String getAdminSql = "SELECT id_user FROM AUD_Administrators WHERE id_admin = ?";
        String getContactsSql = "SELECT id_contact FROM AUD_CXA WHERE id_admin = ?";
        String deleteContactSql = "DELETE FROM AUD_Contacts WHERE id_contact = ?";
        String deleteUserSql = "DELETE FROM AUD_Users WHERE id_user = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int idUser = -1;
            List<Integer> contactIds = new ArrayList<>();

            // 1. Obtener id_user
            try (PreparedStatement ps = conn.prepareStatement(getAdminSql)) {
                ps.setInt(1, idAdmin);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idUser = rs.getInt("id_user");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Obtener todos los contactos del admin
            try (PreparedStatement ps = conn.prepareStatement(getContactsSql)) {
                ps.setInt(1, idAdmin);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        contactIds.add(rs.getInt("id_contact"));
                    }
                }
            }

            // 3. Eliminar contactos
            // Por ON DELETE CASCADE en AUD_CXA(id_contact), se eliminan también las relaciones
            if (!contactIds.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(deleteContactSql)) {
                    for (Integer idContact : contactIds) {
                        ps.setInt(1, idContact);
                        ps.executeUpdate();
                    }
                }
            }

            // 4. Eliminar usuario
            // Por ON DELETE CASCADE en AUD_Administrators(id_user), se elimina también el admin
            try (PreparedStatement ps = conn.prepareStatement(deleteUserSql)) {
                ps.setInt(1, idUser);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar admin en cascada: " + e.getMessage());

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al hacer rollback: " + ex.getMessage());
            }

            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
