package controller;

import com.auditorio.clientereservas.App;
import components.DeviceCard;
import components.ListDeviceCard;
import components.PopUp;
import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Equipment;
import model.RXE;
import model.Reservation;
import service.AuditoriumDraftService;
import service.EquipmentService;
import service.Response;
import session.Session;
import utils.DraftContainer;
import components.TtlChip;
import network.ReservationNotificationHandler;
/**
 * Controlador del formulario para completar una reserva de auditorio.
 */
public class auditorium_form_screen_controller implements Initializable {

    @FXML
    private Button btn_goback;
    @FXML
    private VBox vb_info;
    @FXML
    private ImageView img_logo;
    @FXML
    private HBox hb_device_container;
    @FXML
    private VBox vb_added_devices;
    @FXML
    private Button btn_add_device;
    @FXML
    private Button btn_save_reservation;

    @FXML
    private Label lbl_selected_date;
    @FXML
    private Label lbl_selected_section;
    @FXML
    private TextField txt_event_name;
    @FXML
    private TextField txt_attendees_count;
    @FXML
    private TextArea txt_observations;

    @FXML
    private HBox ttl_chip_container;

    private AuditoriumDraftRequest currentDraft;
    private Reservation selectedReservation;
    private List<Equipment> availableEquipmentList;

    private boolean loadingData = false;

    private TtlChip ttlChip;
    private Runnable expiredListener;
    
    /**
     * Carga el draft actual, los datos base y el estado visual del formulario.
     *
     * @param url ubicacion usada para resolver rutas relativas
     * @param rb recursos de internacionalizacion asociados a la vista
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadingData = true;
        DraftContainer.getInstance().setFlowType("AUDITORIUM");

        Response draftResponse 
                = DraftContainer.getInstance().getDraftResponse();

        if (draftResponse != null
                && draftResponse.isSuccess()
                && draftResponse.getData() instanceof AuditoriumDraftRequest) {

            currentDraft = (AuditoriumDraftRequest) draftResponse.getData();
        }

        if (currentDraft == null
                || currentDraft.getCreatedAt() == null
                || currentDraft.getExpiresAt() == null) {
            hydrateCurrentDraft();
        }

        if (currentDraft != null && currentDraft.getReservation() != null) {
            selectedReservation = currentDraft.getReservation();

        } else {
            selectedReservation 
                    = DraftContainer.getInstance().getSelectedReservation();
        }

        if (selectedReservation == null) {
            PopUp.warning(
                    "Reserva inválida",
                    "No se encontró la sección seleccionada",
                    "Seleccione una fecha y sección antes de continuar con la "
                    + "reserva de auditorio.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );

            try {
                App.setRoot("home_screen");
            } catch (IOException ignored) {
            }
            return;
        }

        lbl_selected_date.setText(
                formatDate(selectedReservation.getReservationDate()));
        
        lbl_selected_section.setText(
                formatSection(selectedReservation.getIdSection()));

        loadAuditoriumDraftData();

        loadAvailableEquipment();
        loadDraftEquipmentCards();

        addAutosaveListeners();

        setupTtlChip();

        loadingData = false;
    }

    /**
     * Configura el chip con el tiempo restante del draft activo.
     */
   private void setupTtlChip() {

    System.out.println(
            "currentDraft: "
            + currentDraft
    );

    if (currentDraft != null) {

        System.out.println(
                "createdAt: "
                + currentDraft.getCreatedAt()
        );

        System.out.println(
                "expiresAt: "
                + currentDraft.getExpiresAt()
        );

        System.out.println(
                "isExpired: "
                + currentDraft.isExpired()
        );
    }

    if (currentDraft == null
            || currentDraft.getCreatedAt() == null
            || currentDraft.getExpiresAt() == null
            || currentDraft.isExpired()
            || ttl_chip_container == null) {
            if (ttl_chip_container != null) {
                ttl_chip_container.getChildren().clear();
                ttl_chip_container.setVisible(false);
                ttl_chip_container.setManaged(false);
            }
            return;
        }

        ttl_chip_container.setVisible(true);
        ttl_chip_container.setManaged(true);

        ttlChip = new TtlChip();
        ttlChip.setOnExpired(this::handleDraftExpired);
        ttl_chip_container.getChildren().setAll(ttlChip);
        ttlChip.start(currentDraft.getCreatedAt(), currentDraft.getExpiresAt());

        expiredListener = this::handleDraftExpired;
        ReservationNotificationHandler.addOnDraftExpired(expiredListener);
    }

