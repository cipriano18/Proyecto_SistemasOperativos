/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.scene.media.AudioClip;

public class PopUp {

    private static final double WIDTH = 650;

    public static void notification(String title, String info, String icon) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);
        VBox root = new VBox();
        root.setPadding(new Insets(0));
        root.setPrefWidth(Region.USE_COMPUTED_SIZE);
        root.getChildren().add(createHeader(title, info, icon));

        Scene scene = new Scene(root);
        addStyles(scene);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        try {
            String soundPath = PopUp.class.getResource("/sounds/notification.mp3").toExternalForm();
            AudioClip sound = new AudioClip(soundPath);
            sound.play();
        } catch (Exception e) {
            System.out.println("No se pudo reproducir el sonido");
        }

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> stage.close());
        delay.play();
    }

    public static boolean warning(
            String title,
            String titleInfo,
            String body,
            String icon,
            int buttons,
            String buttonConfirmText
    ) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);

        AtomicBoolean result = new AtomicBoolean(false);

        VBox root = new VBox();
        root.setMinWidth(WIDTH);
        root.setMaxWidth(WIDTH);
        root.setMinHeight(300);
        root.setMaxHeight(300);

        HBox header = createHeader(title, titleInfo, icon);
        HBox bodyContent = createBody(stage, result, body, buttons, buttonConfirmText);

        root.getChildren().addAll(header, bodyContent);

        Scene scene = new Scene(root);
        addStyles(scene);

        stage.setScene(scene);
        try {
            String soundPath = PopUp.class.getResource("/sounds/warning.mp3").toExternalForm();
            AudioClip sound = new AudioClip(soundPath);
            sound.play();
        } catch (Exception e) {
            System.out.println("No se pudo reproducir el sonido");
        }
        stage.showAndWait();

        return result.get();
    }

    private static HBox createHeader(String title, String info, String icon) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(105);
        header.setPrefWidth(Region.USE_COMPUTED_SIZE);
        header.getStyleClass().add("hero-panel");

        ImageView iconView = new ImageView();
        iconView.setFitHeight(70);
        iconView.setFitWidth(70);
        iconView.setPreserveRatio(true);
        iconView.setPickOnBounds(true);

        try {
            Image image = new Image(PopUp.class.getResourceAsStream("/assets/" + icon));
            iconView.setImage(image);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono: " + icon);
        }

        HBox.setMargin(iconView, new Insets(0, 0, 0, 20));

        VBox infoBox = new VBox();
        infoBox.setSpacing(5);
        HBox.setMargin(infoBox, new Insets(20, 20, 20, 5));

        Label lblTitle = new Label(title);
        lblTitle.setMinWidth(300);
        lblTitle.getStyleClass().add("hero-title");
        lblTitle.setStyle("-fx-text-fill: white;");

        Label lblInfo = new Label(info);
        lblInfo.setMinWidth(300);
        lblInfo.setWrapText(true);
        lblInfo.getStyleClass().add("hero-subtitle");

        infoBox.getChildren().addAll(lblTitle, lblInfo);
        header.getChildren().addAll(iconView, infoBox);

        return header;
    }

    private static HBox createBody(
            Stage stage,
            AtomicBoolean result,
            String body,
            int buttons,
            String buttonConfirmText
    ) {
        HBox bodyRoot = new HBox();
        VBox.setVgrow(bodyRoot, Priority.ALWAYS);

        HBox leftBox = new HBox();
        leftBox.setAlignment(Pos.BOTTOM_LEFT);
        HBox.setMargin(leftBox, new Insets(20, 0, 20, 20));

        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox infoBox = new VBox();
        VBox.setVgrow(infoBox, Priority.ALWAYS);
        infoBox.setPadding(new Insets(0, 10, 0, 0));

        Label lblInformation = new Label(body);
        lblInformation.setMinWidth(300);
        lblInformation.setWrapText(true);
        lblInformation.getStyleClass().add("form-subtitle");

        infoBox.getChildren().add(lblInformation);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setMinWidth(163);
        buttonBox.setPrefWidth(630);
        buttonBox.setPrefHeight(50);
        buttonBox.setSpacing(5);
        buttonBox.setPadding(new Insets(0, 20, 0, 0));

        if (buttons == 2) {
            Button btnCancel = createButton("Cancelar");
            btnCancel.setPrefWidth(67);
            btnCancel.setOnAction(e -> {
                result.set(false);
                stage.close();
            });
            HBox.setHgrow(btnCancel, Priority.ALWAYS);
            buttonBox.getChildren().add(btnCancel);
        }

        Button btnConfirm = createButton(buttonConfirmText);
        btnConfirm.setPrefWidth(89);
        btnConfirm.setOnAction(e -> {
            result.set(true);
            stage.close();
        });

        buttonBox.getChildren().add(btnConfirm);

        contentBox.getChildren().addAll(infoBox, buttonBox);
        leftBox.getChildren().add(contentBox);
        bodyRoot.getChildren().add(leftBox);

        return bodyRoot;
    }

    private static Button createButton(String text) {
        Button button = new Button();
        button.setPrefHeight(44);
        button.setMinWidth(90);
        button.setStyle("-fx-background-radius: 0; -fx-border-radius: 0;");
        button.getStyleClass().add("hero-panel");

        Label label = new Label(text);
        label.getStyleClass().add("hero-subtitle");

        button.setGraphic(label);

        return button;
    }

    private static void addStyles(Scene scene) {
        try {
            scene.getStylesheets().add(PopUp.class.getResource("/styles/main.css").toExternalForm());
            scene.getStylesheets().add(PopUp.class.getResource("/styles/text.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("No se pudieron cargar los estilos del PopUp.");
        }
    }
}
