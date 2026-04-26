package server.handlers;

import controller.AuditoriumReservationController;
import dto.AuditoriumDraftRequest;
import service.Response;

/**
 *
 * @author Reyner
 */
public class AuditoriumReservationRequestHandler {

    public static Response handle(String command, Object obj) {

        switch (command.toUpperCase()) {

            case "GET_AUDITORIUM_RESERVATION_BY_ID": {
                Integer idReservation = (Integer) obj;

                System.out.println("---- GET_AUDITORIUM_RESERVATION_BY_ID ----");
                System.out.println("IdReservation recibido: " + idReservation);

                return AuditoriumReservationController.getAuditoriumReservationById(idReservation);
            }

            case "GET_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID": {
                Integer idClient = (Integer) obj;

                System.out.println("---- GET_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID ----");
                System.out.println("IdClient recibido: " + idClient);

                return AuditoriumReservationController.getAuditoriumReservationsByClientId(idClient);
            }

            case "DELETE_AUDITORIUM_RESERVATION_BY_ID": {
                AuditoriumDraftRequest request = (AuditoriumDraftRequest) obj;

                System.out.println("---- DELETE_AUDITORIUM_RESERVATION_BY_ID ----");
                System.out.println("Objeto recibido: " + request);

                return AuditoriumReservationController.deleteAuditoriumReservationById(
                        request.getReservation().getIdReservation(),
                        request.getIdClient()
                );
            }

            case "DELETE_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID": {
                Integer idClient = (Integer) obj;

                System.out.println("---- DELETE_AUDITORIUM_RESERVATIONS_BY_CLIENT_ID ----");
                System.out.println("IdClient recibido: " + idClient);

                return AuditoriumReservationController.deleteAuditoriumReservationsByClientId(idClient);
            }

            default:
                return new Response(false, "Comando de reservación de auditorio no reconocido", null);
        }
    }
}