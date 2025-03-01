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
import java.time.LocalDate;
import java.util.function.Predicate;

public class VerTrabajador {
    @FXML
    private TableView<Trabajador> tableView;
    @FXML
    private TableColumn<Trabajador, String> nombre_trabajador;
    @FXML
    private TableColumn<Trabajador, String> dni_nie_trabajador;
    @FXML
    private TableColumn<Trabajador, String> email_trabajador;
    @FXML
    private TableColumn<Trabajador, String> telefono_trabajador;
    @FXML
    private TableColumn<Trabajador, String> puesto_trabajador;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private Button verDetalleButton;
    @FXML
    private Button nuevoTrabajadorButton;
    @FXML
    private Button eliminarTrabajadorButton;

    private ObservableList<Trabajador> trabajadoresList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        nombre_trabajador.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        dni_nie_trabajador.setCellValueFactory(new PropertyValueFactory<>("dniNie"));
        email_trabajador.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        telefono_trabajador.setCellValueFactory(new PropertyValueFactory<>("telefonoContacto"));
        puesto_trabajador.setCellValueFactory(new PropertyValueFactory<>("puestoTrabajo"));

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList(
                "Todos", "DNI/NIE", "Nombre", "Email", "Teléfono", "Puesto"));
        searchTypeCombo.setValue("Todos");

        // Cargar los datos
        cargarDatos();

        // Deshabilitar los botones hasta que se seleccione un trabajador
        verDetalleButton.setDisable(true);
        eliminarTrabajadorButton.setDisable(true);

        // Escuchar cambios en la selección de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            verDetalleButton.setDisable(newSelection == null);
            eliminarTrabajadorButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleVerDetalle() {
        Trabajador trabajadorSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (trabajadorSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-trabajador.fxml"));
                Parent root = loader.load();

                DetalleTrabajador controller = loader.getController();
                controller.setTrabajador(trabajadorSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Ficha Detallada: " + trabajadorSeleccionado.getNombreCompleto());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Recargar datos después de cerrar la ventana de detalle (por si hubo cambios)
                cargarDatos();

            } catch (IOException e) {
                mostrarAlerta("Error al abrir el detalle del trabajador", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleNuevoTrabajador() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("nuevo-trabajador.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nuevo Trabajador");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recargar datos después de cerrar la ventana
            cargarDatos();

        } catch (IOException e) {
            mostrarAlerta("Error al abrir formulario de nuevo trabajador", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEliminarTrabajador() {
        Trabajador trabajadorSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (trabajadorSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro que desea eliminar al trabajador "
                    + trabajadorSeleccionado.getNombreCompleto() + "?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    eliminarTrabajador(trabajadorSeleccionado.getId());
                }
            });
        }
    }

    private void eliminarTrabajador(int id) {
        String sql = "DELETE FROM trabajadores WHERE id = " + id;

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement()) {

            int filasAfectadas = stmt.executeUpdate(sql);
            if (filasAfectadas > 0) {
                mostrarInformacion("Trabajador eliminado", "El trabajador ha sido eliminado correctamente.");
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el trabajador.");
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
        filterTrabajadores(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tableView.setItems(trabajadoresList);
    }

    public void cargarDatos() {
        trabajadoresList.clear();
        String query = "SELECT * FROM trabajadores";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Trabajador trabajador = new Trabajador(
                        rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("dni_nie"),
                        rs.getDate("fecha_nacimiento").toLocalDate(),
                        rs.getString("direccion_residencia"),
                        rs.getString("telefono_contacto"),
                        rs.getString("correo_electronico"),
                        rs.getString("numero_afiliacion_seguridad_social"),
                        rs.getDate("fecha_inicio_contrato").toLocalDate(),
                        rs.getString("tipo_contrato"),
                        rs.getString("puesto_trabajo"),
                        rs.getString("centro_trabajo"),
                        rs.getString("grupo_profesional"),
                        rs.getString("jornada_laboral"),
                        rs.getDouble("tipo_retencion_irpf"),
                        rs.getString("datos_bancarios"),
                        rs.getString("regimen_seguridad_social"),
                        rs.getString("titulos_academicos"),
                        rs.getString("cursos_formacion"),
                        rs.getString("experiencia_laboral"),
                        rs.getBoolean("antecedentes_penales")
                );
                trabajadoresList.add(trabajador);
            }
            tableView.setItems(trabajadoresList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar datos", "No se pudieron cargar los trabajadores: " + e.getMessage());
        }
    }

    private void filterTrabajadores(String searchText, String searchType) {
        ObservableList<Trabajador> filteredList = FXCollections.observableArrayList();
        Predicate<Trabajador> predicate = trabajador -> {
            if (searchType.equals("Todos")) {
                return trabajador.getDniNie().toLowerCase().contains(searchText) ||
                        trabajador.getNombreCompleto().toLowerCase().contains(searchText) ||
                        trabajador.getCorreoElectronico().toLowerCase().contains(searchText) ||
                        trabajador.getTelefonoContacto().toLowerCase().contains(searchText) ||
                        trabajador.getPuestoTrabajo().toLowerCase().contains(searchText);
            } else if (searchType.equals("DNI/NIE")) {
                return trabajador.getDniNie().toLowerCase().contains(searchText);
            } else if (searchType.equals("Nombre")) {
                return trabajador.getNombreCompleto().toLowerCase().contains(searchText);
            } else if (searchType.equals("Email")) {
                return trabajador.getCorreoElectronico().toLowerCase().contains(searchText);
            } else if (searchType.equals("Teléfono")) {
                return trabajador.getTelefonoContacto().toLowerCase().contains(searchText);
            } else if (searchType.equals("Puesto")) {
                return trabajador.getPuestoTrabajo().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (Trabajador trabajador : trabajadoresList) {
            if (predicate.test(trabajador)) {
                filteredList.add(trabajador);
            }
        }

        tableView.setItems(filteredList);
    }
}