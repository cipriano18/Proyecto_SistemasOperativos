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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupYearField();

        int currentYear = java.time.LocalDate.now().getYear();
        tf_year.setText(String.valueOf(currentYear));

        loadMonths();

        chb_month.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            loadCalendar();
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
        loadCalendar();
    }

    private void loadMonths() {
        chb_month.getItems().clear();

        chb_month.getItems().addAll(
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        int currentMonth = java.time.LocalDate.now().getMonthValue();
        chb_month.getSelectionModel().select(currentMonth - 1);
    }

    private void loadCalendar() {

        int selectedIndex = chb_month.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            return;
        }

        int month = selectedIndex + 1;

        String yearText = tf_year.getText().trim();

        if (yearText.isEmpty()) {
            System.out.println("Debe ingresar un año");
            builder.buildCalendar(month, java.time.LocalDate.now().getYear(), grid_calendar, new ArrayList<>());
            return;
        }

        int year;

        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            System.out.println("Año inválido");
            builder.buildCalendar(month, java.time.LocalDate.now().getYear(), grid_calendar, new ArrayList<>());
            return;
        }

        int currentYear = java.time.LocalDate.now().getYear();
        int currentMonth = java.time.LocalDate.now().getMonthValue();

        if (year < currentYear) {
            System.out.println("El año no puede ser menor al actual");
            builder.buildCalendar(currentMonth, currentYear, grid_calendar, new ArrayList<>());
            chb_month.getSelectionModel().select(currentMonth - 1);
            tf_year.setText(String.valueOf(currentYear));
            return;
        }

        if (year == currentYear && month < currentMonth) {
            System.out.println("No se puede seleccionar un mes pasado");
            builder.buildCalendar(currentMonth, currentYear, grid_calendar, new ArrayList<>());
            chb_month.getSelectionModel().select(currentMonth - 1);
            return;
        }

        Response response = CalendarService.getCalendarBlocks(month, year);

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
