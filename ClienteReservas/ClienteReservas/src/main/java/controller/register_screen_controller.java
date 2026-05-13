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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Client;
import dto.ClientRequest;
import model.Contact;
import service.Response;
import model.User;
import service.RegisterClient;
import utils.Animations;

/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
public class register_screen_controller implements Initializable {

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
    private VBox vb_title3;
    @FXML
    private HBox hb_form3;

    /**
     * Inicializa animaciones y elementos visuales de la pantalla de registro.
     *
     * @param url ubicacion usada para resolver rutas relativas
     * @param rb recursos de internacionalizacion asociados a la vista
     */
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
        //form3
        Animations anim3 = new Animations();
        anim3.appear(vb_title3, 100, 2, 2000);
        anim3.appear(hb_form3, 100, 2, 2000);
    }

    private static final String NAME_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$";
    private static final String ID_CARD_REGEX = "^[A-Za-z0-9]{9,20}$";
    private static final String USERNAME_REGEX = "[a-zA-Z0-9_]+";
    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

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

    /**
     * Valida los datos del formulario y registra un nuevo cliente.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
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

        boolean hasErrors = false;

        if (fName.isEmpty()) {
            showError(msg_name, "El primer nombre es obligatorio");
            hasErrors = true;
        } else if (!fName.matches(NAME_REGEX)) {
            showError(msg_name, "El primer nombre es inválido");
            hasErrors = true;
        }

        if (fSurname.isEmpty()) {
            showError(msg_surname, "El primer apellido es obligatorio");
            hasErrors = true;
        } else if (!fSurname.matches(NAME_REGEX)) {
            showError(msg_surname, "El primer apellido es inválido");
            hasErrors = true;
        }

        if (mSurname.isEmpty()) {
            showError(msg_Sec_surname, "El segundo apellido es obligatorio");
            hasErrors = true;
        } else if (!mSurname.matches(NAME_REGEX)) {
            showError(msg_Sec_surname, "El segundo apellido es inválido");
            hasErrors = true;
        }

        if (idCard.isEmpty()) {
            showError(msg_card, "La cédula es obligatoria");
            hasErrors = true;
        } else if (!idCard.matches(ID_CARD_REGEX)) {
            showError(msg_card, "La cédula es inválida");
            hasErrors = true;
        }

        if (contactValue.isEmpty()) {
            showError(msg_contact, "El correo es obligatorio");
            hasErrors = true;
        } else if (!contactValue.matches(EMAIL_REGEX)) {
            showError(msg_contact, "El correo debe tener un formato válido");
            hasErrors = true;
        }

        if (username.isEmpty()) {
            showError(msg_user, "El nombre de usuario es obligatorio");
            hasErrors = true;
        } else if (
                username.length() < 4 
                || username.length() > 50 
                || !username.matches(USERNAME_REGEX)) {
            
            showError(msg_user, "El nombre de usuario es inválido");
            hasErrors = true;
        }

        if (password.isEmpty()) {
            showError(msg_pass, "La contraseña es obligatoria");
            hasErrors = true;
        } else if (
                password.length() < 8 
                || !password.matches(".*[A-Z].*") 
                || !password.matches(".*[0-9].*")) {
            
            showError(msg_pass, "La contraseña es inválida");
            hasErrors = true;
        }

        if (hasErrors) {
            PopUp.warning(
                    "Campos obligatorios",
                    "Faltan datos",
                    "Debe completar todos los campos obligatorios.",
                    "back_hand.png",
                    1,
                    2,
                    "Aceptar"
            );
            return;
        }

        User user = new User(3, username, password);
        Client client = new Client(0, fName, mName, fSurname, mSurname, idCard);
        Contact contact = new Contact("EMAIL", contactValue);
        ClientRequest request = new ClientRequest(user, client, contact);

        Response resp = RegisterClient.register(request);

        if (resp.isSuccess()) {
            PopUp.notification(
                    "Cliente registrado",
                    "Inicia sesión con tus nuevas credenciales para continuar.",
                    "check_circle.png",
                    2
            );
            App.setRoot("login_screen");
        } else {
            PopUp.warning(
                    "Error de registro",
                    "Faltan datos",
                    "Verifica los campos digitados.",
                    "back_hand.png",
                    1,
                    1,
                    "Aceptar"
            );
            routeServerError(resp.getMessage());
        }
    }

    /**
     * Redirige un mensaje del servidor al campo mas relacionado.
     *
     * @param msg mensaje recibido desde el servidor
     */
    private void routeServerError(String msg) {
        if (msg == null) {
            showError(msg_user, "Error desconocido");
            return;
        }
        String low = msg.toLowerCase();
        Label target;
        if (
            low.contains("nombre de usuario") 
            || low.contains("ya está en uso")) {
            target = msg_user;
            
        } else if (low.contains("contraseña")) {
            target = msg_pass;
            
        } else if (low.contains("primer nombre")) {
            target = msg_name;
            
        } else if (low.contains("primer apellido")) {
            target = msg_surname;
            
        } else if (low.contains("segundo apellido")) {
            target = msg_Sec_surname;
            
        } else if (low.contains("cédula")) {
            target = msg_card;
            
        } else if (
                low.contains("contacto") 
                || low.contains("teléfono") 
                || low.contains("correo")) {
            target = msg_contact;
            
        } else {
            target = msg_user;
        }
        showError(target, msg);
    }

    /**
     * Muestra un mensaje de error en una etiqueta del formulario.
     *
     * @param label etiqueta donde se mostrara el error
     * @param msg mensaje de error a presentar
     */
    private void showError(Label label, String msg) {
        label.setStyle(
                "-fx-text-fill: #ba1a1a;"
                + " -fx-font-weight:600; "
                + "-fx-font-size:12px;");
        label.setText(msg);
    }

    /**
     * Limpia los mensajes de validacion visibles en el formulario.
     */
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
