package components;

import java.sql.Timestamp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TtlChip extends HBox {

    private final Label lblIcon = new Label("⏱"); // ⏱
    private final Label lblTime = new Label("--:--");
    private final ProgressBar pbRemaining = new ProgressBar(1.0);
    private final Button btnToggleEye = new Button("👁"); // 👁
    private final VBox vbTimeAndBar = new VBox();
    private final Tooltip tooltip = new Tooltip("Tiempo restante");

    private Timeline timeline;
    private long createdAtMs;
    private long expiresAtMs;
    private long totalDurationMs = 1L;
    private Runnable onExpiredCallback;
    private boolean expanded = true;
    private boolean expired = false;

    public TtlChip() {
        getStyleClass().add("ttl-chip");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(8);
        setPadding(new Insets(6, 10, 6, 10));

        lblIcon.getStyleClass().add("ttl-icon");
        lblTime.getStyleClass().add("ttl-time");
        pbRemaining.getStyleClass().add("ttl-bar");
        pbRemaining.setPrefWidth(120);
        pbRemaining.setPrefHeight(6);
        btnToggleEye.getStyleClass().add("ttl-eye-btn");
        btnToggleEye.setFocusTraversable(false);
        btnToggleEye.setOnAction(e -> toggleVisibility());

        vbTimeAndBar.setSpacing(2);
        vbTimeAndBar.setAlignment(Pos.CENTER_LEFT);
        vbTimeAndBar.getChildren().addAll(lblTime, pbRemaining);

        Tooltip.install(this, tooltip);

        getChildren().addAll(lblIcon, vbTimeAndBar, btnToggleEye);

        applyColorClass(1.0);
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
            pbRemaining.setPrefWidth(70);
            vbTimeAndBar.setSpacing(1);
            setPadding(new Insets(3, 6, 3, 6));
        } else {
            pbRemaining.setPrefWidth(120);
            vbTimeAndBar.setSpacing(2);
            setPadding(new Insets(6, 10, 6, 10));
        }
    }

    private void tick() {
        long now = System.currentTimeMillis();
        long remaining = expiresAtMs - now;

        if (remaining <= 0) {
            updateLabels(0);
            applyColorClass(0.0);
            if (!expired) {
                expired = true;
                stop();
                if (onExpiredCallback != null) {
                    Platform.runLater(onExpiredCallback);
                }
            }
            return;
        }

        double fraction = (double) remaining / (double) totalDurationMs;
        if (fraction > 1.0) fraction = 1.0;

        updateLabels(remaining);
        pbRemaining.setProgress(fraction);
        applyColorClass(fraction);
    }

    private void updateLabels(long remainingMs) {
        long totalSeconds = Math.max(0, remainingMs / 1000L);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String text = String.format("%02d:%02d", minutes, seconds);
        lblTime.setText(text);
        tooltip.setText("Vence en " + text);
    }

    private void applyColorClass(double fraction) {
        getStyleClass().removeAll("ttl-ok", "ttl-warn", "ttl-danger");
        if (fraction > 0.5) {
            getStyleClass().add("ttl-ok");
        } else if (fraction > 0.2) {
            getStyleClass().add("ttl-warn");
        } else {
            getStyleClass().add("ttl-danger");
        }
    }

    private void toggleVisibility() {
        expanded = !expanded;
        vbTimeAndBar.setVisible(expanded);
        vbTimeAndBar.setManaged(expanded);
        btnToggleEye.setText(expanded ? "👁" : "👁‍🗨");
    }

    public boolean isExpired() {
        return expired;
    }
}
