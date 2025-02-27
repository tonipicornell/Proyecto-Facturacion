package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetalleArticulo {
    @FXML private TextField codigoField;
    @FXML private TextField codigoBarrasField;
    @FXML private TextField descripcionField;
    @FXML private ComboBox<String> familiaCombo;
    @FXML private TextField costeField;
    @FXML private TextField margenField;
    @FXML private TextField pvpField;
    @FXML private ComboBox<String> proveedorCombo;
    @FXML private TextField stockField;
    @FXML private ComboBox<String> tipoIvaCombo;
    @FXML private TextField precioConIvaField;
    @FXML private TextArea observacionesArea;

    private Articulo articuloActual;
    private int familiaId;
    private int proveedorId;
    private int tipoIvaId;

    public void setArticulo(Articulo articulo) {
        this.articuloActual = articulo;

        // Cargar datos en los campos
        codigoField.setText(articulo.getCodigoArticulo());
        codigoBarrasField.setText(articulo.getCodigoBarrasArticulo());
        descripcionField.setText(articulo.getDescripcionArticulo());
        costeField.setText(String.valueOf(articulo.getCosteArticulo()));
        margenField.setText(String.valueOf(articulo.getMargenComercialArticulo()));
        pvpField.setText(String.valueOf(articulo.getPvpArticulo()));
        stockField.setText(String.valueOf(articulo.getStockArticulo()));
        observacionesArea.setText(articulo.getObservacionesArticulo());
        precioConIvaField.setText(String.valueOf(articulo.getPrecioConIVA()));

        // El código no debería ser editable (clave única)
        codigoField.setEditable(false);

        // Cargar y seleccionar familias, proveedores y tipos de IVA
        cargarFamilias();
        cargarProveedores();
        cargarTiposIva();

        // Establecer valores actuales
        familiaId = articulo.getFamiliaArticulo();
        proveedorId = articulo.getProveedorArticulo();
        tipoIvaId = articulo.getTipoIva();

        // Calcular el precio con IVA cuando cambia el PVP o el tipo de IVA
        pvpField.textProperty().addListener((observable, oldValue, newValue) -> calcularPrecioConIVA());
        tipoIvaCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> calcularPrecioConIVA());
    }

    private void cargarFamilias() {
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idFamiliaArticulos, denominacionFamilias FROM familiaArticulos ORDER BY denominacionFamilias");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idFamiliaArticulos");
                String nombre = rs.getString("denominacionFamilias");
                familiaCombo.getItems().add(nombre);

                // Seleccionar la familia actual del artículo
                if (id == familiaId) {
                    familiaCombo.setValue(nombre);
                }
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar familias", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarProveedores() {
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idProveedor, nombreProveedor FROM proveedores ORDER BY nombreProveedor");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idProveedor");
                String nombre = rs.getString("nombreProveedor");
                proveedorCombo.getItems().add(nombre);

                // Seleccionar el proveedor actual del artículo
                if (id == proveedorId) {
                    proveedorCombo.setValue(nombre);
                }
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar proveedores", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarTiposIva() {
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idTipoIva, descripcionTipoIva, porcentajeTipoIva FROM tiposIva ORDER BY porcentajeTipoIva");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idTipoIva");
                String descripcion = rs.getString("descripcionTipoIva") + " (" + rs.getDouble("porcentajeTipoIva") + "%)";
                tipoIvaCombo.getItems().add(descripcion);

                // Seleccionar el tipo de IVA actual del artículo
                if (id == tipoIvaId) {
                    tipoIvaCombo.setValue(descripcion);
                }
            }
        } catch (SQLException e) {
            mostrarMensaje("Error al cargar tipos de IVA", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void calcularPrecioConIVA() {
        try {
            double pvp = Double.parseDouble(pvpField.getText().isEmpty() ? "0" : pvpField.getText());
            String tipoIvaSeleccionado = tipoIvaCombo.getValue();

            if (tipoIvaSeleccionado != null) {
                // Extraer el porcentaje de IVA del texto del ComboBox
                int inicio = tipoIvaSeleccionado.lastIndexOf("(") + 1;
                int fin = tipoIvaSeleccionado.lastIndexOf("%");
                double porcentajeIva = Double.parseDouble(tipoIvaSeleccionado.substring(inicio, fin));

                // Calcular precio con IVA
                double precioConIva = pvp * (1 + (porcentajeIva / 100));
                precioConIvaField.setText(String.format("%.2f", precioConIva));
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Ignorar errores en la conversión mientras el usuario escribe
        }
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            String sql = "UPDATE articulos SET codigoBarrasArticulo=?, descripcionArticulo=?, familiaArticulo=?, " +
                    "costeArticulo=?, margenComercialArticulo=?, pvpArticulo=?, proveedorArticulo=?, " +
                    "stockArticulo=?, observacionesArticulo=?, tipoIva=?, precioConIVA=? WHERE codigoArticulo=?";

            try (Connection conn = DataBaseConnected.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // Obtener IDs de familias, proveedores y tipos de IVA
                int familiaId = obtenerFamiliaId(familiaCombo.getValue());
                int proveedorId = obtenerProveedorId(proveedorCombo.getValue());
                int tipoIvaId = obtenerTipoIvaId(tipoIvaCombo.getValue());

                pstmt.setString(1, codigoBarrasField.getText());
                pstmt.setString(2, descripcionField.getText());
                pstmt.setInt(3, familiaId);
                pstmt.setDouble(4, Double.parseDouble(costeField.getText().isEmpty() ? "0" : costeField.getText()));
                pstmt.setDouble(5, Double.parseDouble(margenField.getText().isEmpty() ? "0" : margenField.getText()));
                pstmt.setDouble(6, Double.parseDouble(pvpField.getText().isEmpty() ? "0" : pvpField.getText()));
                pstmt.setInt(7, proveedorId);
                pstmt.setDouble(8, Double.parseDouble(stockField.getText().isEmpty() ? "0" : stockField.getText()));
                pstmt.setString(9, observacionesArea.getText());
                pstmt.setInt(10, tipoIvaId);
                pstmt.setDouble(11, Double.parseDouble(precioConIvaField.getText().isEmpty() ? "0" : precioConIvaField.getText()));
                pstmt.setString(12, codigoField.getText());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas > 0) {
                    mostrarMensaje("Artículo actualizado", "Los datos del artículo se han actualizado correctamente", Alert.AlertType.INFORMATION);
                    // Cerrar la ventana
                    cerrarVentana();
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar el artículo", Alert.AlertType.ERROR);
                }

            } catch (SQLException e) {
                mostrarMensaje("Error de base de datos", e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                mostrarMensaje("Error de formato", "Los campos numéricos deben tener valores válidos", Alert.AlertType.ERROR);
            }
        }
    }

    private int obtenerFamiliaId(String nombreFamilia) throws SQLException {
        if (nombreFamilia == null || nombreFamilia.isEmpty()) {
            return 0;
        }
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idFamiliaArticulos FROM familiaArticulos WHERE denominacionFamilias = ?")) {
            pstmt.setString(1, nombreFamilia);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private int obtenerProveedorId(String nombreProveedor) throws SQLException {
        if (nombreProveedor == null || nombreProveedor.isEmpty()) {
            return 0;
        }
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idProveedor FROM proveedores WHERE nombreProveedor = ?")) {
            pstmt.setString(1, nombreProveedor);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private int obtenerTipoIvaId(String descripcionTipoIva) throws SQLException {
        if (descripcionTipoIva == null || descripcionTipoIva.isEmpty()) {
            return 0;
        }
        // Extraer la descripción sin el porcentaje
        String descripcion = descripcionTipoIva.substring(0, descripcionTipoIva.lastIndexOf("(")).trim();
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idTipoIva FROM tiposIva WHERE descripcionTipoIva = ?")) {
            pstmt.setString(1, descripcion);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) codigoField.getScene().getWindow();
        stage.close();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (codigoField.getText().trim().isEmpty()) {
            errores.append("- El código del artículo es obligatorio\n");
        }

        if (descripcionField.getText().trim().isEmpty()) {
            errores.append("- La descripción del artículo es obligatoria\n");
        }

        if (familiaCombo.getValue() == null) {
            errores.append("- Debe seleccionar una familia\n");
        }

        if (proveedorCombo.getValue() == null) {
            errores.append("- Debe seleccionar un proveedor\n");
        }

        if (tipoIvaCombo.getValue() == null) {
            errores.append("- Debe seleccionar un tipo de IVA\n");
        }

        // Validar campos numéricos
        try {
            if (!costeField.getText().trim().isEmpty()) {
                Double.parseDouble(costeField.getText());
            }
            if (!margenField.getText().trim().isEmpty()) {
                Double.parseDouble(margenField.getText());
            }
            if (!pvpField.getText().trim().isEmpty()) {
                Double.parseDouble(pvpField.getText());
            }
            if (!stockField.getText().trim().isEmpty()) {
                Double.parseDouble(stockField.getText());
            }
        } catch (NumberFormatException e) {
            errores.append("- Los campos numéricos (coste, margen, PVP, stock) deben ser valores válidos\n");
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