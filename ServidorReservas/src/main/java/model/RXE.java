/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 * Relaciona una reserva con un equipo y la cantidad solicitada.
 */
public class RXE implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idRxe;
    private int idReservation;
    private int idEquipment;
    private int quantity;

    /**
     * Crea una relacion vacia entre reserva y equipo.
     */
    public RXE() {
    }

    /**
     * Crea una relacion con sus identificadores y cantidad asociada.
     *
     * @param idRxe identificador de la relacion
     * @param idReservation identificador de la reserva
     * @param idEquipment identificador del equipo
     * @param quantity cantidad solicitada del equipo
     */
    public RXE(int idRxe, int idReservation, int idEquipment, int quantity) {
        this.idRxe = idRxe;
        this.idReservation = idReservation;
        this.idEquipment = idEquipment;
        this.quantity = quantity;
    }

    public int getIdRxe() {
        return idRxe;
    }

    public void setIdRxe(int idRxe) {
        this.idRxe = idRxe;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(int idEquipment) {
        this.idEquipment = idEquipment;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
