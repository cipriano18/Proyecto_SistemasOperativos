/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.handlers;

import controller.EquipmentController;
import model.Equipment;
import model.Reservation;
import service.Response;

/**
 * Manejador encargado de procesar las solicitudes relacionadas con equipos.
 *
 * <p>
 * Recibe comandos provenientes del servidor y delega las operaciones
 * correspondientes al {@code EquipmentController}.
 * </p>
 *
 * @author Cipriano
 */
public class EquipmentRequestHandler {

    /**
     * Procesa un comando relacionado con equipos.
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

            case "CREATE_EQUIPMENT":

                return EquipmentController
                        .createEquipment(
                                (Equipment) obj
                        );

            case "UPDATE_EQUIPMENT":

                return EquipmentController
                        .updateEquipment(
                                (Equipment) obj
                        );

            case "GET_EQUIPMENT":

                return EquipmentController
                        .getEquipment(
                                (String) obj
                        );

            case "GET_ALL_EQUIPMENT":

                return EquipmentController
                        .getAllEquipment();

            case "GET_AVAILABLE_EQUIPMENT": {

                Reservation reservation
                        = (Reservation) obj;

                System.out.println(
                        "---- GET_AVAILABLE_EQUIPMENT ----"
                );

                System.out.println(
                        "Objeto recibido: "
                        + reservation
                );

                return EquipmentController
                        .getAvailableEquipmentByDateAndSection(
                                reservation.getReservationDate(),
                                reservation.getIdSection()
                        );
            }

            case "DELETE_EQUIPMENT":

                return EquipmentController
                        .deleteEquipment(
                                (Equipment) obj
                        );

            default:

                return new Response(
                        false,
                        "Comando de equipo no reconocido",
                        null
                );
        }
    }
}
