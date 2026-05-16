package dto;

import java.io.Serializable;
import java.util.List;
import model.RXE;
import model.Reservation;

/**
 * DTO utilizado para transportar la información de una reservación de equipos.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 * <li>Información de la reserva</li>
 * <li>Cliente asociado</li>
 * <li>Nombre del cliente</li>
 * <li>Lista de equipos seleccionados</li>
 * </ul>
 *
 * @author Cipriano
 */
public class EquipmentReservationRequest
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reservation reservation;

    private int idClient;

    private String clientName;

    private List<RXE> equipmentList;

    /**
     * Constructor vacío.
     */
    public EquipmentReservationRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param reservation información de la reserva
     * @param idClient identificador del cliente
     * @param equipmentList lista de equipos asociados
     */
    public EquipmentReservationRequest(
            Reservation reservation,
            int idClient,
            List<RXE> equipmentList
    ) {

        this.reservation = reservation;
        this.idClient = idClient;
        this.equipmentList = equipmentList;
    }

    /**
     * Obtiene la información de la reserva.
     *
     * @return información de la reserva
     */
    public Reservation getReservation() {

        return reservation;
    }

    /**
     * Define la información de la reserva.
     *
     * @param reservation información de la reserva
     */
    public void setReservation(
            Reservation reservation
    ) {

        this.reservation = reservation;
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
     * Obtiene el nombre del cliente.
     *
     * @return nombre del cliente
     */
    public String getClientName() {

        return clientName;
    }

    /**
     * Define el nombre del cliente.
     *
     * @param clientName nombre del cliente
     */
    public void setClientName(
            String clientName
    ) {

        this.clientName = clientName;
    }

    /**
     * Obtiene la lista de equipos asociados.
     *
     * @return lista de equipos
     */
    public List<RXE> getEquipmentList() {

        return equipmentList;
    }

    /**
     * Define la lista de equipos asociados.
     *
     * @param equipmentList lista de equipos
     */
    public void setEquipmentList(
            List<RXE> equipmentList
    ) {

        this.equipmentList = equipmentList;
    }
}
