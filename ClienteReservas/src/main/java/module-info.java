module proyect.clientereservas {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens proyect.clientereservas to javafx.fxml;
    exports proyect.clientereservas;
}
