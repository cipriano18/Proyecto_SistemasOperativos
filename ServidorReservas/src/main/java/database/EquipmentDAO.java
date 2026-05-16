package database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Equipment;

/**
 * Gestiona las operaciones relacionadas con equipos.
 */
public class EquipmentDAO {

    /**
     * Obtiene todos los equipos registrados.
     *
     * @return lista de equipos
     */
    public static List<Equipment> getAllEquipment() {

        List<Equipment> list = new ArrayList<>();

        String sql = "SELECT id_equipment, name, "
                + "available_quantity "
                + "FROM AUD_Equipment "
                + "ORDER BY id_equipment ASC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new Equipment(
                        rs.getInt("id_equipment"),
                        rs.getString("name"),
                        rs.getInt("available_quantity")
                ));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener equipos: "
                    + e.getMessage()
            );
        }

        return list;
    }

    /**
     * Obtiene un equipo mediante su nombre.
     *
     * @param name nombre del equipo
     *
     * @return equipo encontrado o null
     */
    public static Equipment getEquipmentByName(String name) {

        String sql = "SELECT id_equipment, name, "
                + "available_quantity "
                + "FROM AUD_Equipment "
                + "WHERE name = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Equipment(
                        rs.getInt("id_equipment"),
                        rs.getString("name"),
                        rs.getInt("available_quantity")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener equipo "
                    + "por nombre: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Obtiene un equipo mediante su identificador.
     *
     * @param idEquipment identificador del equipo
     *
     * @return equipo encontrado o null
     */
    public static Equipment getEquipmentById(int idEquipment) {

        String sql = "SELECT id_equipment, name, "
                + "available_quantity "
                + "FROM AUD_Equipment "
                + "WHERE id_equipment = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idEquipment);

            try (
                    ResultSet rs = ps.executeQuery()
            ) {

                if (rs.next()) {

                    Equipment equipment = new Equipment();

                    equipment.setIdEquipment(
                            rs.getInt("id_equipment")
                    );

                    equipment.setName(
                            rs.getString("name")
                    );

                    equipment.setTotalQuantity(
                            rs.getInt("available_quantity")
                    );

                    return equipment;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener equipo "
                    + "por id: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene los equipos disponibles por fecha y sección.
     *
     * @param reservationDate fecha de la reserva
     * @param idSection identificador de la sección
     *
     * @return lista de equipos disponibles
     */
    public static List<Equipment>
            getAvailableEquipmentByDateAndSection(
                    Date reservationDate,
                    int idSection
            ) {

        List<Equipment> equipmentList =
                new ArrayList<>();

        String sql = "SELECT e.id_equipment, e.name, "
                + "(e.available_quantity "
                + "- COALESCE(res.reserved_quantity, 0)) "
                + "AS total_available "
                + "FROM AUD_Equipment e "
                + "LEFT JOIN ( "
                + "SELECT rxe.id_equipment, "
                + "SUM(rxe.quantity) AS reserved_quantity "
                + "FROM AUD_RXE rxe "
                + "INNER JOIN AUD_Reservations r "
                + "ON rxe.id_reservation = r.id_reservation "
                + "WHERE r.reservation_date = ? "
                + "AND r.id_section = ? "
                + "GROUP BY rxe.id_equipment "
                + ") res "
                + "ON e.id_equipment = res.id_equipment "
                + "WHERE (e.available_quantity "
                + "- COALESCE(res.reserved_quantity, 0)) > 0 "
                + "ORDER BY e.name";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setDate(1, reservationDate);
            ps.setInt(2, idSection);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Equipment equipment = new Equipment();

                equipment.setIdEquipment(
                        rs.getInt("id_equipment")
                );

                equipment.setName(
                        rs.getString("name")
                );

                equipment.setTotalQuantity(
                        rs.getInt("total_available")
                );

                equipmentList.add(equipment);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener equipos "
                    + "disponibles: "
                    + e.getMessage()
            );
        }

        return equipmentList;
    }

    /**
     * Crea un nuevo equipo.
     *
     * @param equipment equipo a registrar
     *
     * @return true si el equipo fue creado
     */
    public static boolean createEquipment(
            Equipment equipment
    ) {

        String sql = "INSERT INTO AUD_Equipment "
                + "(name, available_quantity) "
                + "VALUES (?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, equipment.getName());

            ps.setInt(
                    2,
                    equipment.getTotalQuantity()
            );

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al crear equipo: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Actualiza la información de un equipo.
     *
     * @param equipment equipo actualizado
     *
     * @return true si la actualización fue exitosa
     */
    public static boolean updateEquipment(
            Equipment equipment
    ) {

        String sql = "UPDATE AUD_Equipment "
                + "SET name = ?, "
                + "available_quantity = ? "
                + "WHERE id_equipment = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, equipment.getName());

            ps.setInt(
                    2,
                    equipment.getTotalQuantity()
            );

            ps.setInt(
                    3,
                    equipment.getIdEquipment()
            );

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar equipo: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Elimina un equipo de la base de datos.
     *
     * @param idEquipment identificador del equipo
     *
     * @return true si el equipo fue eliminado
     */
    public static boolean deleteEquipment(int idEquipment) {

        String sql = "DELETE FROM AUD_Equipment "
                + "WHERE id_equipment = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idEquipment);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}
