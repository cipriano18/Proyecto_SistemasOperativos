/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Cipriano
 */
public class EquipmentReservationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Reservation reservation;
    private List<RXE> equipmentList;

    public EquipmentReservationRequest() {
    }

    public EquipmentReservationRequest(Reservation reservation, List<RXE> equipmentList) {
        this.reservation = reservation;
        this.equipmentList = equipmentList;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public List<RXE> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<RXE> equipmentList) {
        this.equipmentList = equipmentList;
    }
}
