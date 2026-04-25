package draft;

import java.io.Serializable;

public class AuditoriumDraft implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idAuditoriumDraft;
    private int idDraft;
    private String eventName;
    private int attendeesCount;
    private String observations;

    public AuditoriumDraft() {
    }

    public AuditoriumDraft(int idAuditoriumDraft, int idDraft, String eventName, int attendeesCount, String observations) {
        this.idAuditoriumDraft = idAuditoriumDraft;
        this.idDraft = idDraft;
        this.eventName = eventName;
        this.attendeesCount = attendeesCount;
        this.observations = observations;
    }

    public int getIdAuditoriumDraft() {
        return idAuditoriumDraft;
    }

    public void setIdAuditoriumDraft(int idAuditoriumDraft) {
        this.idAuditoriumDraft = idAuditoriumDraft;
    }

    public int getIdDraft() {
        return idDraft;
    }

    public void setIdDraft(int idDraft) {
        this.idDraft = idDraft;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(int attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}