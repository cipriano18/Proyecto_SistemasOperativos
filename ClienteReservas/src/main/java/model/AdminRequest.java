package model;

import java.io.Serializable;

public class AdminRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private Admin admin;
    private Contact contact;

    public AdminRequest() {}

    public AdminRequest(User user, Admin admin, Contact contact) {
        this.user = user;
        this.admin = admin;
        this.contact = contact;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
    public Contact getContact() { return contact; }
    public void setContact(Contact contact) { this.contact = contact; }

    @Override
    public String toString() {
        return "AdminRequest{user=" + user + ", admin=" + admin + ", contact=" + contact + '}';
    }
}