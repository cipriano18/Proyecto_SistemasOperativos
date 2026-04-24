package model;

import java.io.Serializable;
import java.sql.Date;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idReservation;
    private Date reservationDate;
    private int idSection;

    public Reservation() {
    }

    public Reservation(int idReservation, Date reservationDate, int idSection) {
        this.idReservation = idReservation;
        this.reservationDate = reservationDate;
        this.idSection = idSection;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public int getIdSection() {
        return idSection;
    }

    public void setIdSection(int idSection) {
        this.idSection = idSection;
    }
}