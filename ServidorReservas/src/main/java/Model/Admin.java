package model;

import java.io.Serializable;
import model.Person;

public class Admin extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAdmin;

    public Admin() {
        super(0, "", "", "", "", ""); // constructor vacío de Person
    }

    public Admin(int idAdmin, int idUser, String fName, String mName,
                 String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
        this.idAdmin = idAdmin;
    }

    public Admin(int idUser, String fName, String mName,
                 String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }
}
