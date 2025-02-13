package com.example.proyectofacturacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private Stage primaryStage;  // Para hacer referencia a la ventana principal

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;  // Guardamos la ventana principal
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("pantalla-inicial.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 750);
        stage.setTitle("Facturación");
        stage.setScene(scene);

        // Agregar un manejador para el cierre de la ventana principal
        stage.setOnCloseRequest(event -> {
            System.exit(0); // Esto cierra toda la aplicación
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
