package com.example.demomvc.Controller;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CincuentazoGameController implements Initializable {
    private int numberOfBots;
    private MediaPlayer ambientMusicPlayer;
    @FXML
    private AnchorPane gameRootPane;
    @FXML
    private AnchorPane gameElementsContainer;
    @FXML
    private ToggleButton btnPause;
    @FXML
    private Label lblBots;
    // Elementos del MENÚ DE PAUSA
    @FXML
    private StackPane pauseMenuContainer;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Button btnResumeGame;
    @FXML
    private Button btnBackToMenu;
    @FXML
    private Label lblTimer;
    private AnimationTimer gameTimer;
    private long startTime;
    private long pausedTime = 0;



    // 1. INICIALIZACIÓN Y CONEXIÓN DE EVENTOS


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setupGameTimer();
        startGameTimer();

        // --- Conexión del Botón de PAUSA PRINCIPAL ---
        if (lblTimer == null) {
            System.err.println("ERROR FATAL: lblTimer no está conectado al FXML. El contador no funcionará.");
        } else {
            lblTimer.setText("00:00"); // Establecer un valor inicial visible
        }
        if (btnPause != null) {
            btnPause.setOnAction(e -> togglePauseMenu(true)); // Pausar y mostrar el menú
        }

        // --- Conexión del Menú de Pausa ---
        if (btnResumeGame != null) {
            btnResumeGame.setOnAction(e -> togglePauseMenu(false));
        }
        if (btnBackToMenu != null) {
            btnBackToMenu.setOnAction(this::handleBackToMenu);
        }

        if (volumeSlider != null) {
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (ambientMusicPlayer != null) {
                    ambientMusicPlayer.setVolume(newValue.doubleValue() / 100.0);
                }
            });
            volumeSlider.setValue(35);
        }
        if (pauseMenuContainer != null) {
            pauseMenuContainer.setVisible(false);
        }

    }
    private void startGameTimer() {
        pausedTime = 0;
        startTime = System.nanoTime();
        gameTimer.start();
    }

    // 2. LÓGICA DE PAUSA Y REANUDACIÓN


    public void togglePauseMenu(boolean isPausing) {
        if (pauseMenuContainer == null || gameElementsContainer == null) return;

        if (isPausing) {
            gameElementsContainer.setEffect(new GaussianBlur(15));
            if (btnPause != null) {
                btnPause.setVisible(false);
            }
            gameTimer.stop();
            pausedTime = System.nanoTime() - startTime;
            pauseMenuContainer.setVisible(true);

        } else {

            gameElementsContainer.setEffect(null);
            if (btnPause != null) {
                btnPause.setVisible(true);
                btnPause.setSelected(false);
            }
            pauseMenuContainer.setVisible(false);
            startTime = System.nanoTime() - pausedTime;
            gameTimer.start();
        }
    }
    private void handleBackToMenu(javafx.event.ActionEvent event) {
        try {
            if (ambientMusicPlayer != null) {
                ambientMusicPlayer.stop();
                ambientMusicPlayer.dispose();
            }

            Node source = (Node) event.getSource();
            Scene currentScene = source.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                try {
                    if (gameTimer != null) {
                        gameTimer.stop(); // Detiene el bucle del AnimationTimer
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demomvc/Controller/PrimeraVentana50zo.fxml"));
                    Parent root = loader.load();
                    Scene nextScene = new Scene(root);
                    stage.setScene(nextScene);
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();

                    stage.show();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 3. MÉTODOS DE RECEPCIÓN DE DATOS Y LÓGICA BÁSICA

    public void setAmbientMusicPlayer(MediaPlayer player) {
        this.ambientMusicPlayer = player;
    }

    public void setGameData(int numBots) {
        this.numberOfBots = numBots;
        if (lblBots != null) {
            lblBots.setText("Oponentes: " + this.numberOfBots + " Bots");
        }
    }

    private void setupGameTimer() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // El timer se detiene solo si llamamos a gameTimer.stop().
                // Por lo tanto, no necesitamos la bandera aquí.

                long elapsedNanos = now - startTime; // Tiempo total real transcurrido
                long elapsedSeconds = elapsedNanos / 1_000_000_000;

                long minutes = elapsedSeconds / 60;
                long seconds = elapsedSeconds % 60;

                String timeFormatted = String.format("%02d:%02d", minutes, seconds);

                if (lblTimer != null) {
                    lblTimer.setText(timeFormatted);
                }
            }
        };
    }
}