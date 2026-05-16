package dto;

import java.io.Serializable;

/**
 * DTO utilizado para solicitar información relacionada con un calendario.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 * <li>Mes consultado</li>
 * <li>Año consultado</li>
 * <li>Cliente asociado</li>
 * </ul>
 *
 * @author Cipriano
 */
public class CalendarRequest
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private int month;

    private int year;

    private int idClient;

    /**
     * Constructor vacío.
     */
    public CalendarRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param month mes consultado
     * @param year año consultado
     * @param idClient identificador del cliente
     */
    public CalendarRequest(
            int month,
            int year,
            int idClient
    ) {

        this.month = month;
        this.year = year;
        this.idClient = idClient;
    }

    /**
     * Obtiene el mes consultado.
     *
     * @return mes consultado
     */
    public int getMonth() {

        return month;
    }

    /**
     * Define el mes consultado.
     *
     * @param month mes consultado
     */
    public void setMonth(int month) {

        this.month = month;
    }

    /**
     * Obtiene el año consultado.
     *
     * @return año consultado
     */
    public int getYear() {

        return year;
    }

    /**
     * Define el año consultado.
     *
     * @param year año consultado
     */
    public void setYear(int year) {

        this.year = year;
    }

    /**
     * Obtiene el identificador del cliente.
     *
     * @return identificador del cliente
     */
    public int getIdClient() {

        return idClient;
    }

    /**
     * Define el identificador del cliente.
     *
     * @param idClient identificador del cliente
     */
    public void setIdClient(int idClient) {

        this.idClient = idClient;
    }

    /**
     * Retorna una representación textual del objeto.
     *
     * @return representación textual
     */
    @Override
    public String toString() {

        return "CalendarRequest{"
                + "month=" + month
                + ", year=" + year
                + ", idClient=" + idClient
                + '}';
    }
}
