package com.example.demomvc;
import com.example.demomvc.Controller.CincuentazoWelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // 1.
            FXMLLoader fxmlLoader = new FXMLLoader(
                    CincuentazoWelcomeController.class.getResource("PrimeraVentana50zo.fxml")
            );

            Parent root = fxmlLoader.load();
            // 2.
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("50zo - Cincuentazo (Menú Principal)");
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}