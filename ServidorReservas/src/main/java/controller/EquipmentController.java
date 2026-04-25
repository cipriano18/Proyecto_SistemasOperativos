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
 *
 * @author Cipriano
 */
public class EquipmentController {

// Crear equipo
    public static Response createEquipment(Equipment equipment) {

        if (equipment == null) {
            return new Response(false, "El equipo es obligatorio", null);
        }

        if (Validator.isEmpty(equipment.getName())) {
            return new Response(false, "El nombre del equipo es obligatorio", null);
        }

        if (equipment.getTotalQuantity() < 0) {
            return new Response(false, "La cantidad no puede ser negativa", null);
        }

        if (EquipmentDAO.getEquipmentByName(equipment.getName()) != null) {
            return new Response(false, "Ya existe un equipo con ese nombre", null);
        }

        boolean created = EquipmentDAO.createEquipment(equipment);

        if (!created) {
            return new Response(false, "No se pudo crear el equipo", null);
        }

        return new Response(true, "Equipo creado correctamente", null);
    }

// Obtener equipo por nombre
    public static Response getEquipment(String name) {

        if (Validator.isEmpty(name)) {
            return new Response(false, "El nombre del equipo es obligatorio", null);
        }

        Equipment equipment = EquipmentDAO.getEquipmentByName(name);

        if (equipment == null) {
            return new Response(false, "Equipo no encontrado", null);
        }

        return new Response(true, "Equipo encontrado", equipment);
    }

// Obtener todos los equipos
    public static Response getAllEquipment() {
        List<Equipment> equipmentList = EquipmentDAO.getAllEquipment();

        return new Response(true, "Equipos obtenidos correctamente", equipmentList);
    }

// Actualizar equipo
    public static Response updateEquipment(Equipment equipment) {

        if (equipment == null) {
            return new Response(false, "El equipo es obligatorio", null);
        }

        if (equipment.getIdEquipment() <= 0) {
            return new Response(false, "El id del equipo es obligatorio", null);
        }

        if (Validator.isEmpty(equipment.getName())) {
            return new Response(false, "El nombre del equipo es obligatorio", null);
        }

        if (equipment.getTotalQuantity() < 0) {
            return new Response(false, "La cantidad no puede ser negativa", null);
        }

        Equipment existing = EquipmentDAO.getEquipmentById(equipment.getIdEquipment());

        if (existing == null) {
            return new Response(false, "El equipo no existe", null);
        }

        Equipment byName = EquipmentDAO.getEquipmentByName(equipment.getName());

        if (byName != null && byName.getIdEquipment() != equipment.getIdEquipment()) {
            return new Response(false, "Ya existe otro equipo con ese nombre", null);
        }

        boolean updated = EquipmentDAO.updateEquipment(equipment);

        if (!updated) {
            return new Response(false, "No se pudo actualizar el equipo", null);
        }

        return new Response(true, "Equipo actualizado correctamente", null);
    }

    public static Response deleteEquipment(Equipment equipment) {

        if (equipment == null) {
            return new Response(false, "El equipo es obligatorio", null);
        }

        if (equipment.getIdEquipment() <= 0) {
            return new Response(false, "El id del equipo es obligatorio", null);
        }

        Equipment existing = EquipmentDAO.getEquipmentById(equipment.getIdEquipment());

        if (existing == null) {
            return new Response(false, "El equipo no existe", null);
        }

        boolean deleted = EquipmentDAO.deleteEquipment(equipment.getIdEquipment());

        if (!deleted) {
            return new Response(false, "No se pudo eliminar el equipo", null);
        }

        return new Response(true, "Equipo eliminado correctamente", null);
    }
}
