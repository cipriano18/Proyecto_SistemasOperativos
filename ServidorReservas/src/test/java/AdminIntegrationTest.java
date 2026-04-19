

import controller.AdminController;
import database.DBConnection;
import database.UserDAO;
import database.AdminDAO;
import model.Admin;
import model.AdminRequest;
import model.Contact;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AdminIntegrationTest {

    private String testUsername;
    private String testIdentityCard;

    @AfterEach
    public void cleanup() {
        if (testUsername != null) {
            deleteAdminTestDataByUsername(testUsername);
        }
    }

    @Test
    public void testCreateAndDeleteAdminCascade() {
        long suffix = System.currentTimeMillis();

        testUsername = "admin_test_" + suffix;
        testIdentityCard = "123456789" + (suffix % 10);

        // 1. Construir request de creación
        User user = new User();
        user.setIdRole(2); // Administrador
        user.setUsername(testUsername);
        user.setPassword("Admin1234");

        Admin admin = new Admin();
        admin.setfName("Carlos");
        admin.setmName("Andrés");
        admin.setfSurname("Rojas");
        admin.setmSurname("Gómez");
        admin.setIdentityCard(testIdentityCard);

        Contact contact = new Contact();
        contact.setType("EMAIL");
        contact.setContactValue("admin" + suffix + "@mail.com");

        AdminRequest request = new AdminRequest();
        request.setUser(user);
        request.setAdmin(admin);
        request.setContact(contact); // este test asume tu controller actual con un solo contacto

        // 2. Crear admin
        String createResult = AdminController.createAdmin(request);
        assertTrue(createResult.startsWith("SUCCESS"), "La creación debería ser exitosa, pero fue: " + createResult);

        // 3. Recuperar user y admin creados
        User createdUser = UserDAO.getUserByUsername(testUsername);
        assertNotNull(createdUser, "El usuario creado no debería ser null");

        Admin createdAdmin = AdminDAO.getAdminByUserId(createdUser.getIdUser());
        assertNotNull(createdAdmin, "El admin creado no debería ser null");

        int idUser = createdUser.getIdUser();
        int idAdmin = createdAdmin.getIdAdmin();

        // 4. Verificar que existen registros en todas las tablas esperadas
        assertEquals(1, countById("AUD_Users", "id_user", idUser));
        assertEquals(1, countById("AUD_Administrators", "id_admin", idAdmin));
        assertEquals(1, countContactsByAdminId(idAdmin));
        assertEquals(1, countCXAByAdminId(idAdmin));

        // 5. Eliminar admin completo en cascada
        String deleteResult = AdminController.deleteAdmin(idAdmin);
        assertTrue(deleteResult.startsWith("SUCCESS"), "La eliminación debería ser exitosa, pero fue: " + deleteResult);

        // 6. Verificar eliminación en cascada
        assertEquals(0, countById("AUD_Users", "id_user", idUser));
        assertEquals(0, countById("AUD_Administrators", "id_admin", idAdmin));
        assertEquals(0, countCXAByAdminId(idAdmin));
        assertEquals(0, countContactsByAdminId(idAdmin));
    }

    private int countById(String tableName, String idColumn, int idValue) {
        String sql = "SELECT COUNT(*) AS total FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idValue);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            fail("Error contando en tabla " + tableName + ": " + e.getMessage());
        }

        return -1;
    }

    private int countCXAByAdminId(int idAdmin) {
        String sql = "SELECT COUNT(*) AS total FROM AUD_CXA WHERE id_admin = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            fail("Error contando AUD_CXA: " + e.getMessage());
        }

        return -1;
    }

    private int countContactsByAdminId(int idAdmin) {
        String sql = "SELECT COUNT(*) AS total "
                   + "FROM AUD_Contacts c "
                   + "INNER JOIN AUD_CXA cxa ON c.id_contact = cxa.id_contact "
                   + "WHERE cxa.id_admin = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAdmin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            fail("Error contando contactos del admin: " + e.getMessage());
        }

        return -1;
    }

    private void deleteAdminTestDataByUsername(String username) {
        String getAdminSql = "SELECT a.id_admin "
                + "FROM AUD_Administrators a "
                + "INNER JOIN AUD_Users u ON a.id_user = u.id_user "
                + "WHERE u.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(getAdminSql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idAdmin = rs.getInt("id_admin");
                    AdminController.deleteAdmin(idAdmin);
                }
            }

        } catch (SQLException e) {
            System.out.println("Cleanup error: " + e.getMessage());
        }
    }
}