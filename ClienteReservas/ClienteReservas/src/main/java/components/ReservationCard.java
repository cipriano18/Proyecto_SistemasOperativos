package components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ReservationCard extends HBox {

    private final VBox vboxDeviceList = new VBox();

    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction
    ) {
        buildCard(date, blockType, devices, cancelAction, false, null, false, null, 0, null);
    }

    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction,
            boolean showClientInfo,
            String clientName
    ) {
        buildCard(date, blockType, devices, cancelAction, showClientInfo, clientName, false, null, 0, null);
    }

    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction,
            String eventName,
            int attendeesCount,
            String observations
    ) {
        buildCard(date, blockType, devices, cancelAction, false, null, true, eventName, attendeesCount, observations);
    }

    private void buildCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction,
            boolean showClientInfo,
            String clientName,
            boolean showAuditoriumInfo,
            String eventName,
            int attendeesCount,
            String observations
    ) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setMinWidth(150);
        this.setStyle(
                "-fx-background-radius: 5;"
                + "-fx-border-color: #e0e0e0;"
                + "-fx-border-radius: 5;"
        );

        ImageView imgBlockIcon = new ImageView(loadImage(getBlockIcon(blockType)));
        imgBlockIcon.setFitHeight(50);
        imgBlockIcon.setFitWidth(50);
        imgBlockIcon.setOpacity(0.6);
        imgBlockIcon.setPickOnBounds(true);
        imgBlockIcon.setPreserveRatio(true);
        HBox.setMargin(imgBlockIcon, new Insets(0, 10, 0, 10));

        Separator mainSeparator = new Separator();
        mainSeparator.setOrientation(Orientation.VERTICAL);
        mainSeparator.setPrefHeight(200);

        VBox contentBox = new VBox();
        contentBox.setPrefHeight(showAuditoriumInfo ? 165 : (showClientInfo ? 136 : 92));
        contentBox.setPrefWidth(showAuditoriumInfo ? 760 : (showClientInfo ? 903 : 633));

        HBox infoBox = new HBox();
        infoBox.setPrefHeight(100);
        infoBox.setPrefWidth(200);
        infoBox.setSpacing(30);
        VBox.setMargin(infoBox, new Insets(10, 0, 10, 10));

        VBox dateBox = createInfoBox("FECHA", formatDate(date));

        Separator dateBlockSeparator = new Separator();
        dateBlockSeparator.setOrientation(Orientation.VERTICAL);
        dateBlockSeparator.setPrefHeight(200);

        VBox blockBox = createInfoBox("BLOQUE", getBlockName(blockType));

        infoBox.getChildren().addAll(
                dateBox,
                dateBlockSeparator,
                blockBox
        );

        if (showClientInfo) {
            Separator clientSeparator = new Separator();
            clientSeparator.setOrientation(Orientation.VERTICAL);
            clientSeparator.setPrefHeight(200);

            VBox clientBox = createInfoBox(
                    "CLIENTE",
                    clientName != null && !clientName.trim().isEmpty()
                            ? clientName
                            : "Sin nombre"
            );

            infoBox.getChildren().addAll(clientSeparator, clientBox);
        }

        if (showAuditoriumInfo) {
            Separator eventSeparator = new Separator();
            eventSeparator.setOrientation(Orientation.VERTICAL);
            eventSeparator.setPrefHeight(200);

            VBox eventBox = createInfoBox(
                    "EVENTO",
                    eventName != null && !eventName.trim().isEmpty()
                            ? eventName
                            : "Sin nombre"
            );

            Separator attendeesSeparator = new Separator();
            attendeesSeparator.setOrientation(Orientation.VERTICAL);
            attendeesSeparator.setPrefHeight(200);

            VBox attendeesBox = createInfoBox(
                    "ASISTENTES",
                    attendeesCount > 0 ? String.valueOf(attendeesCount) : "No indicado"
            );

            infoBox.getChildren().addAll(
                    eventSeparator,
                    eventBox,
                    attendeesSeparator,
                    attendeesBox
            );
        }

        Label observationsLabel = null;

        if (showAuditoriumInfo) {
            observationsLabel = new Label(
                    "Observaciones: "
                    + (observations != null && !observations.trim().isEmpty()
                    ? observations
                    : "Sin observaciones")
            );
            observationsLabel.getStyleClass().add("field-label");
            observationsLabel.setWrapText(true);
            VBox.setMargin(observationsLabel, new Insets(0, 10, 8, 10));
        }

        Label devicesTitle = new Label("Equipos solicitados");
        devicesTitle.getStyleClass().add("hero-title-2");
        VBox.setMargin(devicesTitle, new Insets(0, 0, 0, 10));

        vboxDeviceList.setSpacing(5);
        vboxDeviceList.setPadding(new Insets(5, 5, 0, 0));
        VBox.setVgrow(vboxDeviceList, Priority.ALWAYS);
        VBox.setMargin(vboxDeviceList, new Insets(0, 0, 10, 10));

        loadDevices(devices);

        contentBox.getChildren().add(infoBox);

        if (observationsLabel != null) {
            contentBox.getChildren().add(observationsLabel);
        }

        contentBox.getChildren().addAll(
                devicesTitle,
                vboxDeviceList
        );

        Region spacer = new Region();
        spacer.setPrefHeight(72);
        spacer.setPrefWidth(358);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnCancelReservation = new Button();
        btnCancelReservation.setMnemonicParsing(false);
        btnCancelReservation.setPrefHeight(40);
        btnCancelReservation.setPrefWidth(40);
        btnCancelReservation.setStyle("-fx-background-color: red;");
        HBox.setMargin(btnCancelReservation, new Insets(0, 10, 0, 10));

        ImageView removeIcon = new ImageView(loadImage("/assets/remove.png"));
        removeIcon.setFitHeight(24);
        removeIcon.setFitWidth(24);
        removeIcon.setPickOnBounds(true);
        removeIcon.setPreserveRatio(true);

        btnCancelReservation.setGraphic(removeIcon);

        btnCancelReservation.setOnAction(event -> {
            if (cancelAction != null) {
                cancelAction.run();
            }
        });

        this.getChildren().addAll(
                imgBlockIcon,
                mainSeparator,
                contentBox,
                spacer,
                btnCancelReservation
        );
    }

    private VBox createInfoBox(String title, String value) {
        VBox box = new VBox(5);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("field-label");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("hero-title-3");
        valueLabel.setWrapText(true);

        box.getChildren().addAll(titleLabel, valueLabel);

        return box;
    }

    private void loadDevices(List<DeviceItem> devices) {
        vboxDeviceList.getChildren().clear();

        if (devices == null || devices.isEmpty()) {
            Label emptyLabel = new Label("Sin equipos solicitados");
            emptyLabel.getStyleClass().add("field-label");
            vboxDeviceList.getChildren().add(emptyLabel);
            return;
        }

        for (DeviceItem device : devices) {
            vboxDeviceList.getChildren().add(createDeviceCard(device));
        }
    }

    private HBox createDeviceCard(DeviceItem device) {
        HBox card = new HBox(5);
        card.setMaxHeight(29);
        card.setMaxWidth(150);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(5));
        card.setStyle(
                "-fx-background-radius: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #e0e0e0;"
        );

        Label deviceName = new Label(device.getName());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPrefHeight(17);
        separator.setPrefWidth(2);

        Label quantity = new Label(String.valueOf(device.getQuantity()));
        HBox.setMargin(quantity, new Insets(0, 5, 0, 0));

        card.getChildren().addAll(
                deviceName,
                spacer,
                separator,
                quantity
        );

        return card;
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private String getBlockName(int blockType) {
        switch (blockType) {
            case 1:
                return "Mañana";
            case 2:
                return "Tarde";
            case 3:
                return "Noche";
            default:
                return "Desconocido";
        }
    }

    private String getBlockIcon(int blockType) {
        switch (blockType) {
            case 1:
                return "/assets/morning.png";
            case 2:
                return "/assets/day.png";
            case 3:
                return "/assets/night.png";
            default:
                return "/assets/night.png";
        }
    }

    private Image loadImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            System.out.println("Recurso de imagen no encontrado: " + path);

            InputStream fallbackStream = getClass().getResourceAsStream("/assets/unknown_item.png");
            if (fallbackStream != null) {
                return new Image(fallbackStream);
            }

            throw new IllegalStateException("No se encontró el recurso solicitado ni el fallback de imagen.");
        }

        return new Image(stream);
    }

    public static class DeviceItem {

        private final String name;
        private final int quantity;

        public DeviceItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
//Ejemplo de uso sin datos del cliente
/*
ReservationCard card = new ReservationCard(
        LocalDate.of(2026, 5, 28),
        3,
        devices,
        () -> {
            System.out.println("Cancelar reserva");
        }
);

vb_info.getChildren().add(card);
*/

//Ejemplo de uso con datos del cliente
/*
ReservationCard card = new ReservationCard(
        LocalDate.of(2026, 1, 3),
        3,
        devices,
        () -> {
            System.out.println("Administrador cancelando reserva");
        },
        true,
        "Makin Artavia Zúñiga"
);

vb_info.getChildren().add(card);
*/
