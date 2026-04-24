/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
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
