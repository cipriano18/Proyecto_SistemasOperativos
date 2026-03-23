module proyect.clientereservas {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.base;

    opens proyect.clientereservas to javafx.fxml;
    opens proyect.clientereservas.controller to javafx.fxml;
    opens model;

    exports proyect.clientereservas;
    exports model;
}