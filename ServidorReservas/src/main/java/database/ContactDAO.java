package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Contact;

public class ContactDAO {

    // Inserta un contacto y retorna el ID generado, -1 si falla
    public static int insertContact(Contact contact) {
        String sql = "INSERT INTO AUD_Contacts (type, contact_value) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, contact.getType());
            ps.setString(2, contact.getContactValue());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(" Error al insertar contacto: " + e.getMessage());
        }
        return -1;
    }

    // Vincular contacto con administrador
    public static boolean insertCXA(int idAdmin, int idContact) {
        String sql = "INSERT INTO AUD_CXA (id_admin, id_contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);
            ps.setInt(2, idContact);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar CXA: " + e.getMessage());
            return false;
        }
    }

    // Vincular contacto con cliente
public static boolean insertCXC(int idClient, int idContact) {

    String sql = "INSERT INTO AUD_CXC (id_client, id_contact) VALUES (?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idClient);
        ps.setInt(2, idContact);

        int rows = ps.executeUpdate();
        return rows > 0;

    } catch (SQLException e) {
        System.out.println("Error al insertar CXC: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
  
    // actualizar un contacto
    public static boolean updateContact(Contact contact) {

        if (contact.getIdContact() <= 0) {
            System.out.println("Error: ID inválido");
            return false;
        }

        if (contact.getType() == null || contact.getContactValue() == null) {
            System.out.println("Error: Datos incompletos");
            return false;
        }

        String sql = "UPDATE AUD_Contacts SET type = ?, contact_value = ? WHERE id_contact = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contact.getType());
            ps.setString(2, contact.getContactValue());
            ps.setInt(3, contact.getIdContact());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar contacto: " + e.getMessage());
            return false;
        }
    }
    public static Contact getContactByAdminId(int idAdmin) {

    String sql = "SELECT c.* FROM AUD_Contacts c " +
                 "INNER JOIN AUD_CXA x ON c.id_contact = x.id_contact " +
                 "WHERE x.id_admin = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idAdmin);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Contact contact = new Contact();
            contact.setIdContact(rs.getInt("id_contact"));
            contact.setType(rs.getString("type"));
            contact.setContactValue(rs.getString("contact_value"));
            return contact;
        }

    } catch (SQLException e) {
        System.out.println("Error obteniendo contacto: " + e.getMessage());
    }

    return null;
}
    // Eliminar contacto de cliente en cascada (CXC + AUD_Contacts)
    public static boolean deleteContactByClientId(int idClient) {
        try (Connection conn = DBConnection.getConnection()) {

            // 1. Obtener el id_contact antes de eliminar
            int idContact = -1;
            String getContact = "SELECT id_contact FROM AUD_CXC WHERE id_client = ?";
            try (PreparedStatement ps = conn.prepareStatement(getContact)) {
                ps.setInt(1, idClient);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idContact = rs.getInt("id_contact");
                }
            }

            if (idContact == -1) {
                System.out.println("No se encontró contacto para el cliente: " + idClient);
                return false;
            }

            // 2. Eliminar de AUD_CXC primero 
            String deleteCXC = "DELETE FROM AUD_CXC WHERE id_client = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteCXC)) {
                ps.setInt(1, idClient);
                ps.executeUpdate();
            }

            // 3. Eliminar de AUD_Contacts 
            String deleteContact = "DELETE FROM AUD_Contacts WHERE id_contact = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteContact)) {
                ps.setInt(1, idContact);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar contacto de cliente: " + e.getMessage());
            return false;
        }
    }
    public static Contact getContactByClientId(int idClient) {

    String sql = "SELECT c.* FROM AUD_Contacts c " +
                 "INNER JOIN AUD_CXC x ON c.id_contact = x.id_contact " +
                 "WHERE x.id_client = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idClient);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Contact contact = new Contact();
            contact.setIdContact(rs.getInt("id_contact"));
            contact.setType(rs.getString("type"));
            contact.setContactValue(rs.getString("contact_value"));
            return contact;
        }

    } catch (SQLException e) {
        System.out.println("Error obteniendo contacto del cliente: " + e.getMessage());
    }

    return null;
}
}
