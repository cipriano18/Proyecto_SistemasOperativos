package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import model.User;
import database.UserDAO;
import database.AdminDAO;
import model.Admin;

public class SuperAdminController {

    @FXML private TableView<User>            adminTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String>  usernameColumn;
    @FXML private TableColumn<User, Integer> roleColumn;   

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisible;
    @FXML private Button        togglePasswordBtn;

    @FXML private Button createButton;
    @FXML private Button selectButton;
    @FXML private Button revokeButton;

    private boolean showingPassword = false;

    private static final String ICON_HIDE = "●";
    private static final String ICON_SHOW = "○";

    // Roles
    private static final int ROLE_SUPER_ADMIN = 1;
    private static final int ROLE_ADMIN       = 2;

    // ─────────────────────────────────────────────────────────────────────
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        // ── Columna Rol
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

        // ── Row factory: resalta toda la fila si es Super Admin ───────────
        adminTable.setRowFactory(tv -> new TableRow<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                getStyleClass().remove("row-super-admin");
                if (!empty && user != null && user.getIdRole() == ROLE_SUPER_ADMIN) {
                    if (!getStyleClass().contains("row-super-admin")) {
                        getStyleClass().add("row-super-admin");
                    }
                }
            }
        });

        // ── Sincronizar campos de contraseña ──────────────────────────────
        passwordField.textProperty().addListener((obs, o, n) -> {
            if (!showingPassword) passwordVisible.setText(n);
        });
        passwordVisible.textProperty().addListener((obs, o, n) -> {
            if (showingPassword) passwordField.setText(n);
        });

        togglePasswordBtn.setText(ICON_SHOW);
        refreshAdminList();
    }

    // ── Toggle visibilidad de contraseña ─────────────────────────────────
    @FXML
    private void handleTogglePassword() {
        showingPassword = !showingPassword;

        if (showingPassword) {
            passwordVisible.setText(passwordField.getText());

            passwordVisible.setVisible(true);
            passwordField.setVisible(false);

            togglePasswordBtn.setText(ICON_HIDE);
            togglePasswordBtn.getStyleClass().add("active");

            passwordVisible.requestFocus();
            passwordVisible.positionCaret(passwordVisible.getText().length());
        } else {
            passwordField.setText(passwordVisible.getText());

            passwordField.setVisible(true);
            passwordVisible.setVisible(false);

            togglePasswordBtn.setText(ICON_SHOW);
            togglePasswordBtn.getStyleClass().remove("active");

            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    private String getPassword() {
        return showingPassword ? passwordVisible.getText() : passwordField.getText();
    }

    private void clearPasswordFields() {
        passwordField.clear();
        passwordVisible.clear();
        if (showingPassword) handleTogglePassword();
    }

    // ── Crear administrador ───────────────────────────────────────────────
    @FXML
    private void handleCreateAdmin() {
        String username = usernameField.getText().trim();
        String password = getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Error", "Debe ingresar usuario y contraseña.");
            return;
        }

        // Verificar si ya existe un Super Admin
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

        User createdUser = UserDAO.getUserByUsername(username);
        if (createdUser == null) {
            showAlert(AlertType.ERROR, "Error", "No se pudo recuperar el usuario creado.");
            return;
        }

        refreshAdminList();
        usernameField.clear();
        clearPasswordFields();
        showAlert(AlertType.INFORMATION, "Éxito", "Super Administrador creado correctamente.");
    }

    // ── Seleccionar Super Admin ───────────────────────────────────────────
    @FXML
    private void handleSelectAdmin() {
        User selected = adminTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Selección inválida",
                    "Debe seleccionar un administrador de la lista.");
            return;
        }

        // Verificar si ya existe un Super Admin
        boolean superAdminExists = UserDAO.getAllAdminUsers().stream()
                .anyMatch(u -> u.getIdRole() == ROLE_SUPER_ADMIN);

        if (superAdminExists) {
            showAlert(AlertType.WARNING, "Error",
                    "Ya existe un Super Administrador. Solo puede haber uno.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Super Admin");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Asignar a \"" + selected.getUsername() + "\" como Super Administrador?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean updated = UserDAO.updateUserRole(selected.getIdUser(), ROLE_SUPER_ADMIN);
                if (updated) {
                    selected.setIdRole(ROLE_SUPER_ADMIN); // actualizar objeto local
                    refreshAdminList();
                    showAlert(AlertType.INFORMATION, "Super Admin asignado",
                            "\"" + selected.getUsername() + "\" ahora es Super Administrador.");
                } else {
                    showAlert(AlertType.ERROR, "Error", "No se pudo actualizar el rol del usuario.");
                }
            }
        });
    }

    // ── Revocar Super Admin ───────────────────────────────────────────────
    @FXML
    private void handleRevokeAdmin() {
        User selected = adminTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Selección inválida",
                    "Debe seleccionar un administrador de la lista.");
            return;
        }

        // Verificar si es Super Admin
        if (selected.getIdRole() != ROLE_SUPER_ADMIN) {
            showAlert(AlertType.WARNING, "Acción inválida",
                    "El usuario \"" + selected.getUsername() + "\" no es Super Administrador.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar acción");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Quitar privilegios de Super Admin a \"" + selected.getUsername() + "\"?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean updated = UserDAO.updateUserRole(selected.getIdUser(), ROLE_ADMIN);
                if (updated) {
                    selected.setIdRole(ROLE_ADMIN);
                    refreshAdminList();
                    showAlert(AlertType.INFORMATION, "Privilegios revocados",
                            "Se quitaron los privilegios de Super Admin a \"" + selected.getUsername() + "\".");
                } else {
                    showAlert(AlertType.ERROR, "Error", "No se pudo actualizar el rol del usuario.");
                }
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────
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