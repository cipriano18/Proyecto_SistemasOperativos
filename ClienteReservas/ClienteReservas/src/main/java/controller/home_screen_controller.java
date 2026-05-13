/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import components.TtlChip;
import draft.EquipmentReservationDraft;
import dto.AuditoriumDraftRequest;
import dto.ClientRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import network.ReservationNotificationHandler;
import service.AuditoriumDraftService;
import service.CalendarService;
import service.EquipmentReservationDraftService;
import service.Response;
import session.Session;
import utils.DraftContainer;

/**
 * Controlador de la pantalla principal del cliente.
 */
public class home_screen_controller implements Initializable {

    @FXML
    private Label lbl_welcome;
    @FXML
    private Button btn_profile;
    @FXML
    private Button btn_auditorium;
    @FXML
    private Button btn_devices;
    @FXML
    private Button btn_schedule;
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

    @FXML
    private HBox draft_recovery_banner;
    @FXML
    private Label lbl_recovery_text;
    @FXML
    private HBox ttl_banner_chip_container;
    @FXML
    private Button btn_continue_draft;
    @FXML
    private Button btn_discard_draft;

    private TtlChip bannerChip;
    private Runnable bannerExpiredListener;
    private Response recoveredDraftResp;
    private String recoveredFlowType;

    /**
     * Verifica la sesion activa y prepara la recuperacion de drafts.
     *
     * @param url ubicacion usada para resolver rutas relativas
     * @param rb recursos de internacionalizacion asociados a la vista
     */
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ClientRequest session = Session.getInstance().getClient();

        if (session == null) {
            PopUp.warning(
                    "Sesión expirada",
                    "Debe iniciar sesión",
                    "Su sesión ha expirado o no es válida. Por favor, inicie "
                    + "sesión nuevamente.",
                    "error.png",
                    1,
                    1,
                    "Ir al login"
            );

            try {
                App.setRoot("login_screen");
            } catch (IOException ignored) {
            }
            return;
        }

        if (lbl_welcome != null && session.getClient() != null) {
            lbl_welcome.setText(
                    "¿Qué hacemos " 
                    + session.getClient().getfName() 
                    + "?");
        }

        hideRecoveryBanner();

        int idClient = session.getClient() != null 
                ? session.getClient().getIdClient() 
                : 0;
        
        if (idClient > 0) {
            checkActiveDraftsAsync(idClient);
        }
    }

    /**
     * Consulta de forma asincrona los drafts activos del cliente.
     *
     * @param idClient identificador del cliente autenticado
     */
    private void checkActiveDraftsAsync(int idClient) {
        Task<Object[]> task = new Task<Object[]>() {
            @Override
            protected Object[] call() {
                Response equipResp = null;
                Response audResp = null;
                try {
                    equipResp = EquipmentReservationDraftService
                            .getEquipmentDraftByClientId(idClient);
                    
                } catch (Exception ignored) {
                }
                try {
                    audResp = AuditoriumDraftService
                            .getAuditoriumDraftByClientId(idClient);
                    
                } catch (Exception ignored) {
                }
                return new Object[] { equipResp, audResp };
            }
        };

        task.setOnSucceeded(e -> {
            Object[] results = task.getValue();
            Response equipResp = (Response) results[0];
            Response audResp = (Response) results[1];
            showRecoveryBannerIfAny(equipResp, audResp);
        });

        Thread t = new Thread(task, "draft-recovery-check");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Muestra el banner de recuperacion si existe un draft vigente.
     *
     * @param equipResp respuesta del draft de equipos
     * @param audResp respuesta del draft de auditorio
     */
    private void showRecoveryBannerIfAny(Response equipResp, Response audResp) {

        if (equipResp != null && equipResp.isSuccess()
                && equipResp.getData() instanceof EquipmentReservationDraft) {

            EquipmentReservationDraft draft 
                    = (EquipmentReservationDraft) equipResp.getData();
            
            if (draft.getExpiresAt() != null && !draft.isExpired()) {
                recoveredDraftResp = equipResp;
                recoveredFlowType = "DEVICE";
                showBanner(
                        "Tienes una reserva de equipos activa", 
                        draft.getCreatedAt(), 
                        draft.getExpiresAt());
                
                return;
            }
        }

        if (audResp != null && audResp.isSuccess()
                && audResp.getData() instanceof AuditoriumDraftRequest) {

            AuditoriumDraftRequest draft 
                    = (AuditoriumDraftRequest) audResp.getData();
            
            Timestamp[] ttl = tryReadAuditoriumTtl(draft);

            recoveredDraftResp = audResp;
            recoveredFlowType = "AUDITORIUM";

            if (ttl != null && ttl[0] != null && ttl[1] != null
                    && System.currentTimeMillis() < ttl[1].getTime()) {
                showBanner(
                        "Tienes una reserva de auditorio activa", 
                        ttl[0],
                        ttl[1]);
                
            } else {
                showBannerNoChip("Tienes una reserva de auditorio activa");
            }
        }
    }

    private void showBanner(
            String text, 
            Timestamp createdAt, 
            Timestamp expiresAt) {
        
        if (draft_recovery_banner == null) return;

        lbl_recovery_text.setText(text);
        draft_recovery_banner.setVisible(true);
        draft_recovery_banner.setManaged(true);

        if (ttl_banner_chip_container != null 
                && createdAt != null 
                && expiresAt != null) {
            
            bannerChip = new TtlChip();
            bannerChip.setCompact(true);
            bannerChip.setOnExpired(this::hideRecoveryBanner);
            ttl_banner_chip_container.getChildren().setAll(bannerChip);
            bannerChip.start(createdAt, expiresAt);
        }

        bannerExpiredListener = this::hideRecoveryBanner;
        ReservationNotificationHandler.addOnDraftExpired(bannerExpiredListener);
    }

    private void showBannerNoChip(String text) {
        if (draft_recovery_banner == null) return;

        lbl_recovery_text.setText(text);
        draft_recovery_banner.setVisible(true);
        draft_recovery_banner.setManaged(true);

        if (ttl_banner_chip_container != null) {
            ttl_banner_chip_container.getChildren().clear();
        }

        bannerExpiredListener = this::hideRecoveryBanner;
        ReservationNotificationHandler.addOnDraftExpired(bannerExpiredListener);
    }

    private void hideRecoveryBanner() {
        Platform.runLater(() -> {
            if (bannerChip != null) {
                bannerChip.stop();
                bannerChip = null;
            }
            if (bannerExpiredListener != null) {
                ReservationNotificationHandler
                        .removeOnDraftExpired(bannerExpiredListener);
                
                bannerExpiredListener = null;
            }
            if (ttl_banner_chip_container != null) {
                ttl_banner_chip_container.getChildren().clear();
            }
            if (draft_recovery_banner != null) {
                draft_recovery_banner.setVisible(false);
                draft_recovery_banner.setManaged(false);
            }
            recoveredDraftResp = null;
            recoveredFlowType = null;
        });
    }

    private static Timestamp[] tryReadAuditoriumTtl(
            AuditoriumDraftRequest req) {
        
        if (req == null) return null;
        try {
            Method m1 = req.getClass().getMethod("getCreatedAt");
            Method m2 = req.getClass().getMethod("getExpiresAt");
            Object a = m1.invoke(req);
            Object b = m2.invoke(req);
            if (a instanceof Timestamp && b instanceof Timestamp) {
                return new Timestamp[] { (Timestamp) a, (Timestamp) b };
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Continua el flujo del draft recuperado.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void OnContinueDraft(ActionEvent event) {
        if (recoveredDraftResp == null || recoveredFlowType == null) {
            hideRecoveryBanner();
            return;
        }

        DraftContainer.getInstance().setDraftResponse(recoveredDraftResp);
        DraftContainer.getInstance().setFlowType(recoveredFlowType);

        if ("DEVICE".equals(recoveredFlowType)) {
            
            if (recoveredDraftResp.getData() 
                instanceof EquipmentReservationDraft) {
                
                Session.getInstance()
                        .setCurrentEquipmentDraftId(
                        ((EquipmentReservationDraft) 
                        recoveredDraftResp.getData()).getIdDraft());
            }
            hideRecoveryBanner();
            try {
                App.setRoot("device_form_screen");
            } catch (IOException ignored) {
            }
        } else if ("AUDITORIUM".equals(recoveredFlowType)) {
            hideRecoveryBanner();
            try {
                App.setRoot("auditorium_form_screen");
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Descarta el draft recuperado tras confirmacion del usuario.
     *
     * @param event evento generado por la accion del usuario
     */
    @FXML
    private void OnDiscardDraft(ActionEvent event) {
        if (recoveredDraftResp == null || recoveredFlowType == null) {
            hideRecoveryBanner();
            return;
        }

        boolean confirm = PopUp.warning(
                "Confirmación",
                "Descartar reserva temporal",
                "¿Está seguro que desea descartar la reserva temporal activa?",
                "back_hand.png",
                2,
                3,
                "Descartar"
        );

        if (!confirm) {
            return;
        }

        int idClient 
                = Session.getInstance().getClient().getClient().getIdClient();

        if ("DEVICE".equals(recoveredFlowType)
                && recoveredDraftResp.getData() 
                instanceof EquipmentReservationDraft) {
            
            EquipmentReservationDraft d 
                    = (EquipmentReservationDraft) recoveredDraftResp.getData();
            
            EquipmentReservationDraftService
                    .discardEquipmentDraft(d.getIdDraft(), idClient);
            
            Session.getInstance().setCurrentEquipmentDraftId(0);
        } else if ("AUDITORIUM".equals(recoveredFlowType)
                && recoveredDraftResp.getData() 
                instanceof AuditoriumDraftRequest) {
            
            AuditoriumDraftRequest req 
                    = (AuditoriumDraftRequest) recoveredDraftResp.getData();
            
            AuditoriumDraftService.discardAuditoriumDraft(req);
        }

        DraftContainer.getInstance().clearAll();
        hideRecoveryBanner();
    }

    /**
     * Abre la pantalla de perfil del cliente.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToProfile(ActionEvent event) throws IOException {
        App.setRoot("profile_screen");
    }

    /**
     * Inicia el flujo de reserva de auditorio.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToAuditorium(ActionEvent event) throws IOException {
        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();

        Response resp = CalendarService.enterReservationsView(idClient);

        if (resp.isSuccess()) {
            DraftContainer.getInstance().setFlowType("AUDITORIUM");
            App.setRoot("device_schedule_screen");
        } else {
            PopUp.warning(
                    "Error de conexión",
                    "Verifique su conexión o intente nuevamente.",
                    "Es posible que el servicio esté temporalmente no "
                    + "disponible o que exista un problema con su conexión "
                    + "a internet.\n"
                    + "Por favor, intente nuevamente más tarde.",
                    "power_off.png",
                    1,
                    1,
                    "Aceptar"
            );
        }
    }

    /**
     * Inicia el flujo de reserva de equipos.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToDevices(ActionEvent event) throws IOException {
        int idClient = Session.getInstance()
                .getClient()
                .getClient()
                .getIdClient();

        Response resp = CalendarService.enterReservationsView(idClient);

        if (resp.isSuccess()) {
            DraftContainer.getInstance().setFlowType("DEVICE");
            App.setRoot("device_schedule_screen");
        } else {
            PopUp.warning(
                    "Error de conexión",
                    "Verifique su conexión o intente nuevamente.",
                    "Es posible que el servicio esté temporalmente no "
                    + "disponible o que exista un problema con su conexión a "
                    + "internet.\n"
                    + "Por favor, intente nuevamente más tarde.",
                    "power_off.png",
                    1,
                    1,
                    "Aceptar"
            );
        }
    }

    /**
     * Abre la pantalla con las reservas activas del cliente.
     *
     * @param event evento generado por la accion del usuario
     * @throws IOException si ocurre un error al cambiar de vista
     */
    @FXML
    private void GoToSchedule(ActionEvent event) throws IOException {
        App.setRoot("client_schedule_screen");
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
