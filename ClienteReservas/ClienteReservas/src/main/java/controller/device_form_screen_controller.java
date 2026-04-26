/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
public class device_form_screen_controller implements Initializable {

    @FXML
    private Button btn_goback;
    @FXML
    private VBox vb_info;
    @FXML
    private ImageView img_logo;
    @FXML
    private HBox hb_device_container;// este tiene que contener los componetes DeviceCard
    @FXML
    private VBox vb_device_card;
    @FXML
    private Label lbl_devise_type;
    @FXML
    private Label lbl_cuantity;
    @FXML
    private HBox hb_list_device_card;
    @FXML
    private Label lbl_cuantity1;
    @FXML
    private ChoiceBox<?> chb_device_type;
    @FXML
    private Label lbl_cuantity11;
    @FXML
    private ChoiceBox<?> chb_cuantity;
    @FXML
    private Button btn_delete_this;
    @FXML
    private Button btn_add_device;
    @FXML
    private Button btn_save_reservation;
    @FXML
    private VBox vb_added_devices; // aqui van los de ListDeviceCard

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void GoToHome(ActionEvent event) {
    }

    @FXML
    private void DeleteThisCard(ActionEvent event) {
    }

    @FXML
    private void AddDeviceToList(ActionEvent event) {
    }

    @FXML
    private void SaveReservation(ActionEvent event) {
    }
    
}
