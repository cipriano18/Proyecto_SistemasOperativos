package components;

import com.auditorio.clientereservas.App;
import draft.EquipmentReservationDraft;
import dto.AuditoriumDraftRequest;
import dto.EquipmentReservationDraftRequest;
import java.io.IOException;
import java.sql.Date;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.CalendarBlock;
import model.Reservation;
import service.AuditoriumDraftService;
import service.CalendarService;
import service.EquipmentReservationDraftService;
import service.Response;
import session.Session;
import utils.CalendarConstants;
import utils.DraftContainer;

/**
 * Construye las tarjetas de días del calendario de reservas.
 *
 * @author Makin Artavia
 */

public class DayCard {
    
    /**
    * Construye una tarjeta visual correspondiente a un día específico
    * del calendario de reservas.
    *
    * <p>
    * La tarjeta incluye la información del día, las secciones disponibles
    * y el estado actual de cada sección según los bloques recibidos.
    * </p>
    *
    * @param number número del día
    * @param date fecha asociada al día
    * @param blocks lista de bloques de calendario utilizados para determinar
    *               el estado de las secciones
    * @return contenedor VBox completamente configurado para representar el día
    */
    public VBox createCard(int number, Date date, List<CalendarBlock> blocks) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPrefWidth(180);
        card.setPrefHeight(200);
        card.setMaxWidth(200);
        card.setMinWidth(145);
        card.getStyleClass().add("hero-panel");
        card.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
        card.setPadding(new Insets(10, 10, 15, 10));

        GridPane.setMargin(card, new Insets(10));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(50);
        header.setPadding(new Insets(5));
        header.setSpacing(8);

        Label lblDia = new Label(String.valueOf(number));
        lblDia.getStyleClass().add("hero-title");
        lblDia.setTextFill(Color.WHITE);

        Label lblNombreDia = new Label(getDayName(date));
        lblNombreDia.getStyleClass().add("hero-subtitle");
        lblNombreDia.setTextFill(Color.WHITE);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        header.getChildren().addAll(lblDia, lblNombreDia, space);
        
        Separator separator = new Separator();
        Label subtitle = new Label("Secciones del dia:");
        subtitle.getStyleClass().add("hero-subtitle");

        Button btnManana = createSectionButton(
                "Mañana",
                "/assets/morning.png",
                date,
                CalendarConstants.SECTION_MORNING,
                blocks
        );

        Button btnTarde = createSectionButton(
                "Tarde",
                "/assets/day.png",
                date,
                CalendarConstants.SECTION_AFTERNOON,
                blocks
        );

        Button btnNoche = createSectionButton(
                "Noche",
                "/assets/night.png",
                date,
                CalendarConstants.SECTION_NIGHT,
                blocks
        );

        card.getChildren().addAll(
                header, 
                separator, 
                subtitle, 
                btnManana, 
                btnTarde, 
                btnNoche);

