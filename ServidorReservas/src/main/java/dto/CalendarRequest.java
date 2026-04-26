package dto;

import java.io.Serializable;

public class CalendarRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int month;
    private int year;
    private int idClient;

    public CalendarRequest() {
    }

    public CalendarRequest(int month, int year, int idClient) {
        this.month = month;
        this.year = year;
        this.idClient = idClient;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    @Override
    public String toString() {
        return "CalendarRequest{" +
                "month=" + month +
                ", year=" + year +
                ", idClient=" + idClient +
                '}';
    }
}
