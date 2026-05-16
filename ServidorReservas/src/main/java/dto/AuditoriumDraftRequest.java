package dto;

import draft.AuditoriumDraft;
import java.io.Serializable;
import java.util.List;
import model.RXE;
import model.Reservation;

/**
 * DTO utilizado para transportar la información de una reserva temporal de
 * auditorio.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 * <li>Información del draft</li>
 * <li>Cliente asociado</li>
 * <li>Reserva base</li>
 * <li>Datos específicos del auditorio</li>
 * <li>Lista de equipos asociados</li>
 * </ul>
 *
 * @author Cipriano
 */
public class AuditoriumDraftRequest
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idDraft;

    private int idClient;

    private Reservation reservation;

    private AuditoriumDraft auditoriumDraft;

    private List<RXE> equipmentList;

    /**
     * Constructor vacío.
     */
    public AuditoriumDraftRequest() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param idDraft identificador del draft
     * @param idClient identificador del cliente
     * @param reservation información de la reserva
     * @param auditoriumDraft información del draft de auditorio
     * @param equipmentList lista de equipos asociados
     */
    public AuditoriumDraftRequest(
            int idDraft,
            int idClient,
            Reservation reservation,
            AuditoriumDraft auditoriumDraft,
            List<RXE> equipmentList
    ) {

        this.idDraft = idDraft;
        this.idClient = idClient;
        this.reservation = reservation;
        this.auditoriumDraft = auditoriumDraft;
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
     * Obtiene la información del draft de auditorio.
     *
     * @return información del draft de auditorio
     */
    public AuditoriumDraft getAuditoriumDraft() {

        return auditoriumDraft;
    }

    /**
     * Define la información del draft de auditorio.
     *
     * @param auditoriumDraft draft de auditorio
     */
    public void setAuditoriumDraft(
            AuditoriumDraft auditoriumDraft
    ) {

        this.auditoriumDraft = auditoriumDraft;
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
