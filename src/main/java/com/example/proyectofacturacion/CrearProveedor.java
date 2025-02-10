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

public class CrearProveedor {

    @FXML
    private TextField introducirProveedor;
    @FXML
    private TextField direccionProveedor;
    @FXML
    private TextField cpProveedor;  // Campo CP agregado
    @FXML
    private TextField poblacionProveedor;
    @FXML
    private TextField introducirCifProveedor;
    @FXML
    private TextField introducirIbanProveedor;
    @FXML
    private TextField telefonoProveedor;
    @FXML
    private TextField provinciaProveedor;
    @FXML
    private TextField paisProveedor;
    @FXML
    private TextField emailProveedor;
    @FXML
    private TextField contactoProveedor;
    @FXML
    private TextArea observacionesProveedor;  // TextArea agregado
    @FXML
    private Button botonCrearProveedor;

    @FXML
    private void crearProveedor() {
        // Validación de campos vacíos
        if (introducirProveedor.getText().isEmpty() || direccionProveedor.getText().isEmpty() ||
                poblacionProveedor.getText().isEmpty() || introducirCifProveedor.getText().isEmpty() ||
                introducirIbanProveedor.getText().isEmpty() || telefonoProveedor.getText().isEmpty() ||
                provinciaProveedor.getText().isEmpty() || paisProveedor.getText().isEmpty() ||
                emailProveedor.getText().isEmpty() || contactoProveedor.getText().isEmpty() ||
                cpProveedor.getText().isEmpty()) {

            // Mostrar un aviso si algún campo está vacío
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Campo obligatorio");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, complete todos los campos.");
            alert.showAndWait();
        } else {
            // Si todo está bien, continuar con la creación del proveedor
            String nombre = introducirProveedor.getText();
            String direccion = direccionProveedor.getText();
            String cp = cpProveedor.getText();
            String poblacion = poblacionProveedor.getText();
            String cif = introducirCifProveedor.getText();
            String iban = introducirIbanProveedor.getText();
            String telefono = telefonoProveedor.getText();
            String provincia = provinciaProveedor.getText();
            String pais = paisProveedor.getText();
            String email = emailProveedor.getText();
            String contacto = contactoProveedor.getText();
            String observaciones = observacionesProveedor.getText();

            // Realizar la inserción en la base de datos
            try (Connection connection = DataBaseConnected.getConnection()) {
                String query = "INSERT INTO proveedores (nombreProveedor, direccionProveedor, cpProveedor, poblacionProveedor, " +
                        "cifProveedor, ibanProveedor, telefonoProveedor, provinciaProveedor, paisProveedor, emailProveedor, " +
                        "contactoProveedor, observacionesProveedor) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                    statement.setString(11, contacto);
                    statement.setString(12, observaciones);

                    // Ejecutar la inserción
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        // Proveedor creado exitosamente
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Éxito");
                        alert.setHeaderText(null);
                        alert.setContentText("Proveedor creado exitosamente.");
                        alert.showAndWait();

                        // Limpiar los campos del formulario
                        limpiarCampos();
                    }
                } catch (SQLException e) {
                    // Manejo de errores si no se pudo insertar
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error de inserción");
                    alert.setHeaderText("No se pudo insertar el proveedor.");
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                // Manejo de errores si no se pudo conectar
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText("No se pudo conectar a la base de datos.");
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Método para limpiar los campos
    private void limpiarCampos() {
        introducirProveedor.clear();
        direccionProveedor.clear();
        cpProveedor.clear();
        poblacionProveedor.clear();
        introducirCifProveedor.clear();
        introducirIbanProveedor.clear();
        telefonoProveedor.clear();
        provinciaProveedor.clear();
        paisProveedor.clear();
        emailProveedor.clear();
        contactoProveedor.clear();
        observacionesProveedor.clear();
    }
}