    private void hydrateCurrentDraft() {
        try {
            if (Session.getInstance().getClient() == null
                    || Session.getInstance().getClient().getClient() == null) {
                return;
            }

            int idClient = Session.getInstance().getClient().getClient()
                    .getIdClient();

            Response resp = AuditoriumDraftService
                    .getAuditoriumDraftByClientId(idClient);

            if (resp != null
                    && resp.isSuccess()
                    && resp.getData() instanceof AuditoriumDraftRequest) {
                currentDraft = (AuditoriumDraftRequest) resp.getData();
            }
        } catch (Exception e) {
            System.out.println(
                    "No se pudo hidratar el draft de auditorio: "
                    + e.getMessage());
        }
    }

    /**
     * Atiende la expiracion del draft y redirige al calendario.
     */
    private void handleDraftExpired() {
        teardownTtlChip();

        DraftContainer.getInstance().clearAll();
        DraftContainer.getInstance().setFlowType("AUDITORIUM");

        PopUp.warning(
                "Reserva vencida",
                "Tiempo agotado",
                "Su reserva temporal de auditorio ha vencido. Debe iniciar el "
                + "proceso nuevamente.",
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
            
            ReservationNotificationHandler
                    .removeOnDraftExpired(expiredListener);
            
            expiredListener = null;
        }
        if (ttl_chip_container != null) {
            ttl_chip_container.getChildren().clear();
            ttl_chip_container.setVisible(false);
            ttl_chip_container.setManaged(false);
        }
    }

    private void loadAuditoriumDraftData() {

        if (currentDraft != null && currentDraft.getAuditoriumDraft() != null) {
            AuditoriumDraft draft = currentDraft.getAuditoriumDraft();

            txt_event_name.setText(
                    draft.getEventName() != null ? draft.getEventName() : "");

            if (draft.getAttendeesCount() > 0) {
                txt_attendees_count.setText(
                        String.valueOf(draft.getAttendeesCount()));
            } else {
                txt_attendees_count.setText("");
            }

            txt_observations.setText(
                    draft.getObservations() != null 
                            ? draft.getObservations() 
                            : "");
        }
    }

    private void addAutosaveListeners() {

        txt_event_name.textProperty().addListener((
                obs, 
                oldValue, 
                newValue) -> {
            
            refreshAuditoriumDraftInServer();
            
        });

        txt_attendees_count.textProperty().addListener((
                obs, 
                oldValue, 
                newValue) -> {
            
            refreshAuditoriumDraftInServer();
            
        });

        txt_observations.textProperty().addListener((
                obs, 
                oldValue, 
                newValue) -> {
            
            refreshAuditoriumDraftInServer();
            
        });
    }

    /**
     * Agrega una nueva tarjeta de equipo al formulario del auditorio.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void AddDeviceToList(ActionEvent event) {

        loadAvailableEquipment();

        if (
            availableEquipmentList == null 
            || availableEquipmentList.isEmpty()) {
            
            PopUp.warning(
                    "Aviso",
                    "Sin equipos disponibles",
                    "No hay equipos disponibles para agregar.",
                    "warning.png",
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
                    "Todos los equipos disponibles ya fueron agregados.",
                    "warning.png",
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

        card.setOnQuantityChange(() -> {
            refreshAuditoriumDraftInServer();
        });

        card.setOnDelete(() -> {

            boolean confirm = PopUp.warning(
                    "Confirmación",
                    "Eliminar equipo",
                    "¿Está seguro que desea eliminar este equipo de la lista?",
                    "warning.png",
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

            refreshAuditoriumDraftInServer();
        });

        VBox.setMargin(card, new Insets(0, 0, 10, 0));
        vb_added_devices.getChildren().add(card);

        refreshAuditoriumDraftInServer();
    }

    private void loadDraftEquipmentCards() {

        if (currentDraft == null || currentDraft.getEquipmentList() == null) {
            return;
        }

        if (availableEquipmentList == null) {
            return;
        }

        vb_added_devices.getChildren().clear();

        for (RXE item : currentDraft.getEquipmentList()) {
List<Equipment> filtered = availableEquipmentList.stream()
        .filter(eq -> eq.getIdEquipment() == item.getIdEquipment())
        .collect(Collectors.toList());
System.out.println(
        "Equipo draft ID: "
        + item.getIdEquipment()
);

System.out.println(
        "Equipos disponibles: "
        + availableEquipmentList.stream()
                .map(Equipment::getIdEquipment)
                .collect(Collectors.toList())
);
if (filtered.isEmpty()) {
    continue;
}

            ListDeviceCard card = new ListDeviceCard(filtered);

            card.setOnQuantityChange(() -> {
                refreshAuditoriumDraftInServer();
            });

            card.setOnDelete(() -> {

                boolean confirm = PopUp.warning(
                        "Confirmación",
                        "Eliminar equipo",
                        "¿Está seguro que desea eliminar este equipo "
                        + "de la lista?",
                        "warning.png",
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
                       ((ListDeviceCard) newLast)
                               .setDeviceChoiceDisabled(false);
                    }
                }

                refreshAuditoriumDraftInServer();
            });

VBox.setMargin(card, new Insets(0, 0, 10, 0));
vb_added_devices.getChildren().add(card);

card.setSelectedEquipment(filtered.get(0));
card.setSelectedQuantity(item.getQuantity());
        }

        if (!vb_added_devices.getChildren().isEmpty()) {
            Node last = vb_added_devices.getChildren()
                    .get(vb_added_devices.getChildren().size() - 1);

            if (last instanceof ListDeviceCard) {
                ((ListDeviceCard) last).setDeviceChoiceDisabled(false);
            }
        }
    }

    private void refreshAuditoriumDraftInServer() {

        if (loadingData) {
            return;
        }

        if (currentDraft == null || currentDraft.getIdDraft() <= 0) {
            return;
        }

        String eventName = txt_event_name.getText() != null
                ? txt_event_name.getText().trim()
                : "";

        String attendeesText = txt_attendees_count.getText() != null
                ? txt_attendees_count.getText().trim()
                : "";

        String observations = txt_observations.getText() != null
                ? txt_observations.getText().trim()
                : "";

        int attendeesCount = 0;

        try {
            if (!attendeesText.isEmpty()) {
                attendeesCount = Integer.parseInt(attendeesText);
            }
        } catch (NumberFormatException e) {
            return;
        }

        List<RXE> equipmentList = getEquipmentListFromCards();

        AuditoriumDraft auditoriumDraft = new AuditoriumDraft();
        auditoriumDraft.setEventName(eventName);
        auditoriumDraft.setAttendeesCount(attendeesCount);
        auditoriumDraft.setObservations(observations);

        AuditoriumDraftRequest request = new AuditoriumDraftRequest();
        request.setIdDraft(currentDraft.getIdDraft());
        request.setIdClient(currentDraft.getIdClient());
        request.setReservation(selectedReservation);
        request.setAuditoriumDraft(auditoriumDraft);
        request.setEquipmentList(equipmentList);

        Response resp = AuditoriumDraftService.updateAuditoriumDraft(request);

        if (resp != null && resp.isSuccess()) {
            currentDraft.setAuditoriumDraft(auditoriumDraft);
            currentDraft.setEquipmentList(equipmentList);
            currentDraft.setReservation(selectedReservation);
        }
    }

    private List<RXE> getEquipmentListFromCards() {

        List<RXE> equipmentList = new ArrayList<>();

        for (Node node : vb_added_devices.getChildren()) {
            if (node instanceof ListDeviceCard) {

                ListDeviceCard card = (ListDeviceCard) node;
                Equipment selected = card.getSelectedEquipment();
                Integer quantity = card.getSelectedQuantity();

                if (selected != null && quantity != null) {
                    RXE rxe = new RXE();
                    rxe.setIdEquipment(selected.getIdEquipment());
                    rxe.setQuantity(quantity);

                    equipmentList.add(rxe);
                }
            }
        }

        return equipmentList;
    }

    /**
     * Descarta el draft actual y abandona el formulario.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void goBack(ActionEvent event) throws IOException {

        boolean confirm = PopUp.warning(
                "Confirmación",
                "Salir de la reserva",
                "Si sale de esta pantalla, perderá la reserva actual y tendrá "
                + "que iniciar el proceso desde cero. ¿Desea continuar?",
                "warning.png",
                2,
                3,
                "Salir"
        );

        if (!confirm) {
            return;
        }

        if (currentDraft != null && currentDraft.getIdDraft() > 0) {

            AuditoriumDraftRequest request = new AuditoriumDraftRequest();
            request.setIdDraft(currentDraft.getIdDraft());
            request.setIdClient(currentDraft.getIdClient());

            Response resp 
                    = AuditoriumDraftService.discardAuditoriumDraft(request);

            if (resp == null || !resp.isSuccess()) {
                PopUp.warning(
                        "Error",
                        "No se pudo descartar",
                        resp != null 
                                ? resp.getMessage() 
                                : "No se pudo conectar con el servidor.",
                        "error.png",
                        1,
                        1,
                        "Aceptar"
                );
                return;
            }
        }

        DraftContainer.getInstance().clearAll();

        DraftContainer.getInstance().setFlowType("AUDITORIUM");
        App.setRoot("device_schedule_screen");

        teardownTtlChip();

        App.setRoot("home_screen");
    }

    private void loadAvailableEquipment() {

        if (selectedReservation == null) {
            System.out.println("No hay reservación seleccionada para "
                    + "cargar equipos");
            return;
        }

        java.sql.Date reservationDate 
                = selectedReservation.getReservationDate();
        
        int idSection = selectedReservation.getIdSection();

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
            String msg = resp != null 
                    ? resp.getMessage() 
                    : "No se pudo conectar al servidor";
            
            System.out.println(msg);
        }
    }

    private String formatDate(java.sql.Date date) {
        return date != null ? date.toString() : "Sin fecha";
    }

    private String formatSection(int idSection) {
        switch (idSection) {
            case 1:
                return "Mañana";
            case 2:
                return "Tarde";
            case 3:
                return "Noche";
            default:
                return "Sección " + idSection;
        }
    }

    /**
     * Valida y confirma la reserva definitiva de auditorio.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void saveAuditoriumReservation(ActionEvent event) {

        String eventName = txt_event_name.getText() != null
                ? txt_event_name.getText().trim()
                : "";

        String attendeesText = txt_attendees_count.getText() != null
                ? txt_attendees_count.getText().trim()
                : "";

        String observations = txt_observations.getText() != null
                ? txt_observations.getText().trim()
                : "";

        if (eventName.isEmpty()) {
            PopUp.warning(
                    "Datos incompletos",
                    "El nombre del evento es obligatorio",
                    "Ingrese el nombre del evento para crear la reserva "
                    + "temporal de auditorio.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        int attendeesCount;

        try {
            attendeesCount = Integer.parseInt(attendeesText);
        } catch (NumberFormatException e) {
            PopUp.warning(
                    "Datos inválidos",
                    "La cantidad de asistentes debe ser numérica",
                    "Ingrese una cantidad válida de asistentes.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        if (attendeesCount <= 0) {
            PopUp.warning(
                    "Datos inválidos",
                    "Cantidad de asistentes inválida",
                    "La cantidad de asistentes debe ser mayor a cero.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        if (currentDraft == null || currentDraft.getIdDraft() <= 0) {
            PopUp.warning(
                    "Reserva temporal no disponible",
                    "No se encontró el draft de auditorio",
                    "Intente seleccionar nuevamente la fecha y sección desde "
                    + "el calendario.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        List<RXE> equipmentList = getEquipmentListFromCards();

        AuditoriumDraft auditoriumDraft = new AuditoriumDraft();
        auditoriumDraft.setEventName(eventName);
        auditoriumDraft.setAttendeesCount(attendeesCount);
        auditoriumDraft.setObservations(observations);

        AuditoriumDraftRequest request = new AuditoriumDraftRequest();
        request.setIdDraft(currentDraft.getIdDraft());
        request.setIdClient(currentDraft.getIdClient());
        request.setReservation(selectedReservation);
        request.setAuditoriumDraft(auditoriumDraft);
        request.setEquipmentList(equipmentList);

        Response updateResp 
                = AuditoriumDraftService.updateAuditoriumDraft(request);

        if (updateResp == null || !updateResp.isSuccess()) {
            PopUp.warning(
                    "Error",
                    "No se pudo guardar",
                    updateResp != null 
                            ? updateResp.getMessage() 
                            : "No se pudo conectar con el servidor.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );
            return;
        }

        boolean confirm = PopUp.warning(
                "Confirmar reserva",
                "¿Desea confirmar la reserva?",
                "Una vez confirmada, la reserva será definitiva.",
                "warning.png",
                2,
                3,
                "Confirmar"
        );

        if (!confirm) {
            return;
        }

        Response confirmResp 
                = AuditoriumDraftService.confirmAuditoriumDraft(request);

        if (confirmResp != null && confirmResp.isSuccess()) {

            PopUp.notification(
                    "Reserva confirmada",
                    "La reserva de auditorio se confirmó correctamente.",
                    "success.png",
                    2
            );

            DraftContainer.getInstance().clearAll();

            teardownTtlChip();

            try {
                App.setRoot("home_screen");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            PopUp.warning(
                    "Error",
                    "No se pudo confirmar",
                    confirmResp != null 
                            ? confirmResp.getMessage() 
                            : "Error desconocido.",
                    "error.png",
                    1,
                    1,
                    "Aceptar"
            );
        }
    }
}
