package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.CalendarBlock;
import service.Response;
import service.CalendarService;
import utils.CalendarBuilder;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.DraftContainer;

public class device_schedule_screen_controller implements Initializable {

    @FXML
    private Button btn_goback;
    @FXML
    private VBox vb_info1;
    @FXML
    private Label lbl_title;
    @FXML
    private Label lbl_audInfo;
    @FXML
    private ImageView img_logo;
    @FXML
    private GridPane grid_calendar;
    @FXML
    private ChoiceBox<String> chb_month;
    @FXML
    private TextField tf_year;
    @FXML
    private Button btn_search;
    
    private final CalendarBuilder builder = new CalendarBuilder();
    private final List<Integer> monthValues = new ArrayList<>();

    private static final String[] MONTH_NAMES = {
        "Enero", "Febrero", "Marzo", "Abril",
        "Mayo", "Junio", "Julio", "Agosto",
        "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupYearField();

        int currentYear = java.time.LocalDate.now().getYear();
        tf_year.setText(String.valueOf(currentYear));

        tf_year.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                clampYearAndRefresh();
            }
        });

        loadMonths();

        chb_month.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                loadCalendar();
            }
        });

        loadCalendar();

        Platform.runLater(() -> {
            Stage stage = (Stage) btn_search.getScene().getWindow();

            stage.setOnCloseRequest(event -> {
                CalendarService.exitReservationsView();
            });
        });
    }

    @FXML
    private void GoToLogin(ActionEvent event) throws IOException {
        CalendarService.exitReservationsView();
        App.setRoot("home_screen");
    }

    @FXML
    private void GetCalendar(ActionEvent event) {
        clampYearAndRefresh();
    }

    private void loadMonths() {
        chb_month.getItems().clear();
        monthValues.clear();

        int currentYear = java.time.LocalDate.now().getYear();
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int yearTyped = parseYearOrCurrent();

        int startMonth = (yearTyped == currentYear) ? currentMonth : 1;

        for (int m = startMonth; m <= 12; m++) {
            chb_month.getItems().add(MONTH_NAMES[m - 1]);
            monthValues.add(m);
        }

        chb_month.getSelectionModel().selectFirst();
    }

    private int parseYearOrCurrent() {
        int currentYear = java.time.LocalDate.now().getYear();
        if (tf_year == null) {
            return currentYear;
        }
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

    private void clampYearAndRefresh() {
        int currentYear = java.time.LocalDate.now().getYear();
        int yearTyped = parseYearOrCurrent();

        if (yearTyped < currentYear) {
            yearTyped = currentYear;
        }

        String desired = String.valueOf(yearTyped);
        if (!desired.equals(tf_year.getText())) {
            tf_year.setText(desired);
        }

        loadMonths();
    }

    private void loadCalendar() {

        int selectedIndex = chb_month.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= monthValues.size()) {
            return;
        }

        int month = monthValues.get(selectedIndex);

        int year = parseYearOrCurrent();
        int currentYear = java.time.LocalDate.now().getYear();
        if (year < currentYear) {
            year = currentYear;
        }

        String flowType = DraftContainer.getInstance().getFlowType();

        Response response;

        if ("AUDITORIUM".equals(flowType)) {
            response = CalendarService.getAuditoriumCalendarBlocks(month, year);
        } else {
            response = CalendarService.getCalendarBlocks(month, year);
        }

        if (response == null) {
            PopUp.warning(
                    "Error de conexión",
                    "No se pudo obtener el calendario",
                    "No se pudo contactar el servidor. Verifique su conexión o intente nuevamente.",
                    "power_off.png",
                    1,
                    "Aceptar"
            );

            builder.buildCalendar(month, year, grid_calendar, new ArrayList<>());
            return;
        }

        if (!response.isSuccess()) {
            PopUp.warning(
                    "Error al cargar calendario",
                    "No se pudo obtener la información",
                    response.getMessage() != null ? response.getMessage() : "Ocurrió un error inesperado.",
                    "error.png",
                    1,
                    "Aceptar"
            );

            builder.buildCalendar(month, year, grid_calendar, new ArrayList<>());
            return;
        }

        List<CalendarBlock> blocks = (List<CalendarBlock>) response.getData();
        builder.buildCalendar(month, year, grid_calendar, blocks);
    }

    private void setupYearField() {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            if (newText.matches("\\d{0,4}")) {
                return change;
            }

            return null;
        };

        tf_year.setTextFormatter(new TextFormatter<>(filter));
    }
}
