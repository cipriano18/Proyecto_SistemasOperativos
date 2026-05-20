
package database;

import draft.EquipmentReservationDraft;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import model.CalendarBlock;
import model.RXE;
import model.Reservation;

/**
 * Gestiona las operaciones relacionadas con drafts de equipos.
 */
public class EquipmentReservationDraftDAO {

    private static final long TTL_MILLIS =
            10 * 60 * 1000L;

    private static final Map<String, Semaphore>
            reservationSemaphores =
            new ConcurrentHashMap<>();

    /**
     * Obtiene o crea un semáforo por fecha y sección.
     *
     * @param reservationDate fecha de la reserva
     * @param idSection identificador de la sección
     *
     * @return semáforo asociado a la reserva
     */
    private static Semaphore getReservationSemaphore(
            Date reservationDate,
            int idSection
    ) {

        String key =
                reservationDate.toString()
                + "|"
                + idSection;

        return reservationSemaphores.computeIfAbsent(
                key,
                k -> new Semaphore(1, true)
        );
    }

    /**
     * Obtiene los drafts bloqueados por mes y año.
     *
     * @param month mes de consulta
     * @param year año de consulta
     * @param idClient identificador del cliente
     *
     * @return lista de bloques del calendario
     */
    public static List<CalendarBlock> getBlockedDraftsByMonth(
            int month,
            int year,
            int idClient
    ) {

        cleanupExpiredDraftsAndCount();

        List<CalendarBlock> blocks =
                new ArrayList<>();

        String sql = "SELECT reservation_date, "
                + "id_section, id_client "
                + "FROM aud_reservationdrafts "
                + "WHERE MONTH(reservation_date) = ? "
                + "AND YEAR(reservation_date) = ? "
                + "AND expires_at > NOW() "
                + "ORDER BY reservation_date, "
                + "id_section";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CalendarBlock block =
                        new CalendarBlock();

                block.setReservationDate(
                        rs.getDate("reservation_date")
                );

                block.setIdSection(
                        rs.getInt("id_section")
                );

                block.setStatus(
                        rs.getInt("id_client") == idClient
                                ? "OWN_DRAFT"
                                : "BLOCKED"
                );

                blocks.add(block);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener drafts "
                    + "bloqueados del calendario: "
                    + e.getMessage()
            );
        }

        return blocks;
    }

    /**
     * Crea un draft de reservación de equipo.
     *
     * @param idClient identificador del cliente
     * @param reservation datos de la reserva
     *
     * @return draft creado o null
     */
    public static EquipmentReservationDraft createDraft(
            int idClient,
            Reservation reservation
    ) {

        if (
                reservation == null
                || reservation.getReservationDate() == null
                || reservation.getIdSection() <= 0
        ) {
            return null;
        }

        Semaphore semaphore =
                getReservationSemaphore(
                        reservation.getReservationDate(),
                        reservation.getIdSection()
                );

        String insertDraftSql =
                "INSERT INTO aud_reservationdrafts "
                + "(id_client, id_section, "
                + "reservation_date, created_at, "
                + "expires_at) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {

            semaphore.acquire();

            cleanupExpiredDrafts();

            EquipmentReservationDraft existingDraft =
                    getDraftBySectionAndDate(
                            reservation.getReservationDate(),
                            reservation.getIdSection()
                    );

            if (existingDraft != null) {

                if (existingDraft.getIdClient()
                        == idClient) {

                    return existingDraft;
                }

                return null;
            }

            long now = System.currentTimeMillis();

            Timestamp createdAt =
                    new Timestamp(now);

            Timestamp expiresAt =
                    new Timestamp(now + TTL_MILLIS);

            try (
                    Connection conn =
                    DBConnection.getConnection();

                    PreparedStatement ps =
                    conn.prepareStatement(
                            insertDraftSql,
                            PreparedStatement
                                    .RETURN_GENERATED_KEYS
                    )
            ) {

                ps.setInt(1, idClient);

                ps.setInt(
                        2,
                        reservation.getIdSection()
                );

                ps.setDate(
                        3,
                        reservation.getReservationDate()
                );

                ps.setTimestamp(4, createdAt);

                ps.setTimestamp(5, expiresAt);

                ps.executeUpdate();

                try (
                        ResultSet rs =
                        ps.getGeneratedKeys()
                ) {

                    if (rs.next()) {

                        int idDraft =
                                rs.getInt(1);

                        EquipmentReservationDraft draft =
                                new EquipmentReservationDraft();

                        draft.setIdDraft(idDraft);

                        draft.setIdClient(idClient);

                        draft.setReservation(
                                reservation
                        );

                        draft.setEquipmentList(
                                new ArrayList<>()
                        );

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

            System.out.println(
                    "Error al crear draft en BD: "
                    + e.getMessage()
            );

            return null;

        } finally {

            semaphore.release();
        }
    }

    /**
     * Actualiza un draft existente.
     *
     * @param draft draft a actualizar
     *
     * @return true si la actualización fue exitosa
     */
    public static boolean updateDraft(
            EquipmentReservationDraft draft
    ) {

        if (
                draft == null
                || draft.getIdDraft() <= 0
        ) {
            return false;
        }

        Reservation reservation =
                draft.getReservation();

        if (
                reservation == null
                || reservation.getReservationDate() == null
                || reservation.getIdSection() <= 0
        ) {
            return false;
        }

        Semaphore semaphore =
                getReservationSemaphore(
                        reservation.getReservationDate(),
                        reservation.getIdSection()
                );

        String deleteDetailsSql =
                "DELETE FROM aud_rdxe "
                + "WHERE id_draft = ?";

        String insertDetailSql =
                "INSERT INTO aud_rdxe "
                + "(id_draft, id_equipment, quantity) "
                + "VALUES (?, ?, ?)";

        try {

            semaphore.acquire();

            EquipmentReservationDraft existing =
                    getDraftById(
                            draft.getIdDraft()
                    );

            if (
                    existing == null
                    || existing.isExpired()
            ) {

                if (existing != null) {

                    deleteDraft(
                            existing.getIdDraft()
                    );
                }

                return false;
            }

            try (
                    Connection conn =
                    DBConnection.getConnection();

                    PreparedStatement psDelete =
                    conn.prepareStatement(
                            deleteDetailsSql
                    );

                    PreparedStatement psInsert =
                    conn.prepareStatement(
                            insertDetailSql
                    )
            ) {

                conn.setAutoCommit(false);

                psDelete.setInt(
                        1,
                        draft.getIdDraft()
                );

                psDelete.executeUpdate();

                List<RXE> equipmentList =
                        draft.getEquipmentList() != null
                        ? draft.getEquipmentList()
                        : new ArrayList<>();

                for (RXE item : equipmentList) {

                    psInsert.setInt(
                            1,
                            draft.getIdDraft()
                    );

                    psInsert.setInt(
                            2,
                            item.getIdEquipment()
                    );

                    psInsert.setInt(
                            3,
                            item.getQuantity()
                    );

                    psInsert.executeUpdate();
                }

                System.out.println(
                        "Si actualiza la tabla aduRdxe"
                );

                conn.commit();

                return true;
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return false;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar "
                    + "draft en BD: "
                    + e.getMessage()
            );

            return false;

        } finally {

            semaphore.release();
        }
    }

    /**
     * Obtiene un draft mediante su identificador.
     *
     * @param idDraft identificador del draft
     *
     * @return draft encontrado o null
     */
    public static EquipmentReservationDraft getDraftById(
            int idDraft
    ) {

        if (idDraft <= 0) {
            return null;
        }

        cleanupExpiredDrafts();

        String sql = "SELECT id_draft, id_client, "
                + "id_section, reservation_date, "
                + "created_at, expires_at "
                + "FROM aud_reservationdrafts "
                + "WHERE id_draft = ?";

        try (
                Connection conn =
                DBConnection.getConnection();

                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idDraft);

            try (
                    ResultSet rs =
                    ps.executeQuery()
            ) {

                if (rs.next()) {

                    EquipmentReservationDraft draft =
                            mapDraft(rs);

                    draft.setEquipmentList(
                            getDraftEquipment(
                                    conn,
                                    draft.getIdDraft()
                            )
                    );

                    return draft;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener draft "
                    + "por id: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Obtiene el último draft activo de un cliente.
     *
     * @param idClient identificador del cliente
     *
     * @return draft encontrado o null
     */
    public static EquipmentReservationDraft getDraftByClientId(
            int idClient
    ) {

        cleanupExpiredDrafts();

        String sql = "SELECT id_draft, id_client, "
                + "id_section, reservation_date, "
                + "created_at, expires_at "
                + "FROM aud_reservationdrafts "
                + "WHERE id_client = ? "
                + "ORDER BY created_at DESC "
                + "LIMIT 1";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idClient);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {

                    EquipmentReservationDraft draft =
                            mapDraft(rs);

                    draft.setEquipmentList(
                            getDraftEquipment(
                                    conn,
                                    draft.getIdDraft()
                            )
                    );

                    return draft;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener draft "
                    + "por cliente: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Obtiene un draft mediante fecha y sección.
     *
     * @param reservationDate fecha de la reserva
     * @param idSection identificador de la sección
     *
     * @return draft encontrado o null
     */
    public static EquipmentReservationDraft getDraftBySectionAndDate(
            Date reservationDate,
            int idSection
    ) {

        cleanupExpiredDrafts();

        String sql = "SELECT id_draft, id_client, "
                + "id_section, reservation_date, "
                + "created_at, expires_at "
                + "FROM aud_reservationdrafts "
                + "WHERE reservation_date = ? "
                + "AND id_section = ? "
                + "LIMIT 1";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {

                    EquipmentReservationDraft draft =
                            mapDraft(rs);

                    draft.setEquipmentList(
                            getDraftEquipment(
                                    conn,
                                    draft.getIdDraft()
                            )
                    );

                    return draft;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener draft "
                    + "por fecha y sección: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Elimina un draft mediante su identificador.
     *
     * @param idDraft identificador del draft
     *
     * @return true si el draft fue eliminado
     */
    public static boolean deleteDraft(int idDraft) {

        if (idDraft <= 0) {
            return false;
        }

        String sql = "DELETE FROM aud_reservationdrafts "
                + "WHERE id_draft = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idDraft);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar draft: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Elimina los drafts expirados.
     */
    public static void cleanupExpiredDrafts() {

        String sql = "DELETE FROM aud_reservationdrafts "
                + "WHERE expires_at <= NOW()";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Error al limpiar drafts "
                    + "expirados: "
                    + e.getMessage()
            );
        }
    }

    /**
     * Convierte un resultado SQL en un draft.
     *
     * @param rs resultado de la consulta
     *
     * @return draft construido
     *
     * @throws SQLException si ocurre un error SQL
     */
    private static EquipmentReservationDraft mapDraft(
            ResultSet rs
    ) throws SQLException {

        EquipmentReservationDraft draft =
                new EquipmentReservationDraft();

        draft.setIdDraft(
                rs.getInt("id_draft")
        );

        draft.setIdClient(
                rs.getInt("id_client")
        );

        Reservation reservation =
                new Reservation();

        reservation.setIdSection(
                rs.getInt("id_section")
        );

        reservation.setReservationDate(
                rs.getDate("reservation_date")
        );

        draft.setReservation(reservation);

        draft.setCreatedAt(
                rs.getTimestamp("created_at")
        );

        draft.setExpiresAt(
                rs.getTimestamp("expires_at")
        );

        return draft;
    }

    /**
     * Obtiene los equipos asociados a un draft.
     *
     * @param conn conexión activa
     * @param idDraft identificador del draft
     *
     * @return lista de equipos del draft
     *
     * @throws SQLException si ocurre un error SQL
     */
    private static List<RXE> getDraftEquipment(
            Connection conn,
            int idDraft
    ) throws SQLException {

        List<RXE> equipmentList =
                new ArrayList<>();

        String sql = "SELECT id_equipment, quantity "
                + "FROM aud_rdxe "
                + "WHERE id_draft = ?";

        try (
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idDraft);

            try (
                    ResultSet rs =
                    ps.executeQuery()
            ) {

                while (rs.next()) {

                    RXE item = new RXE();

                    item.setIdEquipment(
                            rs.getInt("id_equipment")
                    );

                    item.setQuantity(
                            rs.getInt("quantity")
                    );

                    equipmentList.add(item);
                }
            }
        }

        return equipmentList;
    }

    /**
     * Elimina los drafts expirados y retorna la cantidad.
     *
     * @return cantidad de drafts eliminados
     */
    public static int cleanupExpiredDraftsAndCount() {

        String sql = "DELETE FROM aud_reservationdrafts "
                + "WHERE expires_at <= NOW()";

        System.out.println("entro en delete ");

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            int deleted = ps.executeUpdate();

            if (deleted > 0) {

                System.out.println(
                        "Se eliminaron "
                        + deleted
                        + " drafts expirados"
                );

            } else {

                System.out.println(
                        "No hay drafts expirados"
                );
            }

            return deleted;

        } catch (SQLException e) {

            System.out.println(
                    "Error al limpiar drafts "
                    + "expirados: "
                    + e.getMessage()
            );

            return 0;
        }
    }

    /**
     * Obtiene los bloques de drafts expirados.
     *
     * @return lista de bloques disponibles
     */
    public static List<CalendarBlock> getExpiredDraftBlocks() {

        List<CalendarBlock> blocks =
                new ArrayList<>();

        String sql = "SELECT reservation_date, id_section "
                + "FROM aud_reservationdrafts "
                + "WHERE expires_at <= NOW()";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql);
                ResultSet rs =
                ps.executeQuery()
        ) {

            while (rs.next()) {

                CalendarBlock block =
                        new CalendarBlock();

                block.setReservationDate(
                        rs.getDate("reservation_date")
                );

                block.setIdSection(
                        rs.getInt("id_section")
                );

                block.setStatus("AVAILABLE");

                blocks.add(block);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener drafts "
                    + "expirados: "
                    + e.getMessage()
            );
        }

        return blocks;
    }
}