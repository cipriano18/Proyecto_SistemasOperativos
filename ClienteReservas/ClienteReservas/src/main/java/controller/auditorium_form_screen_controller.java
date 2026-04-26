package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import draft.AuditoriumDraft;
import dto.AuditoriumDraftRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Reservation;
import service.AuditoriumDraftService;
import service.Response;
import utils.DraftContainer;

public class auditorium_form_screen_controller implements Initializable {

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

    private AuditoriumDraftRequest currentDraft;
    private Reservation selectedReservation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Response draftResponse = DraftContainer.getInstance().getDraftResponse();
        if (draftResponse != null && draftResponse.isSuccess() && draftResponse.getData() instanceof AuditoriumDraftRequest) {
            currentDraft = (AuditoriumDraftRequest) draftResponse.getData();
            selectedReservation = currentDraft.getReservation();
        } else {
            selectedReservation = DraftContainer.getInstance().getSelectedReservation();
        }

        if (selectedReservation == null) {
            PopUp.warning(
                    "Reserva inválida",
                    "No se encontró la sección seleccionada",
                    "Seleccione una fecha y sección antes de continuar con la reserva de auditorio.",
                    "error.png",
                    1,
                    "Aceptar"
            );

            try {
                App.setRoot("home_screen");
            } catch (IOException ignored) {
            }
            return;
        }

        lbl_selected_date.setText(formatDate(selectedReservation.getReservationDate()));
        lbl_selected_section.setText(formatSection(selectedReservation.getIdSection()));

        if (currentDraft != null && currentDraft.getAuditoriumDraft() != null) {
            AuditoriumDraft draft = currentDraft.getAuditoriumDraft();
            txt_event_name.setText(draft.getEventName() != null ? draft.getEventName() : "");
            txt_attendees_count.setText(String.valueOf(draft.getAttendeesCount()));
            txt_observations.setText(draft.getObservations() != null ? draft.getObservations() : "");
        }
    }

    @FXML
    private void saveAuditoriumDraft(ActionEvent event) {
        String eventName = txt_event_name.getText() != null ? txt_event_name.getText().trim() : "";
        String attendeesText = txt_attendees_count.getText() != null ? txt_attendees_count.getText().trim() : "";
        String observations = txt_observations.getText() != null ? txt_observations.getText().trim() : "";

        if (eventName.isEmpty()) {
            PopUp.warning(
                    "Datos incompletos",
                    "El nombre del evento es obligatorio",
                    "Ingrese el nombre del evento para crear la reserva temporal de auditorio.",
                    "error.png",
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
                    "Aceptar"
            );
            return;
        }

        AuditoriumDraft auditoriumDraft = new AuditoriumDraft();
        auditoriumDraft.setEventName(eventName);
        auditoriumDraft.setAttendeesCount(attendeesCount);
        auditoriumDraft.setObservations(observations);

        if (currentDraft == null || currentDraft.getIdDraft() <= 0) {
            PopUp.warning(
                    "Reserva temporal no disponible",
                    "No se encontró el draft de auditorio",
                    "Intente seleccionar nuevamente la fecha y sección desde el calendario.",
                    "error.png",
                    1,
                    "Aceptar"
            );
            return;
        }

        AuditoriumDraftRequest request = new AuditoriumDraftRequest();
        request.setIdDraft(currentDraft.getIdDraft());
        request.setIdClient(currentDraft.getIdClient());
        request.setReservation(selectedReservation);
        request.setAuditoriumDraft(auditoriumDraft);
        request.setEquipmentList(new ArrayList<>());

        Response response = AuditoriumDraftService.updateAuditoriumDraft(request);

        if (response != null && response.isSuccess()) {
            DraftContainer.getInstance().setDraftResponse(response);

            PopUp.warning(
                    "Reserva temporal actualizada",
                    "Los datos del auditorio fueron guardados",
                    "La reserva temporal de auditorio fue actualizada correctamente.",
                    "check_circle.png",
                    1,
                    "Aceptar"
            );

            try {
                App.setRoot("home_screen");
            } catch (IOException e) {
                PopUp.warning(
                        "Error de navegación",
                        "No se pudo volver al inicio",
                        "La reserva se creó, pero ocurrió un problema al cambiar de pantalla.",
                        "error.png",
                        1,
                        "Aceptar"
                );
            }
            return;
        }

        String msg = response != null ? response.getMessage() : "No se pudo conectar con el servidor";
        PopUp.warning(
                "No se pudo crear la reserva",
                "Ocurrió un problema al guardar el draft",
                msg,
                "error.png",
                1,
                "Aceptar"
        );
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        DraftContainer.getInstance().clearAll();
        App.setRoot("home_screen");
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
}
