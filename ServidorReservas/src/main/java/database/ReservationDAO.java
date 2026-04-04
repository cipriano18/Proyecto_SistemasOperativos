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

    public static boolean createEquipmentReservation(Reservation reservation, List<RXE> equipmentList) {

        String insertReservationSql = "INSERT INTO AUD_Reservations "
                + "(id_client, id_section, reservation_date) "
                + "VALUES (?, ?, ?)";

        String insertRXESql = "INSERT INTO AUD_RXE "
                + "(id_reservation, id_equipment, quantity) "
                + "VALUES (?, ?, ?)";

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
            System.out.println("Error al adquirir semáforos de equipos: " + e.getMessage());
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psInsertReservation = conn.prepareStatement(insertReservationSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement psInsertRXE = conn.prepareStatement(insertRXESql)) {

            conn.setAutoCommit(false);

            // 1. Verificar disponibilidad de cada equipo
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

            // 2. Insertar cabecera en AUD_Reservations
            psInsertReservation.setInt(1, reservation.getIdClient());
            psInsertReservation.setInt(2, reservation.getIdSection());
            psInsertReservation.setDate(3, reservation.getReservationDate());

            psInsertReservation.executeUpdate();

            ResultSet generatedKeys = psInsertReservation.getGeneratedKeys();
            int idReservation = 0;

            if (generatedKeys.next()) {
                idReservation = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // 3. Insertar detalle de equipos en AUD_RXE
            for (RXE item : equipmentList) {
                psInsertRXE.setInt(1, idReservation);
                psInsertRXE.setInt(2, item.getIdEquipment());
                psInsertRXE.setInt(3, item.getQuantity());
                psInsertRXE.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al crear reservación de equipo: " + e.getMessage());
            return false;
        } finally {
            for (Semaphore semaphore : acquiredSemaphores) {
                semaphore.release();
            }
        }
    }
     private static int getAvailableEquipmentQuantity(Connection conn, int idEquipment, java.sql.Date reservationDate, int idSection) {

    String sql = "SELECT "
            + "e.available_quantity - COALESCE(SUM(rxe.quantity), 0) AS available_quantity "
            + "FROM AUD_Equipment e "
            + "LEFT JOIN AUD_RXE rxe "
            + "ON e.id_equipment = rxe.id_equipment "
            + "LEFT JOIN AUD_Reservations r "
            + "ON rxe.id_reservation = r.id_reservation "
            + "AND r.reservation_date = ? "
            + "AND r.id_section = ? "
            + "WHERE e.id_equipment = ? "
            + "GROUP BY e.id_equipment, e.available_quantity";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDate(1, reservationDate);
        ps.setInt(2, idSection);
        ps.setInt(3, idEquipment);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("available_quantity");
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener disponibilidad del equipo: " + e.getMessage());
    }

    return 0;
}
}
