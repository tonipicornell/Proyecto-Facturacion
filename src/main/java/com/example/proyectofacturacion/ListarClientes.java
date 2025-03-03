package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ListarClientes implements Initializable {

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TableColumn<Cliente, String> colNombre;

    @FXML
    private TableColumn<Cliente, String> colDireccion;

    @FXML
    private TableColumn<Cliente, String> colCP;

    @FXML
    private TableColumn<Cliente, String> colPoblacion;

    @FXML
    private TableColumn<Cliente, String> colProvincia;

    @FXML
    private TableColumn<Cliente, String> colPais;

    @FXML
    private TableColumn<Cliente, String> colCIF;

    @FXML
    private TableColumn<Cliente, String> colTelefono;

    @FXML
    private TableColumn<Cliente, String> colEmail;

    @FXML
    private TableColumn<Cliente, String> colIBAN;

    @FXML
    private TableColumn<Cliente, Double> colRiesgo;

    @FXML
    private TableColumn<Cliente, Double> colDescuento;

    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccionCliente"));
        colCP.setCellValueFactory(new PropertyValueFactory<>("cpCliente"));
        colPoblacion.setCellValueFactory(new PropertyValueFactory<>("poblacionCliente"));
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provinciaCliente"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("paisCliente"));
        colCIF.setCellValueFactory(new PropertyValueFactory<>("cifCliente"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telCliente"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailCliente"));
        colIBAN.setCellValueFactory(new PropertyValueFactory<>("ibanCliente"));
        colRiesgo.setCellValueFactory(new PropertyValueFactory<>("riesgoCliente"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuentoCliente"));
    }

    private void cargarDatos() {
        listaClientes.clear();

        String query = "SELECT * FROM clientes";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

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

                listaClientes.add(cliente);
            }

            tablaClientes.setItems(listaClientes);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarLista() {
        cargarDatos();
    }
}