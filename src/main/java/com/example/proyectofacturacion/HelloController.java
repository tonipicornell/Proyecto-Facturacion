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
    private void handleModificarCliente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modificar-cliente.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modificar Cliente");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModificarProveedor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modificar-proveedor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modificar Proveedor");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModificarArticulo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modificar-articulo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Modificar Articulo");
            stage.setScene(new Scene(root, 700, 700));
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

    @FXML
    private void handleFichaCliente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ficha-cliente.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ficha Clientes ");
            stage.setScene(new Scene(root, 650, 450));

            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFichaProveedor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ficha-proveedor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ficha Proveedores ");
            stage.setScene(new Scene(root, 650, 450));

            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFichaArticulos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ficha-articulo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ficha Articulos ");
            stage.setScene(new Scene(root, 670, 450));

            stage.setResizable(false);

            secondaryStages.add(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCrearFactura(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-factura.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear Factura ");
            stage.setScene(new Scene(root, 850, 750));

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