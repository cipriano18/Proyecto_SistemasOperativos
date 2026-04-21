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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Client;
import model.ClientRequest;
import model.Contact;
import model.Response;
import model.User;
import service.RegisterClient;
import utils.Animations;

/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
public class register_screen_controller implements Initializable {

    private String contactType = "EMAIL";
    @FXML
    private Button btn_goback;
    @FXML
    private TextField tf_first_name;
    @FXML
    private Label msg_name;
    @FXML
    private TextField tf_second_name;
    @FXML
    private TextField tf_first_surname;
    @FXML
    private Label msg_surname;
    @FXML
    private TextField tf_second_surname;
    @FXML
    private Label msg_Sec_surname;
    @FXML
    private TextField tf_id_card;
    @FXML
    private Label msg_card;
    @FXML
    private RadioButton rdb_mail;
    @FXML
    private ToggleGroup tg_contact_tipe;
    @FXML
    private RadioButton rdb_phone;
    @FXML
    private Label lbl_contact_type;
    @FXML
    private TextField tf_contact;
    @FXML
    private Label msg_contact;
    @FXML
    private TextField tf_user;
    @FXML
    private Label msg_user;
    @FXML
    private TextField tf_pass;
    @FXML
    private Label msg_pass;
    @FXML
    private Button btn_register;
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
    private Label lbl_form1;
    @FXML
    private VBox vb_form1;
    @FXML
    private HBox hb_form2;
    @FXML
    private Label lbl_form2;
    @FXML
    private VBox vb_title3;
    @FXML
    private HBox hb_form3;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Animations anim = new Animations();
        anim.appear(vb_info, 100, 2, 100);
        anim.breathOpacity(img_logo, 80, 1, 0.65, 1.0);
        anim.appear(vb_msg_new_acount, 100, 1, 0);
        anim.typeWriter(lbl_create, 400, 50);
        anim.typeWriter(lbl_create2, 200, 30);
        //form1
        Animations anim1 = new Animations();
        anim1.appear(lbl_form1, 100, 2, 100);
        anim1.appear(vb_form1, 100, 2, 100);
        //form2
        Animations anim2 = new Animations();
        anim2.appear(lbl_form2, 100, 2, 1000);
        anim2.appear(hb_form2, 100, 2, 1000);
        //form3
        Animations anim3 = new Animations();
        anim3.appear(vb_title3, 100, 2, 2000);
        anim3.appear(hb_form3, 100, 2, 2000);
    }

    @FXML
    private void GoToLogin(ActionEvent event) throws IOException {
        App.setRoot("login_screen");
    }

    @FXML
    private void ChangeToMail(ActionEvent event) {
        lbl_contact_type.setText("CORREO ELECTRÓNICO");
        tf_contact.setPromptText("ejemplo.una@est.una.ac.cr");
        contactType = "EMAIL";
    }

    @FXML
    private void ChangeToPhone(ActionEvent event) {
        lbl_contact_type.setText("TELÉFONO");
        tf_contact.setPromptText("82892226");
        contactType = "PHONE";
    }

    @FXML
    private void RegisterUser(ActionEvent event) throws IOException {
        clearMessages();

        String fName = tf_first_name.getText().trim();
        String mName = tf_second_name.getText().trim();
        String fSurname = tf_first_surname.getText().trim();
        String mSurname = tf_second_surname.getText().trim();
        String idCard = tf_id_card.getText().trim();
        String contactValue = tf_contact.getText().trim();
        String username = tf_user.getText().trim();
        String password = tf_pass.getText().trim();

        if (fName.isEmpty()) { showError(msg_name, "Ingrese el primer nombre."); return; }
        if (fSurname.isEmpty()) { showError(msg_surname, "Ingrese el primer apellido."); return; }
        if (mSurname.isEmpty()) { showError(msg_Sec_surname, "Ingrese el segundo apellido."); return; }
        if (idCard.isEmpty()) { showError(msg_card, "Ingrese la cédula."); return; }
        if (contactValue.isEmpty()) { showError(msg_contact, "Ingrese el contacto."); return; }
        if (username.isEmpty()) { showError(msg_user, "Ingrese el usuario."); return; }
        if (password.isEmpty()) { showError(msg_pass, "Ingrese la contraseña."); return; }

        User user = new User(3, username, password);
        Client client = new Client(0, fName, mName, fSurname, mSurname, idCard);
        Contact contact = new Contact(contactType, contactValue);
        ClientRequest request = new ClientRequest(user, client, contact);

        Response resp = RegisterClient.register(request);

        if (resp.isSuccess()) {
            App.setRoot("login_screen");
        } else {
            showError(msg_user, resp.getMessage());
        }
    }

    private void showError(Label label, String msg) {
        label.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        label.setText(msg);
    }

    private void clearMessages() {
        msg_name.setText("");
        msg_surname.setText("");
        msg_Sec_surname.setText("");
        msg_card.setText("");
        msg_contact.setText("");
        msg_user.setText("");
        msg_pass.setText("");
    }

}
