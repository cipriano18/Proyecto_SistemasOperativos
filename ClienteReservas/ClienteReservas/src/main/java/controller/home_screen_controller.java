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
import model.ClientRequest;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ClientRequest session = Session.getInstance().getClient();
        if (session == null) {
            try {
                App.setRoot("login_screen");
            } catch (IOException ignored) {
            }
            return;
        }
        if (lbl_welcome != null && session.getClient() != null) {
            lbl_welcome.setText("Hola, " + session.getClient().getfName());
        }
    }

    @FXML
    private void GoToProfile(ActionEvent event) throws IOException {
        App.setRoot("profile_screen");
    }
}
