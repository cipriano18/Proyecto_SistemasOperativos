package test;

import controller.AdminController;
import database.AdminDAO;
import database.UserDAO;
import model.Admin;
import model.AdminRequest;
import model.Contact;
import model.Response;
import model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminControllerResponseTest {

    @Test
    public void testAdminControllerResponses() {
        long suffix = System.currentTimeMillis();

        String username = "admin_resp_" + suffix;
        String email = "admin" + suffix + "@mail.com";
        String identityCard = "123456789";

        // =========================
        // 1. CREATE
        // =========================
        User user = new User();
        user.setIdRole(2);
        user.setUsername(username);
        user.setPassword("Admin1234");

        Admin admin = new Admin();
        admin.setfName("Mario");
        admin.setmName("José");
        admin.setfSurname("Rojas");
        admin.setmSurname("Vargas");
        admin.setIdentityCard(identityCard);

        Contact contact = new Contact();
        contact.setType("EMAIL");
        contact.setContactValue(email);

        AdminRequest createRequest = new AdminRequest();
        createRequest.setUser(user);
        createRequest.setAdmin(admin);
        createRequest.setContact(contact);

        Response createResponse = AdminController.createAdmin(createRequest);

        System.out.println("===== CREATE =====");
        System.out.println("Success: " + createResponse.isSuccess());
        System.out.println("Message: " + createResponse.getMessage());
        System.out.println("Data: " + createResponse.getData());

        assertTrue(createResponse.isSuccess());
        assertNotNull(createResponse.getData());

        AdminRequest createdData = (AdminRequest) createResponse.getData();
        assertNotNull(createdData.getUser());
        assertNotNull(createdData.getAdmin());
        assertNotNull(createdData.getContact());

        int idAdmin = createdData.getAdmin().getIdAdmin();
        int idUser = createdData.getUser().getIdUser();

        // =========================
        // 2. GET
        // =========================
        Response getResponse = AdminController.getAdmin(idAdmin);

        System.out.println("===== GET =====");
        System.out.println("Success: " + getResponse.isSuccess());
        System.out.println("Message: " + getResponse.getMessage());
        System.out.println("Data: " + getResponse.getData());

        assertTrue(getResponse.isSuccess());
        assertNotNull(getResponse.getData());

        AdminRequest getData = (AdminRequest) getResponse.getData();
        assertEquals(idAdmin, getData.getAdmin().getIdAdmin());
        assertEquals(idUser, getData.getUser().getIdUser());
        assertEquals(username, getData.getUser().getUsername());

        // =========================
        // 3. UPDATE
        // =========================
        User updateUser = getData.getUser();
        Admin updateAdmin = getData.getAdmin();
        Contact updateContact = getData.getContact();

        updateUser.setUsername("admin_edit_" + suffix);
        updateAdmin.setfName("Mariano");
        updateContact.setContactValue("edit" + suffix + "@mail.com");

        AdminRequest updateRequest = new AdminRequest();
        updateRequest.setUser(updateUser);
        updateRequest.setAdmin(updateAdmin);
        updateRequest.setContact(updateContact);

        Response updateResponse = AdminController.updateAdmin(updateRequest);

        System.out.println("===== UPDATE =====");
        System.out.println("Success: " + updateResponse.isSuccess());
        System.out.println("Message: " + updateResponse.getMessage());
        System.out.println("Data: " + updateResponse.getData());

        assertTrue(updateResponse.isSuccess());
        assertNotNull(updateResponse.getData());

        AdminRequest updatedData = (AdminRequest) updateResponse.getData();
        assertEquals("admin_edit_" + suffix, updatedData.getUser().getUsername());
        assertEquals("Mariano", updatedData.getAdmin().getfName());
        assertEquals("edit" + suffix + "@mail.com", updatedData.getContact().getContactValue());

        // =========================
        // 4. DELETE
        // =========================
        Response deleteResponse = AdminController.deleteAdmin(idAdmin);

        System.out.println("===== DELETE =====");
        System.out.println("Success: " + deleteResponse.isSuccess());
        System.out.println("Message: " + deleteResponse.getMessage());
        System.out.println("Data: " + deleteResponse.getData());

        assertTrue(deleteResponse.isSuccess());
        assertNotNull(deleteResponse.getData());

        AdminRequest deletedData = (AdminRequest) deleteResponse.getData();
        assertEquals(idAdmin, deletedData.getAdmin().getIdAdmin());

        // =========================
        // 5. GET AFTER DELETE
        // =========================
        Response getAfterDeleteResponse = AdminController.getAdmin(idAdmin);

        System.out.println("===== GET AFTER DELETE =====");
        System.out.println("Success: " + getAfterDeleteResponse.isSuccess());
        System.out.println("Message: " + getAfterDeleteResponse.getMessage());
        System.out.println("Data: " + getAfterDeleteResponse.getData());

        assertFalse(getAfterDeleteResponse.isSuccess());
        assertNull(getAfterDeleteResponse.getData());
    }
}