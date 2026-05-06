package model;

import java.io.Serializable;

public class AuditoriumReservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idAuditoriumReservation;
    private int idReservation;
    private String eventName;
    private int attendeesCount;
    private String observations;

    public AuditoriumReservation() {
    }

    public AuditoriumReservation(int idAuditoriumReservation, int idReservation, String eventName, int attendeesCount, String observations) {
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