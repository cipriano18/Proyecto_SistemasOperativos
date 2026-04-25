package server;

import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import model.RXE;
import model.Reservation;
import service.Response;

public class TestClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        crearReservaTemporalAuditorio();
    }

    private static void crearReservaTemporalAuditorio() {

        try (
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            System.out.println("\n=========== CREANDO RESERVA TEMPORAL DE AUDITORIO ===========");

            Reservation reservation = new Reservation();
            reservation.setIdSection(1);
            reservation.setReservationDate(Date.valueOf("2026-05-15"));

            AuditoriumDraft auditoriumDraft = new AuditoriumDraft();
            auditoriumDraft.setEventName("Charla de Inteligencia Artificial");
            auditoriumDraft.setAttendeesCount(80);
            auditoriumDraft.setObservations("Se requiere audio y proyector.");

            List<RXE> equipmentList = new ArrayList<>();

            RXE projector = new RXE();
            projector.setIdEquipment(1);
            projector.setQuantity(1);
            equipmentList.add(projector);

            RXE screen = new RXE();
            screen.setIdEquipment(3);
            screen.setQuantity(1);
            equipmentList.add(screen);

            AuditoriumDraftRequest request = new AuditoriumDraftRequest();
            request.setIdClient(12); // Debe existir en AUD_Clients
            request.setReservation(reservation);
            request.setAuditoriumDraft(auditoriumDraft);
            request.setEquipmentList(equipmentList);

            System.out.println("\n=== DATOS ENVIADOS ===");
            System.out.println("Cliente: " + request.getIdClient());
            System.out.println("Fecha: " + reservation.getReservationDate());
            System.out.println("Sección: " + reservation.getIdSection());
            System.out.println("Evento: " + auditoriumDraft.getEventName());
            System.out.println("Asistentes: " + auditoriumDraft.getAttendeesCount());
            System.out.println("Observaciones: " + auditoriumDraft.getObservations());

            System.out.println("\nEquipos:");
            for (RXE item : equipmentList) {
                System.out.println("- Equipo ID: " + item.getIdEquipment()
                        + " | Cantidad: " + item.getQuantity());
            }

            out.writeObject("START_AUDITORIUM_DRAFT");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            System.out.println("\n=== RESPUESTA ===");

            if (response instanceof Response) {
                Response resp = (Response) response;

                System.out.println("Success: " + resp.isSuccess());
                System.out.println("Message: " + resp.getMessage());
                System.out.println("Data: " + resp.getData());

                if (resp.getData() instanceof AuditoriumDraftRequest) {
                    AuditoriumDraftRequest data = (AuditoriumDraftRequest) resp.getData();

                    System.out.println("\n=== RESERVA TEMPORAL CREADA ===");
                    System.out.println("IdDraft: " + data.getIdDraft());
                    System.out.println("IdClient: " + data.getIdClient());
                    System.out.println("Fecha: " + data.getReservation().getReservationDate());
                    System.out.println("Sección: " + data.getReservation().getIdSection());

                    if (data.getAuditoriumDraft() != null) {
                        System.out.println("Evento: " + data.getAuditoriumDraft().getEventName());
                        System.out.println("Asistentes: " + data.getAuditoriumDraft().getAttendeesCount());
                        System.out.println("Observaciones: " + data.getAuditoriumDraft().getObservations());
                    }

                    if (data.getEquipmentList() != null) {
                        System.out.println("\nEquipos guardados:");
                        for (RXE item : data.getEquipmentList()) {
                            System.out.println("- Equipo ID: " + item.getIdEquipment()
                                    + " | Cantidad: " + item.getQuantity());
                        }
                    }
                }

            } else {
                System.out.println("Respuesta inesperada: " + response);
            }

        } catch (Exception e) {
            System.out.println("Error al crear reserva temporal de auditorio:");
            e.printStackTrace();
        }
    }
}