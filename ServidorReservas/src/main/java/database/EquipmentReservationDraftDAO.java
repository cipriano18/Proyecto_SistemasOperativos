package database;

import model.EquipmentReservationDraft;
import model.RXE;
import model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class EquipmentReservationDraftDAO {

    private static final long TTL_MILLIS = 10 * 60 * 1000L;

    private static final Map<String, Semaphore> reservationSemaphores = new ConcurrentHashMap<>();

    private static Semaphore getReservationSemaphore(Date reservationDate, int idSection) {
        String key = reservationDate.toString() + "|" + idSection;
        return reservationSemaphores.computeIfAbsent(key, k -> new Semaphore(1, true));
    }

    public static EquipmentReservationDraft createDraft(int idClient, Reservation reservation) {
        if (reservation == null || reservation.getReservationDate() == null || reservation.getIdSection() <= 0) {
            return null;
        }

        Semaphore semaphore = getReservationSemaphore(reservation.getReservationDate(), reservation.getIdSection());

        String insertDraftSql = "INSERT INTO AUD_ReservationDrafts " +
                "(id_client, id_section, reservation_date, created_at, expires_at) VALUES (?, ?, ?, ?, ?)";

        try {
            semaphore.acquire();

            cleanupExpiredDrafts();

            EquipmentReservationDraft existingDraft = getDraftBySectionAndDate(
                    reservation.getReservationDate(),
                    reservation.getIdSection()
            );

            if (existingDraft != null) {
                return null;
            }

            long now = System.currentTimeMillis();
            Timestamp createdAt = new Timestamp(now);
            Timestamp expiresAt = new Timestamp(now + TTL_MILLIS);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertDraftSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, idClient);
                ps.setInt(2, reservation.getIdSection());
                ps.setDate(3, reservation.getReservationDate());
                ps.setTimestamp(4, createdAt);
                ps.setTimestamp(5, expiresAt);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idDraft = rs.getInt(1);

                        EquipmentReservationDraft draft = new EquipmentReservationDraft();
                        draft.setIdDraft(idDraft);
                        draft.setIdClient(idClient);
                        draft.setReservation(reservation);
                        draft.setEquipmentList(new ArrayList<>());
                        draft.setCreatedAt(createdAt);
                        draft.setExpiresAt(expiresAt);

                        return draft;
                    }
                }
            }

            return null;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (SQLException e) {
            System.out.println("Error al crear draft en BD: " + e.getMessage());
            return null;
        } finally {
            semaphore.release();
        }
    }

    public static boolean updateDraft(EquipmentReservationDraft draft) {
        if (draft == null || draft.getIdDraft() <= 0) {
            return false;
        }

        Reservation reservation = draft.getReservation();
        if (reservation == null || reservation.getReservationDate() == null || reservation.getIdSection() <= 0) {
            return false;
        }

        Semaphore semaphore = getReservationSemaphore(reservation.getReservationDate(), reservation.getIdSection());

        String deleteDetailsSql = "DELETE FROM AUD_RDXE WHERE id_draft = ?";
        String insertDetailSql = "INSERT INTO AUD_RDXE (id_draft, id_equipment, quantity) VALUES (?, ?, ?)";

        try {
            semaphore.acquire();

            EquipmentReservationDraft existing = getDraftById(draft.getIdDraft());
            if (existing == null || existing.isExpired()) {
                if (existing != null) {
                    deleteDraft(existing.getIdDraft());
                }
                return false;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement psDelete = conn.prepareStatement(deleteDetailsSql);
                 PreparedStatement psInsert = conn.prepareStatement(insertDetailSql)) {

                conn.setAutoCommit(false);

                psDelete.setInt(1, draft.getIdDraft());
                psDelete.executeUpdate();

                List<RXE> equipmentList = draft.getEquipmentList() != null ? draft.getEquipmentList() : new ArrayList<>();

                for (RXE item : equipmentList) {
                    psInsert.setInt(1, draft.getIdDraft());
                    psInsert.setInt(2, item.getIdEquipment());
                    psInsert.setInt(3, item.getQuantity());
                    psInsert.executeUpdate();
                }

                conn.commit();
                return true;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (SQLException e) {
            System.out.println("Error al actualizar draft en BD: " + e.getMessage());
            return false;
        } finally {
            semaphore.release();
        }
    }

    public static EquipmentReservationDraft getDraftById(int idDraft) {
        if (idDraft <= 0) {
            return null;
        }

        cleanupExpiredDrafts();

        String sql = "SELECT id_draft, id_client, id_section, reservation_date, created_at, expires_at " +
                "FROM AUD_ReservationDrafts WHERE id_draft = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDraft);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EquipmentReservationDraft draft = mapDraft(rs);
                    draft.setEquipmentList(getDraftEquipment(conn, draft.getIdDraft()));
                    return draft;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener draft por id: " + e.getMessage());
        }

        return null;
    }

    public static EquipmentReservationDraft getDraftByClientId(int idClient) {
        cleanupExpiredDrafts();

        String sql = "SELECT id_draft, id_client, id_section, reservation_date, created_at, expires_at " +
                "FROM AUD_ReservationDrafts WHERE id_client = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idClient);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EquipmentReservationDraft draft = mapDraft(rs);
                    draft.setEquipmentList(getDraftEquipment(conn, draft.getIdDraft()));
                    return draft;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener draft por cliente: " + e.getMessage());
        }

        return null;
    }

    public static EquipmentReservationDraft getDraftBySectionAndDate(Date reservationDate, int idSection) {
        cleanupExpiredDrafts();

        String sql = "SELECT id_draft, id_client, id_section, reservation_date, created_at, expires_at " +
                "FROM AUD_ReservationDrafts WHERE reservation_date = ? AND id_section = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EquipmentReservationDraft draft = mapDraft(rs);
                    draft.setEquipmentList(getDraftEquipment(conn, draft.getIdDraft()));
                    return draft;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener draft por fecha y sección: " + e.getMessage());
        }

        return null;
    }

    public static boolean deleteDraft(int idDraft) {
        if (idDraft <= 0) {
            return false;
        }

        String sql = "DELETE FROM AUD_ReservationDrafts WHERE id_draft = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDraft);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar draft: " + e.getMessage());
            return false;
        }
    }

    public static void cleanupExpiredDrafts() {
        String sql = "DELETE FROM AUD_ReservationDrafts WHERE expires_at <= NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al limpiar drafts expirados: " + e.getMessage());
        }
    }

    private static EquipmentReservationDraft mapDraft(ResultSet rs) throws SQLException {
        EquipmentReservationDraft draft = new EquipmentReservationDraft();
        draft.setIdDraft(rs.getInt("id_draft"));
        draft.setIdClient(rs.getInt("id_client"));

        Reservation reservation = new Reservation();
        reservation.setIdSection(rs.getInt("id_section"));
        reservation.setReservationDate(rs.getDate("reservation_date"));
        draft.setReservation(reservation);

        draft.setCreatedAt(rs.getTimestamp("created_at"));
        draft.setExpiresAt(rs.getTimestamp("expires_at"));

        return draft;
    }

    private static List<RXE> getDraftEquipment(Connection conn, int idDraft) throws SQLException {
        List<RXE> equipmentList = new ArrayList<>();

        String sql = "SELECT id_equipment, quantity FROM AUD_RDXE WHERE id_draft = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDraft);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RXE item = new RXE();
                    item.setIdEquipment(rs.getInt("id_equipment"));
                    item.setQuantity(rs.getInt("quantity"));
                    equipmentList.add(item);
                }
            }
        }

        return equipmentList;
    }
}