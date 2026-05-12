package service;

import database.AuditoriumDraftDAO;
import database.EquipmentReservationDraftDAO;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import model.CalendarBlock;

/**
 *
 * @author Cipriano
 */
public class ReservationDraftCleanupService {

    private static Timer timer;
    private static boolean running = false;

    public static void start() {
        if (running) {
            System.out.println("El servicio de limpieza de drafts ya está iniciado.");
            return;
        }

        running = true;
        timer = new Timer(true);

        System.out.println("Servicio de limpieza de drafts iniciado...");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                System.out.println("\nEjecutando limpieza de drafts...");
                System.out.println("Hora actual: " + new java.util.Date());

                limpiarDraftsEquipo();
                limpiarDraftsAuditorio();
            }
        }, 0, 15000);
    }

    private static void limpiarDraftsEquipo() {
        List<CalendarBlock> expiredBlocks = EquipmentReservationDraftDAO.getExpiredDraftBlocks();

        int deleted = EquipmentReservationDraftDAO.cleanupExpiredDraftsAndCount();

        System.out.println("Drafts de equipo eliminados: " + deleted);

        if (deleted > 0) {
            for (CalendarBlock block : expiredBlocks) {
                NotificationService.notifyReservationViewers(
                        "RESERVATION_DRAFT_EXPIRED",
                        block
                );
            }
        } else {
            System.out.println("No hay drafts de equipo expirados");
        }
    }

   private static void limpiarDraftsAuditorio() {
    List<CalendarBlock> expiredBlocks = AuditoriumDraftDAO.getExpiredDraftBlocks();

    System.out.println("Bloques de auditorio expirados encontrados: " + expiredBlocks.size());

    int deleted = AuditoriumDraftDAO.cleanupExpiredDraftsAndCount();

    System.out.println("Drafts de auditorio eliminados: " + deleted);

    if (deleted > 0) {

        if (expiredBlocks.isEmpty()) {
            System.out.println("Se eliminaron drafts de auditorio, pero no se encontraron bloques para notificar.");
        }

        for (CalendarBlock block : expiredBlocks) {
            System.out.println("Notificando auditorio vencido: "
                    + block.getReservationDate()
                    + " sección "
                    + block.getIdSection());

            NotificationService.notifyReservationViewers(
                    "AUDITORIUM_DRAFT_EXPIRED",
                    block
            );
        }

    } else {
        System.out.println("No hay drafts de auditorio expirados");
    }
}
    public static void stop() {
        if (!running) {
            System.out.println("El servicio de limpieza de drafts ya está detenido.");
            return;
        }

        running = false;

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        System.out.println("Servicio de limpieza de drafts detenido.");
    }

    public static boolean isRunning() {
        return running;
    }
}