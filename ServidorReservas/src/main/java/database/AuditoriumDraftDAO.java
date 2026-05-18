package database;

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
import model.CalendarBlock;
import model.RXE;
import model.Reservation;

/**
 * Gestiona las operaciones relacionadas con drafts de auditorio.
 */
public class AuditoriumDraftDAO {
    private static final long TTL_MILLIS = 10 * 60 * 1000L;

    private static final Map<String, Semaphore> auditoriumDraftSemaphores =
            new ConcurrentHashMap<>();

    /**
     * Obtiene o crea un semáforo para una fecha y sección.
     *
     * @param reservationDate fecha de la reserva
     * @param idSection identificador de la sección
     *
     * @return semáforo asociado a la fecha y sección
     */
    private static Semaphore getAuditoriumDraftSemaphore(
            Date reservationDate,
            int idSection
    ) {

        String key = reservationDate.toString() + "|" + idSection;

        return auditoriumDraftSemaphores.computeIfAbsent(
                key,
                k -> new Semaphore(1, true)
        );
    }

    /**
     * Obtiene los drafts activos de auditorio por mes y año.
     *
     * @param month mes de consulta
     * @param year año de consulta
     * @param idClient identificador del cliente
     *
     * @return lista de bloques del calendario
     */
    public static List<CalendarBlock> getBlockedAuditoriumDraftsByMonth(
            int month,
            int year,
            int idClient
    ) {

        cleanupExpiredDraftsAndCount();

        List<CalendarBlock> blocks = new java.util.ArrayList<>();

        String sql = "SELECT d.reservation_date, d.id_section, "
                + "d.id_client "
                + "FROM aud_reservationdrafts d "
                + "INNER JOIN aud_auditoriumdrafts ad "
                + "ON d.id_draft = ad.id_draft "
                + "WHERE MONTH(d.reservation_date) = ? "
                + "AND YEAR(d.reservation_date) = ? "
                + "AND d.expires_at > NOW() "
                + "ORDER BY d.reservation_date, d.id_section";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CalendarBlock block = new CalendarBlock();

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
                    "Error al obtener drafts bloqueados "
                    + "de auditorio: "
                    + e.getMessage()
            );
        }

        return blocks;
    }

    /**
     * Crea una reserva temporal de auditorio.
     *
     * @param request datos del draft de auditorio
     *
     * @return draft creado o draft existente
     */
    public static AuditoriumDraftRequest createDraft(
            AuditoriumDraftRequest request
    ) {

        if (request == null || request.getReservation() == null) {
            return null;
        }

        Reservation reservation = request.getReservation();

        if (
                request.getIdClient() <= 0
                || reservation.getIdSection() <= 0
                || reservation.getReservationDate() == null
        ) {
            return null;
        }

        Semaphore semaphore = getAuditoriumDraftSemaphore(
                reservation.getReservationDate(),
                reservation.getIdSection()
        );

        String insertDraftSql = "INSERT INTO aud_reservationdrafts "
                + "(id_client, id_section, reservation_date, "
                + "created_at, expires_at) "
                + "VALUES (?, ?, ?, ?, ?)";

        String insertAuditoriumDraftSql =
                "INSERT INTO aud_auditoriumdrafts "
                + "(id_draft, event_name, attendees_count, "
                + "observations) "
                + "VALUES (?, ?, ?, ?)";

        String insertRDXESql = "INSERT INTO aud_rdxe "
                + "(id_draft, id_equipment, quantity) "
                + "VALUES (?, ?, ?)";

        try {

            semaphore.acquire();

            cleanupExpiredDraftsAndCount();

            if (existsAuditoriumReservationByDateAndSection(
                    reservation.getReservationDate(),
                    reservation.getIdSection()
            )) {
                return null;
            }

            AuditoriumDraftRequest existingDraft =
                    getDraftBySectionAndDate(
                            reservation.getReservationDate(),
                            reservation.getIdSection()
                    );

            if (existingDraft != null) {

                if (existingDraft.getIdClient() == request.getIdClient()) {
                    return existingDraft;
                }

                return null;
            }

            try (
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement psDraft = conn.prepareStatement(
                            insertDraftSql,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );
                    PreparedStatement psAuditoriumDraft =
                    conn.prepareStatement(insertAuditoriumDraftSql);
                    PreparedStatement psRDXE =
                    conn.prepareStatement(insertRDXESql)
            ) {

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

                try (
                        ResultSet generatedKeys =
                        psDraft.getGeneratedKeys()
                ) {

                    if (generatedKeys.next()) {
                        idDraft = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return null;
                    }
                }

                AuditoriumDraft auditoriumDraft =
                        request.getAuditoriumDraft();

                if (auditoriumDraft == null) {

                    auditoriumDraft = new AuditoriumDraft();
                    auditoriumDraft.setEventName("");
                    auditoriumDraft.setAttendeesCount(0);
                    auditoriumDraft.setObservations("");
                }

                auditoriumDraft.setIdDraft(idDraft);

                psAuditoriumDraft.setInt(1, idDraft);
                psAuditoriumDraft.setString(
                        2,
                        auditoriumDraft.getEventName()
                );
                psAuditoriumDraft.setInt(
                        3,
                        auditoriumDraft.getAttendeesCount()
                );
                psAuditoriumDraft.setString(
                        4,
                        auditoriumDraft.getObservations()
                );

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

            System.out.println(
                    "Error al crear draft de auditorio: "
                    + e.getMessage()
            );

            return null;

        } finally {

            semaphore.release();
        }
    }
    
    /**
     * Valida si existe una reserva real para la fecha y sección.
     *
     * @param reservationDate fecha de la reserva
     * @param idSection identificador de la sección
     *
     * @return true si existe una reserva real
     */    
    private static boolean existsAuditoriumReservationByDateAndSection(
            Date reservationDate,
            int idSection
    ) {

        String sql = "SELECT COUNT(*) AS total "
                + "FROM aud_auditoriumreservations ar "
                + "INNER JOIN aud_reservations r "
                + "ON ar.id_reservation = r.id_reservation "
                + "WHERE r.reservation_date = ? "
                + "AND r.id_section = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al validar reserva real "
                    + "de auditorio: "
                    + e.getMessage()
            );
        }

        return false;
    }
    
    /**
     * Valida si existe un draft activo para la fecha y sección.
     *
     * @param reservationDate fecha del draft
     * @param idSection identificador de la sección
     *
     * @return true si existe un draft activo
     */    
    private static boolean existsActiveAuditoriumDraftByDateAndSection(
            Date reservationDate,
            int idSection
    ) {

        String sql = "SELECT COUNT(*) AS total "
                + "FROM aud_auditoriumdrafts ad "
                + "INNER JOIN aud_reservationdrafts d "
                + "ON ad.id_draft = d.id_draft "
                + "WHERE d.reservation_date = ? "
                + "AND d.id_section = ? "
                + "AND d.expires_at > NOW()";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al validar draft activo "
                    + "de auditorio: "
                    + e.getMessage()
            );
        }

        return false;
    }
    
    /**
     * Obtiene la cantidad disponible de un equipo.
     *
     * @param conn conexión activa
     * @param idEquipment identificador del equipo
     *
     * @return cantidad disponible del equipo
     *
     * @throws SQLException si ocurre un error SQL
     */    
    private static int getEquipmentAvailableQuantity(
            Connection conn,
            int idEquipment
    ) throws SQLException {

        String sql = "SELECT available_quantity "
                + "FROM AUD_Equipment "
                + "WHERE id_equipment = ?";

        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idEquipment);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {
                    return rs.getInt("available_quantity");
                }
            }
        }

        return 0;
    }
    

    /**
     * Actualiza un draft de auditorio existente.
     *
     * @param request datos actualizados del draft
     *
     * @return true si la actualización fue exitosa
     */
    public static boolean updateDraft(
            AuditoriumDraftRequest request
    ) {

        if (
                request == null
                || request.getIdDraft() <= 0
                || request.getAuditoriumDraft() == null
        ) {
            return false;
        }

        AuditoriumDraft auditoriumDraft = request.getAuditoriumDraft();

        if (auditoriumDraft.getAttendeesCount() > 200) {

            System.out.println(
                    "Error: El auditorio solo permite "
                    + "máximo 200 personas"
            );

            return false;
        }

        String checkDraftSql = "SELECT expires_at "
                + "FROM aud_reservationdrafts "
                + "WHERE id_draft = ?";

        String updateAuditoriumSql =
                "UPDATE aud_auditoriumdrafts "
                + "SET event_name = ?, attendees_count = ?, "
                + "observations = ? "
                + "WHERE id_draft = ?";

        String deleteRDXESql = "DELETE FROM aud_rdxe "
                + "WHERE id_draft = ?";

        String insertRDXESql = "INSERT INTO aud_rdxe "
                + "(id_draft, id_equipment, quantity) "
                + "VALUES (?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement psCheck =
                conn.prepareStatement(checkDraftSql);
                PreparedStatement psUpdate =
                conn.prepareStatement(updateAuditoriumSql);
                PreparedStatement psDeleteRDXE =
                conn.prepareStatement(deleteRDXESql);
                PreparedStatement psInsertRDXE =
                conn.prepareStatement(insertRDXESql)
        ) {

            conn.setAutoCommit(false);

            psCheck.setInt(1, request.getIdDraft());
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            Timestamp expiresAt = rs.getTimestamp("expires_at");

            if (expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                conn.rollback();
                return false;
            }

            psUpdate.setString(1, auditoriumDraft.getEventName());
            psUpdate.setInt(2, auditoriumDraft.getAttendeesCount());
            psUpdate.setString(3, auditoriumDraft.getObservations());
            psUpdate.setInt(4, request.getIdDraft());

            psUpdate.executeUpdate();

            psDeleteRDXE.setInt(1, request.getIdDraft());
            psDeleteRDXE.executeUpdate();

            List<RXE> equipmentList = request.getEquipmentList();

            if (equipmentList != null && !equipmentList.isEmpty()) {

                for (RXE item : equipmentList) {

                    if (
                            item.getIdEquipment() <= 0
                            || item.getQuantity() <= 0
                    ) {
                        conn.rollback();
                        return false;
                    }

                    int availableQuantity =
                            getEquipmentAvailableQuantity(
                                    conn,
                                    item.getIdEquipment()
                            );

                    if (availableQuantity < item.getQuantity()) {

                        System.out.println(
                                "Error: cantidad solicitada "
                                + "mayor al inventario "
                                + "disponible del equipo "
                                + item.getIdEquipment()
                        );

                        conn.rollback();

                        return false;
                    }

                    psInsertRDXE.setInt(1, request.getIdDraft());
                    psInsertRDXE.setInt(2, item.getIdEquipment());
                    psInsertRDXE.setInt(3, item.getQuantity());
                    psInsertRDXE.executeUpdate();
                }
            }

            conn.commit();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar draft "
                    + "de auditorio: "
                    + e.getMessage()
            );

            return false;
        }
    }
    
    /**
     * Elimina un draft de auditorio.
     *
     * @param idDraft identificador del draft
     *
     * @return true si la eliminación fue exitosa
     */
    public static boolean deleteDraft(int idDraft) {

        if (idDraft <= 0) {
            return false;
        }

        String sql = "DELETE FROM aud_reservationdrafts "
                + "WHERE id_draft = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idDraft);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar draft "
                    + "de auditorio: "
                    + e.getMessage()
            );

            return false;
        }
    }
    
    /**
     * Obtiene un draft de auditorio completo por ID.
     *
     * @param idDraft identificador del draft
     *
     * @return draft encontrado o null
     */
    public static AuditoriumDraftRequest getDraftById(int idDraft) {

        if (idDraft <= 0) {
            return null;
        }

        String draftSql = "SELECT d.id_draft, d.id_client, "
                + "d.id_section, d.reservation_date, "
                + "ad.id_auditorium_draft, ad.event_name, "
                + "ad.attendees_count, ad.observations "
                + "FROM aud_reservationdrafts d "
                + "INNER JOIN aud_auditoriumdrafts ad "
                + "ON d.id_draft = ad.id_draft "
                + "WHERE d.id_draft = ? "
                + "AND d.expires_at > NOW()";

        String equipmentSql = "SELECT id_equipment, quantity "
                + "FROM aud_rdxe "
                + "WHERE id_draft = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement psDraft =
                conn.prepareStatement(draftSql);
                PreparedStatement psEquipment =
                conn.prepareStatement(equipmentSql)
        ) {

            psDraft.setInt(1, idDraft);

            try (
                    ResultSet rs = psDraft.executeQuery()
            ) {

                if (rs.next()) {

                    Reservation reservation = new Reservation();

                    reservation.setIdSection(
                            rs.getInt("id_section")
                    );

                    reservation.setReservationDate(
                            rs.getDate("reservation_date")
                    );

                    AuditoriumDraft auditoriumDraft =
                            new AuditoriumDraft();

                    auditoriumDraft.setIdAuditoriumDraft(
                            rs.getInt("id_auditorium_draft")
                    );

                    auditoriumDraft.setIdDraft(
                            rs.getInt("id_draft")
                    );

                    auditoriumDraft.setEventName(
                            rs.getString("event_name")
                    );

                    auditoriumDraft.setAttendeesCount(
                            rs.getInt("attendees_count")
                    );

                    auditoriumDraft.setObservations(
                            rs.getString("observations")
                    );

                    List<RXE> equipmentList =
                            new java.util.ArrayList<>();

                    psEquipment.setInt(1, idDraft);

                    try (
                            ResultSet rsEquipment =
                            psEquipment.executeQuery()
                    ) {

                        while (rsEquipment.next()) {

                            RXE item = new RXE();

                            item.setIdEquipment(
                                    rsEquipment.getInt(
                                            "id_equipment"
                                    )
                            );

                            item.setQuantity(
                                    rsEquipment.getInt("quantity")
                            );

                            equipmentList.add(item);
                        }
                    }

                    AuditoriumDraftRequest request =
                            new AuditoriumDraftRequest();

                    request.setIdDraft(rs.getInt("id_draft"));
                    request.setIdClient(rs.getInt("id_client"));
                    request.setReservation(reservation);
                    request.setAuditoriumDraft(auditoriumDraft);
                    request.setEquipmentList(equipmentList);

                    return request;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener draft de "
                    + "auditorio por ID: "
                    + e.getMessage()
            );
        }

        return null;
    }
    
    /**
     * Obtiene un draft por fecha y sección.
     *
     * @param reservationDate fecha de la reserva
     * @param idSection identificador de la sección
     *
     * @return draft encontrado o null
     */    
    public static AuditoriumDraftRequest getDraftBySectionAndDate(
            Date reservationDate,
            int idSection
    ) {

        String draftSql = "SELECT d.id_draft, d.id_client, "
                + "d.id_section, d.reservation_date, "
                + "ad.id_auditorium_draft, ad.event_name, "
                + "ad.attendees_count, ad.observations "
                + "FROM aud_reservationdrafts d "
                + "INNER JOIN aud_auditoriumdrafts ad "
                + "ON d.id_draft = ad.id_draft "
                + "WHERE d.reservation_date = ? "
                + "AND d.id_section = ? "
                + "AND d.expires_at > NOW() "
                + "LIMIT 1";

        String equipmentSql = "SELECT id_equipment, quantity "
                + "FROM aud_rdxe "
                + "WHERE id_draft = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement psDraft =
                conn.prepareStatement(draftSql);
                PreparedStatement psEquipment =
                conn.prepareStatement(equipmentSql)
        ) {

            psDraft.setDate(1, reservationDate);
            psDraft.setInt(2, idSection);

            try (
                    ResultSet rs = psDraft.executeQuery()
            ) {

                if (rs.next()) {

                    Reservation reservation = new Reservation();

                    reservation.setIdSection(
                            rs.getInt("id_section")
                    );

                    reservation.setReservationDate(
                            rs.getDate("reservation_date")
                    );

                    AuditoriumDraft auditoriumDraft =
                            new AuditoriumDraft();

                    auditoriumDraft.setIdAuditoriumDraft(
                            rs.getInt("id_auditorium_draft")
                    );

                    auditoriumDraft.setIdDraft(
                            rs.getInt("id_draft")
                    );

                    auditoriumDraft.setEventName(
                            rs.getString("event_name")
                    );

                    auditoriumDraft.setAttendeesCount(
                            rs.getInt("attendees_count")
                    );

                    auditoriumDraft.setObservations(
                            rs.getString("observations")
                    );

                    List<RXE> equipmentList =
                            new java.util.ArrayList<>();

                    psEquipment.setInt(
                            1,
                            rs.getInt("id_draft")
                    );

                    try (
                            ResultSet rsEquipment =
                            psEquipment.executeQuery()
                    ) {

                        while (rsEquipment.next()) {

                            RXE item = new RXE();

                            item.setIdEquipment(
                                    rsEquipment.getInt(
                                            "id_equipment"
                                    )
                            );

                            item.setQuantity(
                                    rsEquipment.getInt("quantity")
                            );

                            equipmentList.add(item);
                        }
                    }

                    AuditoriumDraftRequest request =
                            new AuditoriumDraftRequest();

                    request.setIdDraft(rs.getInt("id_draft"));
                    request.setIdClient(rs.getInt("id_client"));
                    request.setReservation(reservation);
                    request.setAuditoriumDraft(auditoriumDraft);
                    request.setEquipmentList(equipmentList);

                    return request;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener draft de auditorio "
                    + "por fecha y sección: "
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
    public static AuditoriumDraftRequest getDraftByClientId(int idClient) {

        if (idClient <= 0) {
            return null;
        }

        String draftSql = "SELECT d.id_draft, d.id_client, "
                + "d.id_section, d.reservation_date, "
                + "ad.id_auditorium_draft, ad.event_name, "
                + "ad.attendees_count, ad.observations "
                + "FROM aud_reservationdrafts d "
                + "INNER JOIN aud_auditoriumdrafts ad "
                + "ON d.id_draft = ad.id_draft "
                + "WHERE d.id_client = ? "
                + "AND d.expires_at > NOW() "
                + "ORDER BY d.created_at DESC "
                + "LIMIT 1";

        String equipmentSql = "SELECT id_equipment, quantity "
                + "FROM aud_rdxe "
                + "WHERE id_draft = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement psDraft =
                conn.prepareStatement(draftSql);
                PreparedStatement psEquipment =
                conn.prepareStatement(equipmentSql)
        ) {

            psDraft.setInt(1, idClient);

            try (
                    ResultSet rs = psDraft.executeQuery()
            ) {

                if (rs.next()) {

                    Reservation reservation = new Reservation();

                    reservation.setIdSection(
                            rs.getInt("id_section")
                    );

                    reservation.setReservationDate(
                            rs.getDate("reservation_date")
                    );

                    AuditoriumDraft auditoriumDraft =
                            new AuditoriumDraft();

                    auditoriumDraft.setIdAuditoriumDraft(
                            rs.getInt("id_auditorium_draft")
                    );

                    auditoriumDraft.setIdDraft(
                            rs.getInt("id_draft")
                    );

                    auditoriumDraft.setEventName(
                            rs.getString("event_name")
                    );

                    auditoriumDraft.setAttendeesCount(
                            rs.getInt("attendees_count")
                    );

                    auditoriumDraft.setObservations(
                            rs.getString("observations")
                    );

                    List<RXE> equipmentList =
                            new java.util.ArrayList<>();

                    psEquipment.setInt(
                            1,
                            rs.getInt("id_draft")
                    );

                    try (
                            ResultSet rsEquipment =
                            psEquipment.executeQuery()
                    ) {

                        while (rsEquipment.next()) {

                            RXE item = new RXE();

                            item.setIdEquipment(
                                    rsEquipment.getInt(
                                            "id_equipment"
                                    )
                            );

                            item.setQuantity(
                                    rsEquipment.getInt("quantity")
                            );

                            equipmentList.add(item);
                        }
                    }

                    AuditoriumDraftRequest request =
                            new AuditoriumDraftRequest();

                    request.setIdDraft(rs.getInt("id_draft"));
                    request.setIdClient(rs.getInt("id_client"));
                    request.setReservation(reservation);
                    request.setAuditoriumDraft(auditoriumDraft);
                    request.setEquipmentList(equipmentList);

                    return request;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener draft de "
                    + "auditorio por cliente: "
                    + e.getMessage()
            );
        }

        return null;
    }
    
    /**
     * Confirma un draft y lo convierte en reserva real.
     *
     * @param idDraft identificador del draft
     * @param idClient identificador del cliente
     *
     * @return true si la confirmación fue exitosa
     */
    public static boolean confirmDraft(int idDraft, int idClient) {

        AuditoriumDraftRequest draftRequest = getDraftById(idDraft);

        if (draftRequest == null) {
            return false;
        }

        if (draftRequest.getIdClient() != idClient) {
            return false;
        }

        Reservation reservation = draftRequest.getReservation();
        AuditoriumDraft auditoriumDraft =
                draftRequest.getAuditoriumDraft();
        List<RXE> equipmentList = draftRequest.getEquipmentList();

        if (reservation == null || auditoriumDraft == null) {
            return false;
        }

        if (auditoriumDraft.getAttendeesCount() > 200) {
            return false;
        }

        if (existsAuditoriumReservationByDateAndSection(
                reservation.getReservationDate(),
                reservation.getIdSection()
        )) {
            return false;
        }

        String insertReservationSql =
                "INSERT INTO aud_reservations "
                + "(id_section, reservation_date) "
                + "VALUES (?, ?)";

        String insertRXCSql = "INSERT INTO aud_rxc "
                + "(id_reservation, id_client) "
                + "VALUES (?, ?)";

        String insertAuditoriumReservationSql =
                "INSERT INTO aud_auditoriumreservations "
                + "(id_reservation, event_name, "
                + "attendees_count, observations) "
                + "VALUES (?, ?, ?, ?)";

        String insertRXESql = "INSERT INTO aud_rxe "
                + "(id_reservation, id_equipment, quantity) "
                + "VALUES (?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement psReservation =
                conn.prepareStatement(
                        insertReservationSql,
                        PreparedStatement.RETURN_GENERATED_KEYS
                );
                PreparedStatement psRXC =
                conn.prepareStatement(insertRXCSql);
                PreparedStatement psAuditoriumReservation =
                conn.prepareStatement(
                        insertAuditoriumReservationSql
                );
                PreparedStatement psRXE =
                conn.prepareStatement(insertRXESql)
        ) {

            conn.setAutoCommit(false);

            psReservation.setInt(1, reservation.getIdSection());
            psReservation.setDate(2, reservation.getReservationDate());
            psReservation.executeUpdate();

            int idReservation;

            try (
                    ResultSet generatedKeys =
                    psReservation.getGeneratedKeys()
            ) {

                if (generatedKeys.next()) {
                    idReservation = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            psRXC.setInt(1, idReservation);
            psRXC.setInt(2, idClient);
            psRXC.executeUpdate();

            psAuditoriumReservation.setInt(1, idReservation);
            psAuditoriumReservation.setString(
                    2,
                    auditoriumDraft.getEventName()
            );
            psAuditoriumReservation.setInt(
                    3,
                    auditoriumDraft.getAttendeesCount()
            );
            psAuditoriumReservation.setString(
                    4,
                    auditoriumDraft.getObservations()
            );
            psAuditoriumReservation.executeUpdate();

            if (equipmentList != null && !equipmentList.isEmpty()) {

                for (RXE item : equipmentList) {

                    int availableQuantity =
                            getEquipmentAvailableQuantity(
                                    conn,
                                    item.getIdEquipment()
                            );

                    if (
                            item.getIdEquipment() <= 0
                            || item.getQuantity() <= 0
                            || availableQuantity < item.getQuantity()
                    ) {
                        conn.rollback();
                        return false;
                    }

                    psRXE.setInt(1, idReservation);
                    psRXE.setInt(2, item.getIdEquipment());
                    psRXE.setInt(3, item.getQuantity());
                    psRXE.executeUpdate();
                }
            }

            conn.commit();

            deleteDraft(idDraft);

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al confirmar draft "
                    + "de auditorio: "
                    + e.getMessage()
            );

            return false;
        }
    }
    
    /**
     * Obtiene los bloques de drafts vencidos.
     *
     * @return lista de bloques vencidos
     */    
    public static List<CalendarBlock> getExpiredDraftBlocks() {

        List<CalendarBlock> blocks = new java.util.ArrayList<>();

        String sql = "SELECT d.reservation_date, d.id_section "
                + "FROM aud_reservationdrafts d "
                + "INNER JOIN aud_auditoriumdrafts ad "
                + "ON d.id_draft = ad.id_draft "
                + "WHERE d.expires_at <= NOW()";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                CalendarBlock block = new CalendarBlock();

                block.setReservationDate(
                        rs.getDate("reservation_date")
                );

                block.setIdSection(
                        rs.getInt("id_section")
                );

                block.setStatus("EXPIRED_AUDITORIUM_DRAFT");

                blocks.add(block);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener drafts vencidos "
                    + "de auditorio: "
                    + e.getMessage()
            );
        }

        return blocks;
    }
    
    /**
     * Elimina los drafts vencidos de auditorio.
     *
     * @return cantidad de drafts eliminados
     */    
    public static int cleanupExpiredDraftsAndCount() {

        String sql = "DELETE d "
                + "FROM aud_reservationdrafts d "
                + "INNER JOIN aud_auditoriumdrafts ad "
                + "ON d.id_draft = ad.id_draft "
                + "WHERE d.expires_at <= NOW()";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            int deleted = ps.executeUpdate();

            System.out.println(
                    "Drafts de auditorio expirados "
                    + "eliminados: "
                    + deleted
            );

            return deleted;

        } catch (SQLException e) {

            System.out.println(
                    "Error al limpiar drafts vencidos "
                    + "de auditorio: "
                    + e.getMessage()
            );

            return 0;
        }
    }
}
