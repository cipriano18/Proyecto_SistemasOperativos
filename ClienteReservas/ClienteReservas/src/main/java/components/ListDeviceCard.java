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
import javafx.util.StringConverter;
import model.Equipment;

public class ListDeviceCard extends HBox {

    private ChoiceBox<Equipment> chbDeviceType;
    private ChoiceBox<Integer> chbQuantity;
    private Button btnDeleteThis;

    public ListDeviceCard(List<Equipment> equipmentList) {
        buildComponent();
        setupEquipmentChoiceBox();

        chbDeviceType.getItems().setAll(equipmentList);

        chbDeviceType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadQuantities(newVal);
            }
        });

        if (!equipmentList.isEmpty()) {
            chbDeviceType.getSelectionModel().selectFirst();
        }
    }

    private void buildComponent() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefHeight(74);
        this.setPrefWidth(740);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-background-radius: 5; "
                + "-fx-border-radius: 5; "
                + "-fx-border-color: #e0e0e0;");

        VBox vbDevice = new VBox();

        Label lblDeviceTitle = new Label("Dispositivo");
        lblDeviceTitle.getStyleClass().add("form-subtitle");

        chbDeviceType = new ChoiceBox<>();
        chbDeviceType.setPrefWidth(150);
        chbDeviceType.getStylesheets().add(
                getClass().getResource("/styles/schedule.css").toExternalForm()
        );

        vbDevice.getChildren().addAll(lblDeviceTitle, chbDeviceType);

        VBox vbQuantity = new VBox();

        Label lblQuantityTitle = new Label("Cantidad");
        lblQuantityTitle.getStyleClass().add("form-subtitle");

        chbQuantity = new ChoiceBox<>();
        chbQuantity.setPrefWidth(150);
        chbQuantity.getStylesheets().add(
                getClass().getResource("/styles/schedule.css").toExternalForm()
        );

        vbQuantity.getChildren().addAll(lblQuantityTitle, chbQuantity);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnDeleteThis = new Button();
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
        deleteIcon.setPreserveRatio(true);

        btnDeleteThis.setGraphic(deleteIcon);

        this.getChildren().addAll(vbDevice, vbQuantity, spacer, btnDeleteThis);
    }

    private void setupEquipmentChoiceBox() {
        chbDeviceType.setConverter(new StringConverter<Equipment>() {
            @Override
            public String toString(Equipment eq) {
                return eq == null ? "" : eq.getName();
            }

            @Override
            public Equipment fromString(String string) {
                return null;
            }
        });
    }

    private void loadQuantities(Equipment eq) {
        chbQuantity.getItems().clear();

        for (int i = 1; i <= eq.getTotalQuantity(); i++) {
            chbQuantity.getItems().add(i);
        }

        if (!chbQuantity.getItems().isEmpty()) {
            chbQuantity.getSelectionModel().selectFirst();
        }
    }

    public Equipment getSelectedEquipment() {
        return chbDeviceType.getValue();
    }

    public Integer getSelectedQuantity() {
        return chbQuantity.getValue();
    }

    public Button getBtnDeleteThis() {
        return btnDeleteThis;
    }

    public void setOnDelete(Runnable action) {
        btnDeleteThis.setOnAction(e -> action.run());
    }

    public void setDeviceChoiceDisabled(boolean disabled) {
        chbDeviceType.setDisable(disabled);
    }
}