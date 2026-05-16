package dto;

import draft.AuditoriumDraft;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import model.RXE;
import model.Reservation;

/**
 * Representa la informacion necesaria para trabajar un draft de auditorio.
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
     * Crea una solicitud vacia de draft de auditorio.
     */
    public AuditoriumDraftRequest() {
    }

    /**
     * Crea una solicitud con la informacion completa del draft.
     *
     * @param idDraft identificador del draft
     * @param idClient identificador del cliente
     * @param reservation reserva base asociada
     * @param auditoriumDraft datos especificos del auditorio
     * @param equipmentList equipos asociados al draft
     */
    public AuditoriumDraftRequest(
            int idDraft, 
            int idClient, 
            Reservation reservation, 
            AuditoriumDraft auditoriumDraft, 
            List<RXE> equipmentList) {
        
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

    public void setAuditoriumDraft(AuditoriumDraft auditoriumDraft) {
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

    /**
     * Indica si el draft ya vencio segun la hora actual.
     *
     * @return {@code true} si el draft ya expiro
     */
    public boolean isExpired() {
        return expiresAt != null
                && System.currentTimeMillis() > expiresAt.getTime();
    }
}
