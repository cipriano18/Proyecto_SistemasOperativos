package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import model.User;
import database.UserDAO;
import server.Server;
import service.ReservationDraftCleanupService;

public class SuperAdminController {

    @FXML private TableView<User> adminTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, Integer> roleColumn;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisible;
    @FXML private Button togglePasswordBtn;

    @FXML private Button createButton;
    @FXML private Button selectButton;
    @FXML private Button revokeButton;
    @FXML private Button serverButton;

    @FXML private HBox adminContent;
    @FXML private Label sectionTitleLabel;

    private boolean showingPassword = false;
    private boolean serverOn = false;
    private Thread serverThread;

    private static final String ICON_HIDE = "●";
    private static final String ICON_SHOW = "○";

    private static final int ROLE_SUPER_ADMIN = 1;
    private static final int ROLE_ADMIN = 2;

    // ─────────────────────────────────────────────────────────
    public void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("idRole"));

        roleColumn.setCellFactory(col -> new TableCell<User, Integer>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Integer role, boolean empty) {
                super.updateItem(role, empty);

                if (empty || role == null) {
                    setGraphic(null);
                    return;
                }

                if (role == ROLE_SUPER_ADMIN) {
                    badge.setText("Super Admin");
                    badge.getStyleClass().setAll("role-badge-super");
                } else {
                    badge.setText("Administrador");
                    badge.getStyleClass().setAll("role-badge-admin");
                }

                setGraphic(badge);
                setText(null);
            }
        });

        adminTable.setRowFactory(tv -> new TableRow<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                getStyleClass().remove("row-super-admin");

                if (!empty && user != null && user.getIdRole() == ROLE_SUPER_ADMIN) {
                    getStyleClass().add("row-super-admin");
                }
            }
        });

        // Sincronizar password visible
        passwordField.textProperty().addListener((obs, o, n) -> {
            if (!showingPassword) passwordVisible.setText(n);
        });

        passwordVisible.textProperty().addListener((obs, o, n) -> {
            if (showingPassword) passwordField.setText(n);
        });

        togglePasswordBtn.setText(ICON_SHOW);

        refreshAdminList();

        // Inicialmente apagado
        setServerState(false);
    }

    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleTogglePassword() {
        showingPassword = !showingPassword;

        if (showingPassword) {
            passwordVisible.setText(passwordField.getText());
            passwordVisible.setVisible(true);
            passwordField.setVisible(false);

            togglePasswordBtn.setText(ICON_HIDE);
            togglePasswordBtn.getStyleClass().add("active");
        } else {
            passwordField.setText(passwordVisible.getText());
            passwordField.setVisible(true);
            passwordVisible.setVisible(false);

            togglePasswordBtn.setText(ICON_SHOW);
            togglePasswordBtn.getStyleClass().remove("active");
        }
    }

    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleToggleServer() {
        if (serverOn) {
            stopServer();
        } else {
            startServer();
        }
    }

    // ─────────────────────────────────────────────────────────
    private void startServer() {
        if (serverOn) return;

        serverThread = new Thread(() -> {
            try {
                Server.startServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();

        ReservationDraftCleanupService.start();

        setServerState(true);

        showAlert(AlertType.INFORMATION, "Servidor iniciado",
                "El servidor se inició correctamente.");
    }

    // ─────────────────────────────────────────────────────────
    private void stopServer() {
        if (!serverOn) return;

        try {
            // IMPORTANTE: estos métodos deben existir
            Server.stopServer();
            ReservationDraftCleanupService.stop();

            if (serverThread != null && serverThread.isAlive()) {
                serverThread.interrupt();
            }

            setServerState(false);

            showAlert(AlertType.INFORMATION, "Servidor apagado",
                    "El servidor se apagó correctamente.");

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error",
                    "No se pudo apagar el servidor correctamente.");
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────────────────
    private void setServerState(boolean serverOn) {
        this.serverOn = serverOn;

        adminContent.setDisable(!serverOn);
        sectionTitleLabel.setDisable(!serverOn);
        revokeButton.setDisable(!serverOn);
        selectButton.setDisable(!serverOn);

        if (serverOn) {
            adminContent.getStyleClass().remove("locked-section");
            sectionTitleLabel.getStyleClass().remove("locked-section");

            serverButton.setText("Apagar servidor");
            serverButton.getStyleClass().remove("btn-primary");
            serverButton.getStyleClass().add("btn-danger");

        } else {
            if (!adminContent.getStyleClass().contains("locked-section")) {
                adminContent.getStyleClass().add("locked-section");
            }

            if (!sectionTitleLabel.getStyleClass().contains("locked-section")) {
                sectionTitleLabel.getStyleClass().add("locked-section");
            }

            serverButton.setText("Prender servidor");
            serverButton.getStyleClass().remove("btn-danger");
            serverButton.getStyleClass().add("btn-primary");
        }
    }

    // ─────────────────────────────────────────────────────────
    private String getPassword() {
        return showingPassword ? passwordVisible.getText() : passwordField.getText();
    }

    private void clearPasswordFields() {
        passwordField.clear();
        passwordVisible.clear();

        if (showingPassword) handleTogglePassword();
    }

    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleCreateAdmin() {
        String username = usernameField.getText().trim();
        String password = getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Error", "Debe ingresar usuario y contraseña.");
            return;
        }

        boolean superAdminExists = UserDAO.getAllAdminUsers().stream()
                .anyMatch(u -> u.getIdRole() == ROLE_SUPER_ADMIN);

        if (superAdminExists) {
            showAlert(AlertType.WARNING, "Error",
                    "Ya existe un Super Administrador. Solo puede haber uno.");
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setIdRole(ROLE_SUPER_ADMIN);

        if (!UserDAO.insertUser(newUser)) {
            showAlert(AlertType.ERROR, "Error", "No se pudo crear el usuario.");
            return;
        }

        refreshAdminList();
        usernameField.clear();
        clearPasswordFields();

        showAlert(AlertType.INFORMATION, "Éxito",
                "Super Administrador creado correctamente.");
    }

    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleSelectAdmin() {
        User selected = adminTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(AlertType.WARNING, "Selección inválida",
                    "Debe seleccionar un administrador.");
            return;
        }

        boolean updated = UserDAO.updateUserRole(selected.getIdUser(), ROLE_SUPER_ADMIN);

        if (updated) {
            refreshAdminList();
        }
    }

    // ─────────────────────────────────────────────────────────
    @FXML
    private void handleRevokeAdmin() {
        User selected = adminTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(AlertType.WARNING, "Selección inválida",
                    "Debe seleccionar un administrador.");
            return;
        }

        boolean updated = UserDAO.updateUserRole(selected.getIdUser(), ROLE_ADMIN);

        if (updated) {
            refreshAdminList();
        }
    }

    // ─────────────────────────────────────────────────────────
    private void refreshAdminList() {
        adminTable.getItems().setAll(UserDAO.getAllAdminUsers());
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}