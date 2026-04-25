/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import dto.AdminRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Admin;
import model.Contact;
import model.User;
import service.AdminProfileService;
import service.Response;
import session.Session;
import utils.Animations;

/**
 * FXML Controller class
 *
 * @author Cipriano
 */

public class admin_profile_screen_controller implements Initializable {

    private String contactType = "EMAIL";

    @FXML private Button btn_goback;
    @FXML private VBox vb_info;
    @FXML private Label lbl_sis;
    @FXML private Label lbl_title;
    @FXML private Label lbl_profile_info;
    @FXML private HBox hb_id_card;
    @FXML private Label lbl_card_name;
    @FXML private Label lbl_card_ced;
    @FXML private Label lbl_card_contact;
    @FXML private Label lbl_form1;
    @FXML private VBox vb_form1;
    @FXML private TextField tf_first_name;
    @FXML private Label msg_name;
    @FXML private TextField tf_second_name;
    @FXML private TextField tf_first_surname;
    @FXML private Label msg_surname;
    @FXML private TextField tf_second_surname;
    @FXML private Label msg_Sec_surname;
    @FXML private TextField tf_id_card;
    @FXML private Label lbl_form2;
    @FXML private HBox hb_form2;
    @FXML private RadioButton rdb_mail;
    @FXML private ToggleGroup tg_contact_tipe;
    @FXML private RadioButton rdb_phone;
    @FXML private Label lbl_contact_type;
    @FXML private TextField tf_contact;
    @FXML private Label msg_contact;
    @FXML private VBox vb_title3;
    @FXML private HBox hb_form3;
    @FXML private TextField tf_user;
    @FXML private TextField tf_pass;
    @FXML private Label msg_pass;
    @FXML private Button btn_save;
    @FXML private Button btn_delete;
    @FXML private Label msg_global;

    private static final String NAME_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{2,50}$";
    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    private static final String PHONE_REGEX = "^[0-9]{8}$";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        AdminRequest session = Session.getInstance().getAdmin();

        if (session == null || session.getAdmin() == null) {
            try {
                App.setRoot("login_screen");
            } catch (IOException ignored) {
            }
            return;
        }

        Admin a = session.getAdmin();
        User u = session.getUser();
        Contact ct = session.getContact();

        lbl_card_name.setText(
                nullSafe(a.getfName()) + " "
                + nullSafe(a.getmName()) + " "
                + nullSafe(a.getfSurname()) + " "
                + nullSafe(a.getmSurname())
        );

        lbl_card_ced.setText(nullSafe(a.getIdentityCard()));

        if (ct != null) {
            lbl_card_contact.setText(nullSafe(ct.getContactValue()));
        } else {
            lbl_card_contact.setText("");
        }

        tf_first_name.setText(nullSafe(a.getfName()));
        tf_second_name.setText(nullSafe(a.getmName()));
        tf_first_surname.setText(nullSafe(a.getfSurname()));
        tf_second_surname.setText(nullSafe(a.getmSurname()));
        tf_id_card.setText(nullSafe(a.getIdentityCard()));
        tf_user.setText(nullSafe(u.getUsername()));

        if (ct != null) {
            String type = ct.getType() == null ? "EMAIL" : ct.getType().toUpperCase();
            contactType = type;
            tf_contact.setText(nullSafe(ct.getContactValue()));

            if ("PHONE".equals(type)) {
                rdb_phone.setSelected(true);
                lbl_contact_type.setText("TELÉFONO");
                tf_contact.setPromptText("82892226");
            } else {
                rdb_mail.setSelected(true);
                lbl_contact_type.setText("CORREO ELECTRÓNICO");
                tf_contact.setPromptText("ejemplo.una@est.una.ac.cr");
            }
        }

        Animations anim = new Animations();
        anim.appear(hb_id_card, 100, 1, 0);
        anim.moveNode(hb_id_card, 0, 70, true, false);
        anim.typeWriter(lbl_profile_info, 200, 30);
        anim.appear(lbl_sis, 100, 1, 0);
        anim.appear(lbl_title, 100, 1, 0);

        Animations anim1 = new Animations();
        anim1.appear(lbl_form1, 100, 2, 100);
        anim1.appear(vb_form1, 100, 2, 100);

        Animations anim2 = new Animations();
        anim2.appear(lbl_form2, 100, 2, 900);
        anim2.appear(hb_form2, 100, 2, 900);

