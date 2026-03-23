package proyect.clientereservas.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import proyect.clientereservas.service.UserService;
import proyect.clientereservas.util.NavigationManager;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private Button registerButton;

    @FXML
    public void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm  = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Complete todos los campos.");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Las contraseñas no coinciden.");
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText("Creando cuenta...");

        new Thread(() -> {
            try {
                // rol 3 = Cliente
                String response = UserService.getInstance().createUser(username, password);

                Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    registerButton.setText("Crear cuenta");

                    if (response.startsWith("SUCCESS")) {
                        showSuccess("¡Cuenta creada! Ya puede iniciar sesión.");
                        clearFields();
                    } else {
                        String msg = response.contains(":") ? response.split(":", 2)[1] : response;
                        showError(msg.trim());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    registerButton.setText("Crear cuenta");
                    showError("Error de conexión con el servidor.");
                });
            }
        }).start();
    }

    @FXML
    public void handleBack() {
        NavigationManager.getInstance().navigateTo("login");
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: #1a6e1a; -fx-font-weight:600; -fx-font-size:12px;");
        messageLabel.setText(msg);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}