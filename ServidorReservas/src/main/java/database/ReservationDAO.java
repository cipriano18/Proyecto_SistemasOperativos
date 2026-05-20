package database;

import dto.EquipmentReservationRequest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import model.CalendarBlock;
import model.Reservation;
import model.RXE;

/**
 * Gestiona las operaciones relacionadas con reservaciones.
 */
public class ReservationDAO {

    private static final Map<Integer, Semaphore>
            equipmentSemaphores =
            new ConcurrentHashMap<>();

    /**
     * Obtiene o crea un semáforo para un equipo.
     *
     * @param idEquipment identificador del equipo
     *
     * @return semáforo asociado al equipo
     */
    private static Semaphore getEquipmentSemaphore(
            int idEquipment
    ) {

        return equipmentSemaphores.computeIfAbsent(
                idEquipment,
                key -> new Semaphore(1, true)
        );
    }

    /**
     * Obtiene los bloques reservados por mes y año.
     *
     * @param month mes de consulta
     * @param year año de consulta
     *
     * @return lista de bloques reservados
     */
    public static List<CalendarBlock> getReservedBlocksByMonth(
            int month,
            int year
    ) {

        List<CalendarBlock> blocks =
                new ArrayList<>();

        String sql =
                "SELECT r.reservation_date, "
                + "r.id_section "
                + "FROM aud_reservations r "
                + "WHERE MONTH(r.reservation_date) = ? "
                + "AND YEAR(r.reservation_date) = ? "
                + "GROUP BY r.reservation_date, "
                + "r.id_section "
                + "HAVING NOT EXISTS ( "
                + "SELECT 1 "
                + "FROM aud_equipment e "
                + "WHERE ( "
                + "e.available_quantity "
                + "- COALESCE(( "
                + "SELECT SUM(rxe.quantity) "
                + "FROM aud_rxe rxe "
                + "INNER JOIN aud_reservations r2 "
                + "ON rxe.id_reservation = "
                + "r2.id_reservation "
                + "WHERE rxe.id_equipment = "
                + "e.id_equipment "
                + "AND r2.reservation_date = "
                + "r.reservation_date "
                + "AND r2.id_section = "
                + "r.id_section "
                + "), 0) "
                + ") > 0 "
                + ") "
                + "ORDER BY r.reservation_date, "
                + "r.id_section";

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

                block.setStatus("RESERVED");

                blocks.add(block);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener reservaciones "
                    + "del calendario: "
                    + e.getMessage()
            );
        }

        return blocks;
    }

    /**
     * Crea una reservación de equipos.
     *
     * @param reservation datos de la reservación
     * @param idClient identificador del cliente
     * @param equipmentList lista de equipos
     *
     * @return true si la reservación fue creada
     */
    public static boolean createEquipmentReservation(
            Reservation reservation,
            int idClient,
            List<RXE> equipmentList
    ) {

        String insertReservationSql =
                "INSERT INTO aud_reservations "
                + "(id_section, reservation_date) "
                + "VALUES (?, ?)";

        String insertRXCSql =
                "INSERT INTO aud_rxc "
                + "(id_reservation, id_client) "
                + "VALUES (?, ?)";

        String insertRXESql =
                "INSERT INTO aud_rxe "
                + "(id_reservation, id_equipment, "
                + "quantity) "
                + "VALUES (?, ?, ?)";

        List<Semaphore> acquiredSemaphores =
                new ArrayList<>();

        try {

            equipmentList.sort(
                    Comparator.comparingInt(
                            RXE::getIdEquipment
                    )
            );

            for (RXE item : equipmentList) {

                Semaphore semaphore =
                        getEquipmentSemaphore(
                                item.getIdEquipment()
                        );

                semaphore.acquire();

                acquiredSemaphores.add(semaphore);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return false;
        }

        try (
                Connection conn = DBConnection.getConnection();

                PreparedStatement psInsertReservation =
                conn.prepareStatement(
                        insertReservationSql,
                        PreparedStatement
                                .RETURN_GENERATED_KEYS
                );

                PreparedStatement psInsertRXC =
                conn.prepareStatement(insertRXCSql);

                PreparedStatement psInsertRXE =
                conn.prepareStatement(insertRXESql)
        ) {

            conn.setAutoCommit(false);

            for (RXE item : equipmentList) {

                int availableQuantity =
                        getAvailableEquipmentQuantity(
                                conn,
                                item.getIdEquipment(),
                                reservation.getReservationDate(),
                                reservation.getIdSection()
                        );

                if (
                        availableQuantity
                        < item.getQuantity()
                ) {

                    conn.rollback();

                    return false;
                }
            }

            psInsertReservation.setInt(
                    1,
                    reservation.getIdSection()
            );

            psInsertReservation.setDate(
                    2,
                    reservation.getReservationDate()
            );

            psInsertReservation.executeUpdate();

            ResultSet generatedKeys =
                    psInsertReservation.getGeneratedKeys();

            int idReservation;

            if (generatedKeys.next()) {

                idReservation =
                        generatedKeys.getInt(1);

            } else {

                conn.rollback();

                return false;
            }

            psInsertRXC.setInt(1, idReservation);
            psInsertRXC.setInt(2, idClient);

            psInsertRXC.executeUpdate();

            for (RXE item : equipmentList) {

                psInsertRXE.setInt(1, idReservation);

                psInsertRXE.setInt(
                        2,
                        item.getIdEquipment()
                );

                psInsertRXE.setInt(
                        3,
                        item.getQuantity()
                );

                psInsertRXE.executeUpdate();
            }

            conn.commit();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al crear reservación: "
                    + e.getMessage()
            );

            return false;

        } finally {

            for (
                    Semaphore semaphore
                    : acquiredSemaphores
            ) {

                semaphore.release();
            }
        }
    }

    /**
     * Obtiene una reservación completa por ID.
     *
     * @param idReservation identificador de la reserva
     *
     * @return reservación encontrada o null
     */
    public static EquipmentReservationRequest
            getEquipmentReservationById(
                    int idReservation
            ) {

        String reservationSql =
                "SELECT r.id_reservation, "
                + "r.id_section, "
                + "r.reservation_date, "
                + "rxc.id_client, "
                + "CONCAT(c.f_name, ' ', "
                + "c.f_surname) AS client_name "
                + "FROM aud_reservations r "
                + "INNER JOIN aud_rxc rxc "
                + "ON r.id_reservation = "
                + "rxc.id_reservation "
                + "INNER JOIN aud_clients c "
                + "ON rxc.id_client = c.id_client "
                + "WHERE r.id_reservation = ?";

        String equipmentSql =
                "SELECT id_rxe, id_reservation, "
                + "id_equipment, quantity "
                + "FROM aud_rxe "
                + "WHERE id_reservation = ?";

        try (
                Connection conn = DBConnection.getConnection();

                PreparedStatement psReservation =
                conn.prepareStatement(reservationSql);

                PreparedStatement psEquipment =
                conn.prepareStatement(equipmentSql)
        ) {

            psReservation.setInt(1, idReservation);

            try (
                    ResultSet rs =
                    psReservation.executeQuery()
            ) {

                if (rs.next()) {

                    Reservation reservation =
                            new Reservation(
                                    rs.getInt(
                                            "id_reservation"
                                    ),
                                    rs.getDate(
                                            "reservation_date"
                                    ),
                                    rs.getInt(
                                            "id_section"
                                    )
                            );

                    int idClient =
                            rs.getInt("id_client");

                    List<RXE> equipmentList =
                            new ArrayList<>();

                    psEquipment.setInt(
                            1,
                            idReservation
                    );

                    try (
                            ResultSet rsEquipment =
                            psEquipment.executeQuery()
                    ) {

                        while (rsEquipment.next()) {

                            RXE item =
                                    new RXE(
                                            rsEquipment.getInt(
                                                    "id_rxe"
                                            ),
                                            rsEquipment.getInt(
                                                    "id_reservation"
                                            ),
                                            rsEquipment.getInt(
                                                    "id_equipment"
                                            ),
                                            rsEquipment.getInt(
                                                    "quantity"
                                            )
                                    );

                            equipmentList.add(item);
                        }
                    }

                    EquipmentReservationRequest request =
                            new EquipmentReservationRequest(
                                    reservation,
                                    idClient,
                                    equipmentList
                            );

                    request.setClientName(
                            rs.getString("client_name")
                    );

                    return request;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener reservación "
                    + "por id: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Obtiene las reservaciones de un cliente.
     *
     * @param idClient identificador del cliente
     *
     * @return lista de reservaciones
     */
    public static List<EquipmentReservationRequest>
            getEquipmentReservationsByClientId(
                    int idClient
            ) {

        List<EquipmentReservationRequest> reservations =
                new ArrayList<>();

        String sql =
                "SELECT r.id_reservation "
                + "FROM aud_reservations r "
                + "INNER JOIN aud_rxc rxc "
                + "ON r.id_reservation = "
                + "rxc.id_reservation "
                + "LEFT JOIN "
                + "aud_auditoriumreservations ar "
                + "ON r.id_reservation = "
                + "ar.id_reservation "
                + "WHERE rxc.id_client = ? "
                + "AND ar.id_reservation IS NULL "
                + "AND r.reservation_date >= CURDATE() "
                + "ORDER BY r.reservation_date ASC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idClient);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                while (rs.next()) {

                    EquipmentReservationRequest request =
                            getEquipmentReservationById(
                                    rs.getInt(
                                            "id_reservation"
                                    )
                            );

                    if (request != null) {

                        reservations.add(request);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener reservaciones "
                    + "por cliente: "
                    + e.getMessage()
            );
        }

        return reservations;
    }

    /**
     * Obtiene las reservaciones por mes y año.
     *
     * @param month mes de consulta
     * @param year año de consulta
     *
     * @return lista de reservaciones
     */
    public static List<EquipmentReservationRequest>
            getEquipmentReservationsByMonth(
                    int month,
                    int year
            ) {

        List<EquipmentReservationRequest> reservations =
                new ArrayList<>();

        String sql =
                "SELECT r.id_reservation "
                + "FROM aud_reservations r "
                + "INNER JOIN aud_rxc rxc "
                + "ON r.id_reservation = "
                + "rxc.id_reservation "
                + "LEFT JOIN "
                + "aud_auditoriumreservations ar "
                + "ON r.id_reservation = "
                + "ar.id_reservation "
                + "WHERE MONTH(r.reservation_date) = ? "
                + "AND YEAR(r.reservation_date) = ? "
                + "AND r.reservation_date >= CURDATE() "
                + "AND ar.id_reservation IS NULL "
                + "ORDER BY r.reservation_date ASC, "
                + "r.id_section ASC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                while (rs.next()) {

                    EquipmentReservationRequest request =
                            getEquipmentReservationById(
                                    rs.getInt(
                                            "id_reservation"
                                    )
                            );

                    if (request != null) {

                        reservations.add(request);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener reservaciones "
                    + "de equipos por mes y año: "
                    + e.getMessage()
            );
        }

        return reservations;
    }

    /**
     * Obtiene la cantidad disponible de un equipo.
     *
     * @param conn conexión activa
     * @param idEquipment identificador del equipo
     * @param reservationDate fecha de reserva
     * @param idSection identificador de la sección
     *
     * @return cantidad disponible
     */
    private static int getAvailableEquipmentQuantity(
            Connection conn,
            int idEquipment,
            Date reservationDate,
            int idSection
    ) {

        String sql =
                "SELECT e.available_quantity "
                + "- COALESCE(( "
                + "SELECT SUM(rxe.quantity) "
                + "FROM aud_rxe rxe "
                + "INNER JOIN aud_reservations r "
                + "ON rxe.id_reservation = "
                + "r.id_reservation "
                + "WHERE rxe.id_equipment = ? "
                + "AND r.reservation_date = ? "
                + "AND r.id_section = ? "
                + "), 0) AS available_quantity "
                + "FROM aud_equipment e "
                + "WHERE e.id_equipment = ?";

        try (
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idEquipment);
            ps.setDate(2, reservationDate);
            ps.setInt(3, idSection);
            ps.setInt(4, idEquipment);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return rs.getInt(
                        "available_quantity"
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener disponibilidad "
                    + "del equipo: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Valida si una reservación pertenece al cliente.
     *
     * @param conn conexión activa
     * @param idReservation identificador reserva
     * @param idClient identificador cliente
     *
     * @return true si pertenece al cliente
     */
    private static boolean reservationBelongsToClient(
            Connection conn,
            int idReservation,
            int idClient
    ) {

        String sql =
                "SELECT COUNT(*) AS total "
                + "FROM aud_rxc "
                + "WHERE id_reservation = ? "
                + "AND id_client = ?";

        try (
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idReservation);
            ps.setInt(2, idClient);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt("total") > 0;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al validar pertenencia "
                    + "de reservación: "
                    + e.getMessage()
            );
        }

        return false;
    }

    /**
     * Obtiene disponibilidad excluyendo una reservación.
     *
     * @param conn conexión activa
     * @param idEquipment identificador equipo
     * @param reservationDate fecha reserva
     * @param idSection identificador sección
     * @param idReservation identificador reserva
     *
     * @return cantidad disponible
     */
    private static int
            getAvailableEquipmentQuantityExcludingReservation(
                    Connection conn,
                    int idEquipment,
                    Date reservationDate,
                    int idSection,
                    int idReservation
            ) {

        String sql =
                "SELECT e.available_quantity "
                + "- COALESCE(( "
                + "SELECT SUM(rxe.quantity) "
                + "FROM aud_rxe rxe "
                + "INNER JOIN aud_reservations r "
                + "ON rxe.id_reservation = "
                + "r.id_reservation "
                + "WHERE rxe.id_equipment = ? "
                + "AND r.reservation_date = ? "
                + "AND r.id_section = ? "
                + "AND r.id_reservation <> ? "
                + "), 0) AS available_quantity "
                + "FROM aud_equipment e "
                + "WHERE e.id_equipment = ?";

        try (
                PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idEquipment);
            ps.setDate(2, reservationDate);
            ps.setInt(3, idSection);
            ps.setInt(4, idReservation);
            ps.setInt(5, idEquipment);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "available_quantity"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener disponibilidad "
                    + "excluyendo reservación: "
                    + e.getMessage()
            );
        }

        return 0;
    }

    /**
     * Actualiza una reservación existente.
     *
     * @param reservation datos de la reserva
     * @param idClient identificador cliente
     * @param equipmentList lista de equipos
     *
     * @return true si la actualización fue exitosa
     */
    public static boolean updateEquipmentReservation(
            Reservation reservation,
            int idClient,
            List<RXE> equipmentList
    ) {

        String updateReservationSql =
                "UPDATE aud_reservations "
                + "SET id_section = ?, "
                + "reservation_date = ? "
                + "WHERE id_reservation = ?";

        String deleteRXESql =
                "DELETE FROM aud_rxe "
                + "WHERE id_reservation = ?";

        String insertRXESql =
                "INSERT INTO aud_rxe "
                + "(id_reservation, id_equipment, "
                + "quantity) "
                + "VALUES (?, ?, ?)";

        List<Semaphore> acquiredSemaphores =
                new ArrayList<>();

        try {

            equipmentList.sort(
                    Comparator.comparingInt(
                            RXE::getIdEquipment
                    )
            );

            for (RXE item : equipmentList) {

                Semaphore semaphore =
                        getEquipmentSemaphore(
                                item.getIdEquipment()
                        );

                semaphore.acquire();

                acquiredSemaphores.add(semaphore);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            return false;
        }

        try (
                Connection conn = DBConnection.getConnection();

                PreparedStatement psUpdateReservation =
                conn.prepareStatement(updateReservationSql);

                PreparedStatement psDeleteRXE =
                conn.prepareStatement(deleteRXESql);

                PreparedStatement psInsertRXE =
                conn.prepareStatement(insertRXESql)
        ) {

            conn.setAutoCommit(false);

            if (
                    !reservationBelongsToClient(
                            conn,
                            reservation.getIdReservation(),
                            idClient
                    )
            ) {

                conn.rollback();

                return false;
            }

            for (RXE item : equipmentList) {

                int availableQuantity =
                        getAvailableEquipmentQuantityExcludingReservation(
                                conn,
                                item.getIdEquipment(),
                                reservation.getReservationDate(),
                                reservation.getIdSection(),
                                reservation.getIdReservation()
                        );

                if (
                        availableQuantity
                        < item.getQuantity()
                ) {

                    conn.rollback();

                    return false;
                }
            }

            psUpdateReservation.setInt(
                    1,
                    reservation.getIdSection()
            );

            psUpdateReservation.setDate(
                    2,
                    reservation.getReservationDate()
            );

            psUpdateReservation.setInt(
                    3,
                    reservation.getIdReservation()
            );

            psUpdateReservation.executeUpdate();

            psDeleteRXE.setInt(
                    1,
                    reservation.getIdReservation()
            );

            psDeleteRXE.executeUpdate();

            for (RXE item : equipmentList) {

                psInsertRXE.setInt(
                        1,
                        reservation.getIdReservation()
                );

                psInsertRXE.setInt(
                        2,
                        item.getIdEquipment()
                );

                psInsertRXE.setInt(
                        3,
                        item.getQuantity()
                );

                psInsertRXE.executeUpdate();
            }

            conn.commit();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar "
                    + "reservación: "
                    + e.getMessage()
            );

            return false;

        } finally {

            for (
                    Semaphore semaphore
                    : acquiredSemaphores
            ) {

                semaphore.release();
            }
        }
    }

    /**
     * Elimina todas las reservaciones de un cliente.
     *
     * @param idClient identificador del cliente
     *
     * @return true si las reservas fueron eliminadas
     */
    public static boolean deleteReservationsByClientId(
            int idClient
    ) {

        String deleteReservationsSql =
                "DELETE FROM aud_reservations "
                + "WHERE id_reservation IN ( "
                + "SELECT id_reservation "
                + "FROM aud_rxc "
                + "WHERE id_client = ? "
                + ")";

        try (
                Connection conn = DBConnection.getConnection()
        ) {

            conn.setAutoCommit(false);

            try (
                    PreparedStatement ps =
                    conn.prepareStatement(
                            deleteReservationsSql
                    )
            ) {

                ps.setInt(1, idClient);

                ps.executeUpdate();
            }

            conn.commit();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar reservas: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Elimina una reservación específica.
     *
     * @param idReservation identificador reserva
     * @param idClient identificador cliente
     *
     * @return true si la reserva fue eliminada
     */
    public static boolean deleteReservationById(
            int idReservation,
            int idClient
    ) {

        String deleteSql =
                "DELETE FROM aud_reservations "
                + "WHERE id_reservation = ?";

        try (
                Connection conn = DBConnection.getConnection();

                PreparedStatement psDelete =
                conn.prepareStatement(deleteSql)
        ) {

            conn.setAutoCommit(false);

            if (
                    !reservationBelongsToClient(
                            conn,
                            idReservation,
                            idClient
                    )
            ) {

                conn.rollback();

                return false;
            }

            psDelete.setInt(1, idReservation);

            int rows = psDelete.executeUpdate();

            conn.commit();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar reservación "
                    + "por id: "
                    + e.getMessage()
            );

            return false;
        }
    }
}