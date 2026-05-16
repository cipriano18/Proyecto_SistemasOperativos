package dto;

import java.io.Serializable;
import java.util.List;
import model.RXE;
import model.Reservation;
/**
 * DTO utilizado para transportar la información
 * de una reserva temporal de equipos.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 *     <li>Identificador del draft</li>
 *     <li>Información de la reserva</li>
 *     <li>Cliente asociado</li>
 *     <li>Lista de equipos seleccionados</li>
 * </ul>
 *
 * @author Cipriano
 */
public class EquipmentReservationDraftRequest
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idDraft;

    private Reservation reservation;

    private int idClient;

    private List<RXE> equipmentList;

    /**
     * Constructor vacío.
     */
    public EquipmentReservationDraftRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param idDraft identificador del draft
     * @param reservation información de la reserva
     * @param idClient identificador del cliente
     * @param equipmentList lista de equipos asociados
     */
    public EquipmentReservationDraftRequest(
            int idDraft,
            Reservation reservation,
            int idClient,
            List<RXE> equipmentList
    ) {

        this.idDraft = idDraft;
        this.reservation = reservation;
        this.idClient = idClient;
        this.equipmentList = equipmentList;
    }

    /**
     * Obtiene el identificador del draft.
     *
     * @return identificador del draft
     */
    public int getIdDraft() {

        return idDraft;
    }

    /**
     * Define el identificador del draft.
     *
     * @param idDraft identificador del draft
     */
    public void setIdDraft(int idDraft) {

        this.idDraft = idDraft;
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