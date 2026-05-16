package model;

import java.io.Serializable;

/**
 * Representa a un cliente registrado en el sistema.
 */
public class Client extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idClient;
    /**
    * Crea un cliente vacío.
    */
    public Client() {
    }
    /**
     * Crea un cliente con sus datos personales basicos.
     *
     * @param idUser identificador del usuario asociado
     * @param fName primer nombre
     * @param mName segundo nombre
     * @param fSurname primer apellido
     * @param mSurname segundo apellido
     * @param identityCard documento de identidad
     */
    public Client(
            int idUser, String fName, String mName,
            String fSurname, String mSurname, String identityCard) {
        
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
    }

    /**
     * Crea un cliente con identificadores y datos personales completos.
     *
     * @param idClient identificador del cliente
     * @param idUser identificador del usuario asociado
     * @param fName primer nombre
     * @param mName segundo nombre
     * @param fSurname primer apellido
     * @param mSurname segundo apellido
     * @param identityCard documento de identidad
     */
    public Client(
            int idClient, int idUser, String fName,
            String mName, String fSurname, String mSurname,
            String identityCard) {
        
        super(idUser, fName, mName, fSurname, mSurname, identityCard);
        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    /**
     * Devuelve el nombre completo del cliente.
     *
     * @return nombre completo del cliente
     */
    @Override
    public String toString() {
        return getfName() 
                + " " 
                + getmName()
                + " " 
                + getfSurname() 
                + " " 
                + getmSurname();
    }
}
