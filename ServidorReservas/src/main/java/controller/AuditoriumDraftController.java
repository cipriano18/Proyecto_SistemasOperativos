package controller;

import database.AuditoriumDraftDAO;
import database.AuditoriumReservationDAO;
import database.EquipmentReservationDraftDAO;
import database.ReservationDAO;
import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.CalendarBlock;
import model.RXE;
import model.Reservation;
import service.Response;

/**
 * Controlador encargado de gestionar las reservas temporales del auditorio.
 *
 * <p>
 * Permite iniciar, actualizar, descartar, consultar y confirmar reservas
 * temporales de auditorio.
 * </p>
 *
 * @author Cipriano
 */
public class AuditoriumDraftController {

    /**
     * Obtiene los bloques del calendario del auditorio para un mes y año
     * determinados.
     *
     * @param month mes consultado
     * @param year año consultado
     * @param idClient identificador del cliente
     * @return respuesta con los bloques del calendario
     */
    public static Response getAuditoriumCalendarBlocks(
            int month,
            int year,
            Integer idClient
    ) {

        if (month <= 0 || month > 12) {
            return new Response(
                    false,
                    "El mes es inválido",
                    null
            );
        }

        if (year <= 0) {
            return new Response(
                    false,
                    "El año es inválido",
                    null
            );
        }

        if (idClient != null && idClient <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        Map<String, CalendarBlock> mergedBlocks = new LinkedHashMap<>();

        mergeBlocks(
                mergedBlocks,
                ReservationDAO.getReservedBlocksByMonth(
                        month,
                        year
                )
        );

        mergeBlocks(
                mergedBlocks,
                AuditoriumReservationDAO.getReservedAuditoriumBlocksByMonth(
                        month,
                        year
                )
        );

        if (idClient != null) {
            mergeBlocks(
                    mergedBlocks,
                    EquipmentReservationDraftDAO.getBlockedDraftsByMonth(
                            month,
                            year,
                            idClient
                    )
            );
        }

        return new Response(
                true,
                "Calendario de auditorio obtenido correctamente",
                new ArrayList<>(mergedBlocks.values())
        );
    }

    /**
     * Fusiona bloques del calendario usando una unica prioridad por estado.
     *
     * @param mergedBlocks mapa acumulado indexado por fecha y seccion
     * @param blocks bloques a integrar
     */
    private static void mergeBlocks(
            Map<String, CalendarBlock> mergedBlocks,
            List<CalendarBlock> blocks
    ) {

        if (blocks == null || blocks.isEmpty()) {
            return;
        }

        for (CalendarBlock block : blocks) {
            if (block == null || block.getReservationDate() == null) {
                continue;
            }

            String key = buildBlockKey(block);
            CalendarBlock current = mergedBlocks.get(key);

            if (current == null
                    || getStatusPriority(block.getStatus())
                    > getStatusPriority(current.getStatus())) {

                mergedBlocks.put(
                        key,
                        new CalendarBlock(
                                block.getReservationDate(),
                                block.getIdSection(),
                                block.getStatus()
                        )
                );
            }
        }
    }

    /**
     * Construye una clave unica para identificar bloques por fecha y seccion.
     *
     * @param block bloque a identificar
     * @return clave compuesta del bloque
     */
    private static String buildBlockKey(CalendarBlock block) {
        return block.getReservationDate()
                + "|"
                + block.getIdSection();
    }

    /**
     * Define la precedencia visual entre estados del calendario.
     *
     * @param status estado del bloque
     * @return prioridad numerica; mayor valor significa mayor precedencia
     */
    private static int getStatusPriority(String status) {

        if ("OWN_DRAFT".equals(status)) {
            return 3;
        }

        if ("BLOCKED".equals(status)) {
            return 2;
        }

        if ("RESERVED".equals(status)) {
            return 1;
        }

        return 0;
    }

    /**
     * Inicia una reserva temporal de auditorio.
     *
     * @param request datos necesarios para crear la reserva temporal
     * @return respuesta del proceso
     */
    public static Response startAuditoriumDraft(
            AuditoriumDraftRequest request
    ) {

        if (request == null) {
            return new Response(
                    false,
                    "La solicitud de reserva temporal "
                    + "de auditorio es obligatoria",
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

        Reservation reservation = request.getReservation();

        if (reservation == null) {
            return new Response(
                    false,
                    "La reserva base es obligatoria",
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

        if (reservation.getIdSection() <= 0) {
            return new Response(
                    false,
                    "La sección es obligatoria",
                    null
            );
        }

        List<RXE> equipmentList = request.getEquipmentList();

        if (equipmentList != null) {
            for (RXE item : equipmentList) {

                if (item == null) {
                    return new Response(
                            false,
                            "La lista contiene un equipo inválido",
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
                            "La cantidad del equipo debe ser "
                            + "mayor que cero",
                            null
                    );
                }
            }
        }

        AuditoriumDraftRequest createdDraft
                = AuditoriumDraftDAO.createDraft(request);

        if (createdDraft == null) {
            return new Response(
                    false,
                    "No se pudo crear la reserva temporal. "
                    + "La fecha y sección ya podrían estar ocupadas",
                    null
            );
        }

        return new Response(
                true,
                "Reserva temporal de auditorio creada correctamente",
                createdDraft
        );
    }

    /**
     * Actualiza una reserva temporal de auditorio.
     *
     * @param request datos actualizados de la reserva temporal
     * @return respuesta del proceso
     */
    public static Response updateAuditoriumDraft(
            AuditoriumDraftRequest request
    ) {

        if (request == null) {
            return new Response(
                    false,
                    "La solicitud de actualización es obligatoria",
                    null
            );
        }

        if (request.getIdDraft() <= 0) {
            return new Response(
                    false,
                    "El id de la reserva temporal es obligatorio",
                    null
            );
        }

        AuditoriumDraft auditoriumDraft
                = request.getAuditoriumDraft();

        if (auditoriumDraft == null) {
            return new Response(
                    false,
                    "Los datos del auditorio son obligatorios",
                    null
            );
        }

        if (auditoriumDraft.getEventName() == null
                || auditoriumDraft.getEventName().isBlank()) {

            return new Response(
                    false,
                    "El nombre del evento es obligatorio",
                    null
            );
        }

        if (auditoriumDraft.getAttendeesCount() < 0) {
            return new Response(
                    false,
                    "La cantidad de asistentes "
                    + "no puede ser negativa",
                    null
            );
        }

        if (auditoriumDraft.getAttendeesCount() > 200) {
            return new Response(
                    false,
                    "La cantidad de asistentes no puede "
                    + "superar 200 personas",
                    null
            );
        }

        List<RXE> equipmentList = request.getEquipmentList();

        if (equipmentList != null) {
            for (RXE item : equipmentList) {

                if (item == null) {
                    return new Response(
                            false,
                            "La lista contiene un equipo inválido",
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
                            "La cantidad del equipo debe ser "
                            + "mayor que cero",
                            null
                    );
                }
            }
        }

        boolean updated
                = AuditoriumDraftDAO.updateDraft(request);

        if (!updated) {
            return new Response(
                    false,
                    "No se pudo actualizar la reserva "
                    + "temporal de auditorio",
                    null
            );
        }

        return new Response(
                true,
                "Reserva temporal de auditorio "
                + "actualizada correctamente",
                request
        );
    }

    /**
     * Descarta una reserva temporal de auditorio.
     *
     * @param idDraft identificador de la reserva temporal
     * @param idClient identificador del cliente
     * @return respuesta del proceso
     */
    public static Response discardAuditoriumDraft(
            int idDraft,
            int idClient
    ) {

        if (idDraft <= 0) {
            return new Response(
                    false,
                    "El id de la reserva temporal es obligatorio",
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

        AuditoriumDraftRequest draft
                = AuditoriumDraftDAO.getDraftById(idDraft);

        if (draft == null) {
            return new Response(
                    false,
                    "Reserva temporal no encontrada o expirada",
                    null
            );
        }

        if (draft.getIdClient() != idClient) {
            return new Response(
                    false,
                    "La reserva temporal no pertenece "
                    + "a este cliente",
                    null
            );
        }

        boolean deleted
                = AuditoriumDraftDAO.deleteDraft(idDraft);

        if (!deleted) {
            return new Response(
                    false,
                    "No se pudo descartar la reserva "
                    + "temporal de auditorio",
                    null
            );
        }

        return new Response(
                true,
                "Reserva temporal de auditorio "
                + "descartada correctamente",
                draft
        );
    }

    /**
     * Obtiene la reserva temporal activa de un cliente.
     *
     * @param idClient identificador del cliente
     * @return respuesta con la reserva temporal activa
     */
    public static Response getAuditoriumDraftByClientId(
            int idClient
    ) {

        if (idClient <= 0) {
            return new Response(
                    false,
                    "El cliente es obligatorio",
                    null
            );
        }

        AuditoriumDraftRequest draft
                = AuditoriumDraftDAO.getDraftByClientId(
                        idClient
                );

        if (draft == null) {
            return new Response(
                    false,
                    "No hay reservas temporales activas "
                    + "para este cliente",
                    null
            );
        }

        return new Response(
                true,
                "Reserva temporal de auditorio "
                + "obtenida correctamente",
                draft
        );
    }

    /**
     * Confirma una reserva temporal de auditorio.
     *
     * @param idDraft identificador de la reserva temporal
     * @param idClient identificador del cliente
     * @return respuesta del proceso
     */
    public static Response confirmAuditoriumDraft(
            int idDraft,
            int idClient
    ) {

        if (idDraft <= 0) {
            return new Response(
                    false,
                    "El id de la reserva temporal es obligatorio",
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

        boolean confirmed
                = AuditoriumDraftDAO.confirmDraft(
                        idDraft,
                        idClient
                );

        if (!confirmed) {
            return new Response(
                    false,
                    "No se pudo confirmar la reserva "
                    + "de auditorio",
                    null
            );
        }

        return new Response(
                true,
                "Reserva de auditorio confirmada correctamente",
                idDraft
        );
    }
}
