/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 * Representa los parametros basicos para consultar un calendario.
 */
public class CalendarRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private int month;
    private int year;

    /**
     * Crea una solicitud de calendario con mes y anio.
     *
     * @param month mes a consultar
     * @param year anio a consultar
     */
    public CalendarRequest(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    /**
     * Devuelve una representacion textual resumida de la solicitud.
     *
     * @return cadena con los datos principales de la solicitud
     */
    @Override
    public String toString() {
        return "CalendarRequest{month=" + month + ", year=" + year + '}';
    }
}
