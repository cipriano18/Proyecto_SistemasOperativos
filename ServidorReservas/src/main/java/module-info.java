module project.servidorreservas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // EXPORTS (clave para que el test funcione)
    exports project.servidorreservas;
    exports model;
    exports controller;
    exports database;

    // OPENS (para JavaFX y reflexión)
    opens project.servidorreservas to javafx.fxml;
    opens controller to javafx.fxml;
    opens model to javafx.base;

    // OPCIONAL pero recomendado para tests
    opens database;
}