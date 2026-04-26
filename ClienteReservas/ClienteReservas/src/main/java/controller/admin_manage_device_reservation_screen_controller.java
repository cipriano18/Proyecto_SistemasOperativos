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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
public class admin_manage_device_reservation_screen_controller implements Initializable {

    @FXML
    private Button btn_goback;
    @FXML
    private VBox vb_info;
    @FXML
    private ImageView img_logo;
    @FXML
    private VBox vb_msg_new_acount;
    @FXML
    private Label lbl_create;
    @FXML
    private Label lbl_create2;
    @FXML
    private VBox vb_list_reservation_auditorium;
    @FXML
    private Label lbl_create21;
    @FXML
    private ChoiceBox<?> chb_month;
    @FXML
    private Label lbl_create211;
    @FXML
    private TextField tf_year;
    @FXML
    private Label lbl_create2111;
    @FXML
    private Button btn_serch;
    @FXML
    private Label lbl_create21111;
    @FXML
    private Button btn_clear_filter;

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
    private void ApplyFilter(ActionEvent event) {
    }

    @FXML
    private void ClearFilter(ActionEvent event) {
    }
    
}
