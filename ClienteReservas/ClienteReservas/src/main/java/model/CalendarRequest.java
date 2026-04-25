/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 *
 * @author cipriano
 */
public class CalendarRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private int month;
    private int year;

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

    @Override
    public String toString() {
        return "CalendarRequest{month=" + month + ", year=" + year + '}';
    }
}
