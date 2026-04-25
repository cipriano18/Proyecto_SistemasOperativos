/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import com.auditorio.clientereservas.App;
import components.PopUp;
import dto.EquipmentReservationDraftRequest;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.CalendarBlock;
import model.Reservation;
import service.CalendarService;
import service.EquipmentReservationDraftService;
import service.Response;
import session.Session;
import utils.CalendarConstants;
import utils.DraftContainer;

public class DayCard {

    public VBox createCard(int number, Date date, List<CalendarBlock> blocks) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPrefWidth(180);
        card.setPrefHeight(200);
        card.setMaxWidth(200);
        card.getStyleClass().add("hero-panel");
        card.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");
        card.setPadding(new Insets(10, 10, 15, 10));

        GridPane.setMargin(card, new Insets(10));

        HBox header = new HBox();
        header.setPrefHeight(50);
        header.setPadding(new Insets(5));

        Label lblDia = new Label(String.valueOf(number));
        lblDia.getStyleClass().add("hero-title");
        lblDia.setTextFill(Color.WHITE);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        header.getChildren().addAll(lblDia, space);

        Label subtitle = new Label("Secciones del día:");
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

        card.getChildren().addAll(header, subtitle, btnManana, btnTarde, btnNoche);

        return card;
    }

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

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconRoute)));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        icon.setPreserveRatio(true);

        Label label = new Label(text);
        label.getStyleClass().add("form-subtitle");

        content.getChildren().addAll(icon, label);
        button.setGraphic(content);

        button.setOnAction(e -> {
            try {
                EquipmentReservationDraftRequest request = new EquipmentReservationDraftRequest();
                int idClient = Session.getInstance()
                        .getClient()
                        .getClient()
                        .getIdClient();
                request.setIdClient(idClient);

                // Reservation
                Reservation reservation = new Reservation();
                reservation.setIdSection(idSection);
                reservation.setReservationDate(date);
                request.setReservation(reservation);

                // Lista vacía
                request.setEquipmentList(new ArrayList<>());
                Response resp = EquipmentReservationDraftService.startEquipmentDraft(request);

                if (resp != null && resp.isSuccess()) {
                    CalendarService.exitReservationsView();
                    DraftContainer.getInstance().setDraftResponse(resp);
                    App.setRoot("device_form_screen");
                } else {
                    String msg = (resp != null) ? resp.getMessage() : "No se pudo conectar al servidor";

                    PopUp.warning(
                            "Error",
                            "No se pudo iniciar la reserva",
                            msg,
                            "error.png",
                            1,
                            "Aceptar"
                    );
                }

            } catch (IOException ex) {
                Logger.getLogger(DayCard.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        return button;
    }

    private String getSectionStatus(Date date, int idSection, List<CalendarBlock> blocks) {
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

    private void applyButtonStatus(Button button, String status) {

        button.getStyleClass().removeAll(
                "section-available",
                "section-reserved",
                "section-blocked"
        );

        // IMPORTANTE: limpiar tooltip anterior
        Tooltip.uninstall(button, button.getTooltip());

        Tooltip tooltip = new Tooltip();

        switch (status) {

            case CalendarConstants.STATUS_RESERVED:
                button.getStyleClass().add("section-reserved");
                button.setDisable(true);
                button.setOpacity(0);
                tooltip.setText("Este espacio ya está reservado");
                Tooltip.install(button, tooltip);
                break;

            case CalendarConstants.STATUS_BLOCKED:
                button.getStyleClass().add("section-blocked");
                button.setDisable(true);

                tooltip.setText("Este espacio está bloqueado");
                Tooltip.install(button, tooltip);
                break;

            default:
                button.getStyleClass().add("section-available");
                button.setDisable(false);

                tooltip.setText("Disponible para reservar");
                button.setTooltip(tooltip); // aquí sí sirve normal
                break;
        }
    }
}
