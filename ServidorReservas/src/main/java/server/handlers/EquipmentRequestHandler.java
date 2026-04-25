/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.EquipmentController;
import model.Equipment;
import service.Response;

/**
 *
 * @author Cipriano
 */
public class EquipmentRequestHandler {

    public static Response handle(String command, Object obj) {

        switch (command.toUpperCase()) {

            case "CREATE_EQUIPMENT":
                return EquipmentController.createEquipment((Equipment) obj);

            case "UPDATE_EQUIPMENT":
                return EquipmentController.updateEquipment((Equipment) obj);

            case "GET_EQUIPMENT":
                return EquipmentController.getEquipment((String) obj);

            case "GET_ALL_EQUIPMENT":
                return EquipmentController.getAllEquipment();
            case "DELETE_EQUIPMENT":
                return EquipmentController.deleteEquipment((Equipment) obj);
            default:
                return new Response(false, "Comando de equipo no reconocido", null);
        }
    }
}
