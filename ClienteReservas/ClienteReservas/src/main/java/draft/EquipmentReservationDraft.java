package draft;

import dto.EquipmentReservationRequest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import model.RXE;
import model.Reservation;

/**
 * Representa un borrador temporal de reserva de equipos.
 */
public class EquipmentReservationDraft 
        extends EquipmentReservationRequest 
        implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int idDraft;
    private Timestamp createdAt;
    private Timestamp expiresAt;

    /**
     * Crea un borrador vacio de reserva de equipos.
     */
    public EquipmentReservationDraft() {
        super();
    }

    /**
     * Crea un borrador con su informacion base y los equipos asociados.
     *
     * @param idDraft identificador del borrador
     * @param createdAt fecha de creacion del borrador
     * @param expiresAt fecha de expiracion del borrador
     * @param reservation reserva base asociada
     * @param idClient identificador del cliente
     * @param equipmentList equipos incluidos en el borrador
     */
    public EquipmentReservationDraft(
            int idDraft, Timestamp createdAt, Timestamp expiresAt,
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

    /**
     * Indica si el borrador ya vencio segun la hora actual.
     *
     * @return {@code true} si el borrador ya expiro
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt.getTime();
    }

    /**
     * Devuelve una representacion textual resumida del borrador.
     *
     * @return cadena con los datos principales del borrador
     */
    @Override
    public String toString() {
        return "EquipmentReservationDraft{" 
                + "idDraft=" + idDraft 
                + ", createdAt=" 
                + createdAt 
                + ", expiresAt=" 
                + expiresAt + '}';
    }
    
}
