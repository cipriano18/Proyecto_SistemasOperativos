/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Client;
import model.ClientRequest;
import model.Contact;
import model.User;

/**
 *
 * @author Cipriano
 */
public class ClientDAO {

    //Funcion para crear un cliente
    public static boolean createClient(Client client) {

        String sql = "INSERT INTO AUD_Clients "
                + "(id_user, f_name, m_name, f_surname, m_surname, identity_card) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, client.getIdUser());
            ps.setString(2, client.getfName());
            ps.setString(3, client.getmName());
            ps.setString(4, client.getfSurname());
            ps.setString(5, client.getmSurname());
            ps.setString(6, client.getIdentityCard());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al crear cliente: " + e.getMessage());
            return false;
        }
    }

    public static Client getClientByUserId(int idUser) {

        Client client = null;
        String sql = "SELECT id_client, id_user, f_name, m_name, f_surname, m_surname, identity_card "
                + "FROM AUD_Clients WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                client = new Client(
                        rs.getInt("id_client"),
                        rs.getInt("id_user"),
                        rs.getString("f_name"),
                        rs.getString("m_name"),
                        rs.getString("f_surname"),
                        rs.getString("m_surname"),
                        rs.getString("identity_card")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por id_user: " + e.getMessage());
        }
        return client;
    }

    public static boolean updateClient(Client client) {
        String sql = "UPDATE AUD_Clients SET "
                + "f_name = ?, m_name = ?, f_surname = ?, m_surname = ?, identity_card = ? "
                + "WHERE id_client = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getfName());
            ps.setString(2, client.getmName());
            ps.setString(3, client.getfSurname());
            ps.setString(4, client.getmSurname());
            ps.setString(5, client.getIdentityCard());
            ps.setInt(6, client.getIdClient());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // Eliminar cliente
    public static boolean deleteClientCascade(int idClient) {
        String sql = "DELETE FROM AUD_Clients WHERE id_client = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
    // Obtener cliente completo con todos sus datos por id_client

    public static ClientRequest getFullClientById(int idClient) {
        String sql = "SELECT c.id_client, c.id_user, c.f_name, c.m_name, "
                + "c.f_surname, c.m_surname, c.identity_card, "
                + "u.username, u.password, "
                + "co.id_contact, co.type AS contact_type, co.contact_value "
                + "FROM AUD_Clients c "
                + "INNER JOIN AUD_Users u ON c.id_user = u.id_user "
                + "LEFT JOIN AUD_CXC cxc ON c.id_client = cxc.id_client "
                + "LEFT JOIN AUD_Contacts co ON cxc.id_contact = co.id_contact "
                + "WHERE c.id_client = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id_user"),
                        3,
                        rs.getString("username"),
                        rs.getString("password")
                );

                Client client = new Client(
                        rs.getInt("id_client"),
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

                ClientRequest clientRequest = new ClientRequest();
                clientRequest.setUser(user);
                clientRequest.setClient(client);
                clientRequest.setContact(contact);

                return clientRequest;
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener cliente completo: " + e.getMessage());
        }

        return null;
    }
      public static Client getClientByIdentityCard(String identityCard) {

    String sql = "SELECT id_client, id_user, f_name, m_name, f_surname, m_surname, identity_card "
            + "FROM AUD_Clients WHERE identity_card = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, identityCard);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Client client = new Client();
            client.setIdClient(rs.getInt("id_client"));
            client.setIdUser(rs.getInt("id_user"));
            client.setfName(rs.getString("f_name"));
            client.setmName(rs.getString("m_name"));
            client.setfSurname(rs.getString("f_surname"));
            client.setmSurname(rs.getString("m_surname"));
            client.setIdentityCard(rs.getString("identity_card"));
            return client;
        }

    } catch (SQLException e) {
        System.out.println("Error al buscar cliente por cédula: " + e.getMessage());
    }

    return null;
}
}
