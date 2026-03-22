package proyect.clientereservas;

import javafx.application.Application;
import javafx.stage.Stage;
import proyect.clientereservas.network.SocketManager;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // vacío por ahora, solo probamos conexión
    }

    @Override
    public void stop() {
        SocketManager.getInstance().disconnect();
        System.out.println("Aplicación cerrada.");
    }

    public static void main(String[] args) {
        try {
            SocketManager.getInstance().connect();
            System.out.println("Conexión exitosa al servidor!");
        } catch (IOException e) {
            System.out.println("Servidor no disponible: " + e.getMessage());
        }

        launch();
    }
}