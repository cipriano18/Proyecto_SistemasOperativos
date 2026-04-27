/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DeviceFormDialog {

    public interface DeviceFormAction {
        void execute(String name, int quantity);
    }

    private final Stage stage;
    private final TextField tfDeviceName;
    private final TextField tfDeviceQuantity;

    public DeviceFormDialog(
            String title,
            String info,
            String initialName,
            Integer initialQuantity,
            String confirmButtonText,
            DeviceFormAction confirmAction
    ) {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setAlwaysOnTop(true);

        VBox root = new VBox();
        root.setPrefWidth(461);
        root.setPrefHeight(282);
        root.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource("/styles/text.css").toExternalForm());

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(100);
        header.setMinWidth(452);
        header.setPrefHeight(107);
        header.setPrefWidth(452);
        header.getStyleClass().add("hero-panel");
        header.setPadding(new Insets(20));

        VBox vbInfo = new VBox(5);
        vbInfo.setPrefWidth(430);
        vbInfo.setPrefHeight(67);

        Label lblAction = new Label(title);
        lblAction.setMinWidth(300);
        lblAction.getStyleClass().add("hero-title");
        lblAction.setStyle("-fx-text-fill: white;");

        Label lblInfo = new Label(info);
        lblInfo.setMinWidth(300);
        lblInfo.setWrapText(true);
        lblInfo.getStyleClass().add("hero-subtitle");

        vbInfo.getChildren().addAll(lblAction, lblInfo);
        header.getChildren().add(vbInfo);

        HBox formContainer = new HBox();
        formContainer.setPrefHeight(71);
        formContainer.setPrefWidth(461);

        VBox nameBox = new VBox(5);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.setPrefHeight(82);
        nameBox.setPrefWidth(263);
        nameBox.setPadding(new Insets(10, 0, 0, 10));
        HBox.setHgrow(nameBox, javafx.scene.layout.Priority.ALWAYS);

        Label lblName = new Label("Nombre");
        lblName.getStyleClass().add("hero-title-3");

        tfDeviceName = new TextField();
        tfDeviceName.setMaxHeight(46);
        tfDeviceName.setMaxWidth(304);
        tfDeviceName.setPrefHeight(46);
        tfDeviceName.setPrefWidth(304);
        tfDeviceName.setText(initialName != null ? initialName : "");

        nameBox.getChildren().addAll(lblName, tfDeviceName);

        VBox quantityBox = new VBox(5);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        quantityBox.setPrefHeight(200);
        quantityBox.setPrefWidth(100);
        quantityBox.setPadding(new Insets(10, 10, 0, 10));
        HBox.setHgrow(quantityBox, javafx.scene.layout.Priority.ALWAYS);

        Label lblQuantity = new Label("Cantidad");
        lblQuantity.getStyleClass().add("hero-title-3");

        tfDeviceQuantity = new TextField();
        tfDeviceQuantity.setMaxHeight(20);
        tfDeviceQuantity.setMaxWidth(200);
        tfDeviceQuantity.setText(initialQuantity != null ? String.valueOf(initialQuantity) : "");

        quantityBox.getChildren().addAll(lblQuantity, tfDeviceQuantity);

        formContainer.getChildren().addAll(nameBox, quantityBox);

        HBox buttonContainer = new HBox(5);
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.setPrefHeight(42);
        buttonContainer.setPrefWidth(461);
        buttonContainer.setPadding(new Insets(0, 10, 10, 0));
        VBox.setVgrow(buttonContainer, javafx.scene.layout.Priority.ALWAYS);

        Button btnCancel = new Button("Cancelar");
        btnCancel.setPrefHeight(36);
        btnCancel.setPrefWidth(72);
        btnCancel.getStyleClass().add("hero-panel");
        btnCancel.setStyle("-fx-background-radius: 0; -fx-border-radius: 0; -fx-text-fill: white;");
        btnCancel.setOnAction(e -> stage.close());

        Button btnSave = new Button(confirmButtonText);
        btnSave.setPrefHeight(36);
        btnSave.setPrefWidth(78);
        btnSave.getStyleClass().add("hero-panel");
        btnSave.setStyle("-fx-background-radius: 0; -fx-border-radius: 0; -fx-text-fill: white;");
        btnSave.setOnAction(e -> apply(confirmAction));

        buttonContainer.getChildren().addAll(btnCancel, btnSave);

        root.getChildren().addAll(header, formContainer, buttonContainer);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void apply(DeviceFormAction confirmAction) {
        String name = tfDeviceName.getText().trim();
        String quantityText = tfDeviceQuantity.getText().trim();

        if (name.isEmpty()) {
            tfDeviceName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            tfDeviceQuantity.requestFocus();
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            tfDeviceQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            tfDeviceQuantity.requestFocus();
            return;
        }

        if (confirmAction != null) {
            confirmAction.execute(name, quantity);
        }

        stage.close();
    }

    public void show() {
        stage.showAndWait();
    }
}


/* 
Ejemplo de agregar

DeviceFormDialog dialog = new DeviceFormDialog(
        "Nuevo Equipo",
        "Ingrese los siguientes datos para registrar un nuevo equipo.",
        null,
        null,
        "Agregar",
        (name, quantity) -> {
            System.out.println("Agregar equipo:");
            System.out.println("Nombre: " + name);
            System.out.println("Cantidad: " + quantity);

            // Aquí llamas tu servicio:
            // EquipmentService.createEquipment(name, quantity);
        }
);

dialog.show();

Ejemplo de Editar

DeviceFormDialog dialog = new DeviceFormDialog(
        "Editar Equipo",
        "Modifique los datos del equipo seleccionado.",
        "Micrófono",
        5,
        "Guardar",
        (name, quantity) -> {
            System.out.println("Editar equipo:");
            System.out.println("Nombre: " + name);
            System.out.println("Cantidad: " + quantity);

            // Aquí llamas tu servicio:
            // EquipmentService.updateEquipment(idEquipment, name, quantity);
        }
);

dialog.show();
*/
