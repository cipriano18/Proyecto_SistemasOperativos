package utils;

import model.Reservation;
import service.Response;

/**
 * Conserva temporalmente el estado de un flujo de reserva en curso.
 */
public class DraftContainer {

    private static DraftContainer instance;

    private Response temp_device_reservation;
    private String flowType; // "DEVICE" o "AUDITORIUM"
    private Reservation selectedReservation;

    /**
     * Crea el contenedor temporal de drafts.
     */
    private DraftContainer() {
    }

    /**
     * Obtiene la instancia compartida del contenedor.
     *
     * @return instancia unica de {@code DraftContainer}
     */
    public static DraftContainer getInstance() {
        if (instance == null) {
            instance = new DraftContainer();
        }
        return instance;
    }

    public void setDraftResponse(Response draftResponse) {
        this.temp_device_reservation = draftResponse;
    }

    public Response getDraftResponse() {
        return temp_device_reservation;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setSelectedReservation(Reservation selectedReservation) {
        this.selectedReservation = selectedReservation;
    }

    public Reservation getSelectedReservation() {
        return selectedReservation;
    }

    /**
     * Limpia unicamente la respuesta temporal del draft de equipos.
     */
    public void clearTempReservationDevice() {
        temp_device_reservation = null;
    }

    /**
     * Limpia el tipo de flujo actualmente almacenado.
     */
    public void clearFlowType() {
        flowType = null;
    }

    /**
     * Limpia toda la informacion temporal del flujo de reserva.
     */
    public void clearAll() {
        temp_device_reservation = null;
        flowType = null;
        selectedReservation = null;
    }
}
