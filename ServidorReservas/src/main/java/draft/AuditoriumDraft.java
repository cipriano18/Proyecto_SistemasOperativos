package draft;

import java.io.Serializable;

/**
 * Modelo que representa un borrador de reservación de auditorio.
 *
 * <p>
 * Contiene la información relacionada con el evento, cantidad de asistentes y
 * observaciones asociadas al borrador.
 * </p>
 *
 * @author Cipriano
 */
public class AuditoriumDraft implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idAuditoriumDraft;
    private int idDraft;
    private String eventName;
    private int attendeesCount;
    private String observations;

    /**
     * Constructor vacío.
     */
    public AuditoriumDraft() {
    }

    /**
     * Constructor con parámetros.
     *
     * @param idAuditoriumDraft identificador del draft de auditorio
     * @param idDraft identificador del draft principal
     * @param eventName nombre del evento
     * @param attendeesCount cantidad de asistentes
     * @param observations observaciones adicionales
     */
    public AuditoriumDraft(
            int idAuditoriumDraft,
            int idDraft,
            String eventName,
            int attendeesCount,
            String observations
    ) {

        this.idAuditoriumDraft = idAuditoriumDraft;
        this.idDraft = idDraft;
        this.eventName = eventName;
        this.attendeesCount = attendeesCount;
        this.observations = observations;
    }

    /**
     * Obtiene el identificador del draft de auditorio.
     *
     * @return identificador del draft de auditorio
     */
    public int getIdAuditoriumDraft() {

        return idAuditoriumDraft;
    }

    /**
     * Define el identificador del draft de auditorio.
     *
     * @param idAuditoriumDraft identificador del draft
     */
    public void setIdAuditoriumDraft(
            int idAuditoriumDraft
    ) {

        this.idAuditoriumDraft = idAuditoriumDraft;
    }

    /**
     * Obtiene el identificador del draft principal.
     *
     * @return identificador del draft
     */
    public int getIdDraft() {

        return idDraft;
    }

    /**
     * Define el identificador del draft principal.
     *
     * @param idDraft identificador del draft
     */
    public void setIdDraft(int idDraft) {

        this.idDraft = idDraft;
    }

    /**
     * Obtiene el nombre del evento.
     *
     * @return nombre del evento
     */
    public String getEventName() {

        return eventName;
    }

    /**
     * Define el nombre del evento.
     *
     * @param eventName nombre del evento
     */
    public void setEventName(String eventName) {

        this.eventName = eventName;
    }

    /**
     * Obtiene la cantidad de asistentes.
     *
     * @return cantidad de asistentes
     */
    public int getAttendeesCount() {

        return attendeesCount;
    }

    /**
     * Define la cantidad de asistentes.
     *
     * @param attendeesCount cantidad de asistentes
     */
    public void setAttendeesCount(
            int attendeesCount
    ) {

        this.attendeesCount = attendeesCount;
    }

    /**
     * Obtiene las observaciones del evento.
     *
     * @return observaciones del evento
     */
    public String getObservations() {

        return observations;
    }

    /**
     * Define las observaciones del evento.
     *
     * @param observations observaciones adicionales
     */
    public void setObservations(
            String observations
    ) {

        this.observations = observations;
    }
}
