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
 * DTO utilizado para transportar la información completa de un administrador.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 * <li>Usuario</li>
 * <li>Administrador</li>
 * <li>Contacto</li>
 * </ul>
 *
 * @author Cipriano
 */
public class AdminRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;

    private Admin admin;

    private Contact contact;

    /**
     * Constructor vacío.
     */
    public AdminRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param user información del usuario
     * @param admin información del administrador
     * @param contact información del contacto
     */
    public AdminRequest(
            User user,
            Admin admin,
            Contact contact
    ) {

        this.user = user;
        this.admin = admin;
        this.contact = contact;
    }

    /**
     * Obtiene el usuario asociado.
     *
     * @return usuario asociado
     */
    public User getUser() {

        return user;
    }

    /**
     * Obtiene la información del administrador.
     *
     * @return administrador asociado
     */
    public Admin getAdmin() {

        return admin;
    }

    /**
     * Obtiene la información del contacto.
     *
     * @return contacto asociado
     */
    public Contact getContact() {

        return contact;
    }

    /**
     * Define el usuario asociado.
     *
     * @param user usuario asociado
     */
    public void setUser(User user) {

        this.user = user;
    }

    /**
     * Define la información del administrador.
     *
     * @param admin administrador asociado
     */
    public void setAdmin(Admin admin) {

        this.admin = admin;
    }

    /**
     * Define la información del contacto.
     *
     * @param contact contacto asociado
     */
    public void setContact(Contact contact) {

        this.contact = contact;
    }

    /**
     * Retorna una representación textual del objeto.
     *
     * @return representación textual
     */
    @Override
    public String toString() {

        return "AdminRequest{"
                + "user=" + user
                + ", admin=" + admin
                + ", contact=" + contact
                + '}';
    }
}
