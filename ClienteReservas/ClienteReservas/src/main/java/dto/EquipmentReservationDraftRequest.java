/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
import java.util.List;
import model.RXE;
import model.Reservation;

public class EquipmentReservationDraftRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idDraft;
    private Reservation reservation;
    private int idClient;
    private List<RXE> equipmentList;

    public EquipmentReservationDraftRequest() {
    }

    public EquipmentReservationDraftRequest(int idDraft, Reservation reservation, int idClient, List<RXE> equipmentList) {
        this.idDraft = idDraft;
        this.reservation = reservation;
        this.idClient = idClient;
        this.equipmentList = equipmentList;
    }

    public int getIdDraft() {
        return idDraft;
    }

    public void setIdDraft(int idDraft) {
        this.idDraft = idDraft;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public List<RXE> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<RXE> equipmentList) {
        this.equipmentList = equipmentList;
    }
}
