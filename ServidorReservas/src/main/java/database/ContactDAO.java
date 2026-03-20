package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Contact;
import utils.Validator;

public class ContactDAO {

    // Insertar un contacto y devolver el objeto completo con id asignado
    public static boolean insertContact(Contact contact) {
        String sql = "INSERT INTO AUD_Contacts (type, contact_value) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, contact.getType());
            ps.setString(2, contact.getContactValue());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    contact.setIdContact(rs.getInt(1)); // asignar id_contact generado
                }
                return true; // éxito
            }

        } catch (SQLException e) {
            System.out.println("ERROR al insertar contacto: " + e.getMessage());
        }
        return false; // fallo
    }

    // Vincular contacto con administrador
    public static boolean linkContactToAdmin(int idAdmin, Contact contact) {
        String sql = "INSERT INTO AUD_CXA (id_admin, id_contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);
            ps.setInt(2, contact.getIdContact());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("ERROR al vincular contacto con administrador: " + e.getMessage());
            return false;
        }
    }

    // Vincular contacto con cliente
    public static boolean linkContactToClient(int idClient, Contact contact) {
        String sql = "INSERT INTO AUD_CXC (id_client, id_contact) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            ps.setInt(2, contact.getIdContact());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("ERROR al vincular contacto con cliente: " + e.getMessage());
            return false;
        }
    }
}
