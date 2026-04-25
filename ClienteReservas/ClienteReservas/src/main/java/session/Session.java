package session;

import dto.ClientRequest;
import dto.AdminRequest;

public class Session {

    private static Session instance;

    private ClientRequest client;
    private AdminRequest admin;

    private Session() {
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // ===== CLIENT =====
    public ClientRequest getClient() {
        return client;
    }

    public void setClient(ClientRequest client) {
        this.client = client;
        this.admin = null; // limpia admin
    }

    // ===== ADMIN / SUPER ADMIN =====
    public AdminRequest getAdmin() {
        return admin;
    }

    public void setAdmin(AdminRequest admin) {
        this.admin = admin;
        this.client = null; // limpia cliente
    }

    // ===== GENERAL =====
    public void clear() {
        this.client = null;
        this.admin = null;
    }

    public boolean isLoggedIn() {
        return client != null || admin != null;
    }

    public int getRole() {
        if (admin != null && admin.getUser() != null) {
            return admin.getUser().getIdRole();
        }
        if (client != null && client.getUser() != null) {
            return client.getUser().getIdRole();
        }
        return 0;
    }
}
