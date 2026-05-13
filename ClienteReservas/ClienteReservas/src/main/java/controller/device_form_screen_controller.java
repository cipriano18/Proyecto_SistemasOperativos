/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import components.DeviceCard;
import components.ListDeviceCard;
import components.PopUp;
import components.TtlChip;
import draft.EquipmentReservationDraft;
import dto.EquipmentReservationDraftRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Equipment;
import model.RXE;
import network.ReservationNotificationHandler;
import service.EquipmentReservationDraftService;
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
    @FXML
    private HBox ttl_chip_container;
    private int currentDraftId = 0;
    private EquipmentReservationDraft currentDraft;
    private List<Equipment> availableEquipmentList;
    private TtlChip ttlChip;
    private Runnable expiredListener;

    /**
     * Carga el draft activo, los equipos disponibles y el indicador de tiempo.
     *
     * @param url ubicacion usada para resolver rutas relativas
     * @param rb recursos de internacionalizacion asociados a la vista
     */
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Response draftResp = DraftContainer.getInstance().getDraftResponse();

        if (draftResp != null
                && draftResp.getData() instanceof EquipmentReservationDraft) {

            currentDraft = (EquipmentReservationDraft) draftResp.getData();

            currentDraftId = currentDraft.getIdDraft();
            Session.getInstance().setCurrentEquipmentDraftId(currentDraftId);

            System.out.println(
                    "Draft recuperado desde DraftContainer: "
                    + currentDraft);

            System.out.println(
                    "ID draft guardado en Session: "
                    + currentDraftId);

            loadAvailableEquipment();
            loadDraftEquipmentCards();

            setupTtlChip();

        } else {
            currentDraftId = Session.getInstance().getCurrentEquipmentDraftId();
            System.out.println(
                    "No se encontró draft en DraftContainer. ID en Session: "
                    + currentDraftId);
        }
    }

    /**
     * Configura el chip que muestra el tiempo restante del draft.
     */
    private void setupTtlChip() {
        if (currentDraft == null
                || currentDraft.getCreatedAt() == null
                || currentDraft.getExpiresAt() == null
                || ttl_chip_container == null) {
            return;
        }

        ttlChip = new TtlChip();
        ttlChip.setOnExpired(this::handleDraftExpired);
        ttl_chip_container.getChildren().setAll(ttlChip);
        ttlChip.start(currentDraft.getCreatedAt(), currentDraft.getExpiresAt());

        expiredListener = this::handleDraftExpired;
        ReservationNotificationHandler.addOnDraftExpired(expiredListener);
    }

    /**
     * Atiende la expiracion del draft y redirige al calendario.
     */
    private void handleDraftExpired() {
        if (ttlChip != null) {
            ttlChip.stop();
        }
        if (expiredListener != null) {
            ReservationNotificationHandler.removeOnDraftExpired(
                    expiredListener);
            expiredListener = null;
        }

        Session.getInstance().setCurrentEquipmentDraftId(0);
        DraftContainer.getInstance().setDraftResponse(null);

        PopUp.warning(
                "Reserva vencida",
                "Tiempo agotado",
                "Su reserva temporal ha vencido. Debe iniciar el proceso "
                + "nuevamente.",
                "back_hand.png",
                1,
                1,
                "Aceptar"
        );

        try {
            App.setRoot("device_schedule_screen");
        } catch (IOException ignored) {
        }
    }

    private void teardownTtlChip() {
        if (ttlChip != null) {
            ttlChip.stop();
            ttlChip = null;
        }
        if (expiredListener != null) {
            ReservationNotificationHandler.removeOnDraftExpired(
                    expiredListener);

            expiredListener = null;
        }
        if (ttl_chip_container != null) {
            ttl_chip_container.getChildren().clear();
        }
    }

    /**
     * Descarta el draft activo y regresa al calendario de reservas.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToHome(ActionEvent event) throws IOException {

        boolean confirm = PopUp.warning(
                "Confirmación",
                "Salir de la reserva",
                "Si sale de esta pantalla, perderá la reserva actual y tendrá "
                + "que iniciar el proceso desde cero. ¿Desea continuar?",
                "back_hand.png",
                2,
                3,
                "Salir"
        );

        if (!confirm) {
            return;
        }

        int idClient
                = Session.getInstance().getClient().getClient().getIdClient();

        if (currentDraftId > 0) {
            EquipmentReservationDraftRequest request
                    = new EquipmentReservationDraftRequest();

            request.setIdDraft(currentDraftId);
            request.setIdClient(idClient);

            Response resp
                    = ReservationDraftService.discardEquipmentDraft(request);

            if (resp == null || !resp.isSuccess()) {
                PopUp.warning(
                        "Error",
                        "No se pudo descartar",
                        resp != null
                                ? resp.getMessage()
                                : "No se pudo conectar con el servidor.",
                        "dangerous.png",
                        1,
                        1,
                        "Aceptar"
                );
                return;
            }

            Session.getInstance().setCurrentEquipmentDraftId(0);
            DraftContainer.getInstance().setDraftResponse(null);
        }

        teardownTtlChip();

        App.setRoot("device_schedule_screen");
    }

    /**
     * Agrega una nueva tarjeta de equipo a la reserva temporal.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void AddDeviceToList(ActionEvent event) {

        loadAvailableEquipment();

        if (availableEquipmentList == null
                || availableEquipmentList.isEmpty()) {

            PopUp.warning(
                    "Aviso",
                    "Sin equipos disponibles",
                    "No hay equipos disponibles para agregar.",
                    "devices_off.png",
                    1,
                    2,
                    "Aceptar"
            );
            return;
        }

        Set<Integer> usedIds = new HashSet<>();

        for (Node n : vb_added_devices.getChildren()) {
            if (n instanceof ListDeviceCard) {
                Equipment selected
                        = ((ListDeviceCard) n).getSelectedEquipment();

                if (selected != null) {
                    usedIds.add(selected.getIdEquipment());
                }
            }
        }

        List<Equipment> filtered = availableEquipmentList.stream()
                .filter(eq -> !usedIds.contains(eq.getIdEquipment()))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            PopUp.warning(
                    "Aviso",
                    "Equipos ya agregados",
                    "Todos los dispositivos disponibles ya fueron agregados.",
                    "devices_off.png",
                    1,
                    2,
                    "Aceptar"
            );
            return;
        }

        if (!vb_added_devices.getChildren().isEmpty()) {
            Node last = vb_added_devices.getChildren()
                    .get(vb_added_devices.getChildren().size() - 1);

            if (last instanceof ListDeviceCard) {
                ((ListDeviceCard) last).setDeviceChoiceDisabled(true);
            }
        }

        ListDeviceCard card = new ListDeviceCard(filtered);

        //  CAMBIO DE CANTIDAD
        card.setOnQuantityChange(() -> {
            refreshDraftInServer();
        });

        //  DELETE
        card.setOnDelete(() -> {

            boolean confirm = PopUp.warning(
                    "Confirmación",
                    "Eliminar dispositivo",
                    "¿Está seguro que desea eliminar este dispositivo de "
                    + "la lista?",
                    "back_hand.png",
                    2,
                    3,
                    "Eliminar"
            );

            if (!confirm) {
                return;
            }

            vb_added_devices.getChildren().remove(card);

            if (!vb_added_devices.getChildren().isEmpty()) {
                Node newLast = vb_added_devices.getChildren()
                        .get(vb_added_devices.getChildren().size() - 1);

                if (newLast instanceof ListDeviceCard) {
                    ((ListDeviceCard) newLast).setDeviceChoiceDisabled(false);
                }
            }

            refreshDraftInServer();
        });

        vb_added_devices.setMargin(card, new Insets(0, 0, 10, 0));
        vb_added_devices.getChildren().add(card);

        refreshDraftInServer();
    }

    /**
     * Confirma la reserva de equipos usando el draft activo.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void SaveReservation(ActionEvent event) {

        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();

        if (currentDraftId <= 0) {
            PopUp.warning(
                    "Error",
                    "Reserva no encontrada",
                    "No hay una reserva temporal activa para confirmar.",
                    "dangerous.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        List<RXE> equipmentList = new ArrayList<>();

        for (Node node : vb_added_devices.getChildren()) {
            if (node instanceof ListDeviceCard) {

                ListDeviceCard card = (ListDeviceCard) node;
                Equipment selected = card.getSelectedEquipment();

                if (selected != null) {
                    RXE rxe = new RXE();

                    rxe.setIdEquipment(selected.getIdEquipment());
                    rxe.setQuantity(card.getSelectedQuantity());

                    equipmentList.add(rxe);
                }
            }
        }

        if (equipmentList.isEmpty()) {
            PopUp.warning(
                    "Aviso",
                    "Equipo requerido",
                    "Debe seleccionar al menos un equipo para confirmar la "
                    + "reserva.",
                    "back_hand.png",
                    1,
                    2,
                    "Aceptar"
            );
            return;
        }

        EquipmentReservationDraftRequest request
                = new EquipmentReservationDraftRequest();

        request.setIdDraft(currentDraftId);
        request.setIdClient(idClient);
        request.setEquipmentList(equipmentList);

        Response updateResp
                = EquipmentReservationDraftService
                        .updateEquipmentDraft(request);

        if (updateResp == null || !updateResp.isSuccess()) {
            PopUp.warning(
                    "Error",
                    "No se pudo actualizar",
                    updateResp != null
                            ? updateResp.getMessage()
                            : "No se pudo conectar con el servidor.",
                    "back_hand.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        Response confirmResp
                = EquipmentReservationDraftService.confirmEquipmentDraft(
                        currentDraftId,
                        idClient
                );

        if (confirmResp != null && confirmResp.isSuccess()) {

            PopUp.notification(
                    "Reserva confirmada",
                    "La reserva de equipos se confirmó correctamente.",
                    "check_circle.png",
                    2
            );

            Session.getInstance().setCurrentEquipmentDraftId(0);
            DraftContainer.getInstance().setDraftResponse(null);

            teardownTtlChip();

            try {
                App.setRoot("device_schedule_screen");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            PopUp.warning(
                    "Error",
                    "No se pudo confirmar",
                    confirmResp != null
                            ? confirmResp.getMessage()
                            : "Error desconocido.",
                    "back_hand.png",
                    1,
                    1,
                    "Aceptar"
            );
        }
    }

    private void loadDraftEquipmentCards() {

        if (currentDraft == null || currentDraft.getEquipmentList() == null) {
            return;
        }

        vb_added_devices.getChildren().clear();

        for (RXE item : currentDraft.getEquipmentList()) {

            List<Equipment> filtered = availableEquipmentList.stream()
                    .filter(eq -> eq.getIdEquipment() == item.getIdEquipment())
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                continue;
            }

            ListDeviceCard card = new ListDeviceCard(filtered);

            card.setOnQuantityChange(() -> {
                refreshDraftInServer();
            });

            card.setOnDelete(() -> {

                boolean confirm = PopUp.warning(
                        "Confirmación",
                        "Eliminar dispositivo",
                        "¿Está seguro que desea eliminar este dispositivo de "
                        + "la lista?",
                        "back_hand.png",
                        2,
                        3,
                        "Eliminar"
                );

                if (!confirm) {
                    return;
                }

                vb_added_devices.getChildren().remove(card);
                refreshDraftInServer();
            });

            VBox.setMargin(card, new Insets(0, 0, 10, 0));
            vb_added_devices.getChildren().add(card);

            card.setSelectedQuantity(item.getQuantity());
        }
    }

    private void loadAvailableEquipment() {

        if (currentDraft == null || currentDraft.getReservation() == null) {
            System.out.println(
                    "No hay draft o reservación para cargar equipos");
            return;
        }

        java.sql.Date reservationDate
                = currentDraft.getReservation().getReservationDate();

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

            System.out.println(
                    "Equipos cargados: "
                    + availableEquipmentList.size());

        } else {
            String msg = (resp != null)
                    ? resp.getMessage()
                    : "No se pudo conectar al servidor";
            System.out.println(msg);
        }
    }

    private void refreshDraftInServer() {

        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();

        if (currentDraftId <= 0) {
            return;
        }

        List<RXE> equipmentList = new ArrayList<>();

        for (Node node : vb_added_devices.getChildren()) {
            if (node instanceof ListDeviceCard) {

                ListDeviceCard c = (ListDeviceCard) node;
                Equipment eq = c.getSelectedEquipment();
                Integer quantity = c.getSelectedQuantity();

                if (eq != null && quantity != null) {
                    RXE rxe = new RXE();
                    rxe.setIdEquipment(eq.getIdEquipment());
                    rxe.setQuantity(quantity);

                    equipmentList.add(rxe);
                }
            }
        }

        EquipmentReservationDraftRequest request
                = new EquipmentReservationDraftRequest();

        request.setIdDraft(currentDraftId);
        request.setIdClient(idClient);
        request.setEquipmentList(equipmentList);

        EquipmentReservationDraftService.updateEquipmentDraft(request);
    }
}
