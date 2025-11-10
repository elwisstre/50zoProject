module com.example.demomvc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.example.demomvc.Controller to javafx.fxml;
    opens com.example.demomvc to javafx.fxml;
    exports com.example.demomvc;
    exports com.example.demomvc.Controller;
}