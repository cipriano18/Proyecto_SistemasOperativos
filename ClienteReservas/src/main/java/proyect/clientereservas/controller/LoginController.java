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

        new Thread(() -> {
            try {
                String response = AuthService.getInstance().login(username, password);

                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Iniciar Sesión");

                    if (response.startsWith("SUCCESS")) {
                        navigateByRole(AuthService.getInstance().getCurrentRole());
                    } else {
                        String msg = response.contains(":") ? response.split(":", 2)[1] : response;
                        showError(msg.trim());
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

    private void navigateByRole(int role) {
        switch (role) {
            case 1:
                NavigationManager.getInstance().navigateTo("dashboard_superadmin");
                break;
            case 2:
                NavigationManager.getInstance().navigateTo("dashboard_admin");
                break;
            case 3:
                NavigationManager.getInstance().navigateTo("dashboard_cliente");
                break;
            default:
                showError("Rol no reconocido.");
        }
    }

    @FXML
    public void handleGoToRegister() {
        NavigationManager.getInstance().navigateTo("register");
    }

    @FXML
    public void handleSupport() {
        messageLabel.setStyle("-fx-text-fill: #43474f; -fx-font-size:12px;");
        messageLabel.setText("Soporte: soporte@una.cr");
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        messageLabel.setText(msg);
    }
}