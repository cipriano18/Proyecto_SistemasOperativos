/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 * Representa un medio de contacto asociado a un usuario o persona.
 */
public class Contact implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idContact;
    private String type;
    private String contactValue;

    /**
     * Crea un contacto vacio.
     */
    public Contact() {
    }

    /**
     * Crea un contacto con identificador, tipo y valor.
     *
     * @param idContact identificador del contacto
     * @param type tipo de contacto
     * @param contactValue valor del contacto
     */
    public Contact(int idContact, String type, String contactValue) {
        this.idContact = idContact;
        this.type = type;
        this.contactValue = contactValue;
    }

    /**
     * Crea un contacto nuevo con tipo y valor.
     *
     * @param type tipo de contacto
     * @param contactValue valor del contacto
     */
    public Contact(String type, String contactValue) {
        this.type = type;
        this.contactValue = contactValue;
    }

    public int getIdContact() { 
        return idContact; 
    }
    public void setIdContact(int idContact) { 
        this.idContact = idContact; 
    }

    public String getType() { 
        return type; 
    }
    
    public void setType(String type) { 
        this.type = type; 
    }

    public String getContactValue() { 
        return contactValue; 
    }
    public void setContactValue(String contactValue) { 
        this.contactValue = contactValue; 
    }

    /**
     * Devuelve una representacion textual resumida del contacto.
     *
     * @return cadena con los datos principales del contacto
     */
    @Override
    public String toString() {
        return "Contact{id=" 
                + idContact 
                + ", type=" 
                + type 
                + ", value=" 
                + contactValue 
                + "}";
    }
}
