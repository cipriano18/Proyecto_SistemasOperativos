package model;

import java.io.Serializable;

public class Client extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idClient;

   

    public Client(int idUser, String fName, String mName, String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
    }

    public Client(int idClient, int idUser, String fName, String mName, String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return getfName() + " " + getmName() + " " + getfSurname() + " " + getmSurname();
    }
}