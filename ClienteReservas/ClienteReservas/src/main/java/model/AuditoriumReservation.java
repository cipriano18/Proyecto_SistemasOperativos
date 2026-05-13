package model;

import java.io.Serializable;

/**
 * Representa la informacion propia de una reserva de auditorio.
 */
public class AuditoriumReservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idAuditoriumReservation;
    private int idReservation;
    private String eventName;
    private int attendeesCount;
    private String observations;

    /**
     * Crea una reserva de auditorio vacia.
     */
    public AuditoriumReservation() {
    }

    /**
     * Crea una reserva de auditorio con su informacion principal.
     *
     * @param idAuditoriumReservation identificador de la reserva de auditorio
     * @param idReservation identificador de la reserva general
     * @param eventName nombre del evento
     * @param attendeesCount cantidad de asistentes
     * @param observations observaciones asociadas al evento
     */
    public AuditoriumReservation(
            int idAuditoriumReservation, 
            int idReservation, 
            String eventName, 
            int attendeesCount,
            String observations) {
        
        this.idAuditoriumReservation = idAuditoriumReservation;
        this.idReservation = idReservation;
        this.eventName = eventName;
        this.attendeesCount = attendeesCount;
        this.observations = observations;
    }

    public int getIdAuditoriumReservation() {
        return idAuditoriumReservation;
    }

    public void setIdAuditoriumReservation(int idAuditoriumReservation) {
        this.idAuditoriumReservation = idAuditoriumReservation;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
