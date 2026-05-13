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
 * Agrupa la informacion necesaria para operar con un administrador.
 */
public class AdminRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private Admin admin;
    private Contact contact;

    /**
     * Crea una solicitud vacia de administrador.
     */
    public AdminRequest() {
    }

    /**
     * Crea una solicitud con los datos del usuario, administrador y contacto.
     *
     * @param user credenciales del usuario asociado
     * @param admin informacion personal del administrador
     * @param contact informacion de contacto asociada
     */
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

    /**
     * Devuelve una representacion textual resumida de la solicitud.
     *
     * @return cadena con los datos principales de la solicitud
     */
    @Override
    public String toString() {
        return "AdminRequest{" 
                + "user=" 
                + user 
                + ", admin=" 
                + admin 
                + ", contact=" 
                + contact 
                + '}';
    }
    
}
