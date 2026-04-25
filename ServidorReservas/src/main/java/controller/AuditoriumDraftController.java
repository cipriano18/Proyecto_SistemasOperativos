package controller;

import database.AuditoriumDraftDAO;
import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import java.util.List;
import model.RXE;
import model.Reservation;
import service.Response;

/**
 *
 * @author Reyner
 */
public class AuditoriumDraftController {
    public static Response startAuditoriumDraft(AuditoriumDraftRequest request) {

        if (request == null) {
            return new Response(false, "La solicitud de reserva temporal de auditorio es obligatoria", null);
        }

        if (request.getIdClient() <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        Reservation reservation = request.getReservation();

        if (reservation == null) {
            return new Response(false, "La reserva base es obligatoria", null);
        }

        if (reservation.getReservationDate() == null) {
            return new Response(false, "La fecha de reserva es obligatoria", null);
        }

        if (reservation.getIdSection() <= 0) {
            return new Response(false, "La sección es obligatoria", null);
        }

        AuditoriumDraft auditoriumDraft = request.getAuditoriumDraft();

        if (auditoriumDraft == null) {
            return new Response(false, "Los datos del auditorio son obligatorios", null);
        }

        if (auditoriumDraft.getEventName() == null || auditoriumDraft.getEventName().isBlank()) {
            return new Response(false, "El nombre del evento es obligatorio", null);
        }

        if (auditoriumDraft.getAttendeesCount() < 0) {
            return new Response(false, "La cantidad de asistentes no puede ser negativa", null);
        }

        List<RXE> equipmentList = request.getEquipmentList();

        if (equipmentList != null) {
            for (RXE item : equipmentList) {
                if (item == null) {
                    return new Response(false, "La lista contiene un equipo inválido", null);
                }

                if (item.getIdEquipment() <= 0) {
                    return new Response(false, "Equipo inválido", null);
                }

                if (item.getQuantity() <= 0) {
                    return new Response(false, "La cantidad del equipo debe ser mayor que cero", null);
                }
            }
        }

        AuditoriumDraftRequest createdDraft = AuditoriumDraftDAO.createDraft(request);

        if (createdDraft == null) {
            return new Response(
                    false,
                    "No se pudo crear la reserva temporal. La fecha y sección ya podrían estar ocupadas",
                    null
            );
        }

        return new Response(true, "Reserva temporal de auditorio creada correctamente", createdDraft);
    }
    
    public static Response updateAuditoriumDraft(AuditoriumDraftRequest request) {

        if (request == null) {
            return new Response(false, "La solicitud de actualización es obligatoria", null);
        }

        if (request.getIdDraft() <= 0) {
            return new Response(false, "El id de la reserva temporal es obligatorio", null);
        }

        AuditoriumDraft auditoriumDraft = request.getAuditoriumDraft();

        if (auditoriumDraft == null) {
            return new Response(false, "Los datos del auditorio son obligatorios", null);
        }

        if (auditoriumDraft.getEventName() == null || auditoriumDraft.getEventName().isBlank()) {
            return new Response(false, "El nombre del evento es obligatorio", null);
        }

        if (auditoriumDraft.getAttendeesCount() < 0) {
            return new Response(false, "La cantidad de asistentes no puede ser negativa", null);
        }

        if (auditoriumDraft.getAttendeesCount() > 200) {
            return new Response(false, "La cantidad de asistentes no puede superar 200 personas", null);
        }

        List<RXE> equipmentList = request.getEquipmentList();

        if (equipmentList != null) {
            for (RXE item : equipmentList) {
                if (item == null) {
                    return new Response(false, "La lista contiene un equipo inválido", null);
                }

                if (item.getIdEquipment() <= 0) {
                    return new Response(false, "Equipo inválido", null);
                }

                if (item.getQuantity() <= 0) {
                    return new Response(false, "La cantidad del equipo debe ser mayor que cero", null);
                }
            }
        }

        boolean updated = AuditoriumDraftDAO.updateDraft(request);

        if (!updated) {
            return new Response(false, "No se pudo actualizar la reserva temporal de auditorio", null);
        }

        return new Response(true, "Reserva temporal de auditorio actualizada correctamente", request);
    }
}