        return card;
    }
    
    /**
    * Crea un botón representando una sección específica del día
    * dentro del sistema de reservas.
    *
    * <p>
    * El botón adapta automáticamente su apariencia y comportamiento
    * según el estado de disponibilidad de la sección.
    * También administra la lógica para continuar drafts existentes
    * o iniciar nuevas reservas.
    * </p>
    *
    * @param text texto visible del botón
    * @param iconRoute ruta del ícono asociado a la sección
    * @param date fecha de la reserva
    * @param idSection identificador de la sección
    * @param blocks lista de bloques del calendario
    * @return botón configurado para la sección indicada
    */
    
    private Button createSectionButton(
            String text,
            String iconRoute,
            Date date,
            int idSection,
            List<CalendarBlock> blocks
    ) {
        Button button = new Button();
        button.setPrefWidth(160);
        button.setPrefHeight(33);
        button.setMnemonicParsing(false);

        String status = getSectionStatus(date, idSection, blocks);
        applyButtonStatus(button, status);

        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(5);

        ImageView icon = new ImageView(
                new Image(getClass().getResourceAsStream(iconRoute)));
        
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        icon.setPreserveRatio(true);

        Label label = new Label(text);
        label.getStyleClass().add("form-subtitle");

        content.getChildren().addAll(icon, label);
        button.setGraphic(content);

        button.setOnAction(e -> {
            try {
                Reservation reservation = new Reservation();
                reservation.setIdSection(idSection);
                reservation.setReservationDate(date);

                String flowType = DraftContainer.getInstance().getFlowType();
                int idClient = Session.getInstance()
                        .getClient()
                        .getClient()
                        .getIdClient();

                if ("AUDITORIUM".equals(flowType)) {
                    Response existingDraftResp = 
                        AuditoriumDraftService.getAuditoriumDraftByClientId(
                                idClient);

                    if (isSameAuditoriumDraft(existingDraftResp, reservation)) {
                        boolean continueDraft = PopUp.warning(
                                "Reserva en proceso",
                                "Se encontro una reserva temporal",
                                "Parece que tenias una reserva en proceso,"
                                + " deseas continuar?",
                                "question.png",
                                2,
                                3,
                                "Continuar"
                        );

                        if (continueDraft) {
                            DraftContainer
                                    .getInstance()
                                    .setSelectedReservation(reservation);
                            
                            DraftContainer
                                    .getInstance()
                                    .setDraftResponse(existingDraftResp);
                            
                            CalendarService
                                    .exitReservationsView();
                            
                            App.setRoot("auditorium_form_screen");
                        }
                        return;
                    }

                    AuditoriumDraftRequest request 
                            = new AuditoriumDraftRequest();
                    
                    request.setIdClient(idClient);
                    request.setReservation(reservation);
                    request.setEquipmentList(new ArrayList<>());

                    Response resp = AuditoriumDraftService
                            .startAuditoriumDraft(request);

                    if (resp != null && resp.isSuccess()) {
                        DraftContainer
                                .getInstance()
                                .setSelectedReservation(reservation);
                        
                        DraftContainer
                                .getInstance()
                                .setDraftResponse(resp);
                        
                        CalendarService
                                .exitReservationsView();
                        
                        App.setRoot("auditorium_form_screen");
                    } else {
                        
                        String msg 
                                = (resp != null) ? resp.getMessage() 
                                : "No se pudo conectar al servidor";

                        PopUp.warning(
                                "Error",
                                "No se pudo iniciar la reserva",
                                msg,
                                "error.png",
                                1,
                                1,
                                "Aceptar"
                        );
                    }
                    return;
                }

                Response existingDraftResp 
                        = EquipmentReservationDraftService
                                .getEquipmentDraftByClientId(idClient);

                if (isSameEquipmentDraft(existingDraftResp, reservation)) {
                    boolean continueDraft = PopUp.warning(
                            "Reserva en proceso",
                            "Se encontro una reserva temporal",
                            "Parece que tenias una reserva en proceso,"
                            + " deseas continuar?",
                            "question.png",
                            2,
                            3,
                            "Continuar"
                    );

                    if (continueDraft) {
                        CalendarService.exitReservationsView();
                        
                        DraftContainer
                                .getInstance()
                                .setDraftResponse(existingDraftResp);
                        
                        App.setRoot("device_form_screen");
                    }
                    return;
                }

                EquipmentReservationDraftRequest request 
                        = new EquipmentReservationDraftRequest();
                
                request.setIdClient(idClient);
                request.setReservation(reservation);
                request.setEquipmentList(new ArrayList<>());

                Response resp 
                        = EquipmentReservationDraftService
                                .startEquipmentDraft(request);

                if (resp != null && resp.isSuccess()) {
                    CalendarService.exitReservationsView();
                    DraftContainer.getInstance().setDraftResponse(resp);
                    App.setRoot("device_form_screen");
                } else {
                    String msg 
                            = (resp != null) ? resp.getMessage() 
                            : "No se pudo conectar al servidor";

                    PopUp.warning(
                            "Error",
                            "No se pudo iniciar la reserva",
                            msg,
                            "error.png",
                            1,
                            1,
                            "Aceptar"
                    );
                }

            } catch (IOException ex) {
                Logger.getLogger(DayCard.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        });

        return button;
    }
    
    /**
    * Verifica si el draft de equipos existente corresponde
    * a la misma reserva seleccionada actualmente.
    *
    * @param response respuesta obtenida del servidor
    * @param reservation reserva seleccionada por el usuario
    * @return true si ambas reservas coinciden; false en caso contrario
    */
    
    private boolean isSameAuditoriumDraft(
            Response response, 
            Reservation reservation) {
        
        if (
            response == null || 
            !response.isSuccess() || 
            !(response.getData() instanceof AuditoriumDraftRequest)) {
            
            return false;
        }

        AuditoriumDraftRequest draft 
                = (AuditoriumDraftRequest) response.getData();
        
        return sameReservation(draft.getReservation(), reservation);
    }

    private boolean isSameEquipmentDraft(
            Response response, 
            Reservation reservation) {
        
        if (
            response == null || 
            !response.isSuccess() || 
            !(response.getData() instanceof EquipmentReservationDraft)) {
            
            return false;
        }

        EquipmentReservationDraft draft 
                = (EquipmentReservationDraft) response.getData();
        
        return sameReservation(draft.getReservation(), reservation);
    }
    
    /**
    * Compara dos reservas para determinar si pertenecen
    * a la misma fecha y sección.
    *
    * @param existing reserva existente
    * @param selected reserva seleccionada
    * @return true si ambas reservas representan el mismo espacio reservado
    */

    private boolean sameReservation(
            Reservation existing, 
            Reservation selected) {
        
        if (existing == null || selected == null) {
            return false;
        }

        if (
            existing.getReservationDate() == null || 
            selected.getReservationDate() == null) {
            
            return false;
        }

        return existing.getIdSection() == selected.getIdSection()
                && existing.getReservationDate().equals(
                        selected.getReservationDate());
    }
    
    /**
    * Obtiene el estado actual de una sección específica
    * en una fecha determinada.
    *
    * @param date fecha consultada
    * @param idSection identificador de la sección
    * @param blocks lista de bloques del calendario
    * @return estado de la sección según los bloques registrados
    */
    
    private String getSectionStatus(
            Date date, 
            int idSection, 
            List<CalendarBlock> blocks) {
        
        if (blocks == null || blocks.isEmpty()) {
            return CalendarConstants.STATUS_AVAILABLE;
        }

        for (CalendarBlock block : blocks) {
            if (block.getReservationDate().equals(date)
                    && block.getIdSection() == idSection) {
                return block.getStatus();
            }
        }

        return CalendarConstants.STATUS_AVAILABLE;
    }
    
    /**
    * Aplica los estilos visuales y restricciones de interacción
    * a un botón según el estado de la reserva.
    *
    * <p>
    * También configura los tooltips informativos asociados
    * al estado actual de la sección.
    * </p>
    *
    * @param button botón al que se aplicarán los cambios
    * @param status estado actual de la sección
    */
    private void applyButtonStatus(Button button, String status) {

        button.getStyleClass().removeAll(
                "section-available",
                "section-reserved",
                "section-blocked",
                "section-own-draft"
        );

        Tooltip.uninstall(button, button.getTooltip());

        Tooltip tooltip = new Tooltip();

        switch (status) {

            case CalendarConstants.STATUS_RESERVED:
                button.getStyleClass().add("section-reserved");
                button.setDisable(true);
                button.setOpacity(0);
                tooltip.setText("Este espacio ya esta reservado");
                Tooltip.install(button, tooltip);
                break;

            case CalendarConstants.STATUS_BLOCKED:
                button.getStyleClass().add("section-blocked");
                button.setDisable(true);
                tooltip.setText("Este espacio esta bloqueado");
                Tooltip.install(button, tooltip);
                break;

            case CalendarConstants.STATUS_OWN_DRAFT:
                button.getStyleClass().add("section-own-draft");
                button.setDisable(false);
                tooltip.setText("Tienes una reserva temporal en proceso");
                button.setTooltip(tooltip);
                break;

            default:
                button.getStyleClass().add("section-available");
                button.setDisable(false);
                tooltip.setText("Disponible para reservar");
                button.setTooltip(tooltip);
                break;
        }
    }
    /**
    * Obtiene el nombre del día de la semana en español
    * a partir de una fecha.
    *
    * @param date fecha utilizada para obtener el día
    * @return nombre del día con la primera letra en mayúscula
    */
    private String getDayName(Date date) {
        String dayName = date.toLocalDate()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

        return dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
    }
}

