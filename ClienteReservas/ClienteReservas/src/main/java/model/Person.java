/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int idUser;
    protected String fName;
    protected String mName;
    protected String fSurname;
    protected String mSurname;
    protected String identityCard;

    public Person(int idUser, String fName, String mName, String fSurname, String mSurname, String identityCard) {
        this.idUser = idUser;
        this.fName = fName;
        this.mName = mName;
        this.fSurname = fSurname;
        this.mSurname = mSurname;
        this.identityCard = identityCard;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getfSurname() {
        return fSurname;
    }

    public void setfSurname(String fSurname) {
        this.fSurname = fSurname;
    }

    public String getmSurname() {
        return mSurname;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    @Override
    public String toString() {
        return fName + " " + mName + " " + fSurname + " " + mSurname;
    }
}

