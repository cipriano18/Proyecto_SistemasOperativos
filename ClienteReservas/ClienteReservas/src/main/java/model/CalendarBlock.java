/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author User
 */

public class CalendarBlock implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date reservationDate;
    private int idSection;
    private String status;

    public CalendarBlock() {
    }

    public CalendarBlock(Date reservationDate, int idSection, String status) {
        this.reservationDate = reservationDate;
        this.idSection = idSection;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CalendarBlock{" + "reservationDate=" + reservationDate + ", idSection=" + idSection + ", status=" + status + '}';
    }
    
}
