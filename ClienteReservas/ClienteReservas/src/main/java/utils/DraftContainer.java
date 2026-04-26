package utils;

import model.Reservation;
import service.Response;

public class DraftContainer {

    private static DraftContainer instance;

    private Response temp_device_reservation;
    private String flowType; // "DEVICE" o "AUDITORIUM"
    private Reservation selectedReservation;

    private DraftContainer() {
    }

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

    public void clearTempReservationDevice() {
        temp_device_reservation = null;
    }

    public void clearFlowType() {
        flowType = null;
    }

    public void clearAll() {
        temp_device_reservation = null;
        flowType = null;
        selectedReservation = null;
    }
}
