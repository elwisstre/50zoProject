package com.example.demomvc.Controller;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class CincuentazoGameController implements Initializable {

    private int numberOfBots; // Variable para almacenar el dato enviado
    public void setGameData(int numBots) {
        this.numberOfBots = numBots;
        System.out.println("CincuentazoGameController: Juego cargado contra " + this.numberOfBots + " bots.");
        //
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aquí se inicializa la mesa, se reparten cartas, y se inicia la música del juego.
    }


}
