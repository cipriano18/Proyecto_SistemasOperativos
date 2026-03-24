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

/**
 *
 * @author Cipriano
 */
public class ClientDAO {
    public static boolean createClient(Client client) {

        String sql = "INSERT INTO AUD_Clients " +
                     "(id_user, id_type, f_name, m_name, f_surname, m_surname, identity_card) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, client.getIdUser());
            ps.setInt(2, client.getIdType());
            ps.setString(3, client.getfName());
            ps.setString(4, client.getmName());
            ps.setString(5, client.getfSurname());
            ps.setString(6, client.getmSurname());
            ps.setString(7, client.getIdentityCard());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al crear cliente: " + e.getMessage());
            return false;
        }
    }

    public static Client getClientByUserId(int idUser) {

        Client client = null;

        String sql = "SELECT id_client, id_user, id_type, f_name, m_name, f_surname, m_surname, identity_card "
                + "FROM AUD_Clients WHERE id_user = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                client = new Client(
                        rs.getInt("id_client"),
                        rs.getInt("id_user"),
                        rs.getInt("id_type"),
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
                + "id_type = ?, f_name = ?, m_name = ?, "
                + "f_surname = ?, m_surname = ?, identity_card = ? "
                + "WHERE id_client = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, client.getIdType());
            ps.setString(2, client.getfName());
            ps.setString(3, client.getmName());
            ps.setString(4, client.getfSurname());
            ps.setString(5, client.getmSurname());
            ps.setString(6, client.getIdentityCard());
            ps.setInt(7, client.getIdClient());
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
}
