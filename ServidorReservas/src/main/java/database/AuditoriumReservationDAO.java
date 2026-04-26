package database;

import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CalendarBlock;
import model.RXE;
import model.Reservation;

/**
 *
 * @author Reyner
 */
public class AuditoriumReservationDAO {

    // Obtener los bloques reservados de auditorio por mes y año para mostrarlos en el calendario
    public static List<CalendarBlock> getReservedAuditoriumBlocksByMonth(int month, int year) {

        List<CalendarBlock> blocks = new ArrayList<>();

        String sql
                = "SELECT r.reservation_date, r.id_section "
                + "FROM AUD_AuditoriumReservations ar "
                + "INNER JOIN AUD_Reservations r "
                + "    ON ar.id_reservation = r.id_reservation "
                + "WHERE MONTH(r.reservation_date) = ? "
                + "  AND YEAR(r.reservation_date) = ? "
                + "ORDER BY r.reservation_date, r.id_section";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
            System.out.println("Error al obtener reservaciones de auditorio del calendario: " + e.getMessage());
        }

        return blocks;
    }
    
    private static List<RXE> getEquipmentByReservationId(int idReservation) {
        List<RXE> equipmentList = new ArrayList<>();

        String sql =
                "SELECT id_equipment, quantity " +
                "FROM AUD_RXE " +
                "WHERE id_reservation = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReservation);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RXE rxe = new RXE();
                    rxe.setIdEquipment(rs.getInt("id_equipment"));
                    rxe.setQuantity(rs.getInt("quantity"));

                    equipmentList.add(rxe);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener equipos de la reserva: " + e.getMessage());
        }

        return equipmentList;
    }
    
    public static AuditoriumDraftRequest getAuditoriumReservationById(int idReservation) {

        String sql =
                "SELECT r.id_reservation, r.reservation_date, r.id_section, " +
                "       rxc.id_client, ar.event_name, ar.attendees_count, ar.observations " +
                "FROM AUD_Reservations r " +
                "INNER JOIN AUD_RXC rxc ON r.id_reservation = rxc.id_reservation " +
                "INNER JOIN AUD_AuditoriumReservations ar ON r.id_reservation = ar.id_reservation " +
                "WHERE r.id_reservation = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReservation);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Reservation reservation = new Reservation();
                    reservation.setIdReservation(rs.getInt("id_reservation"));
                    reservation.setReservationDate(rs.getDate("reservation_date"));
                    reservation.setIdSection(rs.getInt("id_section"));

                    AuditoriumDraft auditoriumDraft = new AuditoriumDraft();
                    auditoriumDraft.setEventName(rs.getString("event_name"));
                    auditoriumDraft.setAttendeesCount(rs.getInt("attendees_count"));
                    auditoriumDraft.setObservations(rs.getString("observations"));

                    AuditoriumDraftRequest request = new AuditoriumDraftRequest();
                    request.setIdDraft(0);
                    request.setIdClient(rs.getInt("id_client"));
                    request.setReservation(reservation);
                    request.setAuditoriumDraft(auditoriumDraft);
                    request.setEquipmentList(getEquipmentByReservationId(idReservation));

                    return request;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener reserva de auditorio por ID: " + e.getMessage());
        }

        return null;
    }
    
    public static List<AuditoriumDraftRequest> getAuditoriumReservationsByClientId(int idClient) {
        List<AuditoriumDraftRequest> reservations = new ArrayList<>();

        String sql =
                "SELECT r.id_reservation " +
                "FROM AUD_Reservations r " +
                "INNER JOIN AUD_RXC rxc ON r.id_reservation = rxc.id_reservation " +
                "INNER JOIN AUD_AuditoriumReservations ar ON r.id_reservation = ar.id_reservation " +
                "WHERE rxc.id_client = ? " +
                "AND r.reservation_date >= CURDATE() " +
                "ORDER BY r.reservation_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditoriumDraftRequest request =
                            getAuditoriumReservationById(rs.getInt("id_reservation"));

                    if (request != null) {
                        reservations.add(request);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener reservaciones de auditorio por cliente: " + e.getMessage());
        }

        return reservations;
    }
    
    public static boolean deleteAuditoriumReservationById(int idReservation, int idClient) {

        String sql =
                "DELETE r " +
                "FROM AUD_Reservations r " +
                "INNER JOIN AUD_RXC rxc ON r.id_reservation = rxc.id_reservation " +
                "INNER JOIN AUD_AuditoriumReservations ar ON r.id_reservation = ar.id_reservation " +
                "WHERE r.id_reservation = ? " +
                "AND rxc.id_client = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReservation);
            ps.setInt(2, idClient);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar reserva de auditorio: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteAuditoriumReservationsByClientId(int idClient) {

        String sql =
                "DELETE r " +
                "FROM AUD_Reservations r " +
                "INNER JOIN AUD_RXC rxc ON r.id_reservation = rxc.id_reservation " +
                "INNER JOIN AUD_AuditoriumReservations ar ON r.id_reservation = ar.id_reservation " +
                "WHERE rxc.id_client = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar reservas de auditorio por cliente: " + e.getMessage());
            return false;
        }
    }
}
