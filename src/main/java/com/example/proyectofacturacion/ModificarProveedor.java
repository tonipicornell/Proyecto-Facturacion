package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.sql.*;

public class ModificarProveedor {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField introducir_cif_proveedor;

    @FXML
    private TextField introducir_proveedor;

    @FXML
    private TextField direccion_proveedor;

    @FXML
    private TextField cp_proveedor;

    @FXML
    private TextField poblacion_proveedor;

    @FXML
    private TextField provincia_proveedor;

    @FXML
    private TextField pais_proveedor;

    @FXML
    private TextField telefono_proveedor;

    @FXML
    private TextField email_proveedor;

    @FXML
    private TextField introducir_iban_proveedor;

    @FXML
    private TextField contacto_proveedor;

    @FXML
    private TextArea observaciones_proveedor;

    @FXML
    private Button boton_crear_proveedor;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DataBaseConnected.getConnection();
            // Inicialmente ocultar todos los campos excepto CIF
            ocultarCampos(true);

            // Configurar el evento Enter en el campo CIF
            introducir_cif_proveedor.setOnAction(event -> buscarProveedorPorCIF());

            // Configurar el botón modificar
            boton_crear_proveedor.setOnAction(event -> modificarProveedor());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos.");
        }
    }

    private void ocultarCampos(boolean ocultar) {
        // Lista de todos los campos que se deben ocultar/mostrar
        javafx.scene.Node[] campos = {
                introducir_proveedor,
                direccion_proveedor,
                cp_proveedor,
                poblacion_proveedor,
                provincia_proveedor,
                pais_proveedor,
                telefono_proveedor,
                email_proveedor,
                introducir_iban_proveedor,
                contacto_proveedor,
                observaciones_proveedor,
                boton_crear_proveedor
        };

        for (javafx.scene.Node campo : campos) {
            campo.setVisible(!ocultar);
            campo.setManaged(!ocultar); // Esto evita que el espacio se reserve cuando está oculto
        }
    }

    @FXML
    private void buscarProveedorPorCIF() {
        String cif = introducir_cif_proveedor.getText().trim();
        if (cif.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "CIF vacío", "Por favor, introduce un CIF.");
            return;
        }

        String query = "SELECT * FROM proveedores WHERE cifProveedor = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cif);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Mostrar los campos primero
                ocultarCampos(false);

                // Luego rellenar con los datos
                introducir_proveedor.setText(resultSet.getString("nombreProveedor"));
                direccion_proveedor.setText(resultSet.getString("direccionProveedor"));
                cp_proveedor.setText(resultSet.getString("cpProveedor"));
                poblacion_proveedor.setText(resultSet.getString("poblacionProveedor"));
                provincia_proveedor.setText(resultSet.getString("provinciaProveedor"));
                pais_proveedor.setText(resultSet.getString("paisProveedor"));
                telefono_proveedor.setText(resultSet.getString("telefonoProveedor"));
                email_proveedor.setText(resultSet.getString("emailProveedor"));
                introducir_iban_proveedor.setText(resultSet.getString("ibanProveedor"));
                contacto_proveedor.setText(resultSet.getString("contactoProveedor"));
                observaciones_proveedor.setText(resultSet.getString("observacionesProveedor"));
            } else {
                ocultarCampos(true);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Proveedor no encontrado", "No se encontró un proveedor con ese CIF.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la búsqueda", "Ocurrió un error al buscar el proveedor.");
        }
    }

    @FXML
    private void modificarProveedor() {
        try {
            // Validaciones básicas
            if (introducir_cif_proveedor.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error", "El CIF es obligatorio.");
                return;
            }

            if (introducir_proveedor.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error", "El nombre del proveedor es obligatorio.");
                return;
            }

            String query = "UPDATE proveedores SET nombreProveedor=?, direccionProveedor=?, cpProveedor=?, " +
                    "poblacionProveedor=?, provinciaProveedor=?, paisProveedor=?, telefonoProveedor=?, " +
                    "emailProveedor=?, ibanProveedor=?, contactoProveedor=?, " +
                    "observacionesProveedor=? WHERE cifProveedor=?";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, introducir_proveedor.getText().trim());
                stmt.setString(2, direccion_proveedor.getText().trim());
                stmt.setString(3, cp_proveedor.getText().trim());
                stmt.setString(4, poblacion_proveedor.getText().trim());
                stmt.setString(5, provincia_proveedor.getText().trim());
                stmt.setString(6, pais_proveedor.getText().trim());
                stmt.setString(7, telefono_proveedor.getText().trim());
                stmt.setString(8, email_proveedor.getText().trim());
                stmt.setString(9, introducir_iban_proveedor.getText().trim());
                stmt.setString(10, contacto_proveedor.getText().trim());
                stmt.setString(11, observaciones_proveedor.getText().trim());
                stmt.setString(12, introducir_cif_proveedor.getText().trim());

                int filasActualizadas = stmt.executeUpdate();
                if (filasActualizadas > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Proveedor actualizado correctamente.");
                    ocultarCampos(true);
                    introducir_cif_proveedor.clear();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el proveedor.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la actualización",
                    "Ocurrió un error al modificar el proveedor: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}