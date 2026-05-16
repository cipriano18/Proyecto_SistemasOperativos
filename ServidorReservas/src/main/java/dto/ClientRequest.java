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
 * DTO utilizado para transportar la información completa de un cliente.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 * <li>Usuario</li>
 * <li>Cliente</li>
 * <li>Contacto</li>
 * </ul>
 *
 * @author Cipriano
 */
public class ClientRequest
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;

    private Client client;

    private Contact contact;

    /**
     * Constructor vacío.
     */
    public ClientRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param user información del usuario
     * @param client información del cliente
     * @param contact información del contacto
     */
    public ClientRequest(
            User user,
            Client client,
            Contact contact
    ) {

        this.user = user;
        this.client = client;
        this.contact = contact;
    }

    /**
     * Obtiene la información del usuario.
     *
     * @return usuario asociado
     */
    public User getUser() {

        return user;
    }

    /**
     * Obtiene la información del cliente.
     *
     * @return cliente asociado
     */
    public Client getClient() {

        return client;
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
     * Define la información del usuario.
     *
     * @param user usuario asociado
     */
    public void setUser(User user) {

        this.user = user;
    }

    /**
     * Define la información del cliente.
     *
     * @param client cliente asociado
     */
    public void setClient(Client client) {

        this.client = client;
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

        return "ClientRequest{"
                + "user=" + user
                + ", client=" + client
                + ", contact=" + contact
                + '}';
    }
}
