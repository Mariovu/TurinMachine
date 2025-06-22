package com.example.turinmachine;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class TuringAnimation {
    private final List<String> pasos;
    private int currentStep = 0;
    private Timeline timeline;
    private HBox tapeBox;
    private Text stateText;
    private Label stepLabel;

    public TuringAnimation(List<String> pasos) {
        this.pasos = pasos;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animación Máquina de Turing");

        // Panel principal
        VBox mainPane = new VBox(20);
        mainPane.setPadding(new Insets(20));
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setStyle("-fx-background-color: #f0f8ff;");

        // Display de estado
        stateText = new Text("Estado: q0");
        stateText.setFont(Font.font(16));
        HBox stateBox = new HBox(stateText);
        stateBox.setAlignment(Pos.CENTER);

        // Display de pasos
        stepLabel = new Label("Paso: 1/" + pasos.size());
        stepLabel.setFont(Font.font(14));
        HBox stepBox = new HBox(stepLabel);
        stepBox.setAlignment(Pos.CENTER);

        // Cinta
        tapeBox = new HBox(5);
        tapeBox.setAlignment(Pos.CENTER);
        updateTapeDisplay(pasos.get(0));

        // Controles
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        Button prevBtn = new Button("Anterior");
        prevBtn.setOnAction(e -> showPreviousStep());

        Button nextBtn = new Button("Siguiente");
        nextBtn.setOnAction(e -> showNextStep());

        Button playBtn = new Button("Reproducir");
        playBtn.setOnAction(e -> playAnimation());

        Button pauseBtn = new Button("Pausa");
        pauseBtn.setOnAction(e -> pauseAnimation());

        Slider speedSlider = new Slider(0.5, 5, 1);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1);
        speedSlider.setBlockIncrement(0.5);
        speedSlider.setPrefWidth(200);

        controls.getChildren().addAll(prevBtn, playBtn, pauseBtn, nextBtn, new Label("Velocidad:"), speedSlider);

        // Resultado final
        Text resultText = new Text();
        resultText.setFont(Font.font(16));
        resultText.setFill(Color.DARKGREEN);
        String result = pasos.get(pasos.size() - 1);
        if (result.equals("Aceptada") || result.equals("Rechazada")) {
            resultText.setText("Resultado: " + result);
        }

        mainPane.getChildren().addAll(stateBox, stepBox, tapeBox, controls, resultText);

        // Configurar timeline para animación automática
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2), e -> showNextStep()));
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            timeline.setRate(newVal.doubleValue());
        });

        Scene scene = new Scene(mainPane, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateTapeDisplay(String paso) {
        tapeBox.getChildren().clear();

        // Extraer estado y posición de la cabeza
        String[] parts = paso.split("\\[|\\]");
        String estado = "";
        String cinta = paso;
        int headPos = -1;

        if (parts.length >= 3) {
            estado = parts[1];
            headPos = parts[0].length();
            cinta = parts[0] + parts[2];
        }

        stateText.setText("Estado: " + estado);

        // Crear representación visual de la cinta
        for (int i = 0; i < cinta.length(); i++) {
            VBox cell = new VBox();
            cell.setAlignment(Pos.CENTER);

            // Símbolo
            Text symbol = new Text(String.valueOf(cinta.charAt(i)));
            symbol.setFont(Font.font(14));

            // Celda
            Rectangle rect = new Rectangle(40, 40);
            rect.setFill(Color.WHITE);
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(1);

            // Indicador de cabeza
            if (i == headPos) {
                Rectangle headIndicator = new Rectangle(10, 10);
                headIndicator.setFill(Color.RED);
                cell.getChildren().add(headIndicator);
            }

            cell.getChildren().addAll(new StackPane(rect, symbol));
            tapeBox.getChildren().add(cell);
        }
    }

    private void showNextStep() {
        if (currentStep < pasos.size() - 1) {
            currentStep++;
            updateTapeDisplay(pasos.get(currentStep));
            stepLabel.setText("Paso: " + (currentStep + 1) + "/" + pasos.size());
        } else {
            pauseAnimation();
        }
    }

    private void showPreviousStep() {
        if (currentStep > 0) {
            currentStep--;
            updateTapeDisplay(pasos.get(currentStep));
            stepLabel.setText("Paso: " + (currentStep + 1) + "/" + pasos.size());
        }
    }

    private void playAnimation() {
        timeline.play();
    }

    private void pauseAnimation() {
        timeline.pause();
    }
}