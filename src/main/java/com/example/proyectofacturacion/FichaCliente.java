package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableColumn<Cliente, String> iban_cliente;
    @FXML
    private TableColumn<Cliente, String> email_cliente;
    @FXML
    private TableColumn<Cliente, String> telefono_cliente;
    @FXML
    private TableColumn<Cliente, Double> riesgo_cliente;
    @FXML
    private TableColumn<Cliente, Double> descuento_cliente;
    @FXML
    private TableColumn<Cliente, String> direccion_cliente;
    @FXML
    private TableColumn<Cliente, String> cp_cliente;
    @FXML
    private TableColumn<Cliente, String> poblacion_cliente;
    @FXML
    private TableColumn<Cliente, String> provincia_cliente;
    @FXML
    private TableColumn<Cliente, String> pais_cliente;
    @FXML
    private TableColumn<Cliente, String> observaciones_cliente;

    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_cliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        cif_cliente.setCellValueFactory(new PropertyValueFactory<>("cifCliente"));
        iban_cliente.setCellValueFactory(new PropertyValueFactory<>("ibanCliente"));
        email_cliente.setCellValueFactory(new PropertyValueFactory<>("emailCliente"));
        telefono_cliente.setCellValueFactory(new PropertyValueFactory<>("telCliente"));
        riesgo_cliente.setCellValueFactory(new PropertyValueFactory<>("riesgoCliente"));
        descuento_cliente.setCellValueFactory(new PropertyValueFactory<>("descuentoCliente"));
        direccion_cliente.setCellValueFactory(new PropertyValueFactory<>("direccionCliente"));
        cp_cliente.setCellValueFactory(new PropertyValueFactory<>("cpCliente"));
        poblacion_cliente.setCellValueFactory(new PropertyValueFactory<>("poblacionCliente"));
        provincia_cliente.setCellValueFactory(new PropertyValueFactory<>("provinciaCliente"));
        pais_cliente.setCellValueFactory(new PropertyValueFactory<>("paisCliente"));
        observaciones_cliente.setCellValueFactory(new PropertyValueFactory<>("observacionesCliente"));

        // Cargar los datos
        cargarDatos();
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
