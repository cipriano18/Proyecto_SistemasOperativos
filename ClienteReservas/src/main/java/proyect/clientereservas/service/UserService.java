package proyect.clientereservas.service;

import model.User;
import proyect.clientereservas.network.SocketManager;
import java.io.*;

public class UserService {
    private static UserService instance;

    private UserService() {
    }

    public static UserService getInstance() {

        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public String createUser(String userName, String passWord) throws IOException, ClassNotFoundException {
        var conn = SocketManager.getInstance().getConnection();
        User user = new User(3, userName, passWord, "active");

        conn.getOutput().writeUTF("CREATE_USER");
        conn.getOutput().flush();
        conn.getObjectOutput().writeObject(user);
        conn.getObjectOutput().flush();

        return conn.getInput().readUTF();
    }

    public User getUser(int idUser) throws IOException, ClassNotFoundException {
        // crear una instancia de socketmanager
        var conn = SocketManager.getInstance().getConnection();
        User user = new User(0, null, null, null);
        user.setIdUser(idUser);

        conn.getOutput().writeUTF("GET_USER");
        conn.getOutput().flush();
        conn.getObjectOutput().writeObject(user);
        conn.getObjectOutput().flush();
        return (User) conn.getObjectInput().readObject();
    }
}