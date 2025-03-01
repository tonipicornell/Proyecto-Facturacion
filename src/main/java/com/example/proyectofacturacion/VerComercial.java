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

public class VerComercial {
    @FXML
    private TableView<Comercial> tableView;
    @FXML
    private TableColumn<Comercial, String> nif_comercial;
    @FXML
    private TableColumn<Comercial, String> nombre_comercial;
    @FXML
    private TableColumn<Comercial, String> apellidos_comercial;
    @FXML
    private TableColumn<Comercial, String> email_comercial;
    @FXML
    private TableColumn<Comercial, String> puesto_comercial;
    @FXML
    private TableColumn<Comercial, String> zona_comercial;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private Button verDetalleButton;
    @FXML
    private Button nuevoComercialButton;
    @FXML
    private Button eliminarComercialButton;

    private ObservableList<Comercial> comercialesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nif_comercial.setCellValueFactory(new PropertyValueFactory<>("nif"));
        nombre_comercial.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidos_comercial.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        email_comercial.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        puesto_comercial.setCellValueFactory(new PropertyValueFactory<>("puestoTrabajo"));
        zona_comercial.setCellValueFactory(new PropertyValueFactory<>("zonaActividad"));

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList(
                "Todos", "NIF", "Nombre", "Apellidos", "Email", "Puesto", "Zona"));
        searchTypeCombo.setValue("Todos");

        // Cargar los datos
        cargarDatos();

        // Deshabilitar los botones hasta que se seleccione un comercial
        verDetalleButton.setDisable(true);
        eliminarComercialButton.setDisable(true);

        // Escuchar cambios en la selección de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            verDetalleButton.setDisable(newSelection == null);
            eliminarComercialButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleVerDetalle() {
        Comercial comercialSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (comercialSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-comercial.fxml"));
                Parent root = loader.load();

                DetalleComercial controller = loader.getController();
                controller.setComercial(comercialSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Ficha Detallada: " + comercialSeleccionado.getNombreCompleto());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Recargar datos después de cerrar la ventana de detalle (por si hubo cambios)
                cargarDatos();

            } catch (IOException e) {
                mostrarAlerta("Error al abrir el detalle del comercial", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleNuevoComercial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-comercial.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Comercial");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recargar datos después de cerrar la ventana
            cargarDatos();

        } catch (IOException e) {
            mostrarAlerta("Error al abrir formulario de nuevo comercial", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminarComercial() {
        Comercial comercialSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (comercialSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro que desea eliminar al comercial "
                    + comercialSeleccionado.getNombreCompleto() + "?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eliminarComercial(comercialSeleccionado.getId());
                }
            });
        }
    }

    private void eliminarComercial(int id) {
        String sql = "DELETE FROM comerciales WHERE id = " + id;

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement()) {

            int filasAfectadas = stmt.executeUpdate(sql);
            if (filasAfectadas > 0) {
                mostrarInformacion("Comercial eliminado", "El comercial ha sido eliminado correctamente.");
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el comercial.");
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
        filterComerciales(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tableView.setItems(comercialesList);
    }

    public void cargarDatos() {
        comercialesList.clear();
        String query = "SELECT * FROM comerciales";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Comercial comercial = new Comercial(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("nif"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico"),
                        rs.getString("puesto_trabajo"),
                        rs.getString("zona_actividad"),
                        rs.getString("historial_ventas"),
                        rs.getDouble("comisiones"),
                        rs.getDouble("incentivos"),
                        rs.getString("objetivos_alcanzados"),
                        rs.getDate("fecha_contratacion").toLocalDate()
                );
                comercialesList.add(comercial);
            }
            tableView.setItems(comercialesList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar datos", "No se pudieron cargar los comerciales: " + e.getMessage());
        }
    }

    private void filterComerciales(String searchText, String searchType) {
        ObservableList<Comercial> filteredList = FXCollections.observableArrayList();
        Predicate<Comercial> predicate = comercial -> {
            if (searchType.equals("Todos")) {
                return comercial.getNif().toLowerCase().contains(searchText) ||
                        comercial.getNombre().toLowerCase().contains(searchText) ||
                        comercial.getApellidos().toLowerCase().contains(searchText) ||
                        comercial.getCorreoElectronico().toLowerCase().contains(searchText) ||
                        comercial.getPuestoTrabajo().toLowerCase().contains(searchText) ||
                        comercial.getZonaActividad().toLowerCase().contains(searchText);
            } else if (searchType.equals("NIF")) {
                return comercial.getNif().toLowerCase().contains(searchText);
            } else if (searchType.equals("Nombre")) {
                return comercial.getNombre().toLowerCase().contains(searchText);
            } else if (searchType.equals("Apellidos")) {
                return comercial.getApellidos().toLowerCase().contains(searchText);
            } else if (searchType.equals("Email")) {
                return comercial.getCorreoElectronico().toLowerCase().contains(searchText);
            } else if (searchType.equals("Puesto")) {
                return comercial.getPuestoTrabajo().toLowerCase().contains(searchText);
            } else if (searchType.equals("Zona")) {
                return comercial.getZonaActividad().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (Comercial comercial : comercialesList) {
            if (predicate.test(comercial)) {
                filteredList.add(comercial);
            }
        }

        tableView.setItems(filteredList);
    }
}