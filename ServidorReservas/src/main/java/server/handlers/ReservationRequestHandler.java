package server.handlers;

import controller.ReservationController;
import dto.EquipmentReservationRequest;
import service.Response;

/**
 *
 * @author Reyner
 */
public class ReservationRequestHandler {

    public static Response handle(String command, Object obj) {

        switch (command.toUpperCase()) {

            case "UPDATE_EQUIPMENT_RESERVATION": {
                EquipmentReservationRequest request = (EquipmentReservationRequest) obj;

                System.out.println("---- UPDATE_EQUIPMENT_RESERVATION ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationController.updateEquipmentReservation(request);
            }

            case "GET_RESERVATION_BY_ID": {
                Integer idReservation = (Integer) obj;

                System.out.println("---- GET_RESERVATION_BY_ID ----");
                System.out.println("IdReservation recibido: " + idReservation);

                return ReservationController.getReservationById(idReservation);
            }

            case "GET_RESERVATIONS_BY_CLIENT_ID": {
                Integer idClient = (Integer) obj;

                System.out.println("---- GET_RESERVATIONS_BY_CLIENT_ID ----");
                System.out.println("IdClient recibido: " + idClient);

                return ReservationController.getReservationsByClientId(idClient);
            }

            case "DELETE_RESERVATION_BY_ID": {
                EquipmentReservationRequest request = (EquipmentReservationRequest) obj;

                System.out.println("---- DELETE_RESERVATION_BY_ID ----");
                System.out.println("Objeto recibido: " + request);

                return ReservationController.deleteReservationById(
                        request.getReservation().getIdReservation(),
                        request.getIdClient()
                );
            }

            case "DELETE_RESERVATIONS_BY_CLIENT_ID": {
                Integer idClient = (Integer) obj;

                System.out.println("---- DELETE_RESERVATIONS_BY_CLIENT_ID ----");
                System.out.println("IdClient recibido: " + idClient);

                return ReservationController.deleteReservationsByClientId(idClient);
            }

            default:
                return new Response(false, "Comando de reservación no reconocido", null);
        }
    }
}