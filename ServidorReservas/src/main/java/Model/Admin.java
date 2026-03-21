package model;

import java.io.Serializable;

public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAdmin;
    private int idUser;
    private String fName;
    private String mName;
    private String fSurname;
    private String mSurname;
    private String identityCard;

    public Admin() {
    }

    public Admin(int idAdmin, int idUser, String fName, String mName,
            String fSurname, String mSurname, String identityCard) {
        this.idAdmin = idAdmin;
        this.idUser = idUser;
        this.fName = fName;
        this.mName = mName;
        this.fSurname = fSurname;
        this.mSurname = mSurname;
        this.identityCard = identityCard;
    }

    public Admin(int idUser, String fName, String mName,
            String fSurname, String mSurname, String identityCard) {
        this.idUser = idUser;
        this.fName = fName;
        this.mName = mName;
        this.fSurname = fSurname;
        this.mSurname = mSurname;
        this.identityCard = identityCard;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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
