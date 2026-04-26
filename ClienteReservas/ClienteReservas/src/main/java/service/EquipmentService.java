/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.EquipmentReservationDraftRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.Equipment;
import model.Reservation;
import network.ServerConnection;
import network.SocketManager;

public class EquipmentService {

    public static Response createEquipment(Equipment equipment) {
        return sendRequest("CREATE_EQUIPMENT", equipment);
    }

    public static Response getEquipment(String name) {
        return sendRequest("GET_EQUIPMENT", name);
    }

    public static Response getAllEquipment() {
        return sendRequest("GET_ALL_EQUIPMENT", null);
    }

    public static Response updateEquipment(Equipment equipment) {
        return sendRequest("UPDATE_EQUIPMENT", equipment);
    }

    public static Response deleteEquipment(Equipment equipment) {
        return sendRequest("DELETE_EQUIPMENT", equipment);
    }

    public static Response getAvailableEquipment(EquipmentReservationDraftRequest request) {
        return sendRequest("GET_AVAILABLE_EQUIPMENT", request);
    }

    private static Response sendRequest(String action, Object data) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject(action);
            out.flush();

            out.writeObject(data);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            }

            return new Response(false, "Respuesta inesperada del servidor", null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Response getAvailableEquipmentByDateAndSection(java.sql.Date reservationDate, int idSection) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            Reservation reservation = new Reservation();
            reservation.setReservationDate(reservationDate);
            reservation.setIdSection(idSection);

            out.writeObject("GET_AVAILABLE_EQUIPMENT");
            out.flush();

            out.writeObject(reservation);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            } else {
                return new Response(false, "Respuesta inesperada del servidor", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