        Animations anim3 = new Animations();
        anim3.appear(vb_title3, 100, 2, 1800);
        anim3.appear(hb_form3, 100, 2, 1800);
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    @FXML
    private void GoBack(ActionEvent event) throws IOException {
        App.setRoot("admin_home_screen");
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
    private void SaveChanges(ActionEvent event) throws IOException {
        clearMessages();

        AdminRequest session = Session.getInstance().getAdmin();

        if (session == null || session.getAdmin() == null) {
            App.setRoot("login_screen");
            return;
        }

        String fName = tf_first_name.getText().trim();
        String mName = tf_second_name.getText().trim();
        String fSurname = tf_first_surname.getText().trim();
        String mSurname = tf_second_surname.getText().trim();
        String contactValue = tf_contact.getText().trim();
        String newPassword = tf_pass.getText().trim();

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

        if (contactValue.isEmpty()) {
            showError(msg_contact, "El valor del contacto es obligatorio");
            hasErrors = true;
        } else if ("PHONE".equals(contactType) && !contactValue.matches(PHONE_REGEX)) {
            showError(msg_contact, "El teléfono debe contener exactamente 8 dígitos");
            hasErrors = true;
        } else if ("EMAIL".equals(contactType) && !contactValue.matches(EMAIL_REGEX)) {
            showError(msg_contact, "El correo debe tener un formato válido");
            hasErrors = true;
        }

        String passwordToSend;

        if (newPassword.isEmpty()) {
            passwordToSend = session.getUser().getPassword();
        } else if (newPassword.length() < 8 || !newPassword.matches(".*[A-Z].*") || !newPassword.matches(".*[0-9].*")) {
            showError(msg_pass, "La contraseña es inválida");
            hasErrors = true;
            passwordToSend = null;
        } else {
            passwordToSend = newPassword;
        }

        if (hasErrors) {
            PopUp.warning(
                    "Campos obligatorios",
                    "Faltan datos",
                    "Debe completar todos los campos requeridos antes de continuar.",
                    "back_hand.png",
                    1,
                    "Aceptar"
            );
            return;
        }

        User currentUser = session.getUser();
        Admin currentAdmin = session.getAdmin();
        Contact currentContact = session.getContact();

        User user = new User(
                currentUser.getIdUser(),
                currentUser.getIdRole(),
                currentUser.getUsername(),
                passwordToSend
        );

        Admin admin = new Admin(
                currentAdmin.getIdAdmin(),
                currentUser.getIdUser(),
                fName,
                mName,
                fSurname,
                mSurname,
                currentAdmin.getIdentityCard()
        );

        Contact contact;

        if (currentContact != null) {
            contact = new Contact(
                    currentContact.getIdContact(),
                    contactType,
                    contactValue
            );
        } else {
            contact = new Contact(contactType, contactValue);
        }

        AdminRequest request = new AdminRequest(user, admin, contact);

        Response resp = AdminProfileService.update(request);

        if (resp != null && resp.isSuccess()) {
            PopUp.notification(
                    "Administrador actualizado",
                    "Se aplicaron los cambios correctamente.",
                    "check_circle.png"
            );

            Session.getInstance().setAdmin(request);
            App.setRoot("admin_profile_screen");

        } else {
            PopUp.warning(
                    "Algo salió mal",
                    "El servidor negó tus cambios.",
                    resp != null ? resp.getMessage() : "No se pudo contactar el servidor.",
                    "power_off.png",
                    1,
                    "Aceptar"
            );

            routeServerError(resp != null ? resp.getMessage() : "Error de conexión");
        }
    }

    @FXML
    private void DeleteAccount(ActionEvent event) throws IOException {

        AdminRequest session = Session.getInstance().getAdmin();

        if (session == null || session.getAdmin() == null) {
            PopUp.warning(
                    "Sesión inválida",
                    "Debe iniciar sesión",
                    "No se encontró una sesión activa. Por favor, inicie sesión nuevamente.",
                    "person_off.png",
                    1,
                    "Aceptar"
            );

            App.setRoot("login_screen");
            return;
        }

        boolean confirm = PopUp.warning(
                "Eliminar administrador",
                "¿Está seguro?",
                "Esta acción eliminará la cuenta del administrador de forma permanente y no se puede deshacer.",
                "question.png",
                2,
                "Eliminar"
        );

        if (!confirm) {
            return;
        }

        Response resp = AdminProfileService.delete(session.getAdmin().getIdAdmin());

        if (resp == null) {
            PopUp.warning(
                    "Error de conexión",
                    "No se pudo eliminar el administrador",
                    "No se pudo contactar el servidor. Intente nuevamente.",
                    "power_off.png",
                    1,
                    "Aceptar"
            );
            return;
        }

        if (resp.isSuccess()) {
            PopUp.notification(
                    "Administrador eliminado",
                    "Todas las credenciales fueron eliminadas del sistema.",
                    "check_circle.png"
            );

            Session.getInstance().clear();
            App.setRoot("login_screen");
            return;
        }

        PopUp.warning(
                "Error",
                "No se pudo eliminar el administrador",
                resp.getMessage() != null ? resp.getMessage() : "Ocurrió un error inesperado.",
                "error.png",
                1,
                "Aceptar"
        );
    }

    private void routeServerError(String msg) {
        if (msg == null) {
            showError(msg_global, "Error desconocido");
            return;
        }

        String low = msg.toLowerCase();
        Label target;

        if (low.contains("nombre de usuario") || low.contains("ya está en uso")) {
            target = msg_global;
        } else if (low.contains("contraseña")) {
            target = msg_pass;
        } else if (low.contains("primer nombre")) {
            target = msg_name;
        } else if (low.contains("primer apellido")) {
            target = msg_surname;
        } else if (low.contains("segundo apellido")) {
            target = msg_Sec_surname;
        } else if (low.contains("contacto") || low.contains("teléfono") || low.contains("correo")) {
            target = msg_contact;
        } else {
            target = msg_global;
        }

        showError(target, msg);
    }

    private void showError(Label label, String msg) {
        label.setStyle("-fx-text-fill: #ba1a1a; -fx-font-weight:600; -fx-font-size:12px;");
        label.setText(msg);
    }

    private void clearMessages() {
        msg_name.setText("");
        msg_surname.setText("");
        msg_Sec_surname.setText("");
        msg_contact.setText("");
        msg_pass.setText("");
        msg_global.setText("");
    }
}