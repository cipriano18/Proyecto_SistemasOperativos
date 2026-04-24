package utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 *
 * @author Makin Artavia
 */
public class Animations {

    public void moveNode(Node node, int desplazamientoX, int desplazamientoY, boolean haciaDerecha, boolean haciaArriba) {

        Timeline timeline = new Timeline();

        int direccionX = haciaDerecha ? 1 : -1;
        int direccionY = haciaArriba ? -1 : 1;

        for (int i = 0; i < Math.max(desplazamientoX, desplazamientoY); i++) {
            final int count = i;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(i * 15),
                    event -> {
                        if (count < desplazamientoX) {
                            node.setTranslateX(node.getTranslateX() + direccionX);
                        }
                        if (count < desplazamientoY) {
                            node.setTranslateY(node.getTranslateY() + direccionY);
                        }
                    }
            ));
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    public void appear(Node node, int pasos, double duracionSegundos, double delayInicialMs) {

        node.setOpacity(0);

        Timeline timeline = new Timeline();

        double intervalo = (duracionSegundos * 1000) / pasos;

        for (int i = 0; i <= pasos; i++) {
            final int paso = i;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(delayInicialMs + (paso * intervalo)),
                    event -> node.setOpacity((double) paso / pasos)
            ));
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    public void breathEffect(Node node, Color baseColor, double duracionSegundos) {

        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(20);
        shadow.setChoke(0.5);

        node.setEffect(shadow);

        Timeline timeline = new Timeline();

        int steps = 100;
        double interval = (duracionSegundos * 1000) / steps;

        for (int i = 0; i < steps; i++) {
            final int step = i;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(step * interval),
                    event -> {
                        double opacity = 0.3 * Math.sin((step / (double) steps) * 2 * Math.PI) + 0.5;

                        shadow.setColor(Color.rgb(
                                (int) (baseColor.getRed() * 255),
                                (int) (baseColor.getGreen() * 255),
                                (int) (baseColor.getBlue() * 255),
                                opacity
                        ));
                    }
            ));
        }

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void breathOpacity(Node node, int pasos, double duracionSegundos, double opacidadMinima, double opacidadMaxima) {

        Timeline timeline = new Timeline();

        double intervalo = (duracionSegundos * 1000) / pasos;
        double rango = opacidadMaxima - opacidadMinima;

        for (int i = 0; i <= pasos; i++) {
            final int paso = i;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(paso * intervalo),
                    event -> {
                        double progreso = (double) paso / pasos;
                        double opacidad = opacidadMinima + (rango * progreso);
                        node.setOpacity(opacidad);
                    }
            ));
        }

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    public void expandWidth(Region node, double aumento, int pasos, double duracionSegundos) {

        double anchoFinal = node.getWidth();
        double anchoInicial = anchoFinal - aumento;

        node.setPrefWidth(anchoInicial);

        Timeline timeline = new Timeline();

        double intervalo = (duracionSegundos * 1000) / pasos;

        for (int i = 0; i <= pasos; i++) {
            final int paso = i;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(paso * intervalo),
                    event -> {
                        double progreso = (double) paso / pasos;
                        double nuevoAncho = anchoInicial + (aumento * progreso);
                        node.setPrefWidth(nuevoAncho);
                    }
            ));
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    public void growLabel(Label label, double reduccion, int pasos, double duracionSegundos) {

        Font fontOriginal = label.getFont();
        double tamañoOriginal = fontOriginal.getSize();
        double tamañoInicial = tamañoOriginal - reduccion;

        label.setFont(new Font(fontOriginal.getName(), tamañoInicial));

        Timeline timeline = new Timeline();

        double intervalo = (duracionSegundos * 1000) / pasos;

        for (int i = 0; i <= pasos; i++) {
            final int paso = i;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(paso * intervalo),
                    event -> {
                        double progreso = (double) paso / pasos;
                        double tamañoActual = tamañoInicial + (reduccion * progreso);

                        label.setFont(new Font(fontOriginal.getName(), tamañoActual));
                    }
            ));
        }

        timeline.setCycleCount(1);
        timeline.play();
    }

    public void typeWriter(Label label, double delayInicialMs, double intervaloMilisegundos) {
        String textoCompleto = label.getText();
        label.setText("");

        Timeline timeline = new Timeline();

        for (int i = 0; i < textoCompleto.length(); i++) {
            final int index = i + 1;

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(delayInicialMs + (intervaloMilisegundos * index)),
                    event -> label.setText(textoCompleto.substring(0, index))
            ));
        }

        timeline.setCycleCount(1);
        timeline.play();
    }
}
