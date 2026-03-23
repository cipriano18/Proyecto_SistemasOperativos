package proyect.clientereservas;

import javafx.application.Application;
import javafx.stage.Stage;
import proyect.clientereservas.network.SocketManager;
import proyect.clientereservas.util.NavigationManager;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sistema de Reservas — Auditorio");
        stage.setResizable(false);

        NavigationManager.getInstance().setStage(stage);

        try {
            SocketManager.getInstance().connect();
            System.out.println("Conectado al servidor.");
        } catch (IOException e) {
            System.out.println("Servidor no disponible: " + e.getMessage());
        }

        NavigationManager.getInstance().navigateTo("login");
    }

    @Override
    public void stop() {
        SocketManager.getInstance().disconnect();
    }

    public static void main(String[] args) {
        launch();
    }
}