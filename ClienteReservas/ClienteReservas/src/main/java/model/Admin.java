/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 * Representa a un administrador registrado en el sistema.
 */
public class Admin extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAdmin;

    /**
     * Crea un administrador vacio con valores base heredados de persona.
     */
    public Admin() {
        super(0, "", "", "", "", ""); // constructor vacío de Person
    }

    /**
     * Crea un administrador con identificadores y datos personales completos.
     *
     * @param idAdmin identificador del administrador
     * @param idUser identificador del usuario asociado
     * @param fName primer nombre
     * @param mName segundo nombre
     * @param fSurname primer apellido
     * @param mSurname segundo apellido
     * @param identityCard documento de identidad
     */
    public Admin(int idAdmin, int idUser, String fName, String mName,
            String fSurname, String mSurname, String identityCard) {
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
        this.idAdmin = idAdmin;
    }

    /**
     * Crea un administrador nuevo con sus datos personales basicos.
     *
     * @param idUser identificador del usuario asociado
     * @param fName primer nombre
     * @param mName segundo nombre
     * @param fSurname primer apellido
     * @param mSurname segundo apellido
     * @param identityCard documento de identidad
     */
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

