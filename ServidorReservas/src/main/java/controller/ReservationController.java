/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.RXE;
import model.Reservation;
import database.ReservationDAO;
import java.util.List;
import model.Response;

/**
 *
 * @author Cipriano
 */
public class ReservationController {

    public static Response createEquipmentReservation(Reservation reservation, int idClient, List<RXE> equipmentList) {

        if (reservation == null) {
            return new Response(false, "La reserva es obligatoria", null);
        }

        if (idClient <= 0) {
            return new Response(false, "El cliente es obligatorio", null);
        }

        if (reservation.getIdSection() <= 0) {
            return new Response(false, "La sección es obligatoria", null);
        }

        if (reservation.getReservationDate() == null) {
            return new Response(false, "La fecha de reserva es obligatoria", null);
        }

        if (equipmentList == null || equipmentList.isEmpty()) {
            return new Response(false, "Debe seleccionar al menos un equipo", null);
        }

        for (RXE item : equipmentList) {
            if (item == null) {
                return new Response(false, "Hay equipos inválidos en la lista", null);
            }

            if (item.getIdEquipment() <= 0) {
                return new Response(false, "Equipo inválido", null);
            }

            if (item.getQuantity() <= 0) {
                return new Response(false, "La cantidad debe ser mayor que cero", null);
            }
        }

        boolean created = ReservationDAO.createEquipmentReservation(reservation, idClient, equipmentList);

        if (!created) {
            return new Response(false, "No se pudo crear la reservación", null);
        }

        return new Response(true, "Reservación creada correctamente", null);
    }
}
