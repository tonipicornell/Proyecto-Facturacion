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

public class VerDistribuidor {
    @FXML
    private TableView<Distribuidor> tableView;
    @FXML
    private TableColumn<Distribuidor, String> nombre_empresa;
    @FXML
    private TableColumn<Distribuidor, String> direccion_fisica;
    @FXML
    private TableColumn<Distribuidor, String> telefono;
    @FXML
    private TableColumn<Distribuidor, String> correo_electronico;
    @FXML
    private TableColumn<Distribuidor, String> soporte_postventa;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private Button verDetalleButton;
    @FXML
    private Button nuevoDistribuidorButton;
    @FXML
    private Button eliminarDistribuidorButton;

    private ObservableList<Distribuidor> distribuidoresList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_empresa.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        direccion_fisica.setCellValueFactory(new PropertyValueFactory<>("direccionFisica"));
        telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        correo_electronico.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        soporte_postventa.setCellValueFactory(new PropertyValueFactory<>("soportePostventaTexto"));

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList(
                "Todos", "Empresa", "Dirección", "Email", "Teléfono"));
        searchTypeCombo.setValue("Todos");

        // Cargar los datos
        cargarDatos();

        // Deshabilitar los botones hasta que se seleccione un distribuidor
        verDetalleButton.setDisable(true);
        eliminarDistribuidorButton.setDisable(true);

        // Escuchar cambios en la selección de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            verDetalleButton.setDisable(newSelection == null);
            eliminarDistribuidorButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleVerDetalle() {
        Distribuidor distribuidorSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (distribuidorSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-distribuidor.fxml"));
                Parent root = loader.load();

                DetalleDistribuidor controller = loader.getController();
                controller.setDistribuidor(distribuidorSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Ficha Detallada: " + distribuidorSeleccionado.getNombreEmpresa());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Recargar datos después de cerrar la ventana de detalle (por si hubo cambios)
                cargarDatos();

            } catch (IOException e) {
                mostrarAlerta("Error al abrir el detalle del distribuidor", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleNuevoDistribuidor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-distribuidor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Distribuidor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recargar datos después de cerrar la ventana
            cargarDatos();

        } catch (IOException e) {
            mostrarAlerta("Error al abrir formulario de nuevo distribuidor", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminarDistribuidor() {
        Distribuidor distribuidorSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (distribuidorSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro que desea eliminar al distribuidor "
                    + distribuidorSeleccionado.getNombreEmpresa() + "?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eliminarDistribuidor(distribuidorSeleccionado.getId());
                }
            });
        }
    }

    private void eliminarDistribuidor(int id) {
        String sql = "DELETE FROM distribuidores WHERE id = " + id;

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement()) {

            int filasAfectadas = stmt.executeUpdate(sql);
            if (filasAfectadas > 0) {
                mostrarInformacion("Distribuidor eliminado", "El distribuidor ha sido eliminado correctamente.");
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el distribuidor.");
            }

        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = (searchTypeCombo.getValue() != null) ? searchTypeCombo.getValue() : "Todos";
        filterDistribuidores(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tableView.setItems(distribuidoresList);
    }

    public void cargarDatos() {
        distribuidoresList.clear();
        String query = "SELECT * FROM distribuidores";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Distribuidor distribuidor = new Distribuidor(
                        rs.getInt("id"),
                        rs.getString("nombre_empresa"),
                        rs.getString("direccion_fisica"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico"),
                        rs.getString("pagina_web"),
                        rs.getString("condiciones_pago"),
                        rs.getDouble("precio_compra"),
                        rs.getDouble("margen_ganancia"),
                        rs.getInt("volumen_compra"),
                        rs.getString("condiciones_entrega"),
                        rs.getDate("contrato_inicio") != null ? rs.getDate("contrato_inicio").toLocalDate() : null,
                        rs.getDate("contrato_termino") != null ? rs.getDate("contrato_termino").toLocalDate() : null,
                        rs.getString("territorio_asignado"),
                        rs.getString("metodos_envio"),
                        rs.getBoolean("soporte_postventa")
                );
                distribuidoresList.add(distribuidor);
            }
            tableView.setItems(distribuidoresList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar datos", "No se pudieron cargar los distribuidores: " + e.getMessage());
        }
    }

    private void filterDistribuidores(String searchText, String searchType) {
        ObservableList<Distribuidor> filteredList = FXCollections.observableArrayList();
        Predicate<Distribuidor> predicate = distribuidor -> {
            if (searchType.equals("Todos")) {
                return distribuidor.getNombreEmpresa().toLowerCase().contains(searchText) ||
                        distribuidor.getDireccionFisica().toLowerCase().contains(searchText) ||
                        distribuidor.getCorreoElectronico().toLowerCase().contains(searchText) ||
                        distribuidor.getTelefono().toLowerCase().contains(searchText);
            } else if (searchType.equals("Empresa")) {
                return distribuidor.getNombreEmpresa().toLowerCase().contains(searchText);
            } else if (searchType.equals("Dirección")) {
                return distribuidor.getDireccionFisica().toLowerCase().contains(searchText);
            } else if (searchType.equals("Email")) {
                return distribuidor.getCorreoElectronico().toLowerCase().contains(searchText);
            } else if (searchType.equals("Teléfono")) {
                return distribuidor.getTelefono().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (Distribuidor distribuidor : distribuidoresList) {
            if (predicate.test(distribuidor)) {
                filteredList.add(distribuidor);
            }
        }

        tableView.setItems(filteredList);
    }
}