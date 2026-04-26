package controller;

import database.AuditoriumReservationDAO;
import dto.AuditoriumDraftRequest;
import java.util.List;
import service.Response;

/**
 *
 * @author Reyner
 */
public class AuditoriumReservationController {

    public static Response getAuditoriumReservationById(int idReservation) {

        if (idReservation <= 0) {
            return new Response(false, "El id de la reservación es inválido", null);
        }

        AuditoriumDraftRequest request =
                AuditoriumReservationDAO.getAuditoriumReservationById(idReservation);

        if (request == null) {
            return new Response(false, "Reservación de auditorio no encontrada", null);
        }

        return new Response(true, "Reservación de auditorio obtenida correctamente", request);
    }

    public static Response getAuditoriumReservationsByClientId(int idClient) {

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        List<AuditoriumDraftRequest> reservations =
                AuditoriumReservationDAO.getAuditoriumReservationsByClientId(idClient);

        if (reservations == null || reservations.isEmpty()) {
            return new Response(false, "No se encontraron reservaciones de auditorio para este cliente", null);
        }

        return new Response(true, "Reservaciones de auditorio obtenidas correctamente", reservations);
    }

    public static Response deleteAuditoriumReservationById(int idReservation, int idClient) {

        if (idReservation <= 0) {
            return new Response(false, "El id de la reservación es inválido", null);
        }

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        boolean deleted = AuditoriumReservationDAO.deleteAuditoriumReservationById(
                idReservation,
                idClient
        );

        if (!deleted) {
            return new Response(false, "No se pudo eliminar la reservación de auditorio", null);
        }

        return new Response(true, "Reservación de auditorio eliminada correctamente", idReservation);
    }

    public static Response deleteAuditoriumReservationsByClientId(int idClient) {

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        boolean deleted =
                AuditoriumReservationDAO.deleteAuditoriumReservationsByClientId(idClient);

        if (!deleted) {
            return new Response(false, "No se pudieron eliminar las reservaciones de auditorio del cliente", null);
        }

        return new Response(true, "Reservaciones de auditorio del cliente eliminadas correctamente", idClient);
    }
}