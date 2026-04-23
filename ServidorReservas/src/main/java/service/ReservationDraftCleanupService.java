package service;

import database.EquipmentReservationDraftDAO;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import model.CalendarBlock;

public class ReservationDraftCleanupService {

    private static Timer timer;

    public static void start() {
        timer = new Timer(true);

        System.out.println("Servicio de limpieza de drafts iniciado...");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                System.out.println("\nEjecutando limpieza de drafts...");
                System.out.println("Hora actual: " + new java.util.Date());

                List<CalendarBlock> expiredBlocks = EquipmentReservationDraftDAO.getExpiredDraftBlocks();

                int deleted = EquipmentReservationDraftDAO.cleanupExpiredDraftsAndCount();

                System.out.println("Drafts eliminados: " + deleted);

                if (deleted > 0) {
                    for (CalendarBlock block : expiredBlocks) {
                        NotificationService.notifyReservationViewers(
                                "RESERVATION_DRAFT_EXPIRED",
                                block
                        );
                    }
                } else {
                    System.out.println("No hay drafts expirados");
                }
            }
        }, 0, 15000);
    }
}
