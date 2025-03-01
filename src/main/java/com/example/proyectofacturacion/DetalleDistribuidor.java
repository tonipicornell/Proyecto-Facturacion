package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class DetalleDistribuidor {
    @FXML private TextField nombreEmpresaField;
    @FXML private TextField direccionField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField paginaWebField;
    @FXML private TextArea condicionesPagoArea;
    @FXML private TextField precioCompraField;
    @FXML private TextField margenGananciaField;
    @FXML private TextField volumenCompraField;
    @FXML private TextArea condicionesEntregaArea;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaTerminoPicker;
    @FXML private TextField territorioField;
    @FXML private TextArea metodosEnvioArea;
    @FXML private CheckBox soportePostventaCheck;

    private Distribuidor distribuidorActual;

    @FXML
    private void initialize() {
        // Inicializar controles si es necesario
    }

    public void setDistribuidor(Distribuidor distribuidor) {
        this.distribuidorActual = distribuidor;

        // Cargar datos en los campos
        nombreEmpresaField.setText(distribuidor.getNombreEmpresa());
        direccionField.setText(distribuidor.getDireccionFisica());
        telefonoField.setText(distribuidor.getTelefono());
        emailField.setText(distribuidor.getCorreoElectronico());
        paginaWebField.setText(distribuidor.getPaginaWeb());
        condicionesPagoArea.setText(distribuidor.getCondicionesPago());
        precioCompraField.setText(String.valueOf(distribuidor.getPrecioCompra()));
        margenGananciaField.setText(String.valueOf(distribuidor.getMargenGanancia()));
        volumenCompraField.setText(String.valueOf(distribuidor.getVolumenCompra()));
        condicionesEntregaArea.setText(distribuidor.getCondicionesEntrega());
        fechaInicioPicker.setValue(distribuidor.getContratoInicio());
        fechaTerminoPicker.setValue(distribuidor.getContratoTermino());
        territorioField.setText(distribuidor.getTerritorioAsignado());
        metodosEnvioArea.setText(distribuidor.getMetodosEnvio());
        soportePostventaCheck.setSelected(distribuidor.isSoportePostventa());
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            String sql = "UPDATE distribuidores SET nombre_empresa=?, direccion_fisica=?, telefono=?, " +
                    "correo_electronico=?, pagina_web=?, condiciones_pago=?, precio_compra=?, " +
                    "margen_ganancia=?, volumen_compra=?, condiciones_entrega=?, contrato_inicio=?, " +
                    "contrato_termino=?, territorio_asignado=?, metodos_envio=?, soporte_postventa=? " +
                    "WHERE id=?";

            try (Connection conn = DataBaseConnected.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, nombreEmpresaField.getText());
                pstmt.setString(2, direccionField.getText());
                pstmt.setString(3, telefonoField.getText());
                pstmt.setString(4, emailField.getText());
                pstmt.setString(5, paginaWebField.getText());
                pstmt.setString(6, condicionesPagoArea.getText());
                pstmt.setDouble(7, Double.parseDouble(precioCompraField.getText().isEmpty() ? "0" : precioCompraField.getText()));
                pstmt.setDouble(8, Double.parseDouble(margenGananciaField.getText().isEmpty() ? "0" : margenGananciaField.getText()));
                pstmt.setInt(9, Integer.parseInt(volumenCompraField.getText().isEmpty() ? "0" : volumenCompraField.getText()));
                pstmt.setString(10, condicionesEntregaArea.getText());
                pstmt.setDate(11, fechaInicioPicker.getValue() != null ? java.sql.Date.valueOf(fechaInicioPicker.getValue()) : null);
                pstmt.setDate(12, fechaTerminoPicker.getValue() != null ? java.sql.Date.valueOf(fechaTerminoPicker.getValue()) : null);
                pstmt.setString(13, territorioField.getText());
                pstmt.setString(14, metodosEnvioArea.getText());
                pstmt.setBoolean(15, soportePostventaCheck.isSelected());
                pstmt.setInt(16, distribuidorActual.getId());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje("Distribuidor actualizado", "Los datos del distribuidor se han actualizado correctamente", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar el distribuidor", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                mostrarMensaje("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarMensaje("Error de formato", "Los campos numéricos deben tener valores válidos", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreEmpresaField.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (nombreEmpresaField.getText().trim().isEmpty()) {
            errores.append("- El nombre de la empresa es obligatorio\n");
        }

        if (direccionField.getText().trim().isEmpty()) {
            errores.append("- La dirección física es obligatoria\n");
        }

        if (telefonoField.getText().trim().isEmpty()) {
            errores.append("- El teléfono es obligatorio\n");
        }

        // Validar campos numéricos
        try {
            if (!precioCompraField.getText().trim().isEmpty()) {
                Double.parseDouble(precioCompraField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- El precio de compra debe ser un valor numérico\n");
        }

        try {
            if (!margenGananciaField.getText().trim().isEmpty()) {
                Double.parseDouble(margenGananciaField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- El margen de ganancia debe ser un valor numérico\n");
        }

        try {
            if (!volumenCompraField.getText().trim().isEmpty()) {
                Integer.parseInt(volumenCompraField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- El volumen de compra debe ser un valor numérico entero\n");
        }

        // Validar fechas del contrato
        if (fechaInicioPicker.getValue() != null && fechaTerminoPicker.getValue() != null) {
            if (fechaInicioPicker.getValue().isAfter(fechaTerminoPicker.getValue())) {
                errores.append("- La fecha de inicio del contrato no puede ser posterior a la fecha de término\n");
            }
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