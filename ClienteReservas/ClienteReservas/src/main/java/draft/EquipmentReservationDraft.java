package draft;

import dto.EquipmentReservationRequest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import model.RXE;
import model.Reservation;

public class EquipmentReservationDraft extends EquipmentReservationRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idDraft;
    private Timestamp createdAt;
    private Timestamp expiresAt;

    public EquipmentReservationDraft() {
        super();
    }

    public EquipmentReservationDraft(int idDraft, Timestamp createdAt, Timestamp expiresAt,
                                     Reservation reservation, int idClient, List<RXE> equipmentList) {
        super(reservation, idClient, equipmentList);
        this.idDraft = idDraft;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public int getIdDraft() {
        return idDraft;
    }

    public void setIdDraft(int idDraft) {
        this.idDraft = idDraft;
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
        return System.currentTimeMillis() > expiresAt.getTime();
    }

    @Override
    public String toString() {
        return "EquipmentReservationDraft{" + "idDraft=" + idDraft + ", createdAt=" + createdAt + ", expiresAt=" + expiresAt + '}';
    }
    
}