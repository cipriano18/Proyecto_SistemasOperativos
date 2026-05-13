/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Makin Artavia
 */
public class admin_home_screen_controller implements Initializable {

    @FXML
    private Button btn_devices;
    @FXML
    private Button btn_device_reservation;
    @FXML
    private Button btn_auditorium_reservation;
    @FXML
    private Button btn_profile;
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
     * Inicializa la pantalla principal del administrador.
     *
     * @param url ubicacion usada para resolver rutas relativas
     * @param rb recursos de internacionalizacion asociados a la vista
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    /**
     * Abre la pantalla de administracion de equipos.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToDevices(ActionEvent event) throws IOException {
        System.out.println("entro");
         App.setRoot("admin_device_screen");
    }

    /**
     * Abre la pantalla de gestion de reservas de equipos.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToDeviceReservations(ActionEvent event) throws IOException {
        App.setRoot("admin_manage_device_reservation_screen");
    }

    /**
     * Abre la pantalla de gestion de reservas de auditorio.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToAuditoriomReservations(ActionEvent event)
        throws IOException {
        App.setRoot("admin_manage_auditorium_reservation_screen");
    }

    /**
     * Abre la pantalla de perfil del administrador.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToProfile(ActionEvent event) throws IOException {
        App.setRoot("admin_profile_screen");
    }

    /**
     * Regresa a la pantalla de inicio de sesion.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToLogin(ActionEvent event) throws IOException {
        App.setRoot("login_screen");
    }
    
}
