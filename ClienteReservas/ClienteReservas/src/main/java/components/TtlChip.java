package components;

import java.sql.Timestamp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class TtlChip extends HBox {

    private final ImageView imgTimer = new ImageView();
    private final Label lblTime = new Label("--:--");
    private final Tooltip tooltip = new Tooltip("Tiempo restante");

    private Timeline timeline;
    private long createdAtMs;
    private long expiresAtMs;
    private long totalDurationMs = 1L;
    private Runnable onExpiredCallback;
    private boolean expired = false;

    public TtlChip() {
        getStyleClass().add("ttl-chip");

        setAlignment(Pos.CENTER);
        setMaxHeight(50);
        setMinWidth(100);
        setPadding(new Insets(0, 2, 0, 0));
        setSpacing(4);

        setStyle(
                "-fx-border-color: white;"
                + "-fx-border-radius: 50;"
                + "-fx-background-color: Transparent;"
                + "-fx-background-radius: 50;"
        );

        imgTimer.setFitWidth(34);
        imgTimer.setFitHeight(34);
        imgTimer.setPreserveRatio(true);
        imgTimer.setPickOnBounds(true);

        Image timerImage = new Image(
                getClass().getResource("/assets/timer.png").toExternalForm()
        );
        imgTimer.setImage(timerImage);

        lblTime.setAlignment(Pos.CENTER);
        lblTime.setPrefWidth(51);
        lblTime.setPrefHeight(30);
        lblTime.setTextFill(javafx.scene.paint.Color.WHITE);
        lblTime.setFont(Font.font(20));

        Tooltip.install(this, tooltip);

        getChildren().addAll(imgTimer, lblTime);
    }

    public void start(Timestamp createdAt, Timestamp expiresAt) {
        if (createdAt == null || expiresAt == null) {
            return;
        }

        this.createdAtMs = createdAt.getTime();
        this.expiresAtMs = expiresAt.getTime();
        this.totalDurationMs = Math.max(1L, expiresAtMs - createdAtMs);
        this.expired = false;

        stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        tick();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    public void setOnExpired(Runnable callback) {
        this.onExpiredCallback = callback;
    }

    public void setCompact(boolean compact) {
        if (compact) {
            setMinWidth(85);
            setPadding(new Insets(0, 2, 0, 0));
            imgTimer.setFitWidth(28);
            imgTimer.setFitHeight(28);
            lblTime.setFont(Font.font(17));
            lblTime.setPrefWidth(48);
        } else {
            setMinWidth(100);
            setPadding(new Insets(0, 2, 0, 0));
            imgTimer.setFitWidth(34);
            imgTimer.setFitHeight(34);
            lblTime.setFont(Font.font(20));
            lblTime.setPrefWidth(51);
        }
    }

    private void tick() {
        long now = System.currentTimeMillis();
        long remaining = expiresAtMs - now;

        if (remaining <= 0) {
            updateLabels(0);

            if (!expired) {
                expired = true;
                stop();

                if (onExpiredCallback != null) {
                    Platform.runLater(onExpiredCallback);
                }
            }

            return;
        }

        updateLabels(remaining);
    }

    private void updateLabels(long remainingMs) {
        long totalSeconds = Math.max(0, remainingMs / 1000L);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        String text = String.format("%02d:%02d", minutes, seconds);

        lblTime.setText(text);
        tooltip.setText("Vence en " + text);
    }

    public boolean isExpired() {
        return expired;
    }

    public void setBlackStyle() {
        getStyleClass().remove("ttl-chip");
        setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 0;"
                + "-fx-border-radius: 0;"
                + "-fx-border-color: transparent;"
                + "-fx-effect: null;"
        );

        Image timerImage = new Image(
                getClass().getResource("/assets/timer_black.png").toExternalForm()
        );

        imgTimer.setImage(timerImage);

        lblTime.setTextFill(javafx.scene.paint.Color.BLACK);
    }
}
