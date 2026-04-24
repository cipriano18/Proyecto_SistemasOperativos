import controller.ReservationDraftController;
import database.EquipmentReservationDraftDAO;
import model.EquipmentReservationDraft;
import model.EquipmentReservationDraftRequest;
import model.Reservation;
import model.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationDraftControllerTest {

    private int createdDraftId;
/*
    @AfterEach
    public void cleanup() {
        if (createdDraftId > 0) {
            EquipmentReservationDraftDAO.deleteDraft(createdDraftId);
        }
    }
*/
    @Test
    public void testStartEquipmentDraftResponseSuccess() {
        System.out.println("=== INICIO TEST START DRAFT SQL ===");

        Reservation reservation = new Reservation();
        reservation.setReservationDate(Date.valueOf("2026-04-26"));
        reservation.setIdSection(2);

        EquipmentReservationDraftRequest request = new EquipmentReservationDraftRequest();
        request.setIdClient(1);
        request.setReservation(reservation);

        Response response = ReservationDraftController.startEquipmentDraft(request);

        System.out.println("Success: " + response.isSuccess());
        System.out.println("Message: " + response.getMessage());
        System.out.println("Data: " + response.getData());

        assertTrue(response.isSuccess(), "Debería crear el draft correctamente");
        assertEquals("Draft creado correctamente", response.getMessage());
        assertNotNull(response.getData(), "El data no debería ser null");

        EquipmentReservationDraft draft = (EquipmentReservationDraft) response.getData();
        createdDraftId = draft.getIdDraft();

        System.out.println("ID Draft creado: " + createdDraftId);

        assertTrue(draft.getIdDraft() > 0, "El idDraft debería ser mayor que 0");
        assertEquals(1, draft.getIdClient());
        assertEquals(2, draft.getReservation().getIdSection());
        assertEquals(Date.valueOf("2026-04-26"), draft.getReservation().getReservationDate());
        assertNotNull(draft.getCreatedAt(), "createdAt no debería ser null");
        assertNotNull(draft.getExpiresAt(), "expiresAt no debería ser null");

        EquipmentReservationDraft loaded = EquipmentReservationDraftDAO.getDraftById(draft.getIdDraft());

        System.out.println("Draft cargado desde BD: " + loaded);

        assertNotNull(loaded, "El draft debería existir en BD");
        assertEquals(draft.getIdDraft(), loaded.getIdDraft());
        assertEquals(draft.getIdClient(), loaded.getIdClient());
        assertEquals(draft.getReservation().getIdSection(), loaded.getReservation().getIdSection());
        assertEquals(draft.getReservation().getReservationDate(), loaded.getReservation().getReservationDate());

        System.out.println("=== FIN TEST START DRAFT SQL ===");
    }
}