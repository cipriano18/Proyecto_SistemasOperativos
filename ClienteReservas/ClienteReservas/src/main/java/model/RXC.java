package model;

import java.io.Serializable;

/**
 * Relaciona una reserva con el cliente al que pertenece.
 */
public class RXC implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idRxc;
    private int idReservation;
    private int idClient;

    /**
     * Crea una relacion vacia entre reserva y cliente.
     */
    public RXC() {
    }

    /**
     * Crea una relacion con sus identificadores asociados.
     *
     * @param idRxc identificador de la relacion
     * @param idReservation identificador de la reserva
     * @param idClient identificador del cliente
     */
    public RXC(int idRxc, int idReservation, int idClient) {
        this.idRxc = idRxc;
        this.idReservation = idReservation;
        this.idClient = idClient;
    }

    public int getIdRxc() {
        return idRxc;
    }

    public void setIdRxc(int idRxc) {
        this.idRxc = idRxc;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
}
