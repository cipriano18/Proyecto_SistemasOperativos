/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Cipriano
 */
public class ClientRequest implements Serializable {

    private static final long serialVersionUID = 1L;
   private User user;
    private Client client;
    private Contact contact;
    
    public ClientRequest() {
    }

    public ClientRequest(User user, Client client, Contact contact) {
        this.user = user;
        this.client = client;
        this.contact = contact;
    }

    public User getUser() {
        return user;
    }

    public Client getClient() {
        return client;
    }

    public Contact getContact() {
        return contact;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "ClientRequest{" + 
                "user=" + user + 
                ", client=" + client + 
                ", contact=" + contact + '}';
    }
    
}