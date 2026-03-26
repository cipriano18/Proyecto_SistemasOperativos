/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import database.EquipmentDAO;
import java.util.List;
import model.Equipment;
import utils.Validator;

/**
 *
 * @author Cipriano
 */
public class EquipmentController {

    // Crear equipo
    public static String createEquipment(Equipment equipment) {

        // Validaciones
        if (Validator.isEmpty(equipment.getName())) {
            return "ERROR:El nombre del equipo es obligatorio";
        }

        if (equipment.getAvailableQuantity() < 0) {
            return "ERROR:La cantidad no puede ser negativa";
        }

        // Verificar si ya existe
        if (EquipmentDAO.getEquipmentByName(equipment.getName()) != null) {
            return "ERROR:Ya existe un equipo con ese nombre";
        }

        boolean created = EquipmentDAO.createEquipment(equipment);
        if (!created) {
            return "ERROR:No se pudo crear el equipo";
        }

        return "SUCCESS:Equipo creado correctamente";
    }

    // Obtener equipo por nombre
    public static Equipment getEquipment(String name) {
        Equipment equipment = EquipmentDAO.getEquipmentByName(name);
        if (equipment == null) {
            System.out.println("ERROR:Equipo no encontrado");
        }
        return equipment;
    }

    // Obtener todos los equipos
    public static List<Equipment> getAllEquipment() {
        return EquipmentDAO.getAllEquipment();
    }
}
