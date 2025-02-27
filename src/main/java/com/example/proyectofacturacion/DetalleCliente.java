package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetalleCliente {
    @FXML private TextField nombreField;
    @FXML private TextField cifField;
    @FXML private TextField direccionField;
    @FXML private TextField cpField;
    @FXML private TextField poblacionField;
    @FXML private TextField provinciaField;
    @FXML private TextField paisField;
    @FXML private TextField telField;
    @FXML private TextField emailField;
    @FXML private TextField ibanField;
    @FXML private TextField riesgoField;
    @FXML private TextField descuentoField;
    @FXML private TextArea observacionesArea;

    @FXML private Button guardarButton;
    @FXML private Button cancelarButton;

    private Cliente clienteActual;

    public void setCliente(Cliente cliente) {
        this.clienteActual = cliente;

        // Llenar los campos con la información del cliente
        nombreField.setText(cliente.getNombreCliente());
        cifField.setText(cliente.getCifCliente());
        direccionField.setText(cliente.getDireccionCliente());
        cpField.setText(cliente.getCpCliente());
        poblacionField.setText(cliente.getPoblacionCliente());
        provinciaField.setText(cliente.getProvinciaCliente());
        paisField.setText(cliente.getPaisCliente());
        telField.setText(cliente.getTelCliente());
        emailField.setText(cliente.getEmailCliente());
        ibanField.setText(cliente.getIbanCliente());
        riesgoField.setText(String.valueOf(cliente.getRiesgoCliente()));
        descuentoField.setText(String.valueOf(cliente.getDescuentoCliente()));
        observacionesArea.setText(cliente.getObservacionesCliente());
    }

    @FXML
    private void guardarCliente() {
        try {
            String sql = "UPDATE clientes SET nombreCliente = ?, direccionCliente = ?, cpCliente = ?, " +
                    "poblacionCliente = ?, provinciaCliente = ?, paisCliente = ?, cifCliente = ?, " +
                    "telCliente = ?, emailCliente = ?, ibanCliente = ?, riesgoCliente = ?, " +
                    "descuentoCliente = ?, observacionesCliente = ? " +
                    "WHERE cifCliente = ?";

            Connection conn = DataBaseConnected.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nombreField.getText());
            pstmt.setString(2, direccionField.getText());
            pstmt.setString(3, cpField.getText());
            pstmt.setString(4, poblacionField.getText());
            pstmt.setString(5, provinciaField.getText());
            pstmt.setString(6, paisField.getText());
            pstmt.setString(7, cifField.getText());
            pstmt.setString(8, telField.getText());
            pstmt.setString(9, emailField.getText());
            pstmt.setString(10, ibanField.getText());
            pstmt.setDouble(11, Double.parseDouble(riesgoField.getText()));
            pstmt.setDouble(12, Double.parseDouble(descuentoField.getText()));
            pstmt.setString(13, observacionesArea.getText());

            // Condición WHERE para actualizar el cliente correcto
            pstmt.setString(14, clienteActual.getCifCliente());

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            cerrarVentana();

        } catch (SQLException e) {
            e.printStackTrace();
            // Mostrar un alert al usuario
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Mostrar un alert al usuario indicando que los campos numéricos tienen formato incorrecto
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
}