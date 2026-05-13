package dto;

import java.io.Serializable;

/**
 * Representa los parametros necesarios para consultar un calendario.
 */
public class CalendarRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int month;
    private int year;
    private int idClient;

    /**
     * Crea una solicitud vacia de calendario.
     */
    public CalendarRequest() {
    }

    /**
     * Crea una solicitud con el periodo y cliente a consultar.
     *
     * @param month mes a consultar
     * @param year anio a consultar
     * @param idClient identificador del cliente
     */
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

    /**
     * Devuelve una representacion textual resumida de la solicitud.
     *
     * @return cadena con los datos principales de la solicitud
     */
    @Override
    public String toString() {
        return "CalendarRequest{" +
                "month=" + month +
                ", year=" + year +
                ", idClient=" + idClient +
                '}';
    }
}
