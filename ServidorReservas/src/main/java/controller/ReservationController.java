/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.EquipmentReservationRequest;
import model.RXE;
import model.Reservation;
import database.ReservationDAO;
import java.util.List;

/**
 *
 * @author Cipriano
 */
public class ReservationController {
      public static String createEquipmentReservation(EquipmentReservationRequest equipmentReservation) {

        if (equipmentReservation == null) {
            return "ERROR:La solicitud de reserva es obligatoria";
        }

        Reservation reservation = equipmentReservation.getReservation();
        List<RXE> equipmentList = equipmentReservation.getEquipmentList();

        if (reservation == null) {
            return "ERROR:La reserva es obligatoria";
        }

        if (reservation.getIdClient() <= 0) {
            return "ERROR:El cliente es obligatorio";
        }

        if (reservation.getIdSection() <= 0) {
            return "ERROR:La sección es obligatoria";
        }

        if (reservation.getReservationDate() == null) {
            return "ERROR:La fecha de reserva es obligatoria";
        }

        if (equipmentList == null || equipmentList.isEmpty()) {
            return "ERROR:Debe seleccionar al menos un equipo";
        }

        for (RXE item : equipmentList) {
            if (item.getIdEquipment() <= 0) {
                return "ERROR:Equipo inválido";
            }

            if (item.getQuantity() <= 0) {
                return "ERROR:La cantidad debe ser mayor que cero";
            }
        }

        boolean created = ReservationDAO.createEquipmentReservation(reservation, equipmentList);

        if (!created) {
            return "ERROR:No se pudo crear la reservación de equipo";
        }

        return "SUCCESS:Reservación de equipo creada correctamente";
    }
}
