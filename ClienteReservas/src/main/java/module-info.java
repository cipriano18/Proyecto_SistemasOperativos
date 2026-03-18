module proyect.clientereservas {
    requires javafx.controls;
    requires javafx.fxml;

    opens proyect.clientereservas to javafx.fxml;
    exports proyect.clientereservas;
}
