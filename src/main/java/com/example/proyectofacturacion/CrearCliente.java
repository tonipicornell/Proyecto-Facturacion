package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearCliente {

    @FXML
    private TextField introducirCliente;
    @FXML
    private TextField direccionCliente;
    @FXML
    private TextField cpCliente;
    @FXML
    private TextField poblacionCliente;
    @FXML
    private TextField introducirCifCliente;
    @FXML
    private TextField introducirIbanCliente;
    @FXML
    private TextField telefonoCliente;
    @FXML
    private TextField provinciaCliente;
    @FXML
    private TextField paisCliente;
    @FXML
    private TextField emailCliente;
    @FXML
    private TextField riesgoCliente;
    @FXML
    private TextField descuentoCliente;
    @FXML
    private TextArea observacionesCliente;
    @FXML
    private Button botonCrearCliente;

    @FXML
    private void initialize() {
        agregarValidacionNumerica(riesgoCliente, "Riesgo");
        agregarValidacionNumerica(descuentoCliente, "Descuento");
    }

    private void agregarValidacionNumerica(TextField textField, String campo) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) { // Solo permite números y punto decimal
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Valor no válido");
                alert.setHeaderText(null);
                alert.setContentText("El campo '" + campo + "' debe ser un número válido.");
                alert.showAndWait();
                textField.setText(oldValue); // Revertir al valor anterior
            }
        });
    }

    @FXML
    private void crearCliente() {
        if (introducirCliente.getText().isEmpty() || direccionCliente.getText().isEmpty() ||
                poblacionCliente.getText().isEmpty() || introducirCifCliente.getText().isEmpty() ||
                introducirIbanCliente.getText().isEmpty() || telefonoCliente.getText().isEmpty() ||
                provinciaCliente.getText().isEmpty() || paisCliente.getText().isEmpty() ||
                emailCliente.getText().isEmpty() || riesgoCliente.getText().isEmpty() ||
                descuentoCliente.getText().isEmpty() || cpCliente.getText().isEmpty()) {

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Campo obligatorio");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, complete todos los campos.");
            alert.showAndWait();
            return;
        }

        double riesgo;
        double descuento;

        try {
            riesgo = Double.parseDouble(riesgoCliente.getText());
            descuento = Double.parseDouble(descuentoCliente.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Valor no válido");
            alert.setHeaderText(null);
            alert.setContentText("Los campos 'Riesgo' y 'Descuento' deben ser números válidos.");
            alert.showAndWait();
            return;
        }

        String nombre = introducirCliente.getText();
        String direccion = direccionCliente.getText();
        String cp = cpCliente.getText();
        String poblacion = poblacionCliente.getText();
        String cif = introducirCifCliente.getText();
        String iban = introducirIbanCliente.getText();
        String telefono = telefonoCliente.getText();
        String provincia = provinciaCliente.getText();
        String pais = paisCliente.getText();
        String email = emailCliente.getText();
        String observaciones = observacionesCliente.getText();

        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "INSERT INTO clientes (nombreCliente, direccionCliente, cpCliente, poblacionCliente, " +
                    "cifCliente, ibanCliente, telCliente, provinciaCliente, paisCliente, emailCliente, " +
                    "riesgoCliente, descuentoCliente, observacionesCliente) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nombre);
                statement.setString(2, direccion);
                statement.setString(3, cp);
                statement.setString(4, poblacion);
                statement.setString(5, cif);
                statement.setString(6, iban);
                statement.setString(7, telefono);
                statement.setString(8, provincia);
                statement.setString(9, pais);
                statement.setString(10, email);
                statement.setDouble(11, riesgo);
                statement.setDouble(12, descuento);
                statement.setString(13, observaciones);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Éxito");
                    alert.setHeaderText(null);
                    alert.setContentText("Cliente creado exitosamente.");
                    alert.showAndWait();
                    limpiarCampos();
                }
            } catch (SQLException e) {
                mostrarError("No se pudo crear el cliente. Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            mostrarError("No se pudo conectar a la base de datos. Error: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        introducirCliente.clear();
        direccionCliente.clear();
        cpCliente.clear();
        poblacionCliente.clear();
        introducirCifCliente.clear();
        introducirIbanCliente.clear();
        telefonoCliente.clear();
        provinciaCliente.clear();
        paisCliente.clear();
        emailCliente.clear();
        riesgoCliente.clear();
        descuentoCliente.clear();
        observacionesCliente.clear();
    }
}
