package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import components.ReservationCard;
import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import dto.EquipmentReservationRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Equipment;
import model.Reservation;
import model.RXE;
import service.AuditoriumReservationService;
import service.EquipmentReservationService;
import service.EquipmentService;
import service.Response;
import session.Session;

public class client_schedule_screen_controller implements Initializable {

    @FXML
    private Button btn_goback;
    @FXML
    private VBox vb_info;
    @FXML
    private ImageView img_logo;
    @FXML
    private VBox vb_msg_new_acount;
    @FXML
    private Label lbl_create;
    @FXML
    private Label lbl_create2;
    @FXML
    private VBox vb_list_reservation_auditorium;
    @FXML
    private VBox vb_msg_new_acount1;
    @FXML
    private Label lbl_create1;
    @FXML
    private Label lbl_create11;
    @FXML
    private VBox vb_list_reservation_device;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadReservations();
    }

    private void loadReservations() {
        vb_list_reservation_auditorium.getChildren().clear();
        vb_list_reservation_device.getChildren().clear();

        if (Session.getInstance().getClient() == null
                || Session.getInstance().getClient().getClient() == null) {
            showLoadError("No se encontró la sesión del cliente.");
            return;
        }

        Response equipmentResponse = EquipmentService.getAllEquipment();
        if (equipmentResponse == null
                || !equipmentResponse.isSuccess()
                || !(equipmentResponse.getData() instanceof List<?>)) {
            showLoadError(
                    equipmentResponse != null
                            ? equipmentResponse.getMessage()
                            : "No se pudo conectar con el servidor."
            );
            return;
        }

        Map<Integer, String> equipmentNames = buildEquipmentNameMap((List<?>) equipmentResponse.getData());

        loadAuditoriumReservations(equipmentNames);
        loadDeviceReservations(equipmentNames);
    }

    private void loadAuditoriumReservations(Map<Integer, String> equipmentNames) {
        vb_list_reservation_auditorium.getChildren().clear();

        int idClient = Session.getInstance().getClient().getClient().getIdClient();
        Response response = AuditoriumReservationService.getAuditoriumReservationsByClientId(idClient);

        if (response == null) {
            showLoadError("No se pudo conectar con el servidor.");
            return;
        }

        if (!response.isSuccess()) {
            showEmptyState(vb_list_reservation_auditorium, "No hay reservas de auditorio activas.");
            return;
        }

        if (!(response.getData() instanceof List<?>)) {
            showLoadError("Se recibieron datos de reservas de auditorio con un formato inesperado.");
            return;
        }

        List<?> rawReservations = (List<?>) response.getData();

        for (Object item : rawReservations) {
            if (!(item instanceof AuditoriumDraftRequest)) {
                showLoadError("Se recibieron datos de reservas de auditorio con un formato inesperado.");
                vb_list_reservation_auditorium.getChildren().clear();
                return;
            }

            AuditoriumDraftRequest request = (AuditoriumDraftRequest) item;
            Reservation reservation = request.getReservation();

            if (reservation == null || reservation.getReservationDate() == null) {
                continue;
            }

            List<ReservationCard.DeviceItem> devices =
                    buildDeviceItems(request.getEquipmentList(), equipmentNames);

            AuditoriumDraft auditoriumDraft = request.getAuditoriumDraft();

            ReservationCard card = new ReservationCard(
                    reservation.getReservationDate().toLocalDate(),
                    reservation.getIdSection(),
                    devices,
                    () -> cancelAuditoriumReservation(request),
                    auditoriumDraft != null ? auditoriumDraft.getEventName() : null,
                    auditoriumDraft != null ? auditoriumDraft.getAttendeesCount() : 0,
                    auditoriumDraft != null ? auditoriumDraft.getObservations() : null
            );
            vb_list_reservation_auditorium.getChildren().add(card);
        }

        if (vb_list_reservation_auditorium.getChildren().isEmpty()) {
            showEmptyState(vb_list_reservation_auditorium, "No hay reservas de auditorio activas.");
        }
    }

    private void loadDeviceReservations(Map<Integer, String> equipmentNames) {
        vb_list_reservation_device.getChildren().clear();

        int idClient = Session.getInstance().getClient().getClient().getIdClient();
        Response response = EquipmentReservationService.getReservationsByClientId(idClient);

        if (response == null) {
            showLoadError("No se pudo conectar con el servidor.");
            return;
        }

        if (!response.isSuccess()) {
            showEmptyState(vb_list_reservation_device, "No hay reservas de equipos activas.");
            return;
        }

        if (!(response.getData() instanceof List<?>)) {
            showLoadError("Se recibieron datos de reservas de equipos con un formato inesperado.");
            return;
        }

        List<?> rawReservations = (List<?>) response.getData();

        for (Object item : rawReservations) {
            if (!(item instanceof EquipmentReservationRequest)) {
                showLoadError("Se recibieron datos de reservas de equipos con un formato inesperado.");
                vb_list_reservation_device.getChildren().clear();
                return;
            }

            EquipmentReservationRequest request = (EquipmentReservationRequest) item;
            Reservation reservation = request.getReservation();

            if (reservation == null || reservation.getReservationDate() == null) {
                continue;
            }

            List<ReservationCard.DeviceItem> devices =
                    buildDeviceItems(request.getEquipmentList(), equipmentNames);

            ReservationCard card = new ReservationCard(
                    reservation.getReservationDate().toLocalDate(),
                    reservation.getIdSection(),
                    devices,
                    () -> cancelDeviceReservation(request)
            );

            vb_list_reservation_device.getChildren().add(card);
        }

        if (vb_list_reservation_device.getChildren().isEmpty()) {
            showEmptyState(vb_list_reservation_device, "No hay reservas de equipos activas.");
        }
    }

    private Map<Integer, String> buildEquipmentNameMap(List<?> rawEquipmentList) {
        Map<Integer, String> equipmentNames = new HashMap<>();

        for (Object item : rawEquipmentList) {
            if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                equipmentNames.put(equipment.getIdEquipment(), equipment.getName());
            }
        }

        return equipmentNames;
    }

    private List<ReservationCard.DeviceItem> buildDeviceItems(
            List<RXE> equipmentList,
            Map<Integer, String> equipmentNames
    ) {
        if (equipmentList == null || equipmentList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReservationCard.DeviceItem> devices = new ArrayList<>();

        for (RXE item : equipmentList) {
            if (item == null) {
                continue;
            }

            String deviceName = equipmentNames.getOrDefault(
                    item.getIdEquipment(),
                    "Equipo #" + item.getIdEquipment()
            );

            devices.add(new ReservationCard.DeviceItem(deviceName, item.getQuantity()));
        }

        return devices;
    }

    private void cancelAuditoriumReservation(AuditoriumDraftRequest currentReservation) {
        boolean confirm = PopUp.warning(
                "Confirmación",
                "Cancelar reserva",
                "¿Desea cancelar esta reserva de auditorio?",
                "warning.png",
                2,
                "Eliminar"
        );

        if (!confirm) {
            return;
        }

        AuditoriumDraftRequest request = new AuditoriumDraftRequest();
        request.setIdClient(Session.getInstance().getClient().getClient().getIdClient());

        Reservation reservation = new Reservation();
        if (currentReservation.getReservation() != null) {
            reservation.setIdReservation(currentReservation.getReservation().getIdReservation());
        }
        request.setReservation(reservation);

        Response response = AuditoriumReservationService.deleteAuditoriumReservationById(request);

        if (response != null && response.isSuccess()) {
            PopUp.notification(
                    "Reserva cancelada",
                    "La reserva de auditorio fue cancelada correctamente.",
                    "success.png"
            );
            loadReservations();
            return;
        }

        PopUp.warning(
                "Error",
                "No se pudo cancelar la reserva",
                response != null ? response.getMessage() : "No se pudo conectar con el servidor.",
                "error.png",
                1,
                "Aceptar"
        );
    }

    private void cancelDeviceReservation(EquipmentReservationRequest currentReservation) {
        boolean confirm = PopUp.warning(
                "Confirmación",
                "Cancelar reserva",
                "¿Desea cancelar esta reserva de equipos?",
                "warning.png",
                2, 
                "Eliminar"
        );

        if (!confirm) {
            return;
        }

        EquipmentReservationRequest request = new EquipmentReservationRequest();
        request.setIdClient(Session.getInstance().getClient().getClient().getIdClient());

        Reservation reservation = new Reservation();
        if (currentReservation.getReservation() != null) {
            reservation.setIdReservation(currentReservation.getReservation().getIdReservation());
        }
        request.setReservation(reservation);

        Response response = EquipmentReservationService.deleteReservationById(request);

        if (response != null && response.isSuccess()) {
            PopUp.notification(
                    "Reserva cancelada",
                    "La reserva de equipos fue cancelada correctamente.",
                    "success.png"
            );
            loadReservations();
            return;
        }

        PopUp.warning(
                "Error",
                "No se pudo cancelar la reserva",
                response != null ? response.getMessage() : "No se pudo conectar con el servidor.",
                "error.png",
                1,
                "Aceptar"
        );
    }

    private void showEmptyState(VBox container, String message) {
        Label emptyLabel = new Label(message);
        emptyLabel.getStyleClass().add("form-subtitle");
        container.getChildren().add(emptyLabel);
    }

    private void showLoadError(String message) {
        PopUp.warning(
                "Error",
                "No se pudieron cargar las reservas",
                message,
                "error.png",
                1,
                "Aceptar"
        );
    }

    @FXML
    private void GoToLogin(ActionEvent event) throws IOException {
        App.setRoot("home_screen");
    }
}
