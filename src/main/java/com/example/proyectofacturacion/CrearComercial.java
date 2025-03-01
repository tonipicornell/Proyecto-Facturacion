package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class CrearComercial {
    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField nifField;
    @FXML
    private TextField direccionField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField correoElectronicoField;
    @FXML
    private TextField puestoTrabajoField;
    @FXML
    private TextField zonaActividadField;
    @FXML
    private TextArea historialVentasArea;
    @FXML
    private TextField comisionesField;
    @FXML
    private TextField incentivosField;
    @FXML
    private TextArea objetivosAlcanzadosArea;
    @FXML
    private DatePicker fechaContratacionPicker;
    @FXML
    private Button guardarButton;
    @FXML
    private Button cancelarButton;

    @FXML
    private void initialize() {
        // Establecer la fecha actual como fecha de contratación por defecto
        fechaContratacionPicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleGuardar() {
        if (!validarCamposObligatorios()) {
            mostrarAlerta("Campos incompletos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        try {
            // Intentar convertir comisiones e incentivos a números
            double comisiones = 0.0;
            double incentivos = 0.0;

            try {
                if (!comisionesField.getText().isEmpty()) {
                    comisiones = Double.parseDouble(comisionesField.getText().replace(",", "."));
                }
                if (!incentivosField.getText().isEmpty()) {
                    incentivos = Double.parseDouble(incentivosField.getText().replace(",", "."));
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error en el formato", "Las comisiones e incentivos deben ser números válidos.");
                return;
            }

            // Verificar si el NIF ya existe
            if (existeNif(nifField.getText())) {
                mostrarAlerta("NIF duplicado", "Ya existe un comercial con ese NIF en la base de datos.");
                return;
            }

            // Insertar en la base de datos
            if (insertarComercial(comisiones, incentivos)) {
                mostrarInformacion("Registro exitoso", "El comercial ha sido registrado correctamente.");
                cerrarVentana();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", "No se pudo guardar el comercial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean existeNif(String nif) throws SQLException {
        String query = "SELECT COUNT(*) FROM comerciales WHERE nif = ?";
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nif);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean insertarComercial(double comisiones, double incentivos) throws SQLException {
        String sql = "INSERT INTO comerciales (nombre, apellidos, nif, direccion, telefono, correo_electronico, " +
                "puesto_trabajo, zona_actividad, historial_ventas, comisiones, incentivos, objetivos_alcanzados, " +
                "fecha_contratacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Configurar todos los parámetros
            int paramIndex = 1;
            stmt.setString(paramIndex++, nombreField.getText());
            stmt.setString(paramIndex++, apellidosField.getText());
            stmt.setString(paramIndex++, nifField.getText());
            stmt.setString(paramIndex++, direccionField.getText());
            stmt.setString(paramIndex++, telefonoField.getText());
            stmt.setString(paramIndex++, correoElectronicoField.getText());
            stmt.setString(paramIndex++, puestoTrabajoField.getText());
            stmt.setString(paramIndex++, zonaActividadField.getText());
            stmt.setString(paramIndex++, historialVentasArea.getText());
            stmt.setDouble(paramIndex++, comisiones);
            stmt.setDouble(paramIndex++, incentivos);
            stmt.setString(paramIndex++, objetivosAlcanzadosArea.getText());
            stmt.setDate(paramIndex++, Date.valueOf(fechaContratacionPicker.getValue()));

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    private boolean validarCamposObligatorios() {
        return !nombreField.getText().isEmpty() &&
                !apellidosField.getText().isEmpty() &&
                !nifField.getText().isEmpty() &&
                fechaContratacionPicker.getValue() != null;
    }

    @FXML
    private void handleCancelar() {
        if (hayCambios()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar cancelación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro que desea cancelar? Se perderán todos los datos ingresados.");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    cerrarVentana();
                }
            });
        } else {
            cerrarVentana();
        }
    }

    private boolean hayCambios() {
        return !nombreField.getText().isEmpty() ||
                !apellidosField.getText().isEmpty() ||
                !nifField.getText().isEmpty() ||
                !direccionField.getText().isEmpty() ||
                !telefonoField.getText().isEmpty() ||
                !correoElectronicoField.getText().isEmpty() ||
                !puestoTrabajoField.getText().isEmpty() ||
                !zonaActividadField.getText().isEmpty() ||
                !historialVentasArea.getText().isEmpty() ||
                !comisionesField.getText().isEmpty() ||
                !incentivosField.getText().isEmpty() ||
                !objetivosAlcanzadosArea.getText().isEmpty() ||
                fechaContratacionPicker.getValue() != null;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
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
}