/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import model.RXE;
import model.Reservation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Cipriano
 */
public class ReservationDAO {
       private static final Map<Integer, Semaphore> equipmentSemaphores = new ConcurrentHashMap<>();

    private static Semaphore getEquipmentSemaphore(int idEquipment) {
        return equipmentSemaphores.computeIfAbsent(idEquipment, key -> new Semaphore(1, true));
    }

    public static boolean createEquipmentReservation(Reservation reservation, int idClient, List<RXE> equipmentList) {

        String insertReservationSql = "INSERT INTO AUD_Reservations (id_section, reservation_date) VALUES (?, ?)";

        String insertRXCSql = "INSERT INTO AUD_RXC (id_reservation, id_client) VALUES (?, ?)";

        String insertRXESql = "INSERT INTO AUD_RXE (id_reservation, id_equipment, quantity) VALUES (?, ?, ?)";

        List<Semaphore> acquiredSemaphores = new ArrayList<>();

        try {
            equipmentList.sort(Comparator.comparingInt(RXE::getIdEquipment));

            for (RXE item : equipmentList) {
                Semaphore semaphore = getEquipmentSemaphore(item.getIdEquipment());
                semaphore.acquire();
                acquiredSemaphores.add(semaphore);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psInsertReservation = conn.prepareStatement(insertReservationSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement psInsertRXC = conn.prepareStatement(insertRXCSql);
             PreparedStatement psInsertRXE = conn.prepareStatement(insertRXESql)) {

            conn.setAutoCommit(false);

            // 1. Verificar disponibilidad
            for (RXE item : equipmentList) {
                int availableQuantity = getAvailableEquipmentQuantity(
                        conn,
                        item.getIdEquipment(),
                        reservation.getReservationDate(),
                        reservation.getIdSection()
                );

                if (availableQuantity < item.getQuantity()) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. Insertar reserva
            psInsertReservation.setInt(1, reservation.getIdSection());
            psInsertReservation.setDate(2, reservation.getReservationDate());

            psInsertReservation.executeUpdate();

            ResultSet generatedKeys = psInsertReservation.getGeneratedKeys();
            int idReservation;

            if (generatedKeys.next()) {
                idReservation = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 3. Insertar relación cliente-reserva (RXC)
            psInsertRXC.setInt(1, idReservation);
            psInsertRXC.setInt(2, idClient);
            psInsertRXC.executeUpdate();

            // 4. Insertar equipos
            for (RXE item : equipmentList) {
                psInsertRXE.setInt(1, idReservation);
                psInsertRXE.setInt(2, item.getIdEquipment());
                psInsertRXE.setInt(3, item.getQuantity());
                psInsertRXE.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al crear reservación: " + e.getMessage());
            return false;
        } finally {
            for (Semaphore semaphore : acquiredSemaphores) {
                semaphore.release();
            }
        }
    }
    private static int getAvailableEquipmentQuantity(Connection conn, int idEquipment, java.sql.Date reservationDate, int idSection) {

        String sql = "SELECT e.available_quantity - COALESCE(("
                + "    SELECT SUM(rxe.quantity) "
                + "    FROM AUD_RXE rxe "
                + "    INNER JOIN AUD_Reservations r "
                + "        ON rxe.id_reservation = r.id_reservation "
                + "    WHERE rxe.id_equipment = ? "
                + "      AND r.reservation_date = ? "
                + "      AND r.id_section = ?"
                + "), 0) AS available_quantity "
                + "FROM AUD_Equipment e "
                + "WHERE e.id_equipment = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipment);
            ps.setDate(2, reservationDate);
            ps.setInt(3, idSection);
            ps.setInt(4, idEquipment);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("available_quantity");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener disponibilidad del equipo: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
    public static boolean deleteReservationsByClientId(int idClient) {

        String deleteReservationsSql =
            "DELETE FROM AUD_Reservations " +
            "WHERE id_reservation IN (" +
            "   SELECT id_reservation FROM AUD_RXC WHERE id_client = ?" +
            ")";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(deleteReservationsSql)) {
                ps.setInt(1, idClient);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar reservas: " + e.getMessage());
            return false;
        }
    }
}
