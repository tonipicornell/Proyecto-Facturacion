package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.sql.*;

public class ModificarCliente {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField introducir_cif_cliente;

    @FXML
    private TextField introducir_cliente;

    @FXML
    private TextField direccion_cliente;

    @FXML
    private TextField cp_cliente;

    @FXML
    private TextField poblacion_cliente;

    @FXML
    private TextField provincia_cliente;

    @FXML
    private TextField pais_cliente;

    @FXML
    private TextField telefono_cliente;

    @FXML
    private TextField email_cliente;

    @FXML
    private TextField introducir_iban_cliente;

    @FXML
    private TextField riesgo_cliente;

    @FXML
    private TextField descuento_cliente;

    @FXML
    private TextArea observaciones_cliente;

    @FXML
    private Button boton_crear_cliente;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DataBaseConnected.getConnection();
            // Inicialmente ocultar todos los campos excepto CIF
            ocultarCampos(true);

            // Configurar el evento Enter en el campo CIF
            introducir_cif_cliente.setOnAction(event -> buscarClientePorCIF());

            // Configurar el botón modificar
            boton_crear_cliente.setOnAction(event -> modificarCliente());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos.");
        }
    }

    private void ocultarCampos(boolean ocultar) {
        // Lista de todos los campos que se deben ocultar/mostrar
        javafx.scene.Node[] campos = {
                introducir_cliente,
                direccion_cliente,
                cp_cliente,
                poblacion_cliente,
                provincia_cliente,
                pais_cliente,
                telefono_cliente,
                email_cliente,
                introducir_iban_cliente,
                riesgo_cliente,
                descuento_cliente,
                observaciones_cliente,
                boton_crear_cliente
        };

        for (javafx.scene.Node campo : campos) {
            campo.setVisible(!ocultar);
            campo.setManaged(!ocultar); // Esto evita que el espacio se reserve cuando está oculto
        }
    }

    @FXML
    private void buscarClientePorCIF() {
        String cif = introducir_cif_cliente.getText().trim();
        if (cif.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "CIF vacío", "Por favor, introduce un CIF.");
            return;
        }

        String query = "SELECT * FROM clientes WHERE cifCliente = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, cif);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Mostrar los campos primero
                ocultarCampos(false);

                // Luego rellenar con los datos
                introducir_cliente.setText(resultSet.getString("nombreCliente"));
                direccion_cliente.setText(resultSet.getString("direccionCliente"));
                cp_cliente.setText(resultSet.getString("cpCliente"));
                poblacion_cliente.setText(resultSet.getString("poblacionCliente"));
                provincia_cliente.setText(resultSet.getString("provinciaCliente"));
                pais_cliente.setText(resultSet.getString("paisCliente"));
                telefono_cliente.setText(resultSet.getString("telCliente"));
                email_cliente.setText(resultSet.getString("emailCliente"));
                introducir_iban_cliente.setText(resultSet.getString("ibanCliente"));
                riesgo_cliente.setText(String.valueOf(resultSet.getDouble("riesgoCliente")));
                descuento_cliente.setText(String.valueOf(resultSet.getDouble("descuentoCliente")));
                observaciones_cliente.setText(resultSet.getString("observacionesCliente"));
            } else {
                ocultarCampos(true);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente no encontrado", "No se encontró un cliente con ese CIF.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la búsqueda", "Ocurrió un error al buscar el cliente.");
        }
    }

    @FXML
    private void modificarCliente() {
        try {
            // Validaciones básicas
            if (introducir_cif_cliente.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error", "El CIF es obligatorio.");
                return;
            }

            String query = "UPDATE clientes SET nombreCliente=?, direccionCliente=?, cpCliente=?, " +
                    "poblacionCliente=?, provinciaCliente=?, paisCliente=?, telCliente=?, " +
                    "emailCliente=?, ibanCliente=?, riesgoCliente=?, descuentoCliente=?, " +
                    "observacionesCliente=? WHERE cifCliente=?";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, introducir_cliente.getText().trim());
                stmt.setString(2, direccion_cliente.getText().trim());
                stmt.setString(3, cp_cliente.getText().trim());
                stmt.setString(4, poblacion_cliente.getText().trim());
                stmt.setString(5, provincia_cliente.getText().trim());
                stmt.setString(6, pais_cliente.getText().trim());
                stmt.setString(7, telefono_cliente.getText().trim());
                stmt.setString(8, email_cliente.getText().trim());
                stmt.setString(9, introducir_iban_cliente.getText().trim());

                // Convertir valores numéricos con manejo de errores
                double riesgo = 0.0;
                double descuento = 0.0;
                try {
                    riesgo = Double.parseDouble(riesgo_cliente.getText().trim());
                    descuento = Double.parseDouble(descuento_cliente.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "Los campos de riesgo y descuento deben ser números válidos.");
                    return;
                }

                stmt.setDouble(10, riesgo);
                stmt.setDouble(11, descuento);
                stmt.setString(12, observaciones_cliente.getText().trim());
                stmt.setString(13, introducir_cif_cliente.getText().trim());

                int filasActualizadas = stmt.executeUpdate();
                if (filasActualizadas > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente actualizado correctamente.");
                    ocultarCampos(true);
                    introducir_cif_cliente.clear();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el cliente.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la actualización",
                    "Ocurrió un error al modificar el cliente: " + e.getMessage());
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