/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class DeviceCard extends VBox {

    private Label lblDeviceType;
    private Label lblQuantity;

    public DeviceCard(String deviceType, int quantity) {

        // Configuración del VBox
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(6);
        this.setPadding(new Insets(5, 5, 5, 5));
        this.setId("vb_device_card");

        // ───── Label: Tipo de dispositivo ─────
        lblDeviceType = new Label(deviceType);
        lblDeviceType.getStyleClass().add("hero-title-2");
        lblDeviceType.setFont(new Font("Tw Cen MT Bold", 17));

        // ───── Label: Cantidad ─────
        lblQuantity = new Label(String.valueOf(quantity));
        lblQuantity.getStyleClass().add("form-subtitle");

        // Agregar al contenedor
        this.getChildren().addAll(lblDeviceType, lblQuantity);
    }

    // Getters y setters
    public void setDeviceType(String text) {
        lblDeviceType.setText(text);
    }

    public void setQuantity(int quantity) {
        lblQuantity.setText(String.valueOf(quantity));
    }

    public String getDeviceType() {
        return lblDeviceType.getText();
    }

    public int getQuantity() {
        return Integer.parseInt(lblQuantity.getText());
    }
}