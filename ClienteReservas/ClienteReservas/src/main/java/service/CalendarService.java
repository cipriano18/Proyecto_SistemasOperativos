/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import model.Response;
import network.ServerConnection;
import network.SocketManager;

public class CalendarService {

    public static Response getCalendarBlocks(int month, int year) {
        try {
            SocketManager socketManager = SocketManager.getInstance();

            if (!socketManager.isConnected()) {
                socketManager.connect();
            }

            ServerConnection connection = socketManager.getConnection();
            ObjectOutputStream out = connection.getObjectOutput();
            ObjectInputStream in = connection.getObjectInput();

            out.writeObject("GET_CALENDAR_BLOCKS");
            out.flush();

            out.writeObject(month);
            out.flush();

            out.writeObject(year);
            out.flush();

            Object response = in.readObject();

            if (response instanceof Response) {
                return (Response) response;
            }

            return new Response(false, "Respuesta inesperada del servidor", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Error al obtener el calendario: " + e.getMessage(), null);
        }
    }
}
