
package components;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ListDeviceCard extends HBox {

    private Label lblDeviceTitle;
    private Label lblQuantityTitle;

    private ChoiceBox<String> chbDeviceType;
    private ChoiceBox<Integer> chbQuantity;

    private Button btnDeleteThis;

    public ListDeviceCard() {
        buildComponent();
    }

    public ListDeviceCard(List<String> deviceTypes, List<Integer> quantities) {
        buildComponent();

        chbDeviceType.getItems().setAll(deviceTypes);
        chbQuantity.getItems().setAll(quantities);
    }

    private void buildComponent() {
        // HBox principal
        this.setId("hb_list_device_card");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(74);
        this.setPrefWidth(740);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-radius: 5; "
                + "-fx-border-radius: 5; "
                + "-fx-border-color: #e0e0e0;");

        // VBox dispositivo
        VBox vbDevice = new VBox();

        lblDeviceTitle = new Label("Dispositivo");
        lblDeviceTitle.getStyleClass().add("form-subtitle");

        chbDeviceType = new ChoiceBox<>();
        chbDeviceType.setPrefWidth(150);
        chbDeviceType.getStylesheets().add(
                getClass().getResource("/styles/schedule.css").toExternalForm()
        );

        vbDevice.getChildren().addAll(lblDeviceTitle, chbDeviceType);

        // VBox cantidad
        VBox vbQuantity = new VBox();

        lblQuantityTitle = new Label("Cantidad");
        lblQuantityTitle.getStyleClass().add("form-subtitle");

        chbQuantity = new ChoiceBox<>();
        chbQuantity.setPrefWidth(150);
        chbQuantity.getStylesheets().add(
                getClass().getResource("/styles/schedule.css").toExternalForm()
        );

        vbQuantity.getChildren().addAll(lblQuantityTitle, chbQuantity);

        // Espaciador
        Region spacer = new Region();
        spacer.setPrefHeight(20);
        spacer.setPrefWidth(338);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botón eliminar
        btnDeleteThis = new Button();
        btnDeleteThis.setAlignment(Pos.CENTER);
        btnDeleteThis.setPrefHeight(38);
        btnDeleteThis.setPrefWidth(38);
        btnDeleteThis.setStyle("-fx-background-color: red; "
                + "-fx-background-radius: 5; "
                + "-fx-border-radius: 5;");

        ImageView deleteIcon = new ImageView(
                new Image(getClass().getResource("/assets/delete.png").toExternalForm())
        );
        deleteIcon.setFitHeight(24);
        deleteIcon.setFitWidth(24);
        deleteIcon.setPickOnBounds(true);
        deleteIcon.setPreserveRatio(true);

        btnDeleteThis.setGraphic(deleteIcon);

        this.getChildren().addAll(vbDevice, vbQuantity, spacer, btnDeleteThis);
    }

    public ChoiceBox<String> getChbDeviceType() {
        return chbDeviceType;
    }

    public ChoiceBox<Integer> getChbQuantity() {
        return chbQuantity;
    }

    public Button getBtnDeleteThis() {
        return btnDeleteThis;
    }

    public String getSelectedDeviceType() {
        return chbDeviceType.getValue();
    }

    public Integer getSelectedQuantity() {
        return chbQuantity.getValue();
    }

    public void setOnDelete(Runnable action) {
        btnDeleteThis.setOnAction(e -> action.run());
    }
}
