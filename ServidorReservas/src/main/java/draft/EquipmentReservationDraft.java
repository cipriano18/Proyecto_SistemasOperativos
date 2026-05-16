package draft;

import dto.EquipmentReservationRequest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import model.RXE;
import model.Reservation;

/**
 * Modelo que representa una reserva temporal de equipos.
 *
 * <p>
 * Extiende la clase {@code EquipmentReservationRequest} para incluir
 * información relacionada con el tiempo de creación y expiración del draft.
 * </p>
 *
 * @author Cipriano
 */
public class EquipmentReservationDraft
        extends EquipmentReservationRequest
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idDraft;

    private Timestamp createdAt;

    private Timestamp expiresAt;

    /**
     * Constructor vacío.
     */
    public EquipmentReservationDraft() {

        super();
    }

    /**
     * Constructor con parámetros.
     *
     * @param idDraft identificador del draft
     * @param createdAt fecha de creación
     * @param expiresAt fecha de expiración
     * @param reservation información de la reserva
     * @param idClient identificador del cliente
     * @param equipmentList lista de equipos reservados
     */
    public EquipmentReservationDraft(
            int idDraft,
            Timestamp createdAt,
            Timestamp expiresAt,
            Reservation reservation,
            int idClient,
            List<RXE> equipmentList
    ) {

        super(
                reservation,
                idClient,
                equipmentList
        );

        this.idDraft = idDraft;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
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
     * Obtiene la fecha de creación del draft.
     *
     * @return fecha de creación
     */
    public Timestamp getCreatedAt() {

        return createdAt;
    }

    /**
     * Define la fecha de creación del draft.
     *
     * @param createdAt fecha de creación
     */
    public void setCreatedAt(
            Timestamp createdAt
    ) {

        this.createdAt = createdAt;
    }

    /**
     * Obtiene la fecha de expiración del draft.
     *
     * @return fecha de expiración
     */
    public Timestamp getExpiresAt() {

        return expiresAt;
    }

    /**
     * Define la fecha de expiración del draft.
     *
     * @param expiresAt fecha de expiración
     */
    public void setExpiresAt(
            Timestamp expiresAt
    ) {

        this.expiresAt = expiresAt;
    }

    /**
     * Indica si el draft ha expirado.
     *
     * @return {@code true} si el draft expiró, {@code false} en caso contrario
     */
    public boolean isExpired() {

        return System.currentTimeMillis()
                > expiresAt.getTime();
    }

    /**
     * Retorna una representación en texto del draft.
     *
     * @return representación textual del draft
     */
    @Override
    public String toString() {

        return "EquipmentReservationDraft{"
                + "idDraft=" + idDraft
                + ", createdAt=" + createdAt
                + ", expiresAt=" + expiresAt
                + '}';
    }
}
