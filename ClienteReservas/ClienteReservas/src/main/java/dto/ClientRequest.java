/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
import model.Client;
import model.Contact;
import model.User;

/**
 * Agrupa la informacion necesaria para operar con un cliente.
 */
public class ClientRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private User user;
    private Client client;
    private Contact contact;

    /**
     * Crea una solicitud vacia de cliente.
     */
    public ClientRequest() {
    }

    /**
     * Crea una solicitud con los datos del usuario, cliente y contacto.
     *
     * @param user credenciales del usuario asociado
     * @param client informacion personal del cliente
     * @param contact informacion de contacto asociada
     */
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

    /**
     * Devuelve una representacion textual resumida de la solicitud.
     *
     * @return cadena con los datos principales de la solicitud
     */
    @Override
    public String toString() {
        return "ClientRequest{"
                + "user=" + user
                + ", client=" + client
                + ", contact=" + contact + '}';
    }

}
