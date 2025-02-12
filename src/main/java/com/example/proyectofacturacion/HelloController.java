package com.example.proyectofacturacion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private void handleCrearCliente(ActionEvent event) {
        try {
            // Cargar el archivo FXML de la vista Crear Cliente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-cliente.fxml"));
            Parent root = loader.load();

            // Crear una nueva escena y ventana
            Stage stage = new Stage();
            stage.setTitle("Crear Cliente");
            stage.setScene(new Scene(root, 600, 400));

            // No se puede expandir la pantalla:
            stage.setResizable(false);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCrearProveedor(ActionEvent event) {
        try {
            // Cargar el archivo FXML de la vista Crear Cliente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-proveedor.fxml"));
            Parent root = loader.load();

            // Crear una nueva escena y ventana
            Stage stage = new Stage();
            stage.setTitle("Crear proveedor");
            stage.setScene(new Scene(root, 600, 400));

            // No se puede expandir la pantalla:
            stage.setResizable(false);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCrearArticulo(ActionEvent event) {
        try {
            // Cargar el archivo FXML de la vista Crear Cliente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-articulo.fxml"));
            Parent root = loader.load();

            // Crear una nueva escena y ventana
            Stage stage = new Stage();
            stage.setTitle("Crear articulo");
            stage.setScene(new Scene(root, 600, 400));

            // No se puede expandir la pantalla:
            stage.setResizable(false);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
