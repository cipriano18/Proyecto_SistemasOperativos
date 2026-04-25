package controller;

import database.EquipmentReservationDraftDAO;
import database.ReservationDAO;
import java.util.ArrayList;
import java.util.List;
import model.CalendarBlock;
import draft.EquipmentReservationDraft;
import dto.EquipmentReservationDraftRequest;
import dto.EquipmentReservationRequest;
import service.Response;
import model.RXE;
import model.Reservation;

public class ReservationDraftController {

    public static Response getCalendarBlocks(int month, int year) {

        if (month <= 0 || month > 12) {
            return new Response(false, "El mes es inválido", null);
        }

        if (year <= 0) {
            return new Response(false, "El año es inválido", null);
        }

        List<CalendarBlock> result = new ArrayList<>();

        List<CalendarBlock> reserved = ReservationDAO.getReservedBlocksByMonth(month, year);
        List<CalendarBlock> blocked = EquipmentReservationDraftDAO.getBlockedDraftsByMonth(month, year);

        result.addAll(reserved);
        result.addAll(blocked);

        return new Response(true, "Calendario obtenido correctamente", result);
    }

    public static Response startEquipmentDraft(EquipmentReservationDraftRequest request) {

        if (request == null) {
            return new Response(false, "La solicitud de la reserva temporal es obligatoria", null);
        }

        int idClient = request.getIdClient();
        Reservation reservation = request.getReservation();

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        if (reservation == null) {
            return new Response(false, "La reserva base es obligatoria", null);
        }

        if (reservation.getReservationDate() == null) {
            return new Response(false, "La fecha de reserva es obligatoria", null);
        }

        if (reservation.getIdSection() <= 0) {
            return new Response(false, "La sección es obligatoria", null);
        }

        EquipmentReservationDraft draft = EquipmentReservationDraftDAO.createDraft(idClient, reservation);

        if (draft == null) {
            return new Response(
                    false,
                    "Ya existe una reserva en proceso para esa fecha y sección, espere un momento e inténtelo de nuevo.",
                    null
            );
        }

        return new Response(true, "Reserva temporal creada correctamente", draft);
    }

    public static Response updateEquipmentDraft(EquipmentReservationDraftRequest request) {

        if (request == null) {
            return new Response(false, "La solicitud de actualización es obligatoria", null);
        }

        if (request.getIdDraft() <= 0) {
            return new Response(false, "El id del draft es obligatorio", null);
        }

        if (request.getIdClient() <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        EquipmentReservationDraft existingDraft = EquipmentReservationDraftDAO.getDraftById(request.getIdDraft());
        if (existingDraft == null) {
            return new Response(false, "Reserva temporal no encontrada o expirada", null);
        }

        if (existingDraft.getIdClient() != request.getIdClient()) {
            return new Response(false, "La reserva temporal no pertenece a este cliente", null);
        }

        List<RXE> equipmentList = request.getEquipmentList();
        if (equipmentList == null) {
            equipmentList = new ArrayList<>();
        }

        for (RXE item : equipmentList) {
            if (item.getIdEquipment() <= 0) {
                return new Response(false, "Equipo inválido", null);
            }

            if (item.getQuantity() <= 0) {
                return new Response(false, "La cantidad debe ser mayor que cero", null);
            }
        }

        existingDraft.setEquipmentList(equipmentList);

        boolean updated = EquipmentReservationDraftDAO.updateDraft(existingDraft);

        if (!updated) {
            return new Response(false, "No se pudo actualizar la reserva temporal", null);
        }

        return new Response(true, "Reserva temporal actualizada correctamente", existingDraft);
    }

    public static Response getEquipmentDraftById(int idDraft) {

        if (idDraft <= 0) {
            return new Response(false, "El id del draft es obligatorio", null);
        }

        EquipmentReservationDraft draft = EquipmentReservationDraftDAO.getDraftById(idDraft);

        if (draft == null) {
            return new Response(false, "Reserva temporal no encontrada o expirada", null);
        }

        return new Response(true, "Reserva temporal obtenida correctamente", draft);
    }

    public static Response getEquipmentDraftByClientId(int idClient) {

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        EquipmentReservationDraft draft = EquipmentReservationDraftDAO.getDraftByClientId(idClient);

        if (draft == null) {
            return new Response(false, "No hay reservas temporales activas para este cliente", null);
        }

        return new Response(true, "Reserva temporal obtenida correctamente", draft);
    }

    public static Response discardEquipmentDraft(int idDraft, int idClient) {

        if (idDraft <= 0) {
            return new Response(false, "El id de la reserva temporal es obligatorio", null);
        }

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        EquipmentReservationDraft draft = EquipmentReservationDraftDAO.getDraftById(idDraft);

        if (draft == null) {
            return new Response(false, "Reserva temporal no encontrada o expirada", null);
        }

        if (draft.getIdClient() != idClient) {
            return new Response(false, "La reserva temporal no pertenece a este cliente", null);
        }

        boolean deleted = EquipmentReservationDraftDAO.deleteDraft(idDraft);

        if (!deleted) {
            return new Response(false, "No se pudo descartar la reserva temporal", null);
        }

        return new Response(true, "Reserva temporal descartada correctamente", draft);
    }

    public static Response confirmEquipmentDraft(int idDraft, int idClient) {

        if (idDraft <= 0) {
            return new Response(false, "El id de la reserva temporal es obligatorio", null);
        }

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        EquipmentReservationDraft draft = EquipmentReservationDraftDAO.getDraftById(idDraft);

        if (draft == null) {
            return new Response(false, "Reserva temporal no encontrada o expirada", null);
        }

        if (draft.getIdClient() != idClient) {
            return new Response(false, "La reserva temporal no pertenece a este cliente", null);
        }

        if (draft.getEquipmentList() == null || draft.getEquipmentList().isEmpty()) {
            return new Response(false, "Debe seleccionar al menos un equipo", null);
        }

        EquipmentReservationRequest request = new EquipmentReservationRequest(
                draft.getReservation(),
                draft.getIdClient(),
                draft.getEquipmentList()
        );

        Response reservationResponse = ReservationController.createEquipmentReservation(request);

        if (!reservationResponse.isSuccess()) {
            return reservationResponse;
        }

        EquipmentReservationDraftDAO.deleteDraft(idDraft);

        return new Response(true, "Reservación confirmada correctamente", draft);
    }
}
