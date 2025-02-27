package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetalleProveedor {
    @FXML private TextField nombreField;
    @FXML private TextField direccionField;
    @FXML private TextField cpField;
    @FXML private TextField poblacionField;
    @FXML private TextField provinciaField;
    @FXML private TextField paisField;
    @FXML private TextField cifField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField ibanField;
    @FXML private TextField contactoField;
    @FXML private TextArea observacionesArea;

    private Proveedor proveedorActual;

    public void setProveedor(Proveedor proveedor) {
        this.proveedorActual = proveedor;

        // Cargar datos en los campos
        nombreField.setText(proveedor.getNombreProveedor());
        direccionField.setText(proveedor.getDireccionProveedor());
        cpField.setText(proveedor.getCpProveedor());
        poblacionField.setText(proveedor.getPoblacionProveedor());
        provinciaField.setText(proveedor.getProvinciaProveedor());
        paisField.setText(proveedor.getPaisProveedor());
        cifField.setText(proveedor.getCifProveedor());
        telefonoField.setText(proveedor.getTelefonoProveedor());
        emailField.setText(proveedor.getEmailProveedor());
        ibanField.setText(proveedor.getIbanProveedor());
        contactoField.setText(proveedor.getContactoProveedor());
        observacionesArea.setText(proveedor.getObservacionesProveedor());

        // El CIF no debería ser editable (clave única)
        cifField.setEditable(false);
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            String sql = "UPDATE proveedores SET nombreProveedor=?, direccionProveedor=?, cpProveedor=?, poblacionProveedor=?, " +
                    "provinciaProveedor=?, paisProveedor=?, telefonoProveedor=?, emailProveedor=?, ibanProveedor=?, " +
                    "contactoProveedor=?, observacionesProveedor=? WHERE cifProveedor=?";

            try (Connection conn = DataBaseConnected.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombreField.getText());
                pstmt.setString(2, direccionField.getText());
                pstmt.setString(3, cpField.getText());
                pstmt.setString(4, poblacionField.getText());
                pstmt.setString(5, provinciaField.getText());
                pstmt.setString(6, paisField.getText());
                pstmt.setString(7, telefonoField.getText());
                pstmt.setString(8, emailField.getText());
                pstmt.setString(9, ibanField.getText());
                pstmt.setString(10, contactoField.getText());
                pstmt.setString(11, observacionesArea.getText());
                pstmt.setString(12, cifField.getText());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje("Proveedor actualizado", "Los datos del proveedor se han actualizado correctamente", Alert.AlertType.INFORMATION);
                    // Cerrar la ventana
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar el proveedor", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                mostrarMensaje("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
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
            errores.append("- El nombre del proveedor es obligatorio\n");
        }

        if (cifField.getText().trim().isEmpty()) {
            errores.append("- El CIF es obligatorio\n");
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