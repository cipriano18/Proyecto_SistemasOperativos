package model;

import java.io.Serializable;

public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idContact;
    private String type;
    private String contactValue;

    public Contact() {}

    public Contact(int idContact, String type, String contactValue) {
        this.idContact = idContact;
        this.type = type;
        this.contactValue = contactValue;
    }

    public Contact(String type, String contactValue) {
        this.type = type;
        this.contactValue = contactValue;
    }

    public int getIdContact() { return idContact; }
    public void setIdContact(int idContact) { this.idContact = idContact; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContactValue() { return contactValue; }
    public void setContactValue(String contactValue) { this.contactValue = contactValue; }

    @Override
    public String toString() {
        return "Contact{id=" + idContact + ", type=" + type + ", value=" + contactValue + "}";
    }
}