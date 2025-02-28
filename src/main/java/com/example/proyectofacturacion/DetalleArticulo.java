package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @FXML private ComboBox<String> paisIvaCombo;
    @FXML private ComboBox<String> tipoIvaCombo;
    @FXML private TextField precioConIvaField;
    @FXML private TextArea observacionesArea;

    private Articulo articuloActual;
    private int familiaId;
    private int proveedorId;
    private int tipoIvaId;

    // Mapas para almacenar los IDs correspondientes a los nombres seleccionados en los ComboBox
    private Map<String, Integer> mapaFamilias = new HashMap<>();
    private Map<String, Integer> mapaProveedores = new HashMap<>();
    private Map<String, List<TipoIvaInfo>> mapaPaises = new HashMap<>();

    // Mapa inverso para obtener nombres a partir de IDs
    private Map<Integer, String> mapaFamiliasInverso = new HashMap<>();
    private Map<Integer, String> mapaProveedoresInverso = new HashMap<>();
    private Map<Integer, TipoIvaInfo> mapaTiposIvaInverso = new HashMap<>();

    // Clase interna para almacenar la información del IVA
    private static class TipoIvaInfo {
        private int id;
        private String pais;
        private String tipo;
        private double valor;

        public TipoIvaInfo(int id, String pais, String tipo, double valor) {
            this.id = id;
            this.pais = pais;
            this.tipo = tipo;
            this.valor = valor;
        }

        public int getId() { return id; }
        public String getPais() { return pais; }
        public String getTipo() { return tipo; }
        public double getValor() { return valor; }

        @Override
        public String toString() {
            return tipo + " (" + valor + "%)";
        }
    }

    public void initialize() {
        // Agregar validaciones para campos numéricos
        if (costeField != null) {
            agregarValidacionNumerica(costeField, "Coste");
        }

        if (margenField != null) {
            agregarValidacionNumerica(margenField, "Margen");
        }

        if (stockField != null) {
            agregarValidacionNumerica(stockField, "Stock");
        }

        // Configurar cálculo automático del precio con IVA
        if (pvpField != null) {
            pvpField.textProperty().addListener((observable, oldValue, newValue) -> calcularPrecioConIVA());
        }

        // Configurar el evento de selección del país
        if (paisIvaCombo != null) {
            paisIvaCombo.setOnAction(event -> {
                String paisSeleccionado = paisIvaCombo.getValue();
                if (paisSeleccionado != null) {
                    cargarTiposIVAPorPais(paisSeleccionado);
                }
            });
        }

        // Configurar el evento de selección del tipo de IVA
        if (tipoIvaCombo != null) {
            tipoIvaCombo.setOnAction(event -> calcularPrecioConIVA());
        }
    }

    private void agregarValidacionNumerica(TextField textField, String campo) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Patrón mejorado que acepta números enteros, números con punto decimal y números con coma decimal
            if (!newValue.isEmpty() && !newValue.matches("\\d*([.,]\\d*)?")) {
                mostrarMensaje("Valor no válido",
                        "El campo '" + campo + "' debe ser un número válido.",
                        Alert.AlertType.WARNING);
                textField.setText(oldValue);
            }
        });
    }

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

        // Cargar las referencias de los ComboBox
        tipoIvaId = articulo.getTipoIva();
        familiaId = articulo.getFamiliaArticulo();
        proveedorId = articulo.getProveedorArticulo();

        // Cargar los datos para los ComboBox
        cargarPaises();
        cargarFamilias();
        cargarProveedores();

        // Configurar cálculo automático
        pvpField.textProperty().addListener((observable, oldValue, newValue) -> calcularPrecioConIVA());
    }

    private void cargarPaises() {
        paisIvaCombo.getItems().clear();
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT pais FROM tiposIva ORDER BY pais");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String pais = rs.getString("pais");
                paisIvaCombo.getItems().add(pais);
                // Precargar los tipos de IVA para cada país
                cargarTiposIVAParaPais(pais);
            }

            // Si ya tenemos un tipo de IVA seleccionado, seleccionar el país correspondiente
            if (tipoIvaId > 0 && mapaTiposIvaInverso.containsKey(tipoIvaId)) {
                TipoIvaInfo tipoIva = mapaTiposIvaInverso.get(tipoIvaId);
                paisIvaCombo.setValue(tipoIva.getPais());
                // Cargar los tipos de IVA para el país seleccionado
                cargarTiposIVAPorPais(tipoIva.getPais());
                // Seleccionar el tipo de IVA específico
                tipoIvaCombo.setValue(tipoIva.toString());
            }
        } catch (SQLException e) {
            mostrarMensaje("Error", "Error al cargar países: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarTiposIVAParaPais(String pais) {
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idTipoIva, pais, tipoIva, iva FROM tiposIva WHERE pais = ?")) {

            pstmt.setString(1, pais);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<TipoIvaInfo> tiposIva = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("idTipoIva");
                    String tipo = rs.getString("tipoIva");
                    double valor = rs.getDouble("iva");
                    TipoIvaInfo tipoIvaInfo = new TipoIvaInfo(id, pais, tipo, valor);
                    tiposIva.add(tipoIvaInfo);
                    mapaTiposIvaInverso.put(id, tipoIvaInfo);
                }
                mapaPaises.put(pais, tiposIva);
            }
        } catch (SQLException e) {
            mostrarMensaje("Error", "Error al cargar tipos de IVA para " + pais + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarTiposIVAPorPais(String pais) {
        tipoIvaCombo.getItems().clear();
        if (mapaPaises.containsKey(pais)) {
            for (TipoIvaInfo tipoIva : mapaPaises.get(pais)) {
                tipoIvaCombo.getItems().add(tipoIva.toString());
            }

            // Si ya tenemos un tipo de IVA seleccionado y pertenece a este país, seleccionarlo
            if (tipoIvaId > 0 && mapaTiposIvaInverso.containsKey(tipoIvaId)) {
                TipoIvaInfo tipoIva = mapaTiposIvaInverso.get(tipoIvaId);
                if (tipoIva.getPais().equals(pais)) {
                    tipoIvaCombo.setValue(tipoIva.toString());
                }
            }
        }
    }

    // Obtener el tipo de IVA seleccionado
    private TipoIvaInfo getTipoIvaSeleccionado() {
        String paisSeleccionado = paisIvaCombo.getValue();
        String tipoIvaSeleccionado = tipoIvaCombo.getValue();

        if (paisSeleccionado != null && tipoIvaSeleccionado != null && mapaPaises.containsKey(paisSeleccionado)) {
            List<TipoIvaInfo> tiposIva = mapaPaises.get(paisSeleccionado);
            for (TipoIvaInfo tipoIva : tiposIva) {
                if (tipoIva.toString().equals(tipoIvaSeleccionado)) {
                    return tipoIva;
                }
            }
        }
        return null;
    }

    private void cargarFamilias() {
        familiaCombo.getItems().clear();
        mapaFamilias.clear();
        mapaFamiliasInverso.clear();

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idFamiliaArticulos, denominacionFamilias FROM familiaArticulos ORDER BY denominacionFamilias");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idFamiliaArticulos");
                String nombre = rs.getString("denominacionFamilias");
                familiaCombo.getItems().add(nombre);
                mapaFamilias.put(nombre, id);
                mapaFamiliasInverso.put(id, nombre);
            }

            // Seleccionar la familia actual del artículo
            if (familiaId > 0 && mapaFamiliasInverso.containsKey(familiaId)) {
                familiaCombo.setValue(mapaFamiliasInverso.get(familiaId));
            }
        } catch (SQLException e) {
            mostrarMensaje("Error", "Error al cargar familias: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarProveedores() {
        proveedorCombo.getItems().clear();
        mapaProveedores.clear();
        mapaProveedoresInverso.clear();

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT idProveedor, nombreProveedor FROM proveedores ORDER BY nombreProveedor");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idProveedor");
                String nombre = rs.getString("nombreProveedor");
                proveedorCombo.getItems().add(nombre);
                mapaProveedores.put(nombre, id);
                mapaProveedoresInverso.put(id, nombre);
            }

            // Seleccionar el proveedor actual del artículo
            if (proveedorId > 0 && mapaProveedoresInverso.containsKey(proveedorId)) {
                proveedorCombo.setValue(mapaProveedoresInverso.get(proveedorId));
            }
        } catch (SQLException e) {
            mostrarMensaje("Error", "Error al cargar proveedores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void calcularPrecioConIVA() {
        if (pvpField == null || precioConIvaField == null) {
            return; // No hacer nada si algún campo es nulo
        }

        try {
            String pvpText = pvpField.getText();
            if (pvpText == null || pvpText.isEmpty()) {
                pvpText = "0";
            }
            double pvp = Double.parseDouble(pvpText.replace(',', '.'));
            TipoIvaInfo tipoIvaInfo = getTipoIvaSeleccionado();

            if (tipoIvaInfo != null) {
                double porcentajeIva = tipoIvaInfo.getValor();
                // Calcular precio con IVA
                double precioConIva = pvp * (1 + (porcentajeIva / 100));
                precioConIvaField.setText(String.format("%.2f", precioConIva));
            }
        } catch (NumberFormatException e) {
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

                // Obtener el tipo de IVA seleccionado
                TipoIvaInfo tipoIvaInfo = getTipoIvaSeleccionado();
                if (tipoIvaInfo == null) {
                    mostrarMensaje("Error", "Debe seleccionar un tipo de IVA válido", Alert.AlertType.ERROR);
                    return;
                }

                // Obtener IDs de los ComboBox
                Integer familiaId = mapaFamilias.get(familiaCombo.getValue());
                Integer proveedorId = mapaProveedores.get(proveedorCombo.getValue());

                pstmt.setString(1, codigoBarrasField.getText());
                pstmt.setString(2, descripcionField.getText());
                pstmt.setInt(3, familiaId);
                pstmt.setDouble(4, Double.parseDouble(costeField.getText().isEmpty() ? "0" : costeField.getText().replace(',', '.')));
                pstmt.setDouble(5, Double.parseDouble(margenField.getText().isEmpty() ? "0" : margenField.getText().replace(',', '.')));
                pstmt.setDouble(6, Double.parseDouble(pvpField.getText().isEmpty() ? "0" : pvpField.getText().replace(',', '.')));
                pstmt.setInt(7, proveedorId);
                pstmt.setDouble(8, Double.parseDouble(stockField.getText().isEmpty() ? "0" : stockField.getText().replace(',', '.')));
                pstmt.setString(9, observacionesArea.getText());
                pstmt.setInt(10, tipoIvaInfo.getId());
                pstmt.setDouble(11, Double.parseDouble(precioConIvaField.getText().isEmpty() ? "0" : precioConIvaField.getText().replace(',', '.')));
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

        if (paisIvaCombo.getValue() == null) {
            errores.append("- Debe seleccionar un país de IVA\n");
        }

        if (tipoIvaCombo.getValue() == null) {
            errores.append("- Debe seleccionar un tipo de IVA\n");
        }

        // Validar campos numéricos
        try {
            if (!costeField.getText().trim().isEmpty()) {
                Double.parseDouble(costeField.getText().replace(',', '.'));
            }
            if (!margenField.getText().trim().isEmpty()) {
                Double.parseDouble(margenField.getText().replace(',', '.'));
            }
            if (!pvpField.getText().trim().isEmpty()) {
                Double.parseDouble(pvpField.getText().replace(',', '.'));
            }
            if (!stockField.getText().trim().isEmpty()) {
                Double.parseDouble(stockField.getText().replace(',', '.'));
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