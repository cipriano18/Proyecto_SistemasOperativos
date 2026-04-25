/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import service.Response;

public class DraftContainer {

    private static DraftContainer instance;

    private Response temp_device_reservation;

    private DraftContainer() {
    }

    public static DraftContainer getInstance() {
        if (instance == null) {
            instance = new DraftContainer();
        }
        return instance;
    }

    // SET
    public void setDraftResponse(Response draftResponse) {
        this.temp_device_reservation = draftResponse;
    }

    // GET
    public Response getDraftResponse() {
        return temp_device_reservation;
    }

    // LIMPIAR reserva de dispositivo temporal
    public void clearTempReservationDevice() {
        temp_device_reservation = null;
    }
}
