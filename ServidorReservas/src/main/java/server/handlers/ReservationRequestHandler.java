package server.handlers;

import controller.ReservationController;
import dto.EquipmentReservationRequest;
import service.Response;

/**
 * Manejador encargado de procesar las solicitudes relacionadas con
 * reservaciones de equipos.
 *
 * <p>
 * Recibe comandos provenientes del servidor y delega las operaciones
 * correspondientes al {@code ReservationController}.
 * </p>
 *
 * @author Reyner
 */
public class ReservationRequestHandler {

    /**
     * Procesa un comando relacionado con reservaciones de equipos.
     *
     * @param command comando recibido
     * @param obj objeto asociado al comando
     * @return respuesta del proceso
     */
    public static Response handle(
            String command,
            Object obj
    ) {

        switch (command.toUpperCase()) {

            case "UPDATE_EQUIPMENT_RESERVATION": {

                EquipmentReservationRequest request
                        = (EquipmentReservationRequest) obj;

                System.out.println(
                        "---- UPDATE_EQUIPMENT_RESERVATION ----"
                );

                System.out.println(
                        "Objeto recibido: "
                        + request
                );

                return ReservationController
                        .updateEquipmentReservation(
                                request
                        );
            }

            case "GET_RESERVATION_BY_ID": {

                Integer idReservation
                        = (Integer) obj;

                System.out.println(
                        "---- GET_RESERVATION_BY_ID ----"
                );

                System.out.println(
                        "IdReservation recibido: "
                        + idReservation
                );

                return ReservationController
                        .getReservationById(
                                idReservation
                        );
            }

            case "GET_RESERVATIONS_BY_CLIENT_ID": {

                Integer idClient
                        = (Integer) obj;

                System.out.println(
                        "---- GET_RESERVATIONS_BY_CLIENT_ID ----"
                );

                System.out.println(
                        "IdClient recibido: "
                        + idClient
                );

                return ReservationController
                        .getReservationsByClientId(
                                idClient
                        );
            }

            case "DELETE_RESERVATION_BY_ID": {

                EquipmentReservationRequest request
                        = (EquipmentReservationRequest) obj;

                System.out.println(
                        "---- DELETE_RESERVATION_BY_ID ----"
                );

                System.out.println(
                        "Objeto recibido: "
                        + request
                );

                return ReservationController
                        .deleteReservationById(
                                request.getReservation()
                                        .getIdReservation(),
                                request.getIdClient()
                        );
            }

            case "DELETE_RESERVATIONS_BY_CLIENT_ID": {

                Integer idClient
                        = (Integer) obj;

                System.out.println(
                        "---- DELETE_RESERVATIONS_BY_CLIENT_ID ----"
                );

                System.out.println(
                        "IdClient recibido: "
                        + idClient
                );

                return ReservationController
                        .deleteReservationsByClientId(
                                idClient
                        );
            }

            case "GET_EQUIPMENT_RESERVATIONS_BY_MONTH": {

                int[] data
                        = (int[]) obj;

                int month = data[0];

                int year = data[1];

                System.out.println(
                        "---- GET_EQUIPMENT_RESERVATIONS_BY_MONTH ----"
                );

                System.out.println(
                        "Month: " + month
                        + " Year: " + year
                );

                return ReservationController
                        .getEquipmentReservationsByMonth(
                                month,
                                year
                        );
            }

            default:

                return new Response(
                        false,
                        "Comando de reservación "
                        + "no reconocido",
                        null
                );
        }
    }
}
