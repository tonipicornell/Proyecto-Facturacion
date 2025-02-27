package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Predicate;

public class FichaProveedor {
    @FXML
    private TableView<Proveedor> tableView;
    @FXML
    private TableColumn<Proveedor, String> nombre_proveedor;
    @FXML
    private TableColumn<Proveedor, String> cif_proveedor;
    @FXML
    private TableColumn<Proveedor, String> email_proveedor;
    @FXML
    private TableColumn<Proveedor, String> telefono_proveedor;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private Button verDetalleButton;

    private ObservableList<Proveedor> proveedoresList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_proveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        cif_proveedor.setCellValueFactory(new PropertyValueFactory<>("cifProveedor"));
        email_proveedor.setCellValueFactory(new PropertyValueFactory<>("emailProveedor"));
        telefono_proveedor.setCellValueFactory(new PropertyValueFactory<>("telefonoProveedor"));

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList("Todos", "CIF", "Nombre", "Email", "Teléfono"));
        searchTypeCombo.setValue("Todos");

        // Cargar los datos
        cargarDatos();

        // Deshabilitar el botón de detalle hasta que se seleccione un proveedor
        verDetalleButton.setDisable(true);

        // Escuchar cambios en la selección de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            verDetalleButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleVerDetalle() {
        Proveedor proveedorSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (proveedorSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-proveedor.fxml"));
                Parent root = loader.load();

                DetalleProveedor controller = loader.getController();
                controller.setProveedor(proveedorSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Detalle del Proveedor: " + proveedorSeleccionado.getNombreProveedor());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Recargar datos después de cerrar la ventana de detalle (por si hubo cambios)
                cargarDatos();

            } catch (IOException e) {
                mostrarAlerta("Error al abrir el detalle del proveedor", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = (searchTypeCombo.getValue() != null) ? searchTypeCombo.getValue() : "Todos";
        filterProveedores(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tableView.setItems(proveedoresList);
    }

    public void cargarDatos() {
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
        }
    }

    private void filterProveedores(String searchText, String searchType) {
        ObservableList<Proveedor> filteredList = FXCollections.observableArrayList();
        Predicate<Proveedor> predicate = proveedor -> {
            if (searchType.equals("Todos")) {
                return proveedor.getCifProveedor().toLowerCase().contains(searchText) ||
                        proveedor.getNombreProveedor().toLowerCase().contains(searchText) ||
                        proveedor.getEmailProveedor().toLowerCase().contains(searchText) ||
                        proveedor.getTelefonoProveedor().toLowerCase().contains(searchText);
            } else if (searchType.equals("CIF")) {
                return proveedor.getCifProveedor().toLowerCase().contains(searchText);
            } else if (searchType.equals("Nombre")) {
                return proveedor.getNombreProveedor().toLowerCase().contains(searchText);
            } else if (searchType.equals("Email")) {
                return proveedor.getEmailProveedor().toLowerCase().contains(searchText);
            } else if (searchType.equals("Teléfono")) {
                return proveedor.getTelefonoProveedor().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (Proveedor proveedor : proveedoresList) {
            if (predicate.test(proveedor)) {
                filteredList.add(proveedor);
            }
        }

        tableView.setItems(filteredList);
    }
}