package com.example.demomvc.Controller;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import com.example.demomvc.Controller.CincuentazoGameController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Slider;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.application.Platform;
import javafx.scene.Scene;


public class CincuentazoWelcomeController implements Initializable {
    //
    @FXML private ToggleButton tglBot1;
    @FXML private ToggleButton tglBot2;
    @FXML private ToggleButton tglBot3;

    //
    @FXML private Button btnJugar;
    @FXML private Button btnSalir;
    private MediaPlayer mediaPlayer; // Este es el reproductor

    //
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // La música se inicializa aquí
        iniciarMusicaAmbiente();

        if (btnJugar != null) {
            btnJugar.setOnAction(this::handlePlayGame);
        }

        if (btnSalir != null) {
            btnSalir.setOnAction(this::handleExitGame);
        }

        if (tglBot1 != null) {
            tglBot1.setSelected(true);
        }

        //
        if (tglBot1 != null && tglBot2 != null && tglBot3 != null) {
            tglBot1.setOnAction(e -> {
                if (tglBot1.isSelected()) {
                    tglBot2.setSelected(false);
                    tglBot3.setSelected(false);
                } else {
                    tglBot1.setSelected(true);
                }
            });

            tglBot2.setOnAction(e -> {
                if (tglBot2.isSelected()) {
                    tglBot1.setSelected(false);
                    tglBot3.setSelected(false);
                } else {
                    tglBot2.setSelected(true);
                }
            });

            tglBot3.setOnAction(e -> {
                if (tglBot3.isSelected()) {
                    tglBot1.setSelected(false);
                    tglBot2.setSelected(false);
                } else {
                    tglBot3.setSelected(true);
                }
            });
        }

    }

    // ...

    private void iniciarMusicaAmbiente() {
        try {
            URL resource = getClass().getResource("/com/example/demomvc/JazzAmbiental.mp3");
            if (resource == null) {
                System.out.println("Error: Archivo de música no encontrado.");
                return;
            }

            Media media = new Media(resource.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.35);
            mediaPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNumBotsSeleccionados() {
        if (tglBot1.isSelected()) {
            return 1;
        } else if (tglBot2.isSelected()) {
            return 2;
        } else if (tglBot3.isSelected()) {
            return 3;
        }
        return 1;
    }
    private void loadGameScene(Stage stageToLoadInto, int numBots, MediaPlayer musicPlayer) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demomvc/SegundaVentana50zo.fxml"));
            Parent gameRoot = fxmlLoader.load();


            CincuentazoGameController gameController = fxmlLoader.getController();
            gameController.setGameData(numBots);


            gameController.setAmbientMusicPlayer(musicPlayer);

            // Aplicar la nueva escena al Stage que estaba oculto
            stageToLoadInto.setScene(new Scene(gameRoot));
            stageToLoadInto.setTitle("Cincuentazo - La Partida");
            stageToLoadInto.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void handlePlayGame(ActionEvent event) {
        int numBotsParaJuego = getNumBotsSeleccionados();
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = currentStage.getScene();


        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), currentScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Cuando el Fade-Out termina:
        fadeOut.setOnFinished(e -> {
            try {


                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demomvc/SegundaVentana50zo.fxml"));
                Parent gameRoot = fxmlLoader.load();


                CincuentazoGameController gameController = fxmlLoader.getController();
                gameController.setGameData(numBotsParaJuego);

                loadGameScene(currentStage, numBotsParaJuego, this.mediaPlayer);

                // Aplicar Fade-In
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), gameRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                currentStage.show();
                fadeIn.play();

            } catch (IOException ex) {
                System.err.println("Error al cargar la ventana de juego: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        fadeOut.play();
    }

    private void handleExitGame(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        Platform.exit();
        System.exit(0);
    }
}