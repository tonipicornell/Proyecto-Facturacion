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

public class ListarProveedores implements Initializable {

    @FXML
    private TableView<Proveedor> tablaProveedores;

    @FXML
    private TableColumn<Proveedor, String> colNombre;

    @FXML
    private TableColumn<Proveedor, String> colDireccion;

    @FXML
    private TableColumn<Proveedor, String> colCP;

    @FXML
    private TableColumn<Proveedor, String> colPoblacion;

    @FXML
    private TableColumn<Proveedor, String> colProvincia;

    @FXML
    private TableColumn<Proveedor, String> colPais;

    @FXML
    private TableColumn<Proveedor, String> colCIF;

    @FXML
    private TableColumn<Proveedor, String> colTelefono;

    @FXML
    private TableColumn<Proveedor, String> colEmail;

    @FXML
    private TableColumn<Proveedor, String> colIBAN;

    @FXML
    private TableColumn<Proveedor, String> colContacto;

    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccionProveedor"));
        colCP.setCellValueFactory(new PropertyValueFactory<>("cpProveedor"));
        colPoblacion.setCellValueFactory(new PropertyValueFactory<>("poblacionProveedor"));
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provinciaProveedor"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("paisProveedor"));
        colCIF.setCellValueFactory(new PropertyValueFactory<>("cifProveedor"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailProveedor"));
        colIBAN.setCellValueFactory(new PropertyValueFactory<>("ibanProveedor"));
        colContacto.setCellValueFactory(new PropertyValueFactory<>("contactoProveedor"));
    }

    private void cargarDatos() {
        listaProveedores.clear();

        String query = "SELECT * FROM proveedores";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Proveedor proveedor = new Proveedor(
                        rs.getString("nombreProveedor"),
                        rs.getString("direccionProveedor"),
                        rs.getString("cpProveedor"),
                        rs.getString("poblacionProveedor"),
                        rs.getString("provinciaProveedor"),
                        rs.getString("paisProveedor"),
                        rs.getString("cifProveedor"),
                        rs.getString("telefonoProveedor"),
                        rs.getString("emailProveedor"),
                        rs.getString("ibanProveedor"),
                        rs.getString("contactoProveedor"),
                        rs.getString("observacionesProveedor")
                );

                listaProveedores.add(proveedor);
            }

            tablaProveedores.setItems(listaProveedores);

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