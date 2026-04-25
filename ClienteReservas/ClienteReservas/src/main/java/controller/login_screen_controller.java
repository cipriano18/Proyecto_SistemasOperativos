/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.ClientRequest;
import model.Response;
import service.AuthService;
import session.Session;
import utils.Animations;

public class login_screen_controller implements Initializable {

    @FXML
    private Label messageLabel;
    @FXML
    private TextField tf_user;
    @FXML
    private PasswordField tf_pass;
    @FXML
    private Button btn_login;
    @FXML
    private Button btn_register;
    @FXML
    private ImageView img_logo;
    @FXML
    private VBox vb_info;
    @FXML
    private VBox vb_right;
    @FXML
    private Label lbl_title;
    @FXML
    private Label lbl_audInfo;
    @FXML
    private Label lbl_welcome;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Animations anim = new Animations();
        anim.breathOpacity(img_logo, 80, 1, 0.65, 1.0);
        anim.appear(vb_info, 100, 2, 0);
        anim.appear(vb_right, 100, 1, 0);
        anim.typeWriter(lbl_title, 400, 50);
        anim.typeWriter(lbl_audInfo, 200, 30);
        anim.typeWriter(lbl_welcome, 400, 50);

    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = tf_user.getText().trim();
        String password = tf_pass.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor complete todos los campos.");
            return;
        }

        Response resp = AuthService.login(username, password);
        if (resp == null) {
            PopUp.warning(
                    "Error de conexión",
                    "Verifique su conexión o intente nuevamente.",
                    "Es posible que el servicio esté temporalmente no disponible o que exista un problema con su conexión a internet.\n"
                    + "Por favor, intente nuevamente más tarde.",
                    "power_off.png",
                    1,
                    "Aceptar"
            );
            return;
        }

        if (!resp.isSuccess()) {
            PopUp.warning(
                    "Credenciales inválidas",
                    "Usuario o contraseña incorrectos",
                    resp.getMessage() != null ? resp.getMessage() : "Intente nuevamente.",
                    "person_off.png",
                    1,
                    "Aceptar"
            );
            System.out.println(resp.getMessage());
            return;
        }

        if (!(resp.getData() instanceof ClientRequest)) {
            PopUp.warning(
                    "Error inesperado",
                    "Respuesta inválida del servidor",
                    "No se pudo procesar la información recibida.",
                    "dangerous.png",
                    1,
                    "Aceptar"
            );
            return;
        }

        ClientRequest data = (ClientRequest) resp.getData();

        if (data.getUser().getIdRole() != 3 && data.getUser().getIdRole() != 2 && data.getUser().getIdRole() != 1) {
            PopUp.warning(
                    "Acceso denegado",
                    "No tiene permisos",
                    "Este usuario no puede acceder a esta aplicación.",
                    "person_off.png",
                    1,
                    "Aceptar"
            );
            return;
        }

        Session.getInstance().setClient(data);
        App.setRoot("home_screen");

    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        messageLabel.setText(msg);
    }

    @FXML
    private void GoToRegister(ActionEvent event) throws IOException {
        App.setRoot("register_screen");
    }
}
