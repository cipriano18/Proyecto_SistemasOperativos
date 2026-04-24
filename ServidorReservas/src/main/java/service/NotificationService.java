/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.io.IOException;
import model.CalendarBlock;
import model.Response;
import server.ClientHandler;
import server.Server;

/**
 *
 * @author Cipriano
 */
public class NotificationService {

    public static void notifyReservationViewers(String message, CalendarBlock block) {

        Response response = new Response(true, message, block);

        System.out.println("\n========== NOTIFICACIÓN A RESERVAS ==========");
        System.out.println("Mensaje: " + message);
        System.out.println("Fecha: " + block.getReservationDate());
        System.out.println("Sección: " + block.getIdSection());
        System.out.println("Estado: " + block.getStatus());
        System.out.println("Clientes conectados: " + Server.clients.size());

        int totalNotificados = 0;

        for (ClientHandler client : Server.clients) {
            System.out.println("------------------------------------");
            System.out.println("Cliente: " + client.getClientAddress());
            System.out.println("IdClient: " + client.getLoggedClientId());
            System.out.println("Está en reservas: " + client.isViewingReservations());

            if (client.isViewingReservations()) {
                try {
                    client.send(response);
                    totalNotificados++;
                    System.out.println("Notificado correctamente");
                } catch (IOException e) {
                    System.out.println("No se pudo notificar: " + e.getMessage());
                }
            }
        }
        System.out.println("Total notificados: " + totalNotificados);
        System.out.println("=============================================\n");
    }
}
