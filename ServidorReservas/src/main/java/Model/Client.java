package model;

import java.io.Serializable;
import model.Person;

public class Client extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idClient;
    private int idType;

    public Client(int idUser, String fName, String mName, String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
    }

    public Client(int idClient, int idUser, int idType, String fName, String mName,
                  String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
        this.idClient = idClient;
        this.idType = idType;
    }

    public Client(int idUser, int idType, String fName, String mName,
                  String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
        this.idType = idType;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }
}