package network;

import java.util.concurrent.CopyOnWriteArrayList;
import service.Response;

/**
 * Gestiona listeners asociados al vencimiento de drafts de reserva.
 */
public class ReservationNotificationHandler {

    private static final CopyOnWriteArrayList<Runnable> listeners 
            = new CopyOnWriteArrayList<>();

    /**
     * Agrega un listener a la lista de notificaciones.
     *
     * @param callback accion a ejecutar cuando un draft expire
     */
    public static void addOnDraftExpired(Runnable callback) {
        if (callback != null) {
            listeners.add(callback);
        }
    }

    /**
     * Elimina un listener registrado previamente.
     *
     * @param callback accion a eliminar de la lista
     */
    public static void removeOnDraftExpired(Runnable callback) {
        if (callback != null) {
            listeners.remove(callback);
        }
    }

    /**
     * Reemplaza los listeners actuales por uno nuevo.
     *
     * @param callback accion unica a mantener registrada
     */
    public static void setOnDraftExpired(Runnable callback) {
        listeners.clear();
        if (callback != null) {
            listeners.add(callback);
        }
    }

    /**
     * Elimina todos los listeners registrados.
     */
    public static void clearOnDraftExpired() {
        listeners.clear();
    }

    /**
     * Notifica a todos los listeners que un draft ha expirado.
     *
     * @param response respuesta recibida asociada al vencimiento
     */
    public static void notifyDraftExpired(Response response) {
        System.out.println("Notificación de draft vencido recibida.");

        for (Runnable callback : listeners) {
            try {
                callback.run();
            } catch (Exception e) {
                System.out.println(
                        "Error en listener de draft vencido: " 
                        + e.getMessage());
            }
        }
    }
}
