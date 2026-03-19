module project.servidorreservas {
    requires javafx.controls;
    requires javafx.fxml;
     requires java.sql;
    opens project.servidorreservas to javafx.fxml;
    exports project.servidorreservas;
}
