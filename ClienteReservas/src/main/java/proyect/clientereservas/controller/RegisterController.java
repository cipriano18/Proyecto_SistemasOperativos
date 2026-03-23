package proyect.clientereservas.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import proyect.clientereservas.service.AdminService;
import proyect.clientereservas.util.NavigationManager;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fNameField;
    @FXML private TextField mNameField;
    @FXML private TextField fSurnameField;
    @FXML private TextField mSurnameField;
    @FXML private TextField identityCardField;
    @FXML private ComboBox<String> contactTypeCombo;
    @FXML private TextField contactValueField;
    @FXML private Label messageLabel;
    @FXML private Button registerButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contactTypeCombo.setItems(FXCollections.observableArrayList(
            "Correo", "Teléfono", "WhatsApp"
        ));
        contactTypeCombo.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleRegister() {
        // Validación básica en cliente
        if (usernameField.getText().trim().isEmpty() ||
            passwordField.getText().trim().isEmpty() ||
            fNameField.getText().trim().isEmpty() ||
            fSurnameField.getText().trim().isEmpty() ||
            mSurnameField.getText().trim().isEmpty() ||
            identityCardField.getText().trim().isEmpty() ||
            contactValueField.getText().trim().isEmpty()) {

            showError("Por favor complete todos los campos obligatorios (*).");
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText("Creando cuenta...");
        messageLabel.setText("");

        String username     = usernameField.getText().trim();
        String password     = passwordField.getText().trim();
        String fName        = fNameField.getText().trim();
        String mName        = mNameField.getText().trim();
        String fSurname     = fSurnameField.getText().trim();
        String mSurname     = mSurnameField.getText().trim();
        String identityCard = identityCardField.getText().trim();
        String contactType  = contactTypeCombo.getValue();
        String contactValue = contactValueField.getText().trim();

        new Thread(() -> {
            try {
                String response = AdminService.getInstance().createAdmin(
                    username, password,
                    fName, mName,
                    fSurname, mSurname,
                    identityCard,
                    contactType, contactValue
                );

                Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    registerButton.setText("Crear administrador");

                    if (response.startsWith("SUCCESS")) {
                        showSuccess("¡Administrador creado correctamente! Ahora puede iniciar sesión.");
                        clearFields();
                    } else {
                        String errorMsg = response.contains(":") ? response.split(":", 2)[1] : response;
                        showError(errorMsg);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    registerButton.setDisable(false);
                    registerButton.setText("Crear administrador");
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
        fNameField.clear();
        mNameField.clear();
        fSurnameField.clear();
        mSurnameField.clear();
        identityCardField.clear();
        contactValueField.clear();
    }
}