package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class DetalleComercial {
    @FXML private TextField nombreField;
    @FXML private TextField apellidosField;
    @FXML private TextField nifField;
    @FXML private TextField direccionField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField puestoField;
    @FXML private TextField zonaField;
    @FXML private TextArea historialVentasArea;
    @FXML private TextField comisionesField;
    @FXML private TextField incentivosField;
    @FXML private TextArea objetivosArea;
    @FXML private DatePicker fechaContratacionPicker;

    private Comercial comercialActual;

    public void setComercial(Comercial comercial) {
        this.comercialActual = comercial;

        // Cargar datos en los campos
        nombreField.setText(comercial.getNombre());
        apellidosField.setText(comercial.getApellidos());
        nifField.setText(comercial.getNif());
        direccionField.setText(comercial.getDireccion());
        telefonoField.setText(comercial.getTelefono());
        emailField.setText(comercial.getCorreoElectronico());
        puestoField.setText(comercial.getPuestoTrabajo());
        zonaField.setText(comercial.getZonaActividad());
        historialVentasArea.setText(comercial.getHistorialVentas());
        comisionesField.setText(String.valueOf(comercial.getComisiones()));
        incentivosField.setText(String.valueOf(comercial.getIncentivos()));
        objetivosArea.setText(comercial.getObjetivosAlcanzados());
        fechaContratacionPicker.setValue(comercial.getFechaContratacion());
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            String sql = "UPDATE comerciales SET nombre=?, apellidos=?, nif=?, " +
                    "direccion=?, telefono=?, correo_electronico=?, " +
                    "puesto_trabajo=?, zona_actividad=?, historial_ventas=?, " +
                    "comisiones=?, incentivos=?, objetivos_alcanzados=?, " +
                    "fecha_contratacion=? WHERE id=?";

            try (Connection conn = DataBaseConnected.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombreField.getText());
                pstmt.setString(2, apellidosField.getText());
                pstmt.setString(3, nifField.getText());
                pstmt.setString(4, direccionField.getText());
                pstmt.setString(5, telefonoField.getText());
                pstmt.setString(6, emailField.getText());
                pstmt.setString(7, puestoField.getText());
                pstmt.setString(8, zonaField.getText());
                pstmt.setString(9, historialVentasArea.getText());
                pstmt.setDouble(10, Double.parseDouble(comisionesField.getText().isEmpty() ? "0" : comisionesField.getText()));
                pstmt.setDouble(11, Double.parseDouble(incentivosField.getText().isEmpty() ? "0" : incentivosField.getText()));
                pstmt.setString(12, objetivosArea.getText());
                pstmt.setDate(13, java.sql.Date.valueOf(fechaContratacionPicker.getValue()));
                pstmt.setInt(14, comercialActual.getId());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje("Comercial actualizado", "Los datos del comercial se han actualizado correctamente", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar el comercial", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                mostrarMensaje("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarMensaje("Error de formato", "Los campos de comisiones e incentivos deben ser valores numéricos", Alert.AlertType.ERROR);
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
            errores.append("- El nombre es obligatorio\n");
        }

        if (apellidosField.getText().trim().isEmpty()) {
            errores.append("- Los apellidos son obligatorios\n");
        }

        if (nifField.getText().trim().isEmpty()) {
            errores.append("- El NIF es obligatorio\n");
        }

        if (fechaContratacionPicker.getValue() == null) {
            errores.append("- La fecha de contratación es obligatoria\n");
        }

        // Validar que comisiones e incentivos sean valores numéricos válidos
        try {
            if (!comisionesField.getText().trim().isEmpty()) {
                Double.parseDouble(comisionesField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- El valor de comisiones debe ser numérico\n");
        }

        try {
            if (!incentivosField.getText().trim().isEmpty()) {
                Double.parseDouble(incentivosField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- El valor de incentivos debe ser numérico\n");
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