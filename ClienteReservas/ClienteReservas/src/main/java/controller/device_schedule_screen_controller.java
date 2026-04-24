/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.CalendarBlock;
import model.Response;
import service.CalendarService;
import utils.CalendarBuilder;
import utils.CalendarConstants;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setupYearField();

        int currentYear = java.time.LocalDate.now().getYear();
        tf_year.setText(String.valueOf(currentYear));

        loadMonths();

        chb_month.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            loadCalendar();
        });

        loadCalendar();
    }

    @FXML
    private void GoToLogin(ActionEvent event) {
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

        int month = chb_month.getSelectionModel().getSelectedIndex() + 1;

        String yearText = tf_year.getText().trim();

        int year;

        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            System.out.println("Año inválido");
            return;
        }

        int currentYear = java.time.LocalDate.now().getYear();
        int currentMonth = java.time.LocalDate.now().getMonthValue();

        if (year < currentYear) {
            System.out.println("El año no puede ser menor al actual");
            return;
        }

        if (year == currentYear && month < currentMonth) {
            System.out.println("No se puede seleccionar un mes pasado");
            return;
        }

        Response response = CalendarService.getCalendarBlocks(month, year);

        if (response.isSuccess()) {
            List<CalendarBlock> blocks = (List<CalendarBlock>) response.getData();

            builder.buildCalendar(month, year, grid_calendar, blocks);
        } else {
            System.out.println(response.getMessage());

            builder.buildCalendar(month, year, grid_calendar, new ArrayList<>());
        }
    }

    private void setupYearField() {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            // Solo números y máximo 4 dígitos
            if (newText.matches("\\d{0,4}")) {
                return change;
            }

            return null;
        };

        tf_year.setTextFormatter(new TextFormatter<>(filter));
    }
}
