package com.example.proyectofacturacion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    private List<Stage> secondaryStages = new ArrayList<>();  // Lista para almacenar las ventanas adicionales

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
            stage.setResizable(false);

            // Añadir la nueva ventana a la lista
            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCrearProveedor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-proveedor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear proveedor");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCrearArticulo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-articulo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear articulo");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTipoIVA(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tipo-IVA.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Consultar Tipo IVA");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para cerrar todas las ventanas secundarias
    public void closeAllSecondaryWindows() {
        for (Stage stage : secondaryStages) {
            stage.close();
        }
    }
}
