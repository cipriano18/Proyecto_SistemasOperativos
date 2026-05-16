package controller;

import database.ReservationDAO;
import java.util.List;
import dto.EquipmentReservationRequest;
import model.RXE;
import model.Reservation;
import service.Response;

/**
 * Controlador encargado de gestionar las reservaciones de equipos.
 *
 * <p>
 * Permite crear, actualizar, consultar y eliminar reservaciones de equipos.
 * </p>
 *
 * @author Cipriano
 */
public class ReservationController {

    /**
     * Crea una nueva reservación de equipos.
     *
     * @param request datos de la reservación
     * @return respuesta del proceso
     */
    public static Response createEquipmentReservation(
            EquipmentReservationRequest request
    ) {

        Response validation
                = validateEquipmentReservationRequest(
                        request,
                        false
                );

        if (!validation.isSuccess()) {
            return validation;
        }

        boolean created
                = ReservationDAO.createEquipmentReservation(
                        request.getReservation(),
                        request.getIdClient(),
                        request.getEquipmentList()
                );

        if (!created) {
            return new Response(
                    false,
                    "No se pudo crear la reservación. "
                    + "Verifique disponibilidad.",
                    null
            );
        }

        return new Response(
                true,
                "Reservación creada correctamente",
                request
        );
    }

    /**
     * Actualiza una reservación de equipos.
     *
     * @param request datos actualizados de la reservación
     * @return respuesta del proceso
     */
    public static Response updateEquipmentReservation(
            EquipmentReservationRequest request
    ) {

        Response validation
                = validateEquipmentReservationRequest(
                        request,
                        true
                );

        if (!validation.isSuccess()) {
            return validation;
        }

        boolean updated
                = ReservationDAO.updateEquipmentReservation(
                        request.getReservation(),
                        request.getIdClient(),
                        request.getEquipmentList()
                );

        if (!updated) {
            return new Response(
                    false,
                    "No se pudo actualizar la reservación. "
                    + "Verifique disponibilidad.",
                    null
            );
        }

        return new Response(
                true,
                "Reservación actualizada correctamente",
                request
        );
    }

    /**
     * Obtiene una reservación por su identificador.
     *
     * @param idReservation identificador de la reservación
     * @return respuesta con la reservación encontrada
     */
    public static Response getReservationById(
            int idReservation
    ) {

        if (idReservation <= 0) {
            return new Response(
                    false,
                    "El id de la reservación es inválido",
                    null
            );
        }

        EquipmentReservationRequest request
                = ReservationDAO.getEquipmentReservationById(
                        idReservation
                );

        if (request == null) {
            return new Response(
                    false,
                    "Reservación no encontrada",
                    null
            );
        }

        return new Response(
                true,
                "Reservación obtenida correctamente",
                request
        );
    }

    /**
     * Obtiene las reservaciones asociadas a un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta con la lista de reservaciones
     */
    public static Response getReservationsByClientId(
            int idClient
    ) {

        if (idClient <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        List<EquipmentReservationRequest> reservations
                = ReservationDAO
                        .getEquipmentReservationsByClientId(
                                idClient
                        );

        if (reservations == null
                || reservations.isEmpty()) {

            return new Response(
                    false,
                    "No se encontraron reservaciones "
                    + "para este cliente",
                    null
            );
        }

        return new Response(
                true,
                "Reservaciones obtenidas correctamente",
                reservations
        );
    }

    /**
     * Obtiene las reservaciones de equipos de un mes y año determinados.
     *
     * @param month mes consultado
     * @param year año consultado
     * @return respuesta con las reservaciones encontradas
     */
    public static Response getEquipmentReservationsByMonth(
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

        List<EquipmentReservationRequest> reservations
                = ReservationDAO
                        .getEquipmentReservationsByMonth(
                                month,
                                year
                        );

        if (reservations == null
                || reservations.isEmpty()) {

            return new Response(
                    false,
                    "No se encontraron reservaciones "
                    + "de equipos para el periodo indicado",
                    null
            );
        }

        return new Response(
                true,
                "Reservaciones de equipos "
                + "obtenidas correctamente",
                reservations
        );
    }

    /**
     * Elimina una reservación por su identificador.
     *
     * @param idReservation identificador de la reservación
     * @param idClient identificador del cliente
     * @return respuesta del proceso
     */
    public static Response deleteReservationById(
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
                = ReservationDAO.deleteReservationById(
                        idReservation,
                        idClient
                );

        if (!deleted) {
            return new Response(
                    false,
                    "No se pudo eliminar la reservación",
                    null
            );
        }

        return new Response(
                true,
                "Reservación eliminada correctamente",
                idReservation
        );
    }

    /**
     * Elimina todas las reservaciones de un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta del proceso
     */
    public static Response deleteReservationsByClientId(
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
                = ReservationDAO
                        .deleteReservationsByClientId(
                                idClient
                        );

        if (!deleted) {
            return new Response(
                    false,
                    "No se pudieron eliminar "
                    + "las reservas del cliente",
                    null
            );
        }

        return new Response(
                true,
                "Reservas del cliente "
                + "eliminadas correctamente",
                idClient
        );
    }

    /**
     * Valida los datos de una reservación de equipos.
     *
     * @param request solicitud de reservación
     * @param requireIdReservation indica si el id es obligatorio
     * @return resultado de la validación
     */
    private static Response validateEquipmentReservationRequest(
            EquipmentReservationRequest request,
            boolean requireIdReservation
    ) {

        if (request == null) {
            return new Response(
                    false,
                    "La solicitud de reserva es obligatoria",
                    null
            );
        }

        Reservation reservation
                = request.getReservation();

        if (reservation == null) {
            return new Response(
                    false,
                    "La reserva es obligatoria",
                    null
            );
        }

        if (requireIdReservation
                && reservation.getIdReservation() <= 0) {

            return new Response(
                    false,
                    "El id de la reservación "
                    + "es obligatorio",
                    null
            );
        }

        if (request.getIdClient() <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        if (reservation.getIdSection() <= 0) {
            return new Response(
                    false,
                    "La sección es obligatoria",
                    null
            );
        }

        if (reservation.getReservationDate() == null) {
            return new Response(
                    false,
                    "La fecha de reserva es obligatoria",
                    null
            );
        }

        List<RXE> equipmentList
                = request.getEquipmentList();

        if (equipmentList == null
                || equipmentList.isEmpty()) {

            return new Response(
                    false,
                    "Debe seleccionar "
                    + "al menos un equipo",
                    null
            );
        }

        for (RXE item : equipmentList) {

            if (item == null) {
                return new Response(
                        false,
                        "La lista contiene "
                        + "un equipo inválido",
                        null
                );
            }

            if (item.getIdEquipment() <= 0) {
                return new Response(
                        false,
                        "Equipo inválido",
                        null
                );
            }

            if (item.getQuantity() <= 0) {
                return new Response(
                        false,
                        "La cantidad debe ser "
                        + "mayor que cero",
                        null
                );
            }
        }

        return new Response(
                true,
                "Validación correcta",
                null
        );
    }
}
