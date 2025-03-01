package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class CrearTrabajador {
    @FXML
    private TextField nombreCompletoField;
    @FXML
    private TextField dniNieField;
    @FXML
    private DatePicker fechaNacimientoPicker;
    @FXML
    private TextField direccionResidenciaField;
    @FXML
    private TextField telefonoContactoField;
    @FXML
    private TextField correoElectronicoField;
    @FXML
    private TextField numeroAfiliacionField;
    @FXML
    private DatePicker fechaInicioContratoPicker;
    @FXML
    private ComboBox<String> tipoContratoCombo;
    @FXML
    private TextField puestoTrabajoField;
    @FXML
    private TextField centroTrabajoField;
    @FXML
    private TextField grupoProfesionalField;
    @FXML
    private ComboBox<String> jornadaLaboralCombo;
    @FXML
    private TextField tipoRetencionIrpfField;
    @FXML
    private TextField datosBancariosField;
    @FXML
    private ComboBox<String> regimenSeguridadSocialCombo;
    @FXML
    private TextArea titulosAcademicosArea;
    @FXML
    private TextArea cursosFormacionArea;
    @FXML
    private TextArea experienciaLaboralArea;
    @FXML
    private CheckBox antecedentesPenalesCheck;
    @FXML
    private Button guardarButton;
    @FXML
    private Button cancelarButton;

    @FXML
    private void initialize() {
        // Inicializar ComboBoxes con opciones predefinidas
        tipoContratoCombo.setItems(FXCollections.observableArrayList(
                "Indefinido", "Temporal", "Prácticas", "Formación", "Obra o servicio"
        ));

        jornadaLaboralCombo.setItems(FXCollections.observableArrayList(
                "Completa", "Parcial", "Reducida", "Por horas"
        ));

        regimenSeguridadSocialCombo.setItems(FXCollections.observableArrayList(
                "General", "Autónomos", "Agrario", "Mar", "Minería", "Empleados del hogar"
        ));

        // Establecer fechas por defecto
        fechaNacimientoPicker.setValue(LocalDate.now().minusYears(25));
        fechaInicioContratoPicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleGuardar() {
        if (!validarCamposObligatorios()) {
            mostrarAlerta("Campos incompletos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        try {
            // Intentar convertir el IRPF a número
            double retencionIrpf = 0.0;
            try {
                if (!tipoRetencionIrpfField.getText().isEmpty()) {
                    retencionIrpf = Double.parseDouble(tipoRetencionIrpfField.getText().replace(",", "."));
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error en el formato", "El porcentaje de IRPF debe ser un número válido.");
                return;
            }

            // Verificar si el DNI/NIE ya existe
            if (existeDniNie(dniNieField.getText())) {
                mostrarAlerta("DNI/NIE duplicado", "Ya existe un trabajador con ese DNI/NIE en la base de datos.");
                return;
            }

            // Insertar en la base de datos
            if (insertarTrabajador(retencionIrpf)) {
                mostrarInformacion("Registro exitoso", "El trabajador ha sido registrado correctamente.");
                cerrarVentana();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", "No se pudo guardar el trabajador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean existeDniNie(String dniNie) throws SQLException {
        String query = "SELECT COUNT(*) FROM trabajadores WHERE dni_nie = ?";
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dniNie);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean insertarTrabajador(double retencionIrpf) throws SQLException {
        String sql = "INSERT INTO trabajadores (nombre_completo, dni_nie, fecha_nacimiento, direccion_residencia, " +
                "telefono_contacto, correo_electronico, numero_afiliacion_seguridad_social, fecha_inicio_contrato, " +
                "tipo_contrato, puesto_trabajo, centro_trabajo, grupo_profesional, jornada_laboral, tipo_retencion_irpf, " +
                "datos_bancarios, regimen_seguridad_social, titulos_academicos, cursos_formacion, experiencia_laboral, " +
                "antecedentes_penales) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Configurar todos los parámetros
            int paramIndex = 1;
            stmt.setString(paramIndex++, nombreCompletoField.getText());
            stmt.setString(paramIndex++, dniNieField.getText());
            stmt.setDate(paramIndex++, Date.valueOf(fechaNacimientoPicker.getValue()));
            stmt.setString(paramIndex++, direccionResidenciaField.getText());
            stmt.setString(paramIndex++, telefonoContactoField.getText());
            stmt.setString(paramIndex++, correoElectronicoField.getText());
            stmt.setString(paramIndex++, numeroAfiliacionField.getText());
            stmt.setDate(paramIndex++, Date.valueOf(fechaInicioContratoPicker.getValue()));
            stmt.setString(paramIndex++, tipoContratoCombo.getValue());
            stmt.setString(paramIndex++, puestoTrabajoField.getText());
            stmt.setString(paramIndex++, centroTrabajoField.getText());
            stmt.setString(paramIndex++, grupoProfesionalField.getText());
            stmt.setString(paramIndex++, jornadaLaboralCombo.getValue());
            stmt.setDouble(paramIndex++, retencionIrpf);
            stmt.setString(paramIndex++, datosBancariosField.getText());
            stmt.setString(paramIndex++, regimenSeguridadSocialCombo.getValue());
            stmt.setString(paramIndex++, titulosAcademicosArea.getText());
            stmt.setString(paramIndex++, cursosFormacionArea.getText());
            stmt.setString(paramIndex++, experienciaLaboralArea.getText());
            stmt.setBoolean(paramIndex++, antecedentesPenalesCheck.isSelected());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    private boolean validarCamposObligatorios() {
        return !nombreCompletoField.getText().isEmpty() &&
                !dniNieField.getText().isEmpty() &&
                fechaNacimientoPicker.getValue() != null &&
                !numeroAfiliacionField.getText().isEmpty() &&
                fechaInicioContratoPicker.getValue() != null;
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
        return !nombreCompletoField.getText().isEmpty() ||
                !dniNieField.getText().isEmpty() ||
                fechaNacimientoPicker.getValue() != null ||
                !direccionResidenciaField.getText().isEmpty() ||
                !telefonoContactoField.getText().isEmpty() ||
                !correoElectronicoField.getText().isEmpty() ||
                !numeroAfiliacionField.getText().isEmpty() ||
                !puestoTrabajoField.getText().isEmpty() ||
                !centroTrabajoField.getText().isEmpty() ||
                !grupoProfesionalField.getText().isEmpty() ||
                !tipoRetencionIrpfField.getText().isEmpty() ||
                !datosBancariosField.getText().isEmpty() ||
                !titulosAcademicosArea.getText().isEmpty() ||
                !cursosFormacionArea.getText().isEmpty() ||
                !experienciaLaboralArea.getText().isEmpty() ||
                antecedentesPenalesCheck.isSelected();
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