/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author Cipriano
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idReservation;
    private int idClient;
    private Date reservationDate;
    private int idSection;

    public Reservation() {
    }

    public Reservation(int idReservation, int idClient, Date reservationDate, int idSection) {
        this.idReservation = idReservation;
        this.idClient = idClient;
        this.reservationDate = reservationDate;
        this.idSection = idSection;
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