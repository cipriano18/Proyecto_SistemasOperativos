package project.servidorreservas;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Admin;
import model.User;
import database.AdminDAO;
import database.UserDAO;

public class SuperAdminController {

    @FXML
    private ListView<Admin> adminListView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    public void initialize() {
        refreshAdminList();
    }

    private void refreshAdminList() {
        adminListView.getItems().setAll(AdminDAO.getAllAdmins());
    }

    @FXML
    private void handleCreateAdmin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Error", "Debe ingresar usuario y contraseña.");
            return;
        }

        // Crear usuario
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setIdRole(2);

        boolean userInserted = UserDAO.insertUser(newUser);
        if (!userInserted) {
            showAlert(AlertType.ERROR, "Error", "No se pudo crear el usuario.");
            return;
        }

        // Recuperar el usuario creado
        User createdUser = UserDAO.getUserByUsername(username);
        if (createdUser == null) {
            showAlert(AlertType.ERROR, "Error", "No se pudo recuperar el usuario creado.");
            return;
        }

        // Crear administrador asociado
        Admin newAdmin = new Admin(createdUser.getIdUser(), username, "", "", "", "TEMP-ID");
        boolean adminInserted = AdminDAO.insertAdmin(newAdmin);

        if (adminInserted) {
            refreshAdminList();
            usernameField.clear();
            passwordField.clear();
            showAlert(AlertType.INFORMATION, "Éxito", "Administrador creado correctamente.");
        } else {
            showAlert(AlertType.ERROR, "Error", "No se pudo crear el administrador.");
        }
    }

    @FXML
    private void handleSelectAdmin() {
        Admin selected = adminListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showAlert(AlertType.INFORMATION, "Super Administrador",
                      "El administrador seleccionado como super administrador es: "
                      + selected.getfName() + " " + selected.getfSurname());
        } else {
            showAlert(AlertType.WARNING, "Selección inválida", "Debe seleccionar un administrador de la lista.");
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
