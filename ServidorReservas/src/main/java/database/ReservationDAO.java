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
import model.EquipmentReservationRequest;
import model.CalendarBlock;
/**
 *
 * @author Cipriano
 */
public class ReservationDAO {
    // Mapa de semáforos por equipo para evitar concurrencia (dos usuarios reservando lo mismo al mismo tiempo)
    private static final Map<Integer, Semaphore> equipmentSemaphores = new ConcurrentHashMap<>();
    // Obtener o crear semáforo para un equipo específico
    private static Semaphore getEquipmentSemaphore(int idEquipment) {
        return equipmentSemaphores.computeIfAbsent(idEquipment, key -> new Semaphore(1, true));
    }
    // Obtener los bloques reservados por mes y año para mostrarlos en el calendario
    public static List<CalendarBlock> getReservedBlocksByMonth(int month, int year) {

        List<CalendarBlock> blocks = new ArrayList<>();

        String sql = "SELECT reservation_date, id_section "
                + "FROM AUD_Reservations "
                + "WHERE MONTH(reservation_date) = ? AND YEAR(reservation_date) = ? "
                + "ORDER BY reservation_date, id_section";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CalendarBlock block = new CalendarBlock();
                block.setReservationDate(rs.getDate("reservation_date"));
                block.setIdSection(rs.getInt("id_section"));
                block.setStatus("RESERVED");
                blocks.add(block);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener reservaciones del calendario: " + e.getMessage());
        }

