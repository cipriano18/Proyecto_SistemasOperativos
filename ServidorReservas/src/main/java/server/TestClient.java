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
        actualizarReservaTemporalAuditorio(9); // Cambiá este ID por un id_draft real
    }

    private static void actualizarReservaTemporalAuditorio(int idDraft) {

        try (
            Socket socket = new Socket(HOST, PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {

            System.out.println("\n=========== ACTUALIZANDO RESERVA TEMPORAL DE AUDITORIO ===========");

            Reservation reservation = new Reservation();
            reservation.setIdSection(2);
            reservation.setReservationDate(Date.valueOf("2026-05-15"));

            AuditoriumDraft auditoriumDraft = new AuditoriumDraft();
            auditoriumDraft.setEventName("Evento actualizado desde TestClient");
            auditoriumDraft.setAttendeesCount(120);
            auditoriumDraft.setObservations("Actualizado correctamente con equipos nuevos.");

            List<RXE> equipmentList = new ArrayList<>();

            RXE mic = new RXE();
            mic.setIdEquipment(2);
            mic.setQuantity(4);
            equipmentList.add(mic);

            AuditoriumDraftRequest request = new AuditoriumDraftRequest();
            request.setIdDraft(idDraft);
            request.setIdClient(1); // Debe coincidir con el cliente dueño del draft
            request.setReservation(reservation);
            request.setAuditoriumDraft(auditoriumDraft);
            request.setEquipmentList(equipmentList);

            System.out.println("\n=== DATOS UPDATE ENVIADOS ===");
            System.out.println("IdDraft: " + request.getIdDraft());
            System.out.println("IdClient: " + request.getIdClient());
            System.out.println("Fecha: " + reservation.getReservationDate());
            System.out.println("Sección: " + reservation.getIdSection());
            System.out.println("Evento: " + auditoriumDraft.getEventName());
            System.out.println("Asistentes: " + auditoriumDraft.getAttendeesCount());
            System.out.println("Observaciones: " + auditoriumDraft.getObservations());

            System.out.println("\nEquipos enviados:");
            for (RXE item : equipmentList) {
                System.out.println("- Equipo ID: " + item.getIdEquipment()
                        + " | Cantidad: " + item.getQuantity());
            }

            out.writeObject("UPDATE_AUDITORIUM_DRAFT");
            out.flush();

            out.writeObject(request);
            out.flush();

            Object response = in.readObject();

            System.out.println("\n=== RESPUESTA UPDATE ===");

            if (response instanceof Response) {
                Response resp = (Response) response;

                System.out.println("Success: " + resp.isSuccess());
                System.out.println("Message: " + resp.getMessage());
                System.out.println("Data: " + resp.getData());

                if (resp.getData() instanceof AuditoriumDraftRequest) {
                    AuditoriumDraftRequest data = (AuditoriumDraftRequest) resp.getData();

                    System.out.println("\n=== DRAFT ACTUALIZADO ===");
                    System.out.println("IdDraft: " + data.getIdDraft());
                    System.out.println("Evento: " + data.getAuditoriumDraft().getEventName());
                    System.out.println("Asistentes: " + data.getAuditoriumDraft().getAttendeesCount());
                    System.out.println("Observaciones: " + data.getAuditoriumDraft().getObservations());
                }

            } else {
                System.out.println("Respuesta inesperada: " + response);
            }

        } catch (Exception e) {
            System.out.println("Error al actualizar reserva temporal de auditorio:");
            e.printStackTrace();
        }
    }
}