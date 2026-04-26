/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dto.EquipmentReservationDraftRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import network.ServerConnection;
import network.SocketManager;

/**
 *
 * @author Cipriano
 */
public class ReservationDraftService {

    public static Response discardEquipmentDraft(EquipmentReservationDraftRequest request) {
        return send("DISCARD_EQUIPMENT_DRAFT", request);
    }

    private static Response send(String command, Object data) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject(command);
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
            return new Response(false, "Error al conectar con el servidor: " + e.getMessage(), null);
        }
    }
}
