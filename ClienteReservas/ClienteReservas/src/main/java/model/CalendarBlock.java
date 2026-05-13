/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Representa un bloque del calendario con su estado de disponibilidad.
 */
public class CalendarBlock implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date reservationDate;
    private int idSection;
    private String status;

    /**
     * Crea un bloque de calendario vacio.
     */
    public CalendarBlock() {
    }

    /**
     * Crea un bloque con fecha, seccion y estado.
     *
     * @param reservationDate fecha del bloque
     * @param idSection identificador de la seccion
     * @param status estado del bloque en el calendario
     */
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

    /**
     * Devuelve una representacion textual resumida del bloque.
     *
     * @return cadena con los datos principales del bloque
     */
    @Override
    public String toString() {
        return "CalendarBlock{" 
                + "reservationDate=" 
                + reservationDate 
                + ", idSection=" 
                + idSection 
                + ", status=" 
                + status 
                + '}';
    }
}
