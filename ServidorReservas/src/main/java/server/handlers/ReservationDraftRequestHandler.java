/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.ReservationDraftController;
import dto.CalendarRequest;
import dto.EquipmentReservationDraftRequest;
import service.Response;

/**
 *
 * @author Cipriano
 */
public class ReservationDraftRequestHandler {

    public static Response handle(String command, Object obj) {

        switch (command.toUpperCase()) {

            case "START_EQUIPMENT_DRAFT": {
                EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                System.out.println("---- START_EQUIPMENT_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationDraftController.startEquipmentDraft(request);
            }

            case "UPDATE_EQUIPMENT_DRAFT": {
                EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                System.out.println("---- UPDATE_EQUIPMENT_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationDraftController.updateEquipmentDraft(request);
            }

            case "GET_EQUIPMENT_DRAFT_BY_ID": {
                Integer idDraft = (Integer) obj;

                System.out.println("---- GET_EQUIPMENT_DRAFT_BY_ID ----");
                System.out.println("IdDraft recibido: " + idDraft);

                return ReservationDraftController.getEquipmentDraftById(idDraft);
            }

            case "GET_EQUIPMENT_DRAFT_BY_CLIENT_ID": {
                Integer idClient = (Integer) obj;

                System.out.println("---- GET_EQUIPMENT_DRAFT_BY_CLIENT_ID ----");
                System.out.println("IdClient recibido: " + idClient);

                return ReservationDraftController.getEquipmentDraftByClientId(idClient);
            }

            case "DISCARD_EQUIPMENT_DRAFT": {
                EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                System.out.println("---- DISCARD_EQUIPMENT_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationDraftController.discardEquipmentDraft(
                        request.getIdDraft(),
                        request.getIdClient()
                );
            }

            case "CONFIRM_EQUIPMENT_DRAFT": {
                EquipmentReservationDraftRequest request = (EquipmentReservationDraftRequest) obj;

                System.out.println("---- CONFIRM_EQUIPMENT_DRAFT ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationDraftController.confirmEquipmentDraft(
                        request.getIdDraft(),
                        request.getIdClient()
                );
            }

            case "GET_CALENDAR_BLOCKS": {
                CalendarRequest request = (CalendarRequest) obj;

                System.out.println("---- GET_CALENDAR_BLOCKS ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationDraftController.getCalendarBlocks(
                        request.getMonth(),
                        request.getYear()
                );
            }

            default:
                return new Response(false, "Comando de drafts no reconocido", null);
        }
    }
}
