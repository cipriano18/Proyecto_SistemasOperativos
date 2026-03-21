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
    public static boolean linkContactToClient(int idClient, Contact contact) {
        String sql = "INSERT INTO AUD_CXC (id_client, id_contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            ps.setInt(2, contact.getIdContact());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("ERROR al vincular contacto con cliente: " + e.getMessage());
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
}
