package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;

/**
 * Gestiona las operaciones relacionadas con usuarios.
 */
public class UserDAO {

    /**
     * Obtiene un usuario mediante su identificador.
     *
     * @param idUser identificador del usuario
     * @return usuario encontrado o null
     */
    public static User getUserById(int idUser) {

        String sql = "SELECT id_user, id_role, username, password "
                + "FROM aud_users "
                + "WHERE id_user = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idUser);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new User(
                        rs.getInt("id_user"),
                        rs.getInt("id_role"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener usuario por ID: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Inserta un nuevo usuario.
     *
     * @param user usuario a registrar
     * @return true si el usuario fue creado correctamente, false en caso contrario
     */
    public static boolean insertUser(User user) {

        String sql = "INSERT INTO aud_users "
                + "(id_role, username, password) "
                + "VALUES (?, ?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, user.getIdRole());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Obtiene un usuario mediante su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return usuario encontrado o null
     */
    public static User getUserByUsername(String username) {

        String sql = "SELECT id_user, id_role, username, password "
                + "FROM aud_users "
                + "WHERE username = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new User(
                        rs.getInt("id_user"),
                        rs.getInt("id_role"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener usuario por username: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Obtiene todos los usuarios administradores y super administradores.
     *
     * @return lista de usuarios con rol administrador o super administrador
     */
    public static List<User> getAllAdminUsers() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT id_user, id_role, username, password "
                + "FROM aud_users "
                + "WHERE id_role = 2 "
                + "OR id_role = 1 "
                + "ORDER BY id_user ASC";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                User user = new User();

                user.setIdUser(rs.getInt("id_user"));
                user.setIdRole(rs.getInt("id_role"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));

                users.add(user);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener usuarios administradores: "
                    + e.getMessage()
            );
        }

        return users;
    }

    /**
     * Actualiza el nombre de usuario y la contraseña de un usuario.
     *
     * @param user usuario con los datos actualizados
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public static boolean updateUser(User user) {

        String sql = "UPDATE aud_users "
                + "SET username = ?, password = ? "
                + "WHERE id_user = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getIdUser());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Actualiza el rol de un usuario.
     *
     * @param idUser identificador del usuario
     * @param newRole nuevo rol del usuario
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public static boolean updateUserRole(int idUser, int newRole) {

        String sql = "UPDATE aud_users "
                + "SET id_role = ? "
                + "WHERE id_user = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, newRole);
            ps.setInt(2, idUser);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar rol de usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Valida el inicio de sesión de un usuario.
     *
     * @param username nombre de usuario
     * @param password contraseña del usuario
     * @return usuario autenticado o null si las credenciales son incorrectas
     */
    public static User validateLogin(
            String username,
            String password
    ) {

        String sql = "SELECT id_user, username, password, id_role "
                + "FROM aud_users "
                + "WHERE username = ? "
                + "AND password = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setIdUser(rs.getInt("id_user"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setIdRole(rs.getInt("id_role"));

                return user;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error en login: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param idUser identificador del usuario
     * @return true si el usuario fue eliminado, false en caso contrario
     */
    public static boolean deleteUser(int idUser) {

        String sql = "DELETE FROM aud_users "
                + "WHERE id_user = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idUser);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }
}