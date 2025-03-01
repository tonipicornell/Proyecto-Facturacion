package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class CrearDistribuidor {
    @FXML
    private TextField nombreEmpresaField;
    @FXML
    private TextField direccionFisicaField;
    @FXML
    private TextField telefonoField;
    @FXML
    private TextField correoElectronicoField;
    @FXML
    private TextField paginaWebField;
    @FXML
    private TextArea condicionesPagoArea;
    @FXML
    private TextField precioCompraField;
    @FXML
    private TextField margenGananciaField;
    @FXML
    private TextField volumenCompraField;
    @FXML
    private TextArea condicionesEntregaArea;
    @FXML
    private DatePicker contratoInicioPicker;
    @FXML
    private DatePicker contratoTerminoPicker;
    @FXML
    private TextArea territorioAsignadoArea;
    @FXML
    private ComboBox<String> metodosEnvioCombo;
    @FXML
    private CheckBox soportePostventaCheck;
    @FXML
    private Button guardarButton;
    @FXML
    private Button cancelarButton;

    @FXML
    private void initialize() {
        // Inicializar ComboBox con opciones predefinidas
        metodosEnvioCombo.setItems(FXCollections.observableArrayList(
                "Terrestre", "Marítimo", "Aéreo", "Mixto", "Urgente", "Standard"
        ));

        // Establecer fechas por defecto
        contratoInicioPicker.setValue(LocalDate.now());
        contratoTerminoPicker.setValue(LocalDate.now().plusYears(1));
    }

    @FXML
    private void handleGuardar() {
        if (!validarCamposObligatorios()) {
            mostrarAlerta("Campos incompletos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        try {
            // Intentar convertir valores numéricos
            double precioCompra = 0.0;
            double margenGanancia = 0.0;
            int volumenCompra = 0;

            try {
                if (!precioCompraField.getText().isEmpty()) {
                    precioCompra = Double.parseDouble(precioCompraField.getText().replace(",", "."));
                }
                if (!margenGananciaField.getText().isEmpty()) {
                    margenGanancia = Double.parseDouble(margenGananciaField.getText().replace(",", "."));
                }
                if (!volumenCompraField.getText().isEmpty()) {
                    volumenCompra = Integer.parseInt(volumenCompraField.getText());
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error en el formato", "Los valores numéricos deben tener un formato válido.");
                return;
            }

            // Verificar si la empresa ya existe
            if (existeEmpresa(nombreEmpresaField.getText())) {
                mostrarAlerta("Empresa duplicada", "Ya existe un distribuidor con ese nombre en la base de datos.");
                return;
            }

            // Insertar en la base de datos
            if (insertarDistribuidor(precioCompra, margenGanancia, volumenCompra)) {
                mostrarInformacion("Registro exitoso", "El distribuidor ha sido registrado correctamente.");
                cerrarVentana();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos", "No se pudo guardar el distribuidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean existeEmpresa(String nombreEmpresa) throws SQLException {
        String query = "SELECT COUNT(*) FROM distribuidores WHERE nombre_empresa = ?";
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreEmpresa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private boolean insertarDistribuidor(double precioCompra, double margenGanancia, int volumenCompra) throws SQLException {
        String sql = "INSERT INTO distribuidores (nombre_empresa, direccion_fisica, telefono, correo_electronico, " +
                "pagina_web, condiciones_pago, precio_compra, margen_ganancia, volumen_compra, " +
                "condiciones_entrega, contrato_inicio, contrato_termino, territorio_asignado, " +
                "metodos_envio, soporte_postventa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Configurar todos los parámetros
            int paramIndex = 1;
            stmt.setString(paramIndex++, nombreEmpresaField.getText());
            stmt.setString(paramIndex++, direccionFisicaField.getText());
            stmt.setString(paramIndex++, telefonoField.getText());
            stmt.setString(paramIndex++, correoElectronicoField.getText());
            stmt.setString(paramIndex++, paginaWebField.getText());
            stmt.setString(paramIndex++, condicionesPagoArea.getText());
            stmt.setDouble(paramIndex++, precioCompra);
            stmt.setDouble(paramIndex++, margenGanancia);
            stmt.setInt(paramIndex++, volumenCompra);
            stmt.setString(paramIndex++, condicionesEntregaArea.getText());

            // Fechas
            if (contratoInicioPicker.getValue() != null) {
                stmt.setDate(paramIndex++, Date.valueOf(contratoInicioPicker.getValue()));
            } else {
                stmt.setNull(paramIndex++, Types.DATE);
            }

            if (contratoTerminoPicker.getValue() != null) {
                stmt.setDate(paramIndex++, Date.valueOf(contratoTerminoPicker.getValue()));
            } else {
                stmt.setNull(paramIndex++, Types.DATE);
            }

            stmt.setString(paramIndex++, territorioAsignadoArea.getText());
            stmt.setString(paramIndex++, metodosEnvioCombo.getValue());
            stmt.setBoolean(paramIndex++, soportePostventaCheck.isSelected());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    private boolean validarCamposObligatorios() {
        return !nombreEmpresaField.getText().isEmpty();
    }

    @FXML
    private void handleCancelar() {
        if (hayCambios()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar cancelación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Está seguro que desea cancelar? Se perderán todos los datos ingresados.");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    cerrarVentana();
                }
            });
        } else {
            cerrarVentana();
        }
    }

    private boolean hayCambios() {
        return !nombreEmpresaField.getText().isEmpty() ||
                !direccionFisicaField.getText().isEmpty() ||
                !telefonoField.getText().isEmpty() ||
                !correoElectronicoField.getText().isEmpty() ||
                !paginaWebField.getText().isEmpty() ||
                !condicionesPagoArea.getText().isEmpty() ||
                !precioCompraField.getText().isEmpty() ||
                !margenGananciaField.getText().isEmpty() ||
                !volumenCompraField.getText().isEmpty() ||
                !condicionesEntregaArea.getText().isEmpty() ||
                contratoInicioPicker.getValue() != null ||
                contratoTerminoPicker.getValue() != null ||
                !territorioAsignadoArea.getText().isEmpty() ||
                metodosEnvioCombo.getValue() != null ||
                soportePostventaCheck.isSelected();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}