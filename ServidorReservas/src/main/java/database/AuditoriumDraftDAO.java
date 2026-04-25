/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import static database.EquipmentReservationDraftDAO.cleanupExpiredDrafts;
import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import model.RXE;
import model.Reservation;

/**
 *
 * @author Reyner
 */
public class AuditoriumDraftDAO {
     private static final long TTL_MILLIS = 10 * 60 * 1000L;

    private static final Map<String, Semaphore> auditoriumDraftSemaphores = new ConcurrentHashMap<>();

    // Obtener o crear semáforo por fecha + sección para evitar dos drafts simultáneos
    private static Semaphore getAuditoriumDraftSemaphore(Date reservationDate, int idSection) {
        String key = reservationDate.toString() + "|" + idSection;
        return auditoriumDraftSemaphores.computeIfAbsent(key, k -> new Semaphore(1, true));
    }

    // Crear una reserva temporal de auditorio
    public static AuditoriumDraftRequest createDraft(AuditoriumDraftRequest request) {

        if (request == null || request.getReservation() == null || request.getAuditoriumDraft() == null) {
            return null;
        }

        Reservation reservation = request.getReservation();

        if (request.getIdClient() <= 0 ||
                reservation.getIdSection() <= 0 ||
                reservation.getReservationDate() == null) {
            return null;
        }

        Semaphore semaphore = getAuditoriumDraftSemaphore(
                reservation.getReservationDate(),
                reservation.getIdSection()
        );

        String insertDraftSql =
                "INSERT INTO AUD_ReservationDrafts " +
                "(id_client, id_section, reservation_date, created_at, expires_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        String insertAuditoriumDraftSql =
                "INSERT INTO AUD_AuditoriumDrafts " +
                "(id_draft, event_name, attendees_count, observations) " +
                "VALUES (?, ?, ?, ?)";

        String insertRDXESql =
                "INSERT INTO AUD_RDXE (id_draft, id_equipment, quantity) " +
                "VALUES (?, ?, ?)";

        try {
            semaphore.acquire();

            cleanupExpiredDrafts();

            if (existsAuditoriumReservationByDateAndSection(
                    reservation.getReservationDate(),
                    reservation.getIdSection())) {
                return null;
            }

            if (existsActiveAuditoriumDraftByDateAndSection(
                    reservation.getReservationDate(),
                    reservation.getIdSection())) {
                return null;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement psDraft = conn.prepareStatement(insertDraftSql, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement psAuditoriumDraft = conn.prepareStatement(insertAuditoriumDraftSql);
                 PreparedStatement psRDXE = conn.prepareStatement(insertRDXESql)) {

                conn.setAutoCommit(false);

                long now = System.currentTimeMillis();
                Timestamp createdAt = new Timestamp(now);
                Timestamp expiresAt = new Timestamp(now + TTL_MILLIS);

                psDraft.setInt(1, request.getIdClient());
                psDraft.setInt(2, reservation.getIdSection());
                psDraft.setDate(3, reservation.getReservationDate());
                psDraft.setTimestamp(4, createdAt);
                psDraft.setTimestamp(5, expiresAt);

                psDraft.executeUpdate();

                int idDraft;

                try (ResultSet generatedKeys = psDraft.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idDraft = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return null;
                    }
                }

                AuditoriumDraft auditoriumDraft = request.getAuditoriumDraft();
                auditoriumDraft.setIdDraft(idDraft);

                psAuditoriumDraft.setInt(1, idDraft);
                psAuditoriumDraft.setString(2, auditoriumDraft.getEventName());
                psAuditoriumDraft.setInt(3, auditoriumDraft.getAttendeesCount());
                psAuditoriumDraft.setString(4, auditoriumDraft.getObservations());
                psAuditoriumDraft.executeUpdate();

                List<RXE> equipmentList = request.getEquipmentList();

                if (equipmentList != null && !equipmentList.isEmpty()) {
                    for (RXE item : equipmentList) {
                        psRDXE.setInt(1, idDraft);
                        psRDXE.setInt(2, item.getIdEquipment());
                        psRDXE.setInt(3, item.getQuantity());
                        psRDXE.executeUpdate();
                    }
                }

                conn.commit();

                request.setIdDraft(idDraft);
                request.setAuditoriumDraft(auditoriumDraft);

                return request;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;

        } catch (SQLException e) {
            System.out.println("Error al crear draft de auditorio: " + e.getMessage());
            return null;

        } finally {
            semaphore.release();
        }
    }
// Validar si ya existe una reserva real de auditorio para la misma fecha y sección
    private static boolean existsAuditoriumReservationByDateAndSection(Date reservationDate, int idSection) {
        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM AUD_AuditoriumReservations ar " +
                "INNER JOIN AUD_Reservations r ON ar.id_reservation = r.id_reservation " +
                "WHERE r.reservation_date = ? AND r.id_section = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al validar reserva real de auditorio: " + e.getMessage());
        }

        return false;
    }
      // Validar si ya existe un draft activo de auditorio para la misma fecha y sección
    private static boolean existsActiveAuditoriumDraftByDateAndSection(Date reservationDate, int idSection) {
        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM AUD_AuditoriumDrafts ad " +
                "INNER JOIN AUD_ReservationDrafts d ON ad.id_draft = d.id_draft " +
                "WHERE d.reservation_date = ? " +
                "AND d.id_section = ? " +
                "AND d.expires_at > NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al validar draft activo de auditorio: " + e.getMessage());
        }

        return false;
    }
}
