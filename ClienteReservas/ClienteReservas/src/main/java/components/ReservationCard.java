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
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Tarjeta visual que muestra la informacion principal de una reserva
 * y los equipos asociados.
 */
public class ReservationCard extends HBox {

    private final VBox vboxDeviceList = new VBox();

    /**
     * Crea una tarjeta de reserva con informacion basica.
     *
     * @param date fecha de la reserva
     * @param blockType bloque horario reservado
     * @param devices equipos solicitados
     * @param cancelAction accion a ejecutar al cancelar la reserva
     */
    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction
    ) {
        buildCard(
                date, blockType, devices, cancelAction,
                false, null, false, null, 0, null);
    }

    /**
     * Crea una tarjeta de reserva con informacion basica y del cliente.
     *
     * @param date fecha de la reserva
     * @param blockType bloque horario reservado
     * @param devices equipos solicitados
     * @param cancelAction accion a ejecutar al cancelar la reserva
     * @param showClientInfo indica si debe mostrarse la informacion del cliente
     * @param clientName nombre del cliente asociado
     */
    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction,
            boolean showClientInfo,
            String clientName
    ) {
        buildCard(
                date, blockType, devices, cancelAction, 
                showClientInfo, clientName, false, null, 
                0, null);
    }

    /**
     * Crea una tarjeta de reserva con informacion del evento y auditorio.
     *
     * @param date fecha de la reserva
     * @param blockType bloque horario reservado
     * @param devices equipos solicitados
     * @param cancelAction accion a ejecutar al cancelar la reserva
     * @param eventName nombre del evento asociado
     * @param attendeesCount cantidad de asistentes
     * @param observations observaciones adicionales de la reserva
     */
    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction,
            String eventName,
            int attendeesCount,
            String observations
    ) {
        buildCard(
                date, blockType, devices, cancelAction, 
                false, null, true, eventName, 
                attendeesCount, observations);
    }

    /**
     * Crea una tarjeta de reserva con informacion completa del cliente
     * y del evento.
     *
     * @param date fecha de la reserva
     * @param blockType bloque horario reservado
     * @param devices equipos solicitados
     * @param cancelAction accion a ejecutar al cancelar la reserva
     * @param clientName nombre del cliente asociado
     * @param eventName nombre del evento asociado
     * @param attendeesCount cantidad de asistentes
     * @param observations observaciones adicionales de la reserva
     */
    public ReservationCard(
            LocalDate date,
            int blockType,
            List<DeviceItem> devices,
            Runnable cancelAction,
            String clientName,
            String eventName,
            int attendeesCount,
            String observations
    ) {
        buildCard(
                date, blockType, devices, cancelAction, 
                true, clientName, true, eventName, 
                attendeesCount, observations);
    }

    /**
     * Construye la tarjeta segun la informacion disponible para la reserva.
     *
     * @param date fecha de la reserva
     * @param blockType bloque horario reservado
     * @param devices equipos solicitados
     * @param cancelAction accion a ejecutar al cancelar la reserva
     * @param showClientInfo indica si debe mostrarse la informacion del cliente
     * @param clientName nombre del cliente asociado
     * @param showAuditoriumInfo indica si deben mostrarse datos del evento
     * @param eventName nombre del evento asociado
     * @param attendeesCount cantidad de asistentes
     * @param observations observaciones adicionales de la reserva
     */
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

        ImageView imgBlockIcon 
                = new ImageView(loadImage(getBlockIcon(blockType)));
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
        contentBox.setMinWidth(0);
        contentBox.setMinHeight(Region.USE_PREF_SIZE);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        FlowPane infoBox = new FlowPane();
        infoBox.setHgap(20);
        infoBox.setVgap(10);
        infoBox.setOrientation(Orientation.HORIZONTAL);
        VBox.setMargin(infoBox, new Insets(10, 10, 10, 10));

        VBox dateBox = createInfoBox("FECHA", formatDate(date));
        VBox blockBox = createInfoBox("BLOQUE", getBlockName(blockType));

        infoBox.getChildren().addAll(dateBox, blockBox);

        if (showClientInfo && !showAuditoriumInfo) {
            VBox clientBox = createInfoBox(
                    "CLIENTE",
                    clientName != null && !clientName.trim().isEmpty()
                            ? clientName
                            : "Sin nombre"
            );

            infoBox.getChildren().add(clientBox);
        }

        if (showAuditoriumInfo) {
            if (showClientInfo) {
                VBox clientBox = createInfoBox(
                        "CLIENTE",
                        clientName != null && !clientName.trim().isEmpty()
                                ? clientName
                                : "Sin nombre"
                );

                infoBox.getChildren().add(clientBox);
            }

            VBox eventBox = createInfoBox(
                    "EVENTO",
                    eventName != null && !eventName.trim().isEmpty()
                            ? eventName
                            : "Sin nombre"
            );

            VBox attendeesBox = createInfoBox(
                    "ASISTENTES",
                    attendeesCount > 0 
                            ? String.valueOf(attendeesCount) 
                            : "No indicado"
            );

            infoBox.getChildren().addAll(eventBox, attendeesBox);
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
        spacer.setMinWidth(20);
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

    /**
     * Crea un bloque de informacion con un titulo y su valor.
     *
     * @param title titulo del dato mostrado
     * @param value valor asociado al titulo
     * @return contenedor visual del dato
     */
    private VBox createInfoBox(String title, String value) {
        VBox box = new VBox(5);
        box.setMinWidth(Region.USE_PREF_SIZE);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("field-label");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("hero-title-3");
        valueLabel.setWrapText(true);
        valueLabel.setMinWidth(Region.USE_PREF_SIZE);
        valueLabel.setMaxWidth(260);
        if (value != null && !value.isEmpty()) {
            valueLabel.setTooltip(new Tooltip(value));
        }

        box.getChildren().addAll(titleLabel, valueLabel);

        return box;
    }

    /**
     * Carga la lista de equipos solicitados dentro de la tarjeta.
     *
     * @param devices equipos asociados a la reserva
     */
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

    /**
     * Crea una tarjeta compacta para representar un equipo solicitado.
     *
     * @param device equipo a representar
     * @return contenedor visual del equipo
     */
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

    /**
     * Convierte una fecha al formato usado por la tarjeta.
     *
     * @param date fecha a formatear
     * @return fecha formateada o una cadena vacia si no hay fecha
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    /**
     * Obtiene el nombre visible del bloque horario indicado.
     *
     * @param blockType identificador del bloque horario
     * @return nombre asociado al bloque
     */
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

    /**
     * Obtiene la ruta del icono correspondiente al bloque horario.
     *
     * @param blockType identificador del bloque horario
     * @return ruta del recurso de imagen
     */
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

    /**
     * Carga una imagen desde recursos y usa una imagen de respaldo
     * si la ruta solicitada no existe.
     *
     * @param path ruta del recurso de imagen
     * @return imagen cargada
     */
    private Image loadImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            System.out.println("Recurso de imagen no encontrado: " + path);

            InputStream fallbackStream 
                    = getClass().getResourceAsStream(
                            "/assets/unknown_item.png");
            if (fallbackStream != null) {
                return new Image(fallbackStream);
            }

            throw new IllegalStateException("No se encontró el recurso "
                    + "solicitado ni el fallback de imagen.");
        }

        return new Image(stream);
    }

    /**
     * Representa un equipo solicitado dentro de una reserva.
     */
    public static class DeviceItem {

        private final String name;
        private final int quantity;

        /**
         * Crea una representacion simple de equipo y cantidad.
         *
         * @param name nombre del equipo
         * @param quantity cantidad solicitada
         */
        public DeviceItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        /**
         * Obtiene el nombre del equipo.
         *
         * @return nombre del equipo
         */
        public String getName() {
            return name;
        }

        /**
         * Obtiene la cantidad solicitada del equipo.
         *
         * @return cantidad del equipo
         */
        public int getQuantity() {
            return quantity;
        }
    }
}