        return blocks;
    }
    // Crear una nueva reservación de equipos validando disponibilidad y evitando concurrencia
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
    // Obtener una reservación completa (cabecera + cliente + equipos) por ID
    public static EquipmentReservationRequest getEquipmentReservationById(int idReservation) {
        String reservationSql =
                "SELECT r.id_reservation, r.id_section, r.reservation_date, rxc.id_client " +
                "FROM AUD_Reservations r " +
                "INNER JOIN AUD_RXC rxc ON r.id_reservation = rxc.id_reservation " +
                "WHERE r.id_reservation = ?";

        String equipmentSql =
                "SELECT id_rxe, id_reservation, id_equipment, quantity " +
                "FROM AUD_RXE WHERE id_reservation = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psReservation = conn.prepareStatement(reservationSql);
             PreparedStatement psEquipment = conn.prepareStatement(equipmentSql)) {

            psReservation.setInt(1, idReservation);

            try (ResultSet rs = psReservation.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getInt("id_reservation"),
                            rs.getDate("reservation_date"),
                            rs.getInt("id_section")
                    );

                    int idClient = rs.getInt("id_client");

                    List<RXE> equipmentList = new ArrayList<>();

                    psEquipment.setInt(1, idReservation);

                    try (ResultSet rsEquipment = psEquipment.executeQuery()) {
                        while (rsEquipment.next()) {
                            RXE item = new RXE(
                                    rsEquipment.getInt("id_rxe"),
                                    rsEquipment.getInt("id_reservation"),
                                    rsEquipment.getInt("id_equipment"),
                                    rsEquipment.getInt("quantity")
                            );

                            equipmentList.add(item);
                        }
                    }

                    return new EquipmentReservationRequest(reservation, idClient, equipmentList);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener reservación por id: " + e.getMessage());
        }

        return null;
    }
    // Obtener todas las reservaciones de un cliente
    public static List<EquipmentReservationRequest> getEquipmentReservationsByClientId(int idClient) {
        List<EquipmentReservationRequest> reservations = new ArrayList<>();

        String sql =
                "SELECT r.id_reservation " +
                "FROM AUD_Reservations r " +
                "INNER JOIN AUD_RXC rxc ON r.id_reservation = rxc.id_reservation " +
                "WHERE rxc.id_client = ? " +
                "ORDER BY r.reservation_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EquipmentReservationRequest request = getEquipmentReservationById(rs.getInt("id_reservation"));

                    if (request != null) {
                        reservations.add(request);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener reservaciones por cliente: " + e.getMessage());
        }

        return reservations;
    }
    
    // Calcular el total de equipos disponibles para una nueva reservación (considera todas las reservas existentes)
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
    // Verificar si una reservación pertenece a un cliente específico
    private static boolean reservationBelongsToClient(Connection conn, int idReservation, int idClient) {
        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM AUD_RXC " +
                "WHERE id_reservation = ? AND id_client = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReservation);
            ps.setInt(2, idClient);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al validar pertenencia de reservación: " + e.getMessage());
        }

        return false;
    }
    
    // Calcular el total de equipos disponibles para actualizar, excluyendo la que se esta actualizando.
    private static int getAvailableEquipmentQuantityExcludingReservation(
            Connection conn,
            int idEquipment,
            java.sql.Date reservationDate,
            int idSection,
            int idReservation
    ) {
        String sql =
                "SELECT e.available_quantity - COALESCE((" +
                "    SELECT SUM(rxe.quantity) " +
                "    FROM AUD_RXE rxe " +
                "    INNER JOIN AUD_Reservations r " +
                "        ON rxe.id_reservation = r.id_reservation " +
                "    WHERE rxe.id_equipment = ? " +
                "      AND r.reservation_date = ? " +
                "      AND r.id_section = ? " +
                "      AND r.id_reservation <> ?" +
                "), 0) AS available_quantity " +
                "FROM AUD_Equipment e " +
                "WHERE e.id_equipment = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEquipment);
            ps.setDate(2, reservationDate);
            ps.setInt(3, idSection);
            ps.setInt(4, idReservation);
            ps.setInt(5, idEquipment);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_quantity");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener disponibilidad excluyendo reservación: " + e.getMessage());
        }

        return 0;
    }
    // Actualizar una reservación existente (cambia sección, fecha y equipos)
    public static boolean updateEquipmentReservation(Reservation reservation, int idClient, List<RXE> equipmentList) {
        String updateReservationSql =
                "UPDATE AUD_Reservations SET id_section = ?, reservation_date = ? WHERE id_reservation = ?";

        String deleteRXESql =
                "DELETE FROM AUD_RXE WHERE id_reservation = ?";

        String insertRXESql =
                "INSERT INTO AUD_RXE (id_reservation, id_equipment, quantity) VALUES (?, ?, ?)";

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
             PreparedStatement psUpdateReservation = conn.prepareStatement(updateReservationSql);
             PreparedStatement psDeleteRXE = conn.prepareStatement(deleteRXESql);
             PreparedStatement psInsertRXE = conn.prepareStatement(insertRXESql)) {

            conn.setAutoCommit(false);

            if (!reservationBelongsToClient(conn, reservation.getIdReservation(), idClient)) {
                conn.rollback();
                return false;
            }

            for (RXE item : equipmentList) {
                int availableQuantity = getAvailableEquipmentQuantityExcludingReservation(
                        conn,
                        item.getIdEquipment(),
                        reservation.getReservationDate(),
                        reservation.getIdSection(),
                        reservation.getIdReservation()
                );

                if (availableQuantity < item.getQuantity()) {
                    conn.rollback();
                    return false;
                }
            }

            psUpdateReservation.setInt(1, reservation.getIdSection());
            psUpdateReservation.setDate(2, reservation.getReservationDate());
            psUpdateReservation.setInt(3, reservation.getIdReservation());
            psUpdateReservation.executeUpdate();

            psDeleteRXE.setInt(1, reservation.getIdReservation());
            psDeleteRXE.executeUpdate();

            for (RXE item : equipmentList) {
                psInsertRXE.setInt(1, reservation.getIdReservation());
                psInsertRXE.setInt(2, item.getIdEquipment());
                psInsertRXE.setInt(3, item.getQuantity());
                psInsertRXE.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar reservación: " + e.getMessage());
            return false;
        } finally {
            for (Semaphore semaphore : acquiredSemaphores) {
                semaphore.release();
            }
        }
    }
    // Eliminar todas las reservaciones de un cliente (incluye cascada en equipos y relación cliente)
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
    // Eliminar una reservación específica validando que pertenezca al cliente
    public static boolean deleteReservationById(int idReservation, int idClient) {
        String deleteSql =
                "DELETE FROM AUD_Reservations WHERE id_reservation = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {

            conn.setAutoCommit(false);

            if (!reservationBelongsToClient(conn, idReservation, idClient)) {
                conn.rollback();
                return false;
            }

            psDelete.setInt(1, idReservation);
            int rows = psDelete.executeUpdate();

            conn.commit();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar reservación por id: " + e.getMessage());
            return false;
        }
    }
}
