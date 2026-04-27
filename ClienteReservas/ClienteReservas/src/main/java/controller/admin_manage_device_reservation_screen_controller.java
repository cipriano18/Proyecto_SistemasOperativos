package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import components.ReservationCard;
import dto.EquipmentReservationRequest;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Equipment;
import model.Reservation;
import model.RXE;
import service.EquipmentReservationService;
import service.EquipmentService;
import service.Response;

public class admin_manage_device_reservation_screen_controller implements Initializable {

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
    private Label lbl_create21;
    @FXML
    private ChoiceBox<String> chb_month;
    @FXML
    private Label lbl_create211;
    @FXML
    private TextField tf_year;
    @FXML
    private Label lbl_create2111;
    @FXML
    private Button btn_serch;
    @FXML
    private Label lbl_create21111;
    @FXML
    private Button btn_clear_filter;

    private final List<Integer> monthValues = new ArrayList<>();

    private static final String[] MONTH_NAMES = {
        "Enero", "Febrero", "Marzo", "Abril",
        "Mayo", "Junio", "Julio", "Agosto",
        "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupYearField();

        int currentYear = LocalDate.now().getYear();
        tf_year.setText(String.valueOf(currentYear));

        loadMonths();

        chb_month.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            if (newIndex != null && newIndex.intValue() >= 0) {
                loadReservationsByMonth();
            }
        });

        tf_year.focusedProperty().addListener((obs, oldValue, focused) -> {
            if (!focused) {
                clampYearAndRefreshMonths();
                loadReservationsByMonth();
            }
        });

        loadReservationsByMonth();
    }

    @FXML
    private void GoToHome(ActionEvent event) throws IOException {
        App.setRoot("admin_home_screen");
    }

    @FXML
    private void ApplyFilter(ActionEvent event) {
        clampYearAndRefreshMonths();
        loadReservationsByMonth();
    }

    @FXML
    private void ClearFilter(ActionEvent event) {
        tf_year.setText(String.valueOf(LocalDate.now().getYear()));
        loadMonths();
        loadReservationsByMonth();
    }

    private void loadMonths() {
        chb_month.getItems().clear();
        monthValues.clear();

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int selectedYear = parseYearOrCurrent();
        int startMonth = selectedYear == currentYear ? currentMonth : 1;

        for (int month = startMonth; month <= 12; month++) {
            chb_month.getItems().add(MONTH_NAMES[month - 1]);
            monthValues.add(month);
        }

        if (!chb_month.getItems().isEmpty()) {
            chb_month.getSelectionModel().selectFirst();
        }
    }

    private void loadReservationsByMonth() {
        vb_list_reservation_auditorium.getChildren().clear();

        int selectedIndex = chb_month.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= monthValues.size()) {
            showEmptyState("No hay meses disponibles para consultar.");
            return;
        }

        int month = monthValues.get(selectedIndex);
        int year = parseYearOrCurrent();

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

        Map<Integer, String> equipmentNames =
                buildEquipmentNameMap((List<?>) equipmentResponse.getData());

        Response response = EquipmentReservationService.getEquipmentReservationsByMonth(month, year);
        if (response == null) {
            showLoadError("No se pudo conectar con el servidor.");
            return;
        }

        if (!response.isSuccess()) {
            showEmptyState("No se encontraron reservaciones de equipos para el periodo indicado.");
            return;
        }

        if (!(response.getData() instanceof List<?>)) {
            showLoadError("Se recibieron datos de reservas con un formato inesperado.");
            return;
        }

        List<?> rawReservations = (List<?>) response.getData();

        for (Object item : rawReservations) {
            if (!(item instanceof EquipmentReservationRequest)) {
                showLoadError("Se recibieron datos de reservas con un formato inesperado.");
                vb_list_reservation_auditorium.getChildren().clear();
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
                    () -> cancelDeviceReservation(request),
                    true,
                    request.getClientName()
            );
            vb_list_reservation_auditorium.getChildren().add(card);
        }

        if (vb_list_reservation_auditorium.getChildren().isEmpty()) {
            showEmptyState("No se encontraron reservaciones de equipos para el periodo indicado.");
        }
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

        request.setIdClient(currentReservation.getIdClient());

        Reservation reservation = new Reservation();

        if (currentReservation.getReservation() != null) {
            reservation.setIdReservation(
                    currentReservation.getReservation().getIdReservation()
            );
        }

        request.setReservation(reservation);

        Response response = EquipmentReservationService.deleteReservationById(request);

        if (response != null && response.isSuccess()) {
            PopUp.notification(
                    "Reserva cancelada",
                    "La reserva de equipos fue cancelada correctamente.",
                    "success.png"
            );

            loadReservationsByMonth();
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

    private int parseYearOrCurrent() {
        int currentYear = LocalDate.now().getYear();
        String text = tf_year.getText() == null ? "" : tf_year.getText().trim();

        if (text.isEmpty()) {
            return currentYear;
        }

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return currentYear;
        }
    }

    private void clampYearAndRefreshMonths() {
        int currentYear = LocalDate.now().getYear();
        int selectedYear = parseYearOrCurrent();

        if (selectedYear < currentYear) {
            selectedYear = currentYear;
        }

        tf_year.setText(String.valueOf(selectedYear));
        loadMonths();
    }

    private void setupYearField() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,4}") ? change : null;
        };

        tf_year.setTextFormatter(new TextFormatter<>(filter));
    }

    private void showEmptyState(String message) {
        Label emptyLabel = new Label(message);
        emptyLabel.getStyleClass().add("form-subtitle");
        vb_list_reservation_auditorium.getChildren().add(emptyLabel);
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
}
