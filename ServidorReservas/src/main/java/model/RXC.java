package model;

import java.io.Serializable;

public class RXC implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idRxc;
    private int idReservation;
    private int idClient;

    public RXC() {
    }

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