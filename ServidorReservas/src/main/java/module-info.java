module proyect.servidorreservas {
    requires javafx.controls;
    requires javafx.fxml;

    opens proyect.servidorreservas to javafx.fxml;
    exports proyect.servidorreservas;
}
