package model;

public class Contact {
    private int idContact;
    private String type;
    private String contactValue;

    // Constructor vacío
    public Contact() {}

    // Constructor con parámetros
    public Contact(int idContact, String type, String contactValue) {
        this.idContact = idContact;
        this.type = type;
        this.contactValue = contactValue;
    }

    // Getters y Setters
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
}
