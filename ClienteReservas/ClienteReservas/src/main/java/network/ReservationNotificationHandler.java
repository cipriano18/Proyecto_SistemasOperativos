package network;

import java.util.concurrent.CopyOnWriteArrayList;
import service.Response;

public class ReservationNotificationHandler {

    private static final CopyOnWriteArrayList<Runnable> listeners = new CopyOnWriteArrayList<>();

    public static void addOnDraftExpired(Runnable callback) {
        if (callback != null) {
            listeners.add(callback);
        }
    }

    public static void removeOnDraftExpired(Runnable callback) {
        if (callback != null) {
            listeners.remove(callback);
        }
    }

    public static void setOnDraftExpired(Runnable callback) {
        listeners.clear();
        if (callback != null) {
            listeners.add(callback);
        }
    }

    public static void clearOnDraftExpired() {
        listeners.clear();
    }

    public static void notifyDraftExpired(Response response) {
        System.out.println("Notificación de draft vencido recibida.");

        for (Runnable callback : listeners) {
            try {
                callback.run();
            } catch (Exception e) {
                System.out.println("Error en listener de draft vencido: " + e.getMessage());
            }
        }
    }
}
