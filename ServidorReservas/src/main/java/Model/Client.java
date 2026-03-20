package model;

import java.io.Serializable;

public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idClient;
    private int idUser;
    private int idType;
    private String fName;
    private String mName;
    private String fSurname;
    private String mSurname;
    private String identityCard;

    public Client(int idClient, int idUser, int idType, String fName, String mName,
            String fSurname, String mSurname, String identityCard) {
        this.idClient = idClient;
        this.idUser = idUser;
        this.idType = idType;
        this.fName = fName;
        this.mName = mName;
        this.fSurname = fSurname;
        this.mSurname = mSurname;
        this.identityCard = identityCard;
    }

    public Client(int idUser, int idType, String fName, String mName,
            String fSurname, String mSurname, String identityCard) {
        this.idUser = idUser;
        this.idType = idType;
        this.fName = fName;
        this.mName = mName;
        this.fSurname = fSurname;
        this.mSurname = mSurname;
        this.identityCard = identityCard;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public String getFSurname() {
        return fSurname;
    }

    public void setFSurname(String fSurname) {
        this.fSurname = fSurname;
    }

    public String getMSurname() {
        return mSurname;
    }

    public void setMSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

}
