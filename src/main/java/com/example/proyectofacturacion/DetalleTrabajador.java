package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class DetalleTrabajador {
    @FXML private TextField nombreField;
    @FXML private TextField dniNieField;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private TextField direccionField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField bancoField;
    @FXML private TextField numeroSsField;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private ComboBox<String> tipoContratoCombo;
    @FXML private TextField puestoField;
    @FXML private TextField centroField;
    @FXML private TextField grupoField;
    @FXML private ComboBox<String> jornadaCombo;
    @FXML private TextField irpfField;
    @FXML private TextField regimenSsField;
    @FXML private TextArea titulosArea;
    @FXML private TextArea cursosArea;
    @FXML private TextArea experienciaArea;
    @FXML private CheckBox antecedentesPenalesCheck;

    private Trabajador trabajadorActual;

    @FXML
    private void initialize() {
        // Inicializar los ComboBox
        tipoContratoCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Indefinido", "Temporal", "Formación", "Prácticas", "Obra y servicio", "Interinidad", "Relevo"));

        jornadaCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Completa", "Parcial", "Reducida", "Por horas", "Fines de semana", "Intensiva verano"));
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajadorActual = trabajador;

        // Cargar datos en los campos
        nombreField.setText(trabajador.getNombreCompleto());
        dniNieField.setText(trabajador.getDniNie());
        fechaNacimientoPicker.setValue(trabajador.getFechaNacimiento());
        direccionField.setText(trabajador.getDireccionResidencia());
        telefonoField.setText(trabajador.getTelefonoContacto());
        emailField.setText(trabajador.getCorreoElectronico());
        bancoField.setText(trabajador.getDatosBancarios());
        numeroSsField.setText(trabajador.getNumeroAfiliacionSeguridadSocial());
        fechaInicioPicker.setValue(trabajador.getFechaInicioContrato());
        tipoContratoCombo.setValue(trabajador.getTipoContrato());
        puestoField.setText(trabajador.getPuestoTrabajo());
        centroField.setText(trabajador.getCentroTrabajo());
        grupoField.setText(trabajador.getGrupoProfesional());
        jornadaCombo.setValue(trabajador.getJornadaLaboral());
        irpfField.setText(String.valueOf(trabajador.getTipoRetencionIrpf()));
        regimenSsField.setText(trabajador.getRegimenSeguridadSocial());
        titulosArea.setText(trabajador.getTitulosAcademicos());
        cursosArea.setText(trabajador.getCursosFormacion());
        experienciaArea.setText(trabajador.getExperienciaLaboral());
        antecedentesPenalesCheck.setSelected(trabajador.isAntecedentesPenales());
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            String sql = "UPDATE trabajadores SET nombre_completo=?, dni_nie=?, fecha_nacimiento=?, " +
                    "direccion_residencia=?, telefono_contacto=?, correo_electronico=?, " +
                    "numero_afiliacion_seguridad_social=?, fecha_inicio_contrato=?, tipo_contrato=?, " +
                    "puesto_trabajo=?, centro_trabajo=?, grupo_profesional=?, jornada_laboral=?, " +
                    "tipo_retencion_irpf=?, datos_bancarios=?, regimen_seguridad_social=?, " +
                    "titulos_academicos=?, cursos_formacion=?, experiencia_laboral=?, " +
                    "antecedentes_penales=? WHERE id=?";

            try (Connection conn = DataBaseConnected.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombreField.getText());
                pstmt.setString(2, dniNieField.getText());
                pstmt.setDate(3, java.sql.Date.valueOf(fechaNacimientoPicker.getValue()));
                pstmt.setString(4, direccionField.getText());
                pstmt.setString(5, telefonoField.getText());
                pstmt.setString(6, emailField.getText());
                pstmt.setString(7, numeroSsField.getText());
                pstmt.setDate(8, java.sql.Date.valueOf(fechaInicioPicker.getValue()));
                pstmt.setString(9, tipoContratoCombo.getValue());
                pstmt.setString(10, puestoField.getText());
                pstmt.setString(11, centroField.getText());
                pstmt.setString(12, grupoField.getText());
                pstmt.setString(13, jornadaCombo.getValue());
                pstmt.setDouble(14, Double.parseDouble(irpfField.getText().isEmpty() ? "0" : irpfField.getText()));
                pstmt.setString(15, bancoField.getText());
                pstmt.setString(16, regimenSsField.getText());
                pstmt.setString(17, titulosArea.getText());
                pstmt.setString(18, cursosArea.getText());
                pstmt.setString(19, experienciaArea.getText());
                pstmt.setBoolean(20, antecedentesPenalesCheck.isSelected());
                pstmt.setInt(21, trabajadorActual.getId());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje("Trabajador actualizado", "Los datos del trabajador se han actualizado correctamente", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar el trabajador", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                mostrarMensaje("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarMensaje("Error de formato", "El campo de retención IRPF debe ser un valor numérico", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (nombreField.getText().trim().isEmpty()) {
            errores.append("- El nombre completo es obligatorio\n");
        }

        if (dniNieField.getText().trim().isEmpty()) {
            errores.append("- El DNI/NIE es obligatorio\n");
        }

        if (fechaNacimientoPicker.getValue() == null) {
            errores.append("- La fecha de nacimiento es obligatoria\n");
        }

        if (numeroSsField.getText().trim().isEmpty()) {
            errores.append("- El número de afiliación a la Seguridad Social es obligatorio\n");
        }

        if (fechaInicioPicker.getValue() == null) {
            errores.append("- La fecha de inicio del contrato es obligatoria\n");
        }

        // Validar que IRPF sea un valor numérico válido
        try {
            if (!irpfField.getText().trim().isEmpty()) {
                Double.parseDouble(irpfField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- El valor de retención IRPF debe ser numérico\n");
        }

        if (errores.length() > 0) {
            mostrarMensaje("Error de validación",
                    "Por favor, corrija los siguientes errores:\n" + errores.toString(),
                    Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}