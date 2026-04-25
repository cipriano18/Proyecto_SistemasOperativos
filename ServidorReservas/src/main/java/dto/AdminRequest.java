/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
import model.Admin;
import model.Contact;
import model.User;

/**
 *
 * @author User
 */
public class AdminRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private Admin admin;
    private Contact contact;

    public AdminRequest() {
    }

    public AdminRequest(User user, Admin admin, Contact contact) {
        this.user = user;
        this.admin = admin;
        this.contact = contact;
    }

    public User getUser() {
        return user;
    }

    public Admin getAdmin() {
        return admin;
    }

    public Contact getContact() {
        return contact;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "AdminRequest{" + "user=" + user + ", admin=" + admin + ", contact=" + contact + '}';
    }
    
}
