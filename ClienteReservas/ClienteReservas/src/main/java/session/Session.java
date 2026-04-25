package session;

import dto.ClientRequest;

public class Session {

    private static Session instance;
    private ClientRequest client;

    private Session() {
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public ClientRequest getClient() {
        return client;
    }

    public void setClient(ClientRequest client) {
        this.client = client;
    }

    public void clear() {
        this.client = null;
    }

    public boolean isLoggedIn() {
        return client != null;
    }
}
