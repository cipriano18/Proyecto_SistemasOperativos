package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Contact;

/**
 * Gestiona las operaciones relacionadas con contactos.
 */
public class ContactDAO {

    /**
     * Inserta un contacto y retorna el ID generado.
     *
     * @param contact contacto a insertar
     *
     * @return ID generado o -1 si ocurre un error
     */
    public static int insertContact(Contact contact) {

        String sql = "INSERT INTO aud_contacts "
                + "(type, contact_value) "
                + "VALUES (?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            ps.setString(1, contact.getType());
            ps.setString(2, contact.getContactValue());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar contacto: "
                    + e.getMessage()
            );
        }

        return -1;
    }

    /**
     * Vincula un contacto con un administrador.
     *
     * @param idAdmin identificador del administrador
     * @param idContact identificador del contacto
     *
     * @return true si la relación fue creada
     */
    public static boolean insertCXA(
            int idAdmin,
            int idContact
    ) {

        String sql = "INSERT INTO aud_cxa "
                + "(id_admin, id_contact) "
                + "VALUES (?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idAdmin);
            ps.setInt(2, idContact);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar CXA: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Vincula un contacto con un cliente.
     *
     * @param idClient identificador del cliente
     * @param idContact identificador del contacto
     *
     * @return true si la relación fue creada
     */
    public static boolean insertCXC(
            int idClient,
            int idContact
    ) {

        String sql = "INSERT INTO aud_cxc "
                + "(id_client, id_contact) "
                + "VALUES (?, ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idClient);
            ps.setInt(2, idContact);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar CXC: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Actualiza la información de un contacto.
     *
     * @param contact contacto con datos actualizados
     *
     * @return true si la actualización fue exitosa
     */
    public static boolean updateContact(Contact contact) {

        if (contact.getIdContact() <= 0) {

            System.out.println(
                    "Error: ID inválido"
            );

            return false;
        }

        if (
                contact.getType() == null
                || contact.getContactValue() == null
        ) {

            System.out.println(
                    "Error: Datos incompletos"
            );

            return false;
        }

        String sql = "UPDATE aud_contacts "
                + "SET type = ?, contact_value = ? "
                + "WHERE id_contact = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, contact.getType());
            ps.setString(2, contact.getContactValue());
            ps.setInt(3, contact.getIdContact());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar contacto: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Obtiene el contacto asociado a un administrador.
     *
     * @param idAdmin identificador del administrador
     *
     * @return contacto encontrado o null
     */
    public static Contact getContactByAdminId(int idAdmin) {

        String sql = "SELECT c.* "
                + "FROM aud_contacts c "
                + "INNER JOIN aud_cxa x "
                + "ON c.id_contact = x.id_contact "
                + "WHERE x.id_admin = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idAdmin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Contact contact = new Contact();

                contact.setIdContact(
                        rs.getInt("id_contact")
                );

                contact.setType(
                        rs.getString("type")
                );

                contact.setContactValue(
                        rs.getString("contact_value")
                );

                return contact;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error obteniendo contacto: "
                    + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Elimina el contacto asociado a un cliente.
     *
     * @param idClient identificador del cliente
     *
     * @return true si el contacto fue eliminado
     */
    public static boolean deleteContactByClientId(int idClient) {

        try (
                Connection conn = DBConnection.getConnection()
        ) {

            int idContact = -1;

            String getContact = "SELECT id_contact "
                    + "FROM aud_cxc "
                    + "WHERE id_client = ?";

            try (
                    PreparedStatement ps =
                    conn.prepareStatement(getContact)
            ) {

                ps.setInt(1, idClient);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    idContact = rs.getInt("id_contact");
                }
            }

            if (idContact == -1) {

                System.out.println(
                        "No se encontró contacto "
                        + "para el cliente: "
                        + idClient
                );

                return false;
            }

            String deleteCXC = "DELETE FROM aud_cxc "
                    + "WHERE id_client = ?";

            try (
                    PreparedStatement ps =
                    conn.prepareStatement(deleteCXC)
            ) {

                ps.setInt(1, idClient);

                ps.executeUpdate();
            }

            String deleteContact = "DELETE "
                    + "FROM aud_contacts "
                    + "WHERE id_contact = ?";

            try (
                    PreparedStatement ps =
                    conn.prepareStatement(deleteContact)
            ) {

                ps.setInt(1, idContact);

                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al eliminar contacto "
                    + "de cliente: "
                    + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Obtiene el contacto asociado a un cliente.
     *
     * @param idClient identificador del cliente
     *
     * @return contacto encontrado o null
     */
    public static Contact getContactByClientId(int idClient) {

        String sql = "SELECT c.* "
                + "FROM aud_contacts c "
                + "INNER JOIN aud_cxc x "
                + "ON c.id_contact = x.id_contact "
                + "WHERE x.id_client = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idClient);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Contact contact = new Contact();

                contact.setIdContact(
                        rs.getInt("id_contact")
                );

                contact.setType(
                        rs.getString("type")
                );

                contact.setContactValue(
                        rs.getString("contact_value")
                );

                return contact;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error obteniendo contacto "
                    + "del cliente: "
                    + e.getMessage()
            );
        }

        return null;
    }
}