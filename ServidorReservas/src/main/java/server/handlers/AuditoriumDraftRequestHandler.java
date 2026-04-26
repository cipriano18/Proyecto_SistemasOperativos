package server.handlers;

import controller.AuditoriumDraftController;
import dto.AuditoriumDraftRequest;
import dto.CalendarRequest;
import service.Response;

public class AuditoriumDraftRequestHandler {

    public static Response handle(String command, Object obj) {

        switch (command.toUpperCase()) {

            case "START_AUDITORIUM_DRAFT": {
                AuditoriumDraftRequest request = (AuditoriumDraftRequest) obj;

                System.out.println("---- START_AUDITORIUM_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                if (request != null) {
                    System.out.println("IdClient: " + request.getIdClient());
                    System.out.println("Reservation: " + request.getReservation());
                    System.out.println("AuditoriumDraft: " + request.getAuditoriumDraft());
                }

                return AuditoriumDraftController.startAuditoriumDraft(request);
            }

            case "UPDATE_AUDITORIUM_DRAFT": {
                AuditoriumDraftRequest request = (AuditoriumDraftRequest) obj;

                System.out.println("---- UPDATE_AUDITORIUM_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                if (request != null) {
                    System.out.println("IdDraft: " + request.getIdDraft());
                    System.out.println("IdClient: " + request.getIdClient());
                    System.out.println("Reservation: " + request.getReservation());
                    System.out.println("AuditoriumDraft: " + request.getAuditoriumDraft());
                    System.out.println("EquipmentList: " + request.getEquipmentList());
                }

                return AuditoriumDraftController.updateAuditoriumDraft(request);
            }

            case "DISCARD_AUDITORIUM_DRAFT": {
                AuditoriumDraftRequest request = (AuditoriumDraftRequest) obj;

                System.out.println("---- DISCARD_AUDITORIUM_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                if (request != null) {
                    System.out.println("IdDraft: " + request.getIdDraft());
                    System.out.println("IdClient: " + request.getIdClient());
                }

                return AuditoriumDraftController.discardAuditoriumDraft(
                        request.getIdDraft(),
                        request.getIdClient()
                );
            }

            case "CONFIRM_AUDITORIUM_DRAFT": {
                AuditoriumDraftRequest request = (AuditoriumDraftRequest) obj;

                System.out.println("---- CONFIRM_AUDITORIUM_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                if (request != null) {
                    System.out.println("IdDraft: " + request.getIdDraft());
                    System.out.println("IdClient: " + request.getIdClient());
                }

                return AuditoriumDraftController.confirmAuditoriumDraft(
                        request.getIdDraft(),
                        request.getIdClient()
                );
            }

            case "GET_CALENDAR_AUDITORIUM": {
                CalendarRequest calendarRequest = (CalendarRequest) obj;

                System.out.println("---- GET_CALENDAR_AUDITORIUM ----");
                System.out.println("Objeto recibido: " + calendarRequest);

                return AuditoriumDraftController.getAuditoriumCalendarBlocks(
                        calendarRequest.getMonth(),
                        calendarRequest.getYear()
                );
            }

            default:
                return new Response(false, "Comando de auditorio no reconocido", null);
        }
    }
}
