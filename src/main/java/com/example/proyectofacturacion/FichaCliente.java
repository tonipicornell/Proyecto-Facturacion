package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FichaCliente {
    @FXML
    private TableView<Cliente> tableView;
    @FXML
    private TableColumn<Cliente, String> nombre_cliente;
    @FXML
    private TableColumn<Cliente, String> cif_cliente;
    @FXML
    private TableColumn<Cliente, String> email_cliente;
    @FXML
    private TableColumn<Cliente, String> telefono_cliente;


    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_cliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        cif_cliente.setCellValueFactory(new PropertyValueFactory<>("cifCliente"));
        email_cliente.setCellValueFactory(new PropertyValueFactory<>("emailCliente"));
        telefono_cliente.setCellValueFactory(new PropertyValueFactory<>("telCliente"));

        // Cargar los datos
        cargarDatos();
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Cliente clienteSeleccionado = tableView.getSelectionModel().getSelectedItem();
            if (clienteSeleccionado != null) {
                abrirDetalleCliente(clienteSeleccionado);
            }
        }
    }

    private void abrirDetalleCliente(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyectofacturacion/detalle-cliente.fxml"));
            Parent root = loader.load();

            DetalleCliente controller = loader.getController();
            controller.setCliente(cliente);

            Stage stage = new Stage();
            stage.setTitle("Detalle de Cliente: " + cliente.getNombreCliente());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Después de cerrar la ventana, actualizar la tabla
            cargarDatos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDatos() {
        clientesList.clear();
        String query = "SELECT * FROM clientes";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getString("nombreCliente"),
                        rs.getString("direccionCliente"),
                        rs.getString("cpCliente"),
                        rs.getString("poblacionCliente"),
                        rs.getString("provinciaCliente"),
                        rs.getString("paisCliente"),
                        rs.getString("cifCliente"),
                        rs.getString("telCliente"),
                        rs.getString("emailCliente"),
                        rs.getString("ibanCliente"),
                        rs.getDouble("riesgoCliente"),
                        rs.getDouble("descuentoCliente"),
                        rs.getString("observacionesCliente")
                );
                clientesList.add(cliente);
            }
            tableView.setItems(clientesList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí podrías mostrar un alert al usuario
        }
    }
}