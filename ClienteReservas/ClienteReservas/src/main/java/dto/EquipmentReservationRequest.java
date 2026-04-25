package dto;

import java.io.Serializable;
import java.util.List;
import model.RXE;
import model.Reservation;

public class EquipmentReservationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Reservation reservation;
    private int idClient;
    private List<RXE> equipmentList;

    public EquipmentReservationRequest() {
    }

    public EquipmentReservationRequest(Reservation reservation, int idClient, List<RXE> equipmentList) {
        this.reservation = reservation;
        this.idClient = idClient;
        this.equipmentList = equipmentList;
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