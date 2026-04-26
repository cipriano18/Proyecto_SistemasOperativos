/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import components.DeviceCard;
import components.ListDeviceCard;
import draft.EquipmentReservationDraft;
import dto.EquipmentReservationDraftRequest;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Equipment;
import service.EquipmentService;
import service.ReservationDraftService;
import service.Response;
import session.Session;
import utils.DraftContainer;


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
    private HBox hb_device_container;
    @FXML
    private VBox vb_device_card;
    @FXML
    private Label lbl_devise_type;
    @FXML
    private Label lbl_cuantity;
    @FXML
    private Button btn_add_device;
    @FXML
    private Button btn_save_reservation;
    @FXML
    private VBox vb_added_devices;
    private int currentDraftId = 0;
private EquipmentReservationDraft currentDraft;
private List<Equipment> availableEquipmentList;
    /**
     * Initializes the controller class.
     */
 @Override
public void initialize(URL url, ResourceBundle rb) {

    Response draftResp = DraftContainer.getInstance().getDraftResponse();

    if (draftResp != null && draftResp.getData() instanceof EquipmentReservationDraft) {
        currentDraft = (EquipmentReservationDraft) draftResp.getData();

        currentDraftId = currentDraft.getIdDraft();
        Session.getInstance().setCurrentEquipmentDraftId(currentDraftId);

        System.out.println("Draft recuperado desde DraftContainer: " + currentDraft);
        System.out.println("ID draft guardado en Session: " + currentDraftId);

loadAvailableEquipment();

    } else {
        currentDraftId = Session.getInstance().getCurrentEquipmentDraftId();
        System.out.println("No se encontró draft en DraftContainer. ID en Session: " + currentDraftId);
    }
}
@FXML
private void GoToHome(ActionEvent event) throws IOException {

    int idClient = Session.getInstance().getClient().getClient().getIdClient();

    System.out.println("Draft a descartar: " + currentDraftId);
    System.out.println("Cliente del draft: " + idClient);

    if (currentDraftId > 0) {
        EquipmentReservationDraftRequest request = new EquipmentReservationDraftRequest();
        request.setIdDraft(currentDraftId);
        request.setIdClient(idClient);

        Response resp = ReservationDraftService.discardEquipmentDraft(request);

        System.out.println(resp != null ? resp.getMessage() : "Respuesta null");

        Session.getInstance().setCurrentEquipmentDraftId(0);
        DraftContainer.getInstance().setDraftResponse(null);
    }

    App.setRoot("device_schedule_screen");
}


@FXML
private void AddDeviceToList(ActionEvent event) {

    if (availableEquipmentList == null || availableEquipmentList.isEmpty()) {
        System.out.println("No hay equipos disponibles para agregar");
        return;
    }

    ListDeviceCard card = new ListDeviceCard(availableEquipmentList);

    card.setOnDelete(() -> {
        vb_added_devices.getChildren().remove(card);
    });

    vb_added_devices.getChildren().add(card);
}
    @FXML
    private void SaveReservation(ActionEvent event) {
    }
private void loadAvailableEquipment() {

    if (currentDraft == null || currentDraft.getReservation() == null) {
        System.out.println("No hay draft o reservación para cargar equipos");
        return;
    }

    java.sql.Date reservationDate = currentDraft.getReservation().getReservationDate();
    int idSection = currentDraft.getReservation().getIdSection();

    Response resp = EquipmentService.getAvailableEquipmentByDateAndSection(
            reservationDate,
            idSection
    );

    if (resp != null && resp.isSuccess()) {

        availableEquipmentList = (List<Equipment>) resp.getData();

        hb_device_container.getChildren().clear();

     for (Equipment eq : availableEquipmentList) {

    DeviceCard card = new DeviceCard(
            eq.getName(),
            eq.getTotalQuantity()
    );

    hb_device_container.getChildren().add(card);
}

        System.out.println("Equipos cargados: " + availableEquipmentList.size());

    } else {
        String msg = (resp != null) ? resp.getMessage() : "No se pudo conectar al servidor";
        System.out.println(msg);
    }
}
}
