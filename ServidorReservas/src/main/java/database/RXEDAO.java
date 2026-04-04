/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import model.RXE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cipriano
 */
public class RXEDAO {
 // Obtener todos los RXE
    public static List<RXE> getAllRXE() {
        List<RXE> list = new ArrayList<>();
        String sql = "SELECT id_rxe, id_reservation, id_equipment, quantity FROM AUD_RXE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new RXE(
                        rs.getInt("id_rxe"),
                        rs.getInt("id_reservation"),
                        rs.getInt("id_equipment"),
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener RXE: " + e.getMessage());
        }

        return list;
    }

    // Obtener RXE por reserva
    public static List<RXE> getRXEByReservation(int idReservation) {
        List<RXE> list = new ArrayList<>();
        String sql = "SELECT id_rxe, id_reservation, id_equipment, quantity FROM AUD_RXE WHERE id_reservation = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReservation);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new RXE(
                        rs.getInt("id_rxe"),
                        rs.getInt("id_reservation"),
                        rs.getInt("id_equipment"),
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener RXE por reserva: " + e.getMessage());
        }

        return list;
    }

    // Obtener RXE por id
    public static RXE getRXEById(int idRxe) {
        String sql = "SELECT id_rxe, id_reservation, id_equipment, quantity FROM AUD_RXE WHERE id_rxe = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRxe);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new RXE(
                        rs.getInt("id_rxe"),
                        rs.getInt("id_reservation"),
                        rs.getInt("id_equipment"),
                        rs.getInt("quantity")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener RXE por id: " + e.getMessage());
        }

        return null;
    }

    // Crear RXE
    public static boolean createRXE(RXE rxe) {
        String sql = "INSERT INTO AUD_RXE (id_reservation, id_equipment, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rxe.getIdReservation());
            ps.setInt(2, rxe.getIdEquipment());
            ps.setInt(3, rxe.getQuantity());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error al crear RXE: " + e.getMessage());
            return false;
        }
    }
}