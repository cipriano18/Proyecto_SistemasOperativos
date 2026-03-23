package proyect.clientereservas.service;

import model.AdminRequest;
import model.User;
import proyect.clientereservas.network.socketManager;
import java.io.IOException;

public class AuthService {

    private static AuthService instance;
    private static Object currentSession;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    public String login(String username, String password) throws IOException, ClassNotFoundException {
        var conn = socketManager.getInstance().getConnection();

        User user = new User(0, username, password, null);

        conn.getOutput().writeUTF("LOGIN");
        conn.getOutput().flush();
        conn.getObjectOutput().writeObject(user);
        conn.getObjectOutput().flush();

        String response = conn.getInput().readUTF();

        if (response.startsWith("SUCCESS")) {
            currentSession = conn.getObjectInput().readObject();
        }

        return response;
    }

    public int getCurrentRole() {
        if (currentSession instanceof AdminRequest) {
            AdminRequest req = (AdminRequest) currentSession;
            return req.getUser().getIdRole();
        }
        if (currentSession instanceof User) {
            User user = (User) currentSession;
            return user.getIdRole();
        }
        return -1;
    }

    public Object getCurrentSession() { return currentSession; }
    public boolean isLoggedIn() { return currentSession != null; }
    public void logout() { currentSession = null; }
}