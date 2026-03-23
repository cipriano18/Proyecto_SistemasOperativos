package proyect.clientereservas.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyect.clientereservas.service.AuthService;
import proyect.clientereservas.util.NavigationManager;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button loginButton;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor complete todos los campos.");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Verificando...");
        messageLabel.setText("");

        // Operación en hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                String response = AuthService.getInstance().login(username, password);

                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Iniciar Sesión");

                    if (response.startsWith("SUCCESS")) {
                        // Navegar al dashboard según rol
                        if (AuthService.getInstance().isAdmin()) {
                            NavigationManager.getInstance().navigateTo("dashboard_admin");
                        } else {
                            NavigationManager.getInstance().navigateTo("dashboard");
                        }
                    } else {
                        String errorMsg = response.contains(":") ? response.split(":", 2)[1] : response;
                        showError(errorMsg);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Iniciar Sesión");
                    showError("Error de conexión con el servidor.");
                });
            }
        }).start();
    }

    @FXML
    public void handleGoToRegister() {
        NavigationManager.getInstance().navigateTo("register");
    }

    @FXML
    public void handleSupport() {
        messageLabel.setStyle("-fx-text-fill: #43474f;");
        messageLabel.setText("Contacte a soporte en: soporte@una.cr");
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        messageLabel.setText(msg);
    }
}