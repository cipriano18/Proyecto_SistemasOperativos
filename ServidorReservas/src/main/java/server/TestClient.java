package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.EquipmentReservationRequest;
import model.Reservation;
import model.RXE;

public class TestClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CLIENTE DE PRUEBA ===");
        System.out.println("1. Probar reserva con proyector");
        System.out.println("2. Probar reserva con pantallas");
        System.out.println("3. Probar reserva con proyector y pantallas");
        System.out.print("Seleccione una opción: ");

        int opcion = sc.nextInt();

        switch (opcion) {
            case 1:
                probarReservaSoloProyector();
                break;
            case 2:
                probarReservaSoloPantallas();
                break;
            case 3:
                probarReservaCompleta();
                break;
            default:
                System.out.println("Opción no válida.");
        }

        sc.close();
    }

    private static void probarReservaSoloProyector() {
        List<RXE> equipos = new ArrayList<>();

        RXE proyector = new RXE();
        proyector.setIdEquipment(1); // 1 = Proyector
        proyector.setQuantity(1);
        equipos.add(proyector);

        enviarReserva(equipos, "Reserva con proyector");
    }

    private static void probarReservaSoloPantallas() {
        List<RXE> equipos = new ArrayList<>();

        RXE pantallas = new RXE();
        pantallas.setIdEquipment(2); // 2 = Pantallas
        pantallas.setQuantity(2);
        equipos.add(pantallas);

        enviarReserva(equipos, "Reserva con pantallas");
    }

    private static void probarReservaCompleta() {
        List<RXE> equipos = new ArrayList<>();

        RXE proyector = new RXE();
        proyector.setIdEquipment(1); // 1 = Proyector
        proyector.setQuantity(1);
        equipos.add(proyector);

        RXE pantallas = new RXE();
        pantallas.setIdEquipment(2); // 2 = Pantallas
        pantallas.setQuantity(2);
        equipos.add(pantallas);

        enviarReserva(equipos, "Reserva con proyector y pantallas");
    }

private static void enviarReserva(List<RXE> equipos, String descripcion) {
    try (
        Socket socket = new Socket(HOST, PORT);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
    ) {
        System.out.println("Conectado al servidor");
        System.out.println("Enviando: " + descripcion);

        Reservation reservation = new Reservation();
        reservation.setIdClient(1);
        reservation.setIdSection(1);
        reservation.setReservationDate(Date.valueOf("2026-04-03"));

        System.out.println("Reserva a enviar:");
        System.out.println("IdClient: " + reservation.getIdClient());
        System.out.println("IdSection: " + reservation.getIdSection());
        System.out.println("Fecha: " + reservation.getReservationDate());

        for (RXE item : equipos) {
            System.out.println("Equipo -> IdEquipment: " + item.getIdEquipment()
                    + ", Quantity: " + item.getQuantity());
        }

        EquipmentReservationRequest request = new EquipmentReservationRequest();
        request.setReservation(reservation);
        request.setEquipmentList(equipos);

        out.writeObject("CREATE_EQUIPMENT_RESERVATION");
        out.flush();

        out.writeObject(request);
        out.flush();

        Object response = in.readObject();
        System.out.println("Respuesta del servidor: " + response);

    } catch (Exception e) {
        System.out.println("Error al probar la reserva:");
        e.printStackTrace();
    }
}
}