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
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private Button verDetalleButton;

    private ObservableList<Cliente> clientesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_cliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        cif_cliente.setCellValueFactory(new PropertyValueFactory<>("cifCliente"));
        email_cliente.setCellValueFactory(new PropertyValueFactory<>("emailCliente"));
        telefono_cliente.setCellValueFactory(new PropertyValueFactory<>("telCliente"));

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList("Todos", "CIF", "Nombre", "Email", "Teléfono"));
        searchTypeCombo.setValue("Todos");

        // Cargar los datos
        cargarDatos();

        // Deshabilitar el botón de detalle hasta que se seleccione un cliente
        verDetalleButton.setDisable(true);

        // Escuchar cambios en la selección de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            verDetalleButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleVerDetalle() {
        Cliente clienteSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-cliente.fxml"));
                Parent root = loader.load();

                DetalleCliente controller = loader.getController();
                controller.setCliente(clienteSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Detalle del Cliente: " + clienteSeleccionado.getNombreCliente());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Recargar datos después de cerrar la ventana de detalle (por si hubo cambios)
                cargarDatos();

            } catch (IOException e) {
                mostrarAlerta("Error al abrir el detalle del cliente", e.getMessage());
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
        filterClients(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tableView.setItems(clientesList);
    }

    public void cargarDatos() {
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
        }
    }

    private void filterClients(String searchText, String searchType) {
        ObservableList<Cliente> filteredList = FXCollections.observableArrayList();
        Predicate<Cliente> predicate = cliente -> {
            if (searchType.equals("Todos")) {
                return cliente.getCifCliente().toLowerCase().contains(searchText) ||
                        cliente.getNombreCliente().toLowerCase().contains(searchText) ||
                        cliente.getEmailCliente().toLowerCase().contains(searchText) ||
                        cliente.getTelCliente().toLowerCase().contains(searchText);
            } else if (searchType.equals("CIF")) {
                return cliente.getCifCliente().toLowerCase().contains(searchText);
            } else if (searchType.equals("Nombre")) {
                return cliente.getNombreCliente().toLowerCase().contains(searchText);
            } else if (searchType.equals("Email")) {
                return cliente.getEmailCliente().toLowerCase().contains(searchText);
            } else if (searchType.equals("Teléfono")) {
                return cliente.getTelCliente().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (Cliente cliente : clientesList) {
            if (predicate.test(cliente)) {
                filteredList.add(cliente);
            }
        }

        tableView.setItems(filteredList);
    }
}