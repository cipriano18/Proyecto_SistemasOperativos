/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.EquipmentDAO;
import java.util.List;
import model.Equipment;
import service.Response;
import utils.Validator;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con equipos.
 *
 * <p>
 * Permite crear, consultar, actualizar y eliminar equipos, además de consultar
 * disponibilidad por fecha y sección.
 * </p>
 *
 * @author Cipriano
 */
public class EquipmentController {

    /**
     * Crea un nuevo equipo.
     *
     * @param equipment datos del equipo
     * @return respuesta del proceso
     */
    public static Response createEquipment(
            Equipment equipment
    ) {

        if (equipment == null) {
            return new Response(
                    false,
                    "El equipo es obligatorio",
                    null
            );
        }

        if (Validator.isEmpty(
                equipment.getName()
        )) {

            return new Response(
                    false,
                    "El nombre del equipo es obligatorio",
                    null
            );
        }

        if (equipment.getTotalQuantity() < 0) {
            return new Response(
                    false,
                    "La cantidad no puede ser negativa",
                    null
            );
        }

        if (EquipmentDAO.getEquipmentByName(
                equipment.getName()
        ) != null) {

            return new Response(
                    false,
                    "Ya existe un equipo "
                    + "con ese nombre",
                    null
            );
        }

        boolean created
                = EquipmentDAO.createEquipment(
                        equipment
                );

        if (!created) {
            return new Response(
                    false,
                    "No se pudo crear el equipo",
                    null
            );
        }

        return new Response(
                true,
                "Equipo creado correctamente",
                null
        );
    }

    /**
     * Obtiene un equipo por su nombre.
     *
     * @param name nombre del equipo
     * @return respuesta con el equipo encontrado
     */
    public static Response getEquipment(
            String name
    ) {

        if (Validator.isEmpty(name)) {
            return new Response(
                    false,
                    "El nombre del equipo es obligatorio",
                    null
            );
        }

        Equipment equipment
                = EquipmentDAO.getEquipmentByName(name);

        if (equipment == null) {
            return new Response(
                    false,
                    "Equipo no encontrado",
                    null
            );
        }

        return new Response(
                true,
                "Equipo encontrado",
                equipment
        );
    }

    /**
     * Obtiene todos los equipos registrados.
     *
     * @return respuesta con la lista de equipos
     */
    public static Response getAllEquipment() {

        List<Equipment> equipmentList
                = EquipmentDAO.getAllEquipment();

        return new Response(
                true,
                "Equipos obtenidos correctamente",
                equipmentList
        );
    }

    /**
     * Obtiene los equipos disponibles para una fecha y sección determinadas.
     *
     * @param reservationDate fecha de reserva
     * @param idSection identificador de la sección
     * @return respuesta con la lista de equipos disponibles
     */
    public static Response
            getAvailableEquipmentByDateAndSection(
                    java.sql.Date reservationDate,
                    int idSection
            ) {

        if (reservationDate == null) {
            return new Response(
                    false,
                    "La fecha de reserva es obligatoria",
                    null
            );
        }

        if (idSection <= 0) {
            return new Response(
                    false,
                    "La sección es obligatoria",
                    null
            );
        }

        List<Equipment> equipmentList
                = EquipmentDAO
                        .getAvailableEquipmentByDateAndSection(
                                reservationDate,
                                idSection
                        );

        return new Response(
                true,
                "Equipos disponibles obtenidos correctamente",
                equipmentList
        );
    }

    /**
     * Actualiza la información de un equipo.
     *
     * @param equipment datos actualizados del equipo
     * @return respuesta del proceso
     */
    public static Response updateEquipment(
            Equipment equipment
    ) {

        if (equipment == null) {
            return new Response(
                    false,
                    "El equipo es obligatorio",
                    null
            );
        }

        if (equipment.getIdEquipment() <= 0) {
            return new Response(
                    false,
                    "El id del equipo es obligatorio",
                    null
            );
        }

        if (Validator.isEmpty(
                equipment.getName()
        )) {

            return new Response(
                    false,
                    "El nombre del equipo es obligatorio",
                    null
            );
        }

        if (equipment.getTotalQuantity() < 0) {
            return new Response(
                    false,
                    "La cantidad no puede ser negativa",
                    null
            );
        }

        Equipment existing
                = EquipmentDAO.getEquipmentById(
                        equipment.getIdEquipment()
                );

        if (existing == null) {
            return new Response(
                    false,
                    "El equipo no existe",
                    null
            );
        }

        Equipment byName
                = EquipmentDAO.getEquipmentByName(
                        equipment.getName()
                );

        if (byName != null
                && byName.getIdEquipment()
                != equipment.getIdEquipment()) {

            return new Response(
                    false,
                    "Ya existe otro equipo "
                    + "con ese nombre",
                    null
            );
        }

        boolean updated
                = EquipmentDAO.updateEquipment(
                        equipment
                );

        if (!updated) {
            return new Response(
                    false,
                    "No se pudo actualizar el equipo",
                    null
            );
        }

        return new Response(
                true,
                "Equipo actualizado correctamente",
                null
        );
    }

    /**
     * Elimina un equipo.
     *
     * @param equipment equipo a eliminar
     * @return respuesta del proceso
     */
    public static Response deleteEquipment(
            Equipment equipment
    ) {

        if (equipment == null) {
            return new Response(
                    false,
                    "El equipo es obligatorio",
                    null
            );
        }

        if (equipment.getIdEquipment() <= 0) {
            return new Response(
                    false,
                    "El id del equipo es obligatorio",
                    null
            );
        }

        Equipment existing
                = EquipmentDAO.getEquipmentById(
                        equipment.getIdEquipment()
                );

        if (existing == null) {
            return new Response(
                    false,
                    "El equipo no existe",
                    null
            );
        }

        boolean deleted
                = EquipmentDAO.deleteEquipment(
                        equipment.getIdEquipment()
                );

        if (!deleted) {
            return new Response(
                    false,
                    "No se pudo eliminar el equipo",
                    null
            );
        }

        return new Response(
                true,
                "Equipo eliminado correctamente",
                null
        );
    }
}
