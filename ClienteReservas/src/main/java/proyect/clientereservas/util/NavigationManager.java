package proyect.clientereservas.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import proyect.clientereservas.Main;
import java.io.IOException;

public class NavigationManager {

    private static NavigationManager instance;
    private Stage stage;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) instance = new NavigationManager();
        return instance;
    }

    public void setStage(Stage stage) { this.stage = stage; }

    public void navigateTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/proyect/clientereservas/view/" + fxmlName + ".fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                Main.class.getResource("/proyect/clientereservas/styles/main.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error navegando a " + fxmlName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public <T> T navigateAndGetController(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            Main.class.getResource("/proyect/clientereservas/view/" + fxmlName + ".fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            Main.class.getResource("/proyect/clientereservas/styles/main.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
        return loader.getController();
    }
}