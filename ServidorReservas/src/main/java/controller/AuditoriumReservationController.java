package controller;

import database.AuditoriumReservationDAO;
import dto.AuditoriumDraftRequest;
import dto.AuditoriumReservationRequest;
import java.util.List;
import service.Response;

/**
 * Controlador encargado de gestionar las reservaciones confirmadas del
 * auditorio.
 *
 * <p>
 * Permite consultar y eliminar reservaciones de auditorio por identificador,
 * cliente o periodo mensual.
 * </p>
 *
 * @author Reyner
 */
public class AuditoriumReservationController {

    /**
     * Obtiene una reservación de auditorio por su identificador.
     *
     * @param idReservation identificador de la reservación
     * @return respuesta con la reservación encontrada
     */
    public static Response getAuditoriumReservationById(
            int idReservation
    ) {

        if (idReservation <= 0) {
            return new Response(
                    false,
                    "El id de la reservación es inválido",
                    null
            );
        }

        AuditoriumDraftRequest request
                = AuditoriumReservationDAO
                        .getAuditoriumReservationById(
                                idReservation
                        );

        if (request == null) {
            return new Response(
                    false,
                    "Reservación de auditorio no encontrada",
                    null
            );
        }

        return new Response(
                true,
                "Reservación de auditorio "
                + "obtenida correctamente",
                request
        );
    }

    /**
     * Obtiene las reservaciones de auditorio asociadas a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta con la lista de reservaciones
     */
    public static Response getAuditoriumReservationsByClientId(
            int idClient
    ) {

        if (idClient <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        List<AuditoriumDraftRequest> reservations
                = AuditoriumReservationDAO
                        .getAuditoriumReservationsByClientId(
                                idClient
                        );

        if (reservations == null || reservations.isEmpty()) {
            return new Response(
                    false,
                    "No se encontraron reservaciones "
                    + "de auditorio para este cliente",
                    null
            );
        }

        return new Response(
                true,
                "Reservaciones de auditorio "
                + "obtenidas correctamente",
                reservations
        );
    }

    /**
     * Obtiene las reservaciones de auditorio de un mes y año determinados.
     *
     * @param month mes consultado
     * @param year año consultado
     * @return respuesta con las reservaciones encontradas
     */
    public static Response getAuditoriumReservationsByMonth(
            int month,
            int year
    ) {

        if (month <= 0 || month > 12) {
            return new Response(
                    false,
                    "Mes inválido",
                    null
            );
        }

        if (year <= 0) {
            return new Response(
                    false,
                    "Año inválido",
                    null
            );
        }

        List<AuditoriumReservationRequest> reservations
                = AuditoriumReservationDAO
                        .getAuditoriumReservationsByMonth(
                                month,
                                year
                        );

        if (reservations == null || reservations.isEmpty()) {
            return new Response(
                    false,
                    "No se encontraron reservaciones "
                    + "de auditorio para el periodo indicado",
                    null
            );
        }

        return new Response(
                true,
                "Reservaciones de auditorio "
                + "obtenidas correctamente",
                reservations
        );
    }

    /**
     * Elimina una reservación de auditorio por su identificador.
     *
     * @param idReservation identificador de la reservación
     * @param idClient identificador del cliente
     * @return respuesta del proceso
     */
    public static Response deleteAuditoriumReservationById(
            int idReservation,
            int idClient
    ) {

        if (idReservation <= 0) {
            return new Response(
                    false,
                    "El id de la reservación es inválido",
                    null
            );
        }

        if (idClient <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        boolean deleted
                = AuditoriumReservationDAO
                        .deleteAuditoriumReservationById(
                                idReservation,
                                idClient
                        );

        if (!deleted) {
            return new Response(
                    false,
                    "No se pudo eliminar la reservación "
                    + "de auditorio",
                    null
            );
        }

        return new Response(
                true,
                "Reservación de auditorio "
                + "eliminada correctamente",
                idReservation
        );
    }

    /**
     * Elimina todas las reservaciones de auditorio asociadas a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del proceso
     */
    public static Response deleteAuditoriumReservationsByClientId(
            int idClient
    ) {

        if (idClient <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        boolean deleted
                = AuditoriumReservationDAO
                        .deleteAuditoriumReservationsByClientId(
                                idClient
                        );

        if (!deleted) {
            return new Response(
                    false,
                    "No se pudieron eliminar las "
                    + "reservaciones de auditorio "
                    + "del cliente",
                    null
            );
        }

        return new Response(
                true,
                "Reservaciones de auditorio del cliente "
                + "eliminadas correctamente",
                idClient
        );
    }
}
