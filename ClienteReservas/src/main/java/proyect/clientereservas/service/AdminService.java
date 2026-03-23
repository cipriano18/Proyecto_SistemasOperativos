package proyect.clientereservas.service;

import model.Admin;
import model.AdminRequest;
import model.Contact;
import model.User;
import proyect.clientereservas.network.SocketManager;
import java.io.IOException;

public class AdminService {

    private static AdminService instance;
    private AdminService() {}

    public static AdminService getInstance() {
        if (instance == null) instance = new AdminService();
        return instance;
    }

    public String createAdmin(String username, String password,
                              String fName, String mName,
                              String fSurname, String mSurname,
                              String identityCard,
                              String contactType, String contactValue) throws IOException, ClassNotFoundException {

        var conn = SocketManager.getInstance().getConnection();

        User user = new User(2, username, password, "A"); // rol 2 = Administrador
        Admin admin = new Admin(0, fName, mName, fSurname, mSurname, identityCard);
        Contact contact = new Contact(contactType, contactValue);
        AdminRequest request = new AdminRequest(user, admin, contact);

        conn.getOutput().writeUTF("CREATE_ADMIN");
        conn.getOutput().flush();
        conn.getObjectOutput().writeObject(request);
        conn.getObjectOutput().flush();

        return conn.getInput().readUTF();
    }
}