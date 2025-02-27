package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetalleCliente {
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
    @FXML private TextField riesgoField;
    @FXML private TextField descuentoField;
    @FXML private TextArea observacionesArea;

    private Cliente clienteActual;

    public void setCliente(Cliente cliente) {
        this.clienteActual = cliente;

        // Cargar datos en los campos
        nombreField.setText(cliente.getNombreCliente());
        direccionField.setText(cliente.getDireccionCliente());
        cpField.setText(cliente.getCpCliente());
        poblacionField.setText(cliente.getPoblacionCliente());
        provinciaField.setText(cliente.getProvinciaCliente());
        paisField.setText(cliente.getPaisCliente());
        cifField.setText(cliente.getCifCliente());
        telefonoField.setText(cliente.getTelCliente());
        emailField.setText(cliente.getEmailCliente());
        ibanField.setText(cliente.getIbanCliente());
        riesgoField.setText(String.valueOf(cliente.getRiesgoCliente()));
        descuentoField.setText(String.valueOf(cliente.getDescuentoCliente()));
        observacionesArea.setText(cliente.getObservacionesCliente());

        // El CIF no debería ser editable (clave única)
        cifField.setEditable(false);
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            String sql = "UPDATE clientes SET nombreCliente=?, direccionCliente=?, cpCliente=?, poblacionCliente=?, " +
                    "provinciaCliente=?, paisCliente=?, telCliente=?, emailCliente=?, ibanCliente=?, " +
                    "riesgoCliente=?, descuentoCliente=?, observacionesCliente=? WHERE cifCliente=?";

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
                pstmt.setDouble(10, Double.parseDouble(riesgoField.getText().isEmpty() ? "0" : riesgoField.getText()));
                pstmt.setDouble(11, Double.parseDouble(descuentoField.getText().isEmpty() ? "0" : descuentoField.getText()));
                pstmt.setString(12, observacionesArea.getText());
                pstmt.setString(13, cifField.getText());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje("Cliente actualizado", "Los datos del cliente se han actualizado correctamente", Alert.AlertType.INFORMATION);
                    // Cerrar la ventana
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar el cliente", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                mostrarMensaje("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarMensaje("Error de formato", "Los campos de riesgo y descuento deben ser valores numéricos", Alert.AlertType.ERROR);
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
            errores.append("- El nombre del cliente es obligatorio\n");
        }

        if (cifField.getText().trim().isEmpty()) {
            errores.append("- El CIF es obligatorio\n");
        }

        // Validar que riesgo y descuento sean valores numéricos válidos
        try {
            if (!riesgoField.getText().trim().isEmpty()) {
                Double.parseDouble(riesgoField.getText());
            }
            if (!descuentoField.getText().trim().isEmpty()) {
                Double.parseDouble(descuentoField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- Los valores de riesgo y descuento deben ser numéricos\n");
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