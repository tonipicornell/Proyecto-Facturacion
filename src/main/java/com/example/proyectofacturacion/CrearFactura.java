package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;

public class CrearFactura {

    // Cliente
    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private TextField txtDireccion, txtCIF, txtPoblacion, txtProvincia, txtPais;

    // Factura
    @FXML private TextField txtNumeroFactura;
    @FXML private DatePicker dateFechaFactura, dateFechaCobro;
    @FXML private ComboBox<String> comboFormaPago;
    @FXML private CheckBox checkCobrada;
    @FXML private TextArea txtObservaciones;

    // Artículos
    @FXML private ComboBox<Articulo> comboArticulos;
    @FXML private TextField txtCantidad, txtPrecioSinIVA, txtPrecioConIVA, txtDescuento;
    @FXML private Button btnAgregarLinea, btnEliminarLinea;

    // Tabla de líneas de factura
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colCodigo, colDescripcion;
    @FXML private TableColumn<LineaFactura, Double> colCantidad, colPrecio, colDescuento, colImporte, colIVA;

    // Totales
    @FXML private TextField txtBaseImponible, txtTotalIVA, txtTotalFactura;

    // Botones de acción
    @FXML private Button btnGuardar, btnCancelar;

    // Listas para los ComboBox
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final ObservableList<Articulo> listaArticulos = FXCollections.observableArrayList();
    private final ObservableList<String> listaFormasPago = FXCollections.observableArrayList();
    private final ObservableList<LineaFactura> lineasFactura = FXCollections.observableArrayList();

    // Constantes
    private static final double IVA_POR_DEFECTO = 21.0;
    private static final String CANTIDAD_POR_DEFECTO = "1";

    @FXML
    public void initialize() {
        dateFechaFactura.setValue(LocalDate.now());
        configurarTabla();
        cargarDatosIniciales();
        configurarListeners();
        generarNumeroFactura();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colIVA.setCellValueFactory(new PropertyValueFactory<>("porcentajeIVA"));
        tablaLineasFactura.setItems(lineasFactura);
    }

    private void cargarDatosIniciales() {
        cargarClientes();
        cargarArticulos();
        cargarFormasPago();
    }

    private void cargarClientes() {
        ejecutarConsulta("SELECT * FROM clientes",
                rs -> {
                    Cliente cliente = new Cliente(
                            rs.getString("nombreCliente"),
                            rs.getString("direccionCliente"),
                            rs.getString("cpCliente"),
                            rs.getString("poblacionCliente"),
                            rs.getString("provinciaCliente"),
                            rs.getString("paisCliente"),
                            rs.getString("cifCliente"),
                            rs.getString("telCliente"),
                            rs.getString("emailCliente"),
                            rs.getString("ibanCliente"),
                            rs.getDouble("riesgoCliente"),
                            rs.getDouble("descuentoCliente"),
                            rs.getString("observacionesCliente")
                    );
                    listaClientes.add(cliente);
                },
                "Error al cargar clientes"
        );
        comboClientes.setItems(listaClientes);
        comboClientes.setConverter(new ClienteStringConverter());
    }

    private void cargarArticulos() {
        String sql = "SELECT a.*, f.denominacionFamilias, t.iva " +
                "FROM articulos a " +
                "LEFT JOIN familiaArticulos f ON a.familiaArticulo = f.idFamiliaArticulos " +
                "LEFT JOIN tiposIva t ON a.tipoIva = t.idTipoIva";

        ejecutarConsulta(sql,
                rs -> {
                    double precioSinIVA = rs.getDouble("pvpArticulo");
                    double porcentajeIVA = rs.getDouble("iva");
                    double precioConIVA = precioSinIVA * (1 + porcentajeIVA / 100);

                    Articulo articulo = new Articulo(
                            rs.getString("codigoArticulo"),
                            rs.getString("codigoBarrasArticulo"),
                            rs.getString("descripcionArticulo"),
                            rs.getInt("familiaArticulo"),
                            rs.getString("denominacionFamilias"),
                            rs.getDouble("costeArticulo"),
                            rs.getDouble("margenComercialArticulo"),
                            precioSinIVA,
                            rs.getInt("proveedorArticulo"),
                            rs.getDouble("stockArticulo"),
                            rs.getString("observacionesArticulo"),
                            rs.getInt("tipoIva"),
                            precioConIVA
                    );
                    listaArticulos.add(articulo);
                },
                "Error al cargar artículos"
        );
        comboArticulos.setItems(listaArticulos);
        comboArticulos.setConverter(new ArticuloStringConverter());
    }

    private void cargarFormasPago() {
        ejecutarConsulta("SELECT * FROM formaPago",
                rs -> listaFormasPago.add(rs.getString("tipoFormaPago")),
                "Error al cargar formas de pago"
        );
        comboFormaPago.setItems(listaFormasPago);
    }

    private void configurarListeners() {
        comboClientes.setOnAction(e -> {
            Cliente cliente = comboClientes.getValue();
            if (cliente != null) {
                txtDireccion.setText(cliente.getDireccionCliente());
                txtCIF.setText(cliente.getCifCliente());
                txtPoblacion.setText(cliente.getPoblacionCliente());
                txtProvincia.setText(cliente.getProvinciaCliente());
                txtPais.setText(cliente.getPaisCliente());
                txtDescuento.setText(String.valueOf(cliente.getDescuentoCliente()));
            }
        });

        comboArticulos.setOnAction(e -> {
            Articulo articulo = comboArticulos.getValue();
            if (articulo != null) {
                txtPrecioSinIVA.setText(String.format("%.2f", articulo.getPvpArticulo()));
                txtPrecioConIVA.setText(String.format("%.2f", articulo.getPrecioConIVA()));
            }
        });

        txtPrecioSinIVA.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarPrecioConIVA(newValue);
        });

        btnAgregarLinea.setOnAction(e -> agregarLineaFactura());
        btnEliminarLinea.setOnAction(e -> eliminarLineaFactura());
        btnGuardar.setOnAction(e -> guardarFactura());
        btnCancelar.setOnAction(e -> limpiarFormulario());
    }

    private void actualizarPrecioConIVA(String newValue) {
        Articulo articulo = comboArticulos.getValue();
        if (articulo != null && !newValue.isEmpty()) {
            try {
                double precioSinIVA = Double.parseDouble(newValue.replace(',', '.'));
                double porcentajeIVA = obtenerPorcentajeIVA(articulo.getTipoIva());
                double precioConIVA = precioSinIVA * (1 + porcentajeIVA / 100);
                txtPrecioConIVA.setText(String.format("%.2f", precioConIVA));
            } catch (NumberFormatException ex) {
                txtPrecioConIVA.setText("");
            }
        }
    }

    private void generarNumeroFactura() {
        ejecutarConsulta("SELECT MAX(numeroFacturaCliente) as ultimo FROM facturasClientes",
                rs -> {
                    int ultimoNumero = rs.getInt("ultimo");
                    txtNumeroFactura.setText(String.valueOf(ultimoNumero + 1));
                },
                "Error al generar número de factura"
        );
    }

    private void agregarLineaFactura() {
        Articulo articulo = comboArticulos.getValue();
        if (articulo == null) {
            mostrarError("Error", "Debe seleccionar un artículo");
            return;
        }

        try {
            if (txtCantidad.getText().isEmpty()) {
                txtCantidad.setText(CANTIDAD_POR_DEFECTO);
            }

            if (txtPrecioSinIVA.getText().isEmpty()) {
                txtPrecioSinIVA.setText(String.format("%.2f", articulo.getPvpArticulo()));
            }

            double cantidad = parseDouble(txtCantidad.getText());
            double precioSinIVA = parseDouble(txtPrecioSinIVA.getText());
            double descuento = txtDescuento.getText().isEmpty() ? 0 : parseDouble(txtDescuento.getText());

            double porcentajeIVA = obtenerPorcentajeIVA(articulo.getTipoIva());
            double importeSinIVA = cantidad * precioSinIVA * (1 - descuento / 100);

            LineaFactura linea = new LineaFactura(
                    articulo.getCodigoArticulo(),
                    articulo.getDescripcionArticulo(),
                    cantidad,
                    precioSinIVA,
                    descuento,
                    importeSinIVA,
                    porcentajeIVA,
                    articulo.getProveedorArticulo()
            );

            lineasFactura.add(linea);
            calcularTotales();

            comboArticulos.setValue(null);
            txtCantidad.setText(CANTIDAD_POR_DEFECTO);
            txtPrecioSinIVA.setText("");
            txtPrecioConIVA.setText("");

        } catch (NumberFormatException e) {
            mostrarError("Error de formato", "Los valores numéricos no son válidos: " + e.getMessage());
        }
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value.replace(',', '.'));
    }

    private double obtenerPorcentajeIVA(int idTipoIva) {
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT iva FROM tiposIva WHERE idTipoIva = ?")) {

            pstmt.setInt(1, idTipoIva);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("iva");
            }

        } catch (SQLException e) {
            mostrarError("Error al obtener IVA", e.getMessage());
        }

        return IVA_POR_DEFECTO;
    }

    private void eliminarLineaFactura() {
        LineaFactura lineaSeleccionada = tablaLineasFactura.getSelectionModel().getSelectedItem();
        if (lineaSeleccionada != null) {
            lineasFactura.remove(lineaSeleccionada);
            calcularTotales();
        } else {
            mostrarError("Error", "Debe seleccionar una línea para eliminar");
        }
    }

    private void calcularTotales() {
        double baseImponible = 0;
        double totalIVA = 0;

        for (LineaFactura linea : lineasFactura) {
            baseImponible += linea.getImporte();
            totalIVA += linea.getImporte() * linea.getPorcentajeIVA() / 100;
        }

        double totalFactura = baseImponible + totalIVA;

        txtBaseImponible.setText(String.format("%.2f", baseImponible));
        txtTotalIVA.setText(String.format("%.2f", totalIVA));
        txtTotalFactura.setText(String.format("%.2f", totalFactura));
    }

    private void guardarFactura() {
        if (!validarDatos()) {
            return;
        }

        try (Connection conn = DataBaseConnected.getConnection()) {
            conn.setAutoCommit(false);

            try {
                int idFactura = guardarCabeceraFactura(conn);
                guardarLineasFactura(conn, idFactura);
                conn.commit();
                mostrarInformacion("Éxito", "Factura guardada correctamente");
                limpiarFormulario();
                generarNumeroFactura();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            mostrarError("Error al guardar factura", e.getMessage());
        }
    }

    private boolean validarDatos() {
        StringBuilder errores = new StringBuilder();

        if (comboClientes.getValue() == null) {
            errores.append("- Debe seleccionar un cliente\n");
        }

        if (comboFormaPago.getValue() == null) {
            errores.append("- Debe seleccionar una forma de pago\n");
        }

        if (dateFechaFactura.getValue() == null) {
            errores.append("- Debe seleccionar una fecha de factura\n");
        }

        if (checkCobrada.isSelected() && dateFechaCobro.getValue() == null) {
            errores.append("- Si la factura está cobrada, debe indicar la fecha de cobro\n");
        }

        if (lineasFactura.isEmpty()) {
            errores.append("- Debe agregar al menos una línea a la factura\n");
        }

        if (errores.length() > 0) {
            mostrarError("Validación", "Corrija los siguientes errores:\n" + errores.toString());
            return false;
        }

        return true;
    }

    private int guardarCabeceraFactura(Connection conn) throws SQLException {
        String sql = "INSERT INTO facturasClientes (numeroFacturaCliente, fechaFacturaCliente, " +
                "idClienteFactura, baseImponibleFacturaCliente, ivaFacturaCliente, " +
                "totalFacturaCliente, cobradaFactura, formaCobroFactura, fechaCobroFactura, " +
                "observacionesFacturaClientes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int formaPagoId = obtenerIdFormaPago(conn, comboFormaPago.getValue());

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, Integer.parseInt(txtNumeroFactura.getText()));
            pstmt.setDate(2, Date.valueOf(dateFechaFactura.getValue()));
            pstmt.setInt(3, obtenerIdCliente(conn, comboClientes.getValue().getNombreCliente()));
            pstmt.setDouble(4, parseDouble(txtBaseImponible.getText()));
            pstmt.setDouble(5, parseDouble(txtTotalIVA.getText()));
            pstmt.setDouble(6, parseDouble(txtTotalFactura.getText()));
            pstmt.setBoolean(7, checkCobrada.isSelected());
            pstmt.setInt(8, formaPagoId);

            if (dateFechaCobro.getValue() != null) {
                pstmt.setDate(9, Date.valueOf(dateFechaCobro.getValue()));
            } else if (checkCobrada.isSelected()) {
                pstmt.setDate(9, Date.valueOf(LocalDate.now()));
            } else {
                pstmt.setNull(9, Types.DATE);
            }

            pstmt.setString(10, txtObservaciones.getText());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se generó ID para la factura");
                }
            }
        }
    }

    private void guardarLineasFactura(Connection conn, int idFactura) throws SQLException {
        String sql = "INSERT INTO lineasFacturasClientes (numeroFacturaCliente, idArticulo, " +
                "descripcionArticulo, codigoArticulo, pvpArticulo, ivaArticulo, " +
                "idProveedorArticulo, nombreProveedorArticulo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (LineaFactura linea : lineasFactura) {
                pstmt.setInt(1, Integer.parseInt(txtNumeroFactura.getText()));
                pstmt.setInt(2, obtenerIdArticulo(conn, linea.getCodigoArticulo()));
                pstmt.setString(3, linea.getDescripcion());
                pstmt.setString(4, linea.getCodigoArticulo());
                pstmt.setDouble(5, linea.getPrecio());
                pstmt.setDouble(6, linea.getPorcentajeIVA());
                pstmt.setInt(7, linea.getIdProveedor());
                pstmt.setString(8, obtenerNombreProveedor(conn, linea.getIdProveedor()));
                pstmt.executeUpdate();
            }
        }
    }

    private int obtenerIdCliente(Connection conn, String nombreCliente) throws SQLException {
        return obtenerIdGenerico(conn, "SELECT id FROM clientes WHERE nombreCliente = ?",
                nombreCliente, "Cliente no encontrado");
    }

    private int obtenerIdArticulo(Connection conn, String codigoArticulo) throws SQLException {
        return obtenerIdGenerico(conn, "SELECT idArticulo FROM articulos WHERE codigoArticulo = ?",
                codigoArticulo, "Artículo no encontrado");
    }

    private int obtenerIdFormaPago(Connection conn, String tipoFormaPago) throws SQLException {
        return obtenerIdGenerico(conn, "SELECT idFormaPago FROM formaPago WHERE tipoFormaPago = ?",
                tipoFormaPago, "Forma de pago no encontrada");
    }

    private int obtenerIdGenerico(Connection conn, String sql, String param, String mensajeError) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException(mensajeError + ": " + param);
                }
            }
        }
    }

    private String obtenerNombreProveedor(Connection conn, int idProveedor) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT nombreProveedor FROM proveedores WHERE idProveedor = ?")) {
            pstmt.setInt(1, idProveedor);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString("nombreProveedor") : "";
            }
        }
    }

    private void limpiarFormulario() {
        comboClientes.setValue(null);
        txtDireccion.clear();
        txtCIF.clear();
        txtPoblacion.clear();
        txtProvincia.clear();
        txtPais.clear();

        comboFormaPago.setValue(null);
        dateFechaFactura.setValue(LocalDate.now());
        dateFechaCobro.setValue(null);
        checkCobrada.setSelected(false);
        txtObservaciones.clear();

        comboArticulos.setValue(null);
        txtCantidad.setText(CANTIDAD_POR_DEFECTO);
        txtPrecioSinIVA.clear();
        txtPrecioConIVA.clear();
        txtDescuento.clear();

        lineasFactura.clear();

        txtBaseImponible.setText("0.00");
        txtTotalIVA.setText("0.00");
        txtTotalFactura.setText("0.00");
    }

    private void mostrarError(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método utilitario para ejecutar consultas SQL
    private void ejecutarConsulta(String sql, ConsultaCallback callback, String mensajeError) {
        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                callback.procesar(rs);
            }

        } catch (SQLException e) {
            mostrarError(mensajeError, e.getMessage());
            if (mensajeError.contains("número de factura")) {
                txtNumeroFactura.setText("1");
            }
        }
    }

    // Interfaz funcional para callback de consultas SQL
    @FunctionalInterface
    private interface ConsultaCallback {
        void procesar(ResultSet rs) throws SQLException;
    }

    // Clases para conversión en ComboBox
    private static class ClienteStringConverter extends javafx.util.StringConverter<Cliente> {
        @Override
        public String toString(Cliente cliente) {
            return cliente == null ? "" : cliente.getNombreCliente();
        }

        @Override
        public Cliente fromString(String string) {
            return null;
        }
    }

    private static class ArticuloStringConverter extends javafx.util.StringConverter<Articulo> {
        @Override
        public String toString(Articulo articulo) {
            return articulo == null ? "" : articulo.getCodigoArticulo() + " - " + articulo.getDescripcionArticulo();
        }

        @Override
        public Articulo fromString(String string) {
            return null;
        }
    }

    // Clase para representar una línea de factura
    public static class LineaFactura {
        private String codigoArticulo;
        private String descripcion;
        private double cantidad;
        private double precio;
        private double descuento;
        private double importe;
        private double porcentajeIVA;
        private int idProveedor;

        public LineaFactura(String codigoArticulo, String descripcion, double cantidad,
                            double precio, double descuento, double importe,
                            double porcentajeIVA, int idProveedor) {
            this.codigoArticulo = codigoArticulo;
            this.descripcion = descripcion;
            this.cantidad = cantidad;
            this.precio = precio;
            this.descuento = descuento;
            this.importe = importe;
            this.porcentajeIVA = porcentajeIVA;
            this.idProveedor = idProveedor;
        }

        // Getters y setters
        public String getCodigoArticulo() { return codigoArticulo; }
        public void setCodigoArticulo(String codigoArticulo) { this.codigoArticulo = codigoArticulo; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public double getCantidad() { return cantidad; }
        public void setCantidad(double cantidad) { this.cantidad = cantidad; }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }

        public double getDescuento() { return descuento; }
        public void setDescuento(double descuento) { this.descuento = descuento; }

        public double getImporte() { return importe; }
        public void setImporte(double importe) { this.importe = importe; }

        public double getPorcentajeIVA() { return porcentajeIVA; }
        public void setPorcentajeIVA(double porcentajeIVA) { this.porcentajeIVA = porcentajeIVA; }

        public int getIdProveedor() { return idProveedor; }
        public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }
    }
}