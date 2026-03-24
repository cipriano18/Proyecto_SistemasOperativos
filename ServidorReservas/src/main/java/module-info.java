module project.servidorreservas {
    requires javafx.controls;
    requires javafx.fxml;
     requires java.sql;
    requires java.base;
    opens project.servidorreservas to javafx.fxml;
    exports project.servidorreservas;
    opens model to javafx.base; 
}
