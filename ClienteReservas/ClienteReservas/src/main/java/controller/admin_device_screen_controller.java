/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.auditorio.clientereservas.App;
import components.PopUp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.Equipment;
import service.EquipmentService;
import service.Response;
import components.DeviceFormDialog;
import components.PopUp;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
/**
 * FXML Controller class
 *
 * @author Alvaro Artavia
 */
public class admin_device_screen_controller implements Initializable {

    @FXML
    private Button btn_goback;
    @FXML
    private VBox vb_info;
    @FXML
    private ImageView img_logo;
    @FXML
    private TextField chb_selected_device;
    @FXML
    private Button btn_delete;
    @FXML
    private Button btn_edit;
    @FXML
    private Button btn_add;
    @FXML
    private TableView<Equipment> tbl_device_list;
    @FXML
    private TableColumn<Equipment, String> tc_name;
    @FXML
    private TableColumn<Equipment, Integer> tc_quantity;
    private Equipment selectedEquipment;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_quantity.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        loadEquipmentTable();
    }    

    @FXML
    private void GoToHome(ActionEvent event) throws IOException{
        App.setRoot("admin_home_screen");
    }

    @FXML
private void DeleteDevice(ActionEvent event) {

    if (selectedEquipment == null) {
        PopUp.warning(
                "Aviso",
                "Seleccione un equipo",
                "Debe seleccionar un equipo de la tabla antes de eliminar.",
                "back_hand.png",
                1,
                "Aceptar"
        );
        return;
    }

    boolean confirm = PopUp.warning(
            "Confirmación",
            "Eliminar equipo",
            "¿Está seguro que desea eliminar el equipo seleccionado?",
            "back_hand.png",
            2,
            "Eliminar"
    );

    if (!confirm) {
        return;
    }

    Response resp = EquipmentService.deleteEquipment(selectedEquipment);

    if (resp != null && resp.isSuccess()) {
        PopUp.notification(
                "Equipo eliminado",
                "El equipo se eliminó correctamente.",
                "check_circle.png"
        );

        selectedEquipment = null;
        chb_selected_device.clear();
        loadEquipmentTable();

    } else {
        PopUp.warning(
                "Error",
                "No se pudo eliminar el equipo",
                resp != null ? resp.getMessage() : "No se pudo conectar con el servidor.",
                "dangerous.png",
                1,
                "Aceptar"
        );
    }
}

   @FXML
private void EditDevice(ActionEvent event) {

    if (selectedEquipment == null) {
        PopUp.warning(
                "Aviso",
                "Seleccione un equipo",
                "Debe seleccionar un equipo de la tabla antes de editar.",
                "back_hand.png",
                1,
                "Aceptar"
        );
        return;
    }

    DeviceFormDialog dialog = new DeviceFormDialog(
            "Editar Equipo",
            "Modifique los datos del equipo seleccionado.",
            selectedEquipment.getName(),
            selectedEquipment.getTotalQuantity(),
            "Guardar",
            (name, quantity) -> {

                selectedEquipment.setName(name);
                selectedEquipment.setTotalQuantity(quantity);

                Response resp = EquipmentService.updateEquipment(selectedEquipment);

                if (resp != null && resp.isSuccess()) {

                    PopUp.notification(
                            "Equipo actualizado",
                            "El equipo se actualizó correctamente.",
                            "check_circle.png"
                    );

                    chb_selected_device.clear();
                    selectedEquipment = null;
                    loadEquipmentTable();

                } else {
                    PopUp.warning(
                            "Error",
                            "No se pudo actualizar el equipo",
                            resp != null ? resp.getMessage() : "No se pudo conectar con el servidor.",
                            "dangerous.png",
                            1,
                            "Aceptar"
                    );
                }
            }
    );

    dialog.show();
}

    @FXML
    private void AddDevice(ActionEvent event) {

    DeviceFormDialog dialog = new DeviceFormDialog(
            "Nuevo Equipo",
            "Ingrese los datos para registrar un nuevo equipo.",
            null,
            null,
            "Agregar",
            (name, quantity) -> {

                Equipment equipment = new Equipment();
                equipment.setName(name);
                equipment.setTotalQuantity(quantity);

                Response resp = EquipmentService.createEquipment(equipment);

                if (resp != null && resp.isSuccess()) {

                    PopUp.notification(
                            "Equipo creado",
                            "El equipo se registró correctamente.",
                            "check_circle.png"
                    );

  loadEquipmentTable();
                } else {
                    PopUp.warning(
                            "Error",
                            "No se pudo crear el equipo",
                            resp != null ? resp.getMessage() : "No se pudo conectar con el servidor.",
                            "dangerous.png",
                            1,
                            "Aceptar"
                    );
                }
            }
    );

    dialog.show();
}
@FXML
private void GetSelectedDevice(MouseEvent event) {
    selectedEquipment = tbl_device_list.getSelectionModel().getSelectedItem();

    if (selectedEquipment != null) {
        chb_selected_device.setText(selectedEquipment.getName());
    }
}
     private void loadEquipmentTable() {

        Response resp = EquipmentService.getAllEquipment();

        if (resp != null && resp.isSuccess()) {

            List<Equipment> equipmentList = (List<Equipment>) resp.getData();

            ObservableList<Equipment> data =
                    FXCollections.observableArrayList(equipmentList);

            tbl_device_list.setItems(data);

        } else {
            PopUp.warning(
                    "Error",
                    "No se pudieron cargar los equipos",
                    resp != null ? resp.getMessage() : "No se pudo conectar con el servidor.",
                    "dangerous.png",
                    1,
                    "Aceptar"
            );
        }
    }
}
