/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class login_screen_controller implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button loginButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor complete todos los campos.");
            return;
        }
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) throws IOException {
        App.setRoot("register_screen");
    }

    @FXML
    private void handleSupport(ActionEvent event) {
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        messageLabel.setText(msg);
    }
}
