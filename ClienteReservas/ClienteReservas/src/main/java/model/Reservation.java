package model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Representa una reserva con su fecha y bloque horario.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idReservation;
    private Date reservationDate;
    private int idSection;

    /**
     * Crea una reserva vacia.
     */
    public Reservation() {
    }

    /**
     * Crea una reserva con su identificador, fecha y seccion.
     *
     * @param idReservation identificador de la reserva
     * @param reservationDate fecha reservada
     * @param idSection identificador del bloque horario
     */
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
