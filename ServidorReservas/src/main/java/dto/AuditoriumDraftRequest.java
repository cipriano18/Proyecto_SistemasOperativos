package dto;

import draft.AuditoriumDraft;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import model.Reservation;
import model.RXE;

/**
 * DTO utilizado para transportar la información de una reserva temporal
 * de auditorio.
 *
 * <p>
 * Contiene:
 * </p>
 *
 * <ul>
 *     <li>Información del draft</li>
 *     <li>Cliente asociado</li>
 *     <li>Reserva base</li>
 *     <li>Datos específicos del auditorio</li>
 *     <li>Lista de equipos asociados</li>
 *     <li>Fecha de creación y expiración del draft</li>
 * </ul>
 *
 * @author Cipriano
 */
public class AuditoriumDraftRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idDraft;
    private int idClient;
    private Reservation reservation;
    private AuditoriumDraft auditoriumDraft;
    private List<RXE> equipmentList;
    private Timestamp createdAt;
    private Timestamp expiresAt;

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

    public int getIdDraft() {
        return idDraft;
    }

    public void setIdDraft(int idDraft) {
        this.idDraft = idDraft;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public AuditoriumDraft getAuditoriumDraft() {
        return auditoriumDraft;
    }

    public void setAuditoriumDraft(
            AuditoriumDraft auditoriumDraft
    ) {
        this.auditoriumDraft = auditoriumDraft;
    }

    public List<RXE> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<RXE> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null
                && System.currentTimeMillis() > expiresAt.getTime();
    }
}