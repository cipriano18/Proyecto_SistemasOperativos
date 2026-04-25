/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Equipment;

/**
 *
 * @author Cipriano
 */
public class EquipmentDAO {

    // Obtener todos los equipos
    public static List<Equipment> getAllEquipment() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT id_equipment, name, available_quantity FROM AUD_Equipment ORDER BY id_equipment ASC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Equipment(
                        rs.getInt("id_equipment"),
                        rs.getString("name"),
                        rs.getInt("available_quantity")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener equipos: " + e.getMessage());
        }
        return list;
    }

    // Obtener equipo por nombre
    public static Equipment getEquipmentByName(String name) {
        String sql = "SELECT id_equipment, name, available_quantity FROM AUD_Equipment WHERE name = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Equipment(
                        rs.getInt("id_equipment"),
                        rs.getString("name"),
                        rs.getInt("available_quantity")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener equipo por nombre: " + e.getMessage());
        }
        return null;
    }

    public static Equipment getEquipmentById(int idEquipment) {

        String sql = "SELECT id_equipment, name, available_quantity FROM AUD_Equipment WHERE id_equipment = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipment);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Equipment equipment = new Equipment();

                    equipment.setIdEquipment(rs.getInt("id_equipment"));
                    equipment.setName(rs.getString("name"));
                    equipment.setTotalQuantity(rs.getInt("available_quantity"));

                    return equipment;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener equipo por id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
    // Obtener todos los equipos disponibles por fecha y sección
    // Retorna el total DISPONIBLE en totalQuantity (no el total original)
    public static List<Equipment> getAvailableEquipmentByDateAndSection(Date reservationDate, int idSection) {

        List<Equipment> equipmentList = new ArrayList<>();

        String sql =
                "SELECT e.id_equipment, e.name, " +
                "       (e.available_quantity - COALESCE(SUM(rxe.quantity), 0)) AS total_available " +
                "FROM AUD_Equipment e " +
                "LEFT JOIN AUD_RXE rxe " +
                "    ON e.id_equipment = rxe.id_equipment " +
                "LEFT JOIN AUD_Reservations r " +
                "    ON rxe.id_reservation = r.id_reservation " +
                "   AND r.reservation_date = ? " +
                "   AND r.id_section = ? " +
                "GROUP BY e.id_equipment, e.name, e.available_quantity " +
                "HAVING total_available > 0 " +
                "ORDER BY e.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Equipment equipment = new Equipment();
                equipment.setIdEquipment(rs.getInt("id_equipment"));
                equipment.setName(rs.getString("name"));

                // 🔥 IMPORTANTE: aquí guardamos el DISPONIBLE en totalQuantity
                equipment.setTotalQuantity(rs.getInt("total_available"));

                equipmentList.add(equipment);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener equipos disponibles: " + e.getMessage());
        }

        return equipmentList;
    }

    // Crear equipo
    public static boolean createEquipment(Equipment equipment) {
        String sql = "INSERT INTO AUD_Equipment (name, available_quantity) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipment.getName());
            ps.setInt(2, equipment.getTotalQuantity());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear equipo: " + e.getMessage());
            return false;
        }
    }

    // Actualizar equipo
    public static boolean updateEquipment(Equipment equipment) {
        String sql = "UPDATE AUD_Equipment SET name = ?, available_quantity = ? WHERE id_equipment = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, equipment.getName());
            ps.setInt(2, equipment.getTotalQuantity());
            ps.setInt(3, equipment.getIdEquipment());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar equipo: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteEquipment(int idEquipment) {

        String sql = "DELETE FROM AUD_Equipment WHERE id_equipment = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEquipment);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
