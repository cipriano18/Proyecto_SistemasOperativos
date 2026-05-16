package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Role;

/**
 * Gestiona las operaciones relacionadas con roles.
 */
public class RoleDAO {

    /**
     * Obtiene todos los roles registrados.
     *
     * @return lista de roles
     */
    public static List<Role> getAllRoles() {

        List<Role> roles = new ArrayList<>();

        String sql = "SELECT id_role, name "
                + "FROM AUD_Roles "
                + "ORDER BY id_role ASC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                roles.add(new Role(
                        rs.getInt("id_role"),
                        rs.getString("name")
                ));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener roles: "
                    + e.getMessage()
            );
        }

        return roles;
    }

    /**
     * Obtiene un rol mediante su identificador.
     *
     * @param idRole identificador del rol
     *
     * @return rol encontrado o null
     */
    public static Role getRoleById(int idRole) {

        String sql = "SELECT id_role, name "
                + "FROM AUD_Roles "
                + "WHERE id_role = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idRole);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Role(
                        rs.getInt("id_role"),
                        rs.getString("name")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener rol: "
                    + e.getMessage()
            );
        }

        return null;
    }
}