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

public class FichaProveedor {
    @FXML
    private TableView<Proveedor> tableView;
    @FXML
    private TableColumn<Proveedor, String> nombre_proveedor;
    @FXML
    private TableColumn<Proveedor, String> cif_proveedor;
    @FXML
    private TableColumn<Proveedor, String> direccion_proveedor;
    @FXML
    private TableColumn<Proveedor, String> cp_proveedor;
    @FXML
    private TableColumn<Proveedor, String> poblacion_proveedor;
    @FXML
    private TableColumn<Proveedor, String> provincia_proveedor;
    @FXML
    private TableColumn<Proveedor, String> pais_proveedor;
    @FXML
    private TableColumn<Proveedor, String> telefono_proveedor;
    @FXML
    private TableColumn<Proveedor, String> email_proveedor;
    @FXML
    private TableColumn<Proveedor, String> iban_proveedor;
    @FXML
    private TableColumn<Proveedor, String> contacto_proveedor;
    @FXML
    private TableColumn<Proveedor, String> observaciones_proveedor;

    private ObservableList<Proveedor> proveedoresList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_proveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        cif_proveedor.setCellValueFactory(new PropertyValueFactory<>("cifProveedor"));
        direccion_proveedor.setCellValueFactory(new PropertyValueFactory<>("direccionProveedor"));
        cp_proveedor.setCellValueFactory(new PropertyValueFactory<>("cpProveedor"));
        poblacion_proveedor.setCellValueFactory(new PropertyValueFactory<>("poblacionProveedor"));
        provincia_proveedor.setCellValueFactory(new PropertyValueFactory<>("provinciaProveedor"));
        pais_proveedor.setCellValueFactory(new PropertyValueFactory<>("paisProveedor"));
        telefono_proveedor.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));
        email_proveedor.setCellValueFactory(new PropertyValueFactory<>("emailProveedor"));
        iban_proveedor.setCellValueFactory(new PropertyValueFactory<>("ibanProveedor"));
        contacto_proveedor.setCellValueFactory(new PropertyValueFactory<>("contactoProveedor"));
        observaciones_proveedor.setCellValueFactory(new PropertyValueFactory<>("observacionesProveedor"));

        // Cargar los datos
        cargarDatos();
    }

    private void cargarDatos() {
        proveedoresList.clear();
        String query = "SELECT * FROM proveedores";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
                proveedoresList.add(proveedor);
            }
            tableView.setItems(proveedoresList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí podrías mostrar un alert al usuario
        }
    }
}
