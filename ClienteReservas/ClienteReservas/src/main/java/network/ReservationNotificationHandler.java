/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package network;

import service.Response;

/**
 *
 * @author cipriano
 */
public class ReservationNotificationHandler {

    private static Runnable onDraftExpired;

    public static void setOnDraftExpired(Runnable callback) {
        onDraftExpired = callback;
    }

    public static void clearOnDraftExpired() {
        onDraftExpired = null;
    }

    public static void notifyDraftExpired(Response response) {
        System.out.println("Notificación de draft vencido recibida.");

        if (onDraftExpired != null) {
            onDraftExpired.run();
        }
    }
}