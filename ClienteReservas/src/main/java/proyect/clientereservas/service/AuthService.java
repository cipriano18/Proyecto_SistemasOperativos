package proyect.clientereservas.service;

import model.AdminRequest;
import model.User;
import proyect.clientereservas.network.SocketManager;
import java.io.IOException;

public class AuthService {

    private static AuthService instance;
    private static Object currentSession; // User o AdminRequest seguun el rol

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

   
    public String login(String username, String password) throws IOException, ClassNotFoundException {
        var conn = SocketManager.getInstance().getConnection();

        User user = new User(0, username, password, null);

        conn.getOutput().writeUTF("LOGIN");
        conn.getOutput().flush();
        conn.getObjectOutput().writeObject(user);
        conn.getObjectOutput().flush();

        String response = conn.getInput().readUTF();

        if (response.startsWith("SUCCESS")) {
            // El servidor manda el objeto de sesión después del SUCCESS
            currentSession = conn.getObjectInput().readObject();
        }

        return response;
    }

    public Object getCurrentSession() { return currentSession; }

    public boolean isAdmin() {
        return currentSession instanceof AdminRequest;
    }

    public boolean isLoggedIn() { return currentSession != null; }

    public void logout() { currentSession = null; }
}