package dto;

import java.io.Serializable;
import java.util.List;
import model.AuditoriumReservation;
import model.RXE;
import model.Reservation;

/**
 * Agrupa la informacion de una reserva de auditorio y sus equipos.
 */
public class AuditoriumReservationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Reservation reservation;
    private int idClient;
    private String clientName;
    private AuditoriumReservation auditoriumReservation;
    private List<RXE> equipmentList;

    /**
     * Crea una solicitud vacia de reserva de auditorio.
     */
    public AuditoriumReservationRequest() {
    }

    /**
     * Crea una solicitud con la informacion principal de la reserva.
     *
     * @param reservation reserva base asociada
     * @param idClient identificador del cliente
     * @param auditoriumReservation datos del evento de auditorio
     * @param equipmentList equipos asociados a la reserva
     */
    public AuditoriumReservationRequest(
            Reservation reservation, 
            int idClient, 
            AuditoriumReservation auditoriumReservation, 
            List<RXE> equipmentList) {
        
        this.reservation = reservation;
        this.idClient = idClient;
        this.auditoriumReservation = auditoriumReservation;
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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public AuditoriumReservation getAuditoriumReservation() {
        return auditoriumReservation;
    }

    public void setAuditoriumReservation(
            AuditoriumReservation auditoriumReservation) {
        
        this.auditoriumReservation = auditoriumReservation;
    }

    public List<RXE> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<RXE> equipmentList) {
        this.equipmentList = equipmentList;
    }
}
