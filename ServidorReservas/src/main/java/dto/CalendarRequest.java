package dto;

import java.io.Serializable;

public class CalendarRequest implements Serializable {
private static final long serialVersionUID = 1L;
    private int month;
    private int year;

    public CalendarRequest() {
    }

    public CalendarRequest(int month, int year) {
        this.month = month;
        this.year = year;
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

    @Override
    public String toString() {
        return "CalendarRequest{" +
                "month=" + month +
                ", year=" + year +
                '}';
    }
}