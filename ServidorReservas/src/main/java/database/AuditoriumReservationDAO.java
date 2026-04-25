package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.CalendarBlock;

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

}
