package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import service.Response;
import utils.DraftContainer;

/**
 * FXML Controller class
 *
 * @author Reyner
 */
public class auditorium_form_screen_controller implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Response resp = DraftContainer.getInstance().getDraftResponse();

    }    
    
}
