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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import dto.ClientRequest;
import model.Response;
import service.CalendarService;
import session.Session;

/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
public class home_screen_controller implements Initializable {

    @FXML
    private Label lbl_welcome;
    @FXML
    private Button btn_profile;
    @FXML
    private Button btn_auditorium;
    @FXML
    private Button btn_devices;
    @FXML
    private Button btn_schedule;
    @FXML
    private Button btn_leave;
    @FXML
    private VBox vb_info;
    @FXML
    private Label lbl_title;
    @FXML
    private Label lbl_info;
    @FXML
    private ImageView img_image1;
    @FXML
    private VBox vb_info1;
    @FXML
    private Label lbl_title1;
    @FXML
    private Label lbl_info1;
    @FXML
    private ImageView img_image11;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ClientRequest session = Session.getInstance().getClient();

        if (session == null) {
            PopUp.warning(
                    "Sesión expirada",
                    "Debe iniciar sesión",
                    "Su sesión ha expirado o no es válida. Por favor, inicie sesión nuevamente.",
                    "error.png",
                    1,
                    "Ir al login"
            );

            try {
                App.setRoot("login_screen");
            } catch (IOException ignored) {
            }
            return;
        }

        if (lbl_welcome != null && session.getClient() != null) {
            lbl_welcome.setText("¿Qué hacemos " + session.getClient().getfName() + "?");
        }

    }

    @FXML
    private void GoToProfile(ActionEvent event) throws IOException {
        App.setRoot("profile_screen");
    }

    @FXML
    private void GoToAuditorium(ActionEvent event) {
    }

    @FXML
    private void GoToDevices(ActionEvent event) throws IOException {
        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();
        Response resp = CalendarService.enterReservationsView(idClient);
        if (resp.isSuccess()) {
            App.setRoot("device_schedule_screen");
        } else {
            PopUp.warning(
                    "Error de conexión",
                    "Verifique su conexión o intente nuevamente.",
                    "Es posible que el servicio esté temporalmente no disponible o que exista un problema con su conexión a internet.\n"
                    + "Por favor, intente nuevamente más tarde.",
                    "power_off.png",
                    1,
                    "Aceptar"
            );
        }

    }

    @FXML
    private void GoToSchedule(ActionEvent event) throws IOException {

    }

    @FXML
    private void GoToLogin(ActionEvent event) throws IOException {
        App.setRoot("login_screen");
    }
}
