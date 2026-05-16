package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import components.TtlChip;
import dto.AuditoriumDraftRequest;
import draft.EquipmentReservationDraft;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CalendarBlock;
import network.ReservationNotificationHandler;
import service.AuditoriumDraftService;
import service.CalendarService;
import service.EquipmentReservationDraftService;
import service.Response;
import session.Session;
import utils.CalendarBuilder;
import utils.DraftContainer;

/**
 * Controlador de la pantalla de calendario para seleccionar reservas.
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
    @FXML
    private HBox hb_ttl_indicator;

    private final CalendarBuilder builder = new CalendarBuilder();
    private final List<Integer> monthValues = new ArrayList<>();

    private TtlChip ttlChip;
    private Runnable expiredListener;
    private Runnable refreshListener;

    private static final String[] MONTH_NAMES = {
        "Enero", "Febrero", "Marzo", "Abril",
        "Mayo", "Junio", "Julio", "Agosto",
        "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    /**
     * Inicializa filtros, calendario e indicador de tiempo del draft.
     *
     * @param url ubicacion usada para resolver rutas relativas
     * @param rb recursos de internacionalizacion asociados a la vista
     */
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

        chb_month.getSelectionModel().selectedIndexProperty().addListener((
                obs, 
                oldValue, 
                newValue) -> {
            
            if (newValue.intValue() >= 0) {
                loadCalendar();
            }
        });

        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();

        CalendarService.enterReservationsView(idClient);

        refreshListener = () -> {
            System.out.println("Refrescando calendario por broadcast...");
            loadCalendar();
        };
        ReservationNotificationHandler.addOnDraftExpired(refreshListener);

        setupTtlIndicator(idClient);

        loadCalendar();

        Platform.runLater(() -> {
            Stage stage = (Stage) btn_search.getScene().getWindow();

            stage.setOnCloseRequest(event -> {
                CalendarService.exitReservationsView();
                teardownTtlIndicator();
                ReservationNotificationHandler.clearOnDraftExpired();
            });
        });
    }

    /**
     * Configura el indicador de tiempo restante del draft activo.
     *
     * @param idClient identificador del cliente autenticado
     */
    private void setupTtlIndicator(int idClient) {
        if (hb_ttl_indicator == null) {
            return;
        }

        try {
            String flowType = DraftContainer.getInstance().getFlowType();
            TimestampRange ttl = null;

            if ("AUDITORIUM".equals(flowType)) {
                Response resp = AuditoriumDraftService
                        .getAuditoriumDraftByClientId(idClient);

                if (resp != null && resp.isSuccess()
                        && resp.getData() instanceof AuditoriumDraftRequest) {
                    AuditoriumDraftRequest draft
                            = (AuditoriumDraftRequest) resp.getData();

                    if (draft.getCreatedAt() != null
                            && draft.getExpiresAt() != null
                            && !draft.isExpired()) {
                        ttl = new TimestampRange(
                                draft.getCreatedAt(),
                                draft.getExpiresAt()
                        );
                    }
                }
            } else {
                Response resp = EquipmentReservationDraftService
                        .getEquipmentDraftByClientId(idClient);

                if (resp != null && resp.isSuccess()
                        && resp.getData() instanceof EquipmentReservationDraft) {
                    EquipmentReservationDraft draft
                            = (EquipmentReservationDraft) resp.getData();

                    if (draft.getCreatedAt() != null
                            && draft.getExpiresAt() != null
                            && !draft.isExpired()) {
                        ttl = new TimestampRange(
                                draft.getCreatedAt(),
                                draft.getExpiresAt()
                        );
                    }
                }
            }

            if (ttl == null) {
                teardownTtlIndicator();
                return;
            }

            ttlChip = new TtlChip();
            ttlChip.setCompact(true);
            ttlChip.setOnExpired(() -> {
                teardownTtlIndicator();
                loadCalendar();
            });

            hb_ttl_indicator.getChildren().setAll(ttlChip);
            hb_ttl_indicator.setVisible(true);
            hb_ttl_indicator.setManaged(true);

            ttlChip.start(ttl.createdAt, ttl.expiresAt);

            expiredListener = () -> {
                teardownTtlIndicator();
            };
            ReservationNotificationHandler.addOnDraftExpired(expiredListener);
        } catch (Exception e) {
            System.out.println(
                    "No se pudo cargar el indicador de TTL: " 
                    + e.getMessage());
        }
    }

    /**
     * Libera el indicador de tiempo restante y sus listeners asociados.
     */
    private void teardownTtlIndicator() {
        if (ttlChip != null) {
            ttlChip.stop();
            ttlChip = null;
        }
        if (expiredListener != null) {
            ReservationNotificationHandler
                    .removeOnDraftExpired(expiredListener);
            
            expiredListener = null;
        }
        if (hb_ttl_indicator != null) {
            hb_ttl_indicator.getChildren().clear();
            hb_ttl_indicator.setVisible(false);
            hb_ttl_indicator.setManaged(false);
        }
    }

    /**
     * Regresa a la pantalla principal y libera recursos del calendario.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToLogin(ActionEvent event) throws IOException {
        CalendarService.exitReservationsView();
        teardownTtlIndicator();
        if (refreshListener != null) {
            ReservationNotificationHandler
                    .removeOnDraftExpired(refreshListener);
            
            refreshListener = null;
        }
        App.setRoot("home_screen");
    }

    /**
     * Aplica el filtro de fecha y vuelve a cargar el calendario.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void GetCalendar(ActionEvent event) {
        clampYearAndRefresh();
    }

    /**
     * Carga los meses disponibles segun el anio seleccionado.
     */
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

    /**
     * Obtiene el anio digitado o devuelve el anio actual si es invalido.
     *
     * @return anio a utilizar en la consulta
     */
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

    /**
     * Ajusta el anio al rango permitido y recarga filtros y calendario.
     */
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
        loadCalendar();
    }

    /**
     * Consulta los bloques disponibles y reconstruye el calendario.
     */
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

        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();

        Response response;

        if ("AUDITORIUM".equals(flowType)) {
            response = CalendarService.getAuditoriumCalendarBlocks(
                    month, 
                    year, 
                    idClient);
            
        } else {
            response = CalendarService.getCalendarBlocks(month, year, idClient);
        }

        if (response == null) {
            PopUp.warning(
                    "Error de conexión",
                    "No se pudo obtener el calendario",
                    "No se pudo contactar el servidor. Verifique su conexión o "
                    + "intente nuevamente.",
                    "power_off.png",
                    1,
                    1,
                    "Aceptar"
            );

            builder.buildCalendar(
                    month, 
                    year, 
                    grid_calendar, 
                    new ArrayList<>());
            
            return;
        }

        if (!response.isSuccess()) {
            PopUp.warning(
                    "Error al cargar calendario",
                    "No se pudo obtener la información",
                    response.getMessage() != null 
                            ? response.getMessage() 
                            : "Ocurrió un error inesperado.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );

            builder.buildCalendar(
                    month, 
                    year, 
                    grid_calendar, 
                    new ArrayList<>());
            
            return;
        }

        List<CalendarBlock> blocks = (List<CalendarBlock>) response.getData();
        builder.buildCalendar(month, year, grid_calendar, blocks);
    }

    /**
     * Restringe el campo de anio a valores numericos de cuatro digitos.
     */
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

    private static final class TimestampRange {

        private final java.sql.Timestamp createdAt;
        private final java.sql.Timestamp expiresAt;

        private TimestampRange(
                java.sql.Timestamp createdAt,
                java.sql.Timestamp expiresAt) {
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
        }
    }
}
