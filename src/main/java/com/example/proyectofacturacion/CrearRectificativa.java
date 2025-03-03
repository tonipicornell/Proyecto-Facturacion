package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;

public class CrearRectificativa {

    // Cliente
    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private TextField txtDireccion, txtCIF, txtPoblacion, txtProvincia, txtPais;

    // Factura Original
    @FXML private ComboBox<Integer> comboFacturasOriginal;
    @FXML private TextField txtFechaFacturaOriginal, txtTotalFacturaOriginal;

    // Rectificativa
    @FXML private TextField txtNumeroRectificativa;
    @FXML private DatePicker dateFechaRectificativa;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblMotivoRectificativa;
    @FXML private ComboBox<String> comboMotivoRectificativa;

    // Artículos
    @FXML private ComboBox<Articulo> comboArticulos;
    @FXML private TextField txtCantidad, txtPrecioSinIVA, txtPrecioConIVA, txtDescuento;
    @FXML private Button btnAgregarLinea, btnEliminarLinea;

    // Tabla de líneas de rectificativa
    @FXML private TableView<LineaRectificativa> tablaLineasRectificativa;
    @FXML private TableColumn<LineaRectificativa, String> colCodigo, colDescripcion;
    @FXML private TableColumn<LineaRectificativa, Double> colCantidad, colPrecio, colDescuento, colImporte, colIVA;

    // Totales
    @FXML private TextField txtBaseImponible, txtTotalIVA, txtTotalRectificativa;

    // Botones de acción
    @FXML private Button btnGuardar, btnCancelar;

    // Listas para los ComboBox
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final ObservableList<Integer> listaFacturasCliente = FXCollections.observableArrayList();
    private final ObservableList<Articulo> listaArticulos = FXCollections.observableArrayList();
    private final ObservableList<String> listaMotivosRectificativa = FXCollections.observableArrayList(
            "Error en datos fiscales", "Error en importes", "Devolución de mercancía",
            "Descuento posterior a factura", "Error en cantidad", "Error en IVA aplicado",
            "Anulación total de factura", "Otro motivo"
    );
    private final ObservableList<LineaRectificativa> lineasRectificativa = FXCollections.observableArrayList();

    // Constantes
    private static final double IVA_POR_DEFECTO = 21.0;
    private static final String CANTIDAD_POR_DEFECTO = "1";
    private static final String SQL_ID_CLIENTE = "SELECT id FROM clientes WHERE nombreCliente = ?";
    private static final String SQL_ID_ARTICULO = "SELECT idArticulo FROM articulos WHERE codigoArticulo = ?";
    private static final String SQL_NOMBRE_PROVEEDOR = "SELECT nombreProveedor FROM proveedores WHERE idProveedor = ?";

    @FXML
    public void initialize() {
        dateFechaRectificativa.setValue(LocalDate.now());
        configurarTabla();
        cargarDatosIniciales();
        configurarListeners();
        generarNumeroRectificativa();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colIVA.setCellValueFactory(new PropertyValueFactory<>("porcentajeIVA"));
        tablaLineasRectificativa.setItems(lineasRectificativa);
    }

    private void cargarDatosIniciales() {
        cargarClientes();
        cargarArticulos();
        comboMotivoRectificativa.setItems(listaMotivosRectificativa);
    }

    private void cargarClientes() {
        String sql = "SELECT * FROM clientes";
        ejecutarConsulta(sql, rs -> {
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
        }, "Error al cargar clientes");

        comboClientes.setItems(listaClientes);
        comboClientes.setConverter(new ClienteStringConverter());
    }

    private void cargarFacturasCliente(int idCliente) {
        listaFacturasCliente.clear();
        String sql = "SELECT numeroFacturaCliente FROM facturasClientes WHERE idClienteFactura = " + idCliente;
        ejecutarConsulta(sql, rs ->
                        listaFacturasCliente.add(rs.getInt("numeroFacturaCliente")),
                "Error al cargar facturas del cliente"
        );
        comboFacturasOriginal.setItems(listaFacturasCliente);
    }

    private void cargarArticulos() {
        String sql = "SELECT a.*, f.denominacionFamilias, t.iva " +
                "FROM articulos a " +
                "LEFT JOIN familiaArticulos f ON a.familiaArticulo = f.idFamiliaArticulos " +
                "LEFT JOIN tiposIva t ON a.tipoIva = t.idTipoIva";

        ejecutarConsulta(sql, rs -> {
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
        }, "Error al cargar artículos");

        comboArticulos.setItems(listaArticulos);
        comboArticulos.setConverter(new ArticuloStringConverter());
    }

    private void configurarListeners() {
        comboClientes.setOnAction(e -> {
            Optional.ofNullable(comboClientes.getValue()).ifPresent(cliente -> {
                txtDireccion.setText(cliente.getDireccionCliente());
                txtCIF.setText(cliente.getCifCliente());
                txtPoblacion.setText(cliente.getPoblacionCliente());
                txtProvincia.setText(cliente.getProvinciaCliente());
                txtPais.setText(cliente.getPaisCliente());
                txtDescuento.setText(String.valueOf(cliente.getDescuentoCliente()));

                try (Connection conn = DataBaseConnected.getConnection()) {
                    int idCliente = obtenerIdCliente(conn, cliente.getNombreCliente());
                    cargarFacturasCliente(idCliente);
                } catch (SQLException ex) {
                    mostrarError("Error al obtener ID del cliente", ex.getMessage());
                }
            });
        });

        comboFacturasOriginal.setOnAction(e ->
                Optional.ofNullable(comboFacturasOriginal.getValue())
                        .ifPresent(this::cargarDatosFacturaOriginal));

        comboArticulos.setOnAction(e ->
                Optional.ofNullable(comboArticulos.getValue()).ifPresent(articulo -> {
                    txtPrecioSinIVA.setText(String.format("%.2f", articulo.getPvpArticulo()));
                    txtPrecioConIVA.setText(String.format("%.2f", articulo.getPrecioConIVA()));
                    txtCantidad.setText(CANTIDAD_POR_DEFECTO);
                }));

        txtPrecioSinIVA.textProperty().addListener((observable, oldValue, newValue) ->
                actualizarPrecioConIVA(newValue));

        btnAgregarLinea.setOnAction(e -> agregarLineaRectificativa());
        btnEliminarLinea.setOnAction(e -> eliminarLineaRectificativa());
        btnGuardar.setOnAction(e -> guardarRectificativa());
        btnCancelar.setOnAction(e -> limpiarFormulario());
    }

    private void cargarDatosFacturaOriginal(int numeroFactura) {
        String sql = "SELECT fechaFacturaCliente, totalFacturaCliente FROM facturasClientes " +
                "WHERE numeroFacturaCliente = " + numeroFactura;

        ejecutarConsulta(sql, rs -> {
            Date fechaFactura = rs.getDate("fechaFacturaCliente");
            double totalFactura = rs.getDouble("totalFacturaCliente");

            txtFechaFacturaOriginal.setText(fechaFactura.toString());
            txtTotalFacturaOriginal.setText(String.format("%.2f", totalFactura));
        }, "Error al cargar datos de la factura original");
    }

    private void actualizarPrecioConIVA(String newValue) {
        Articulo articulo = comboArticulos.getValue();
        if (articulo != null && !newValue.isEmpty()) {
            try {
                double precioSinIVA = parseDouble(newValue);
                double porcentajeIVA = obtenerPorcentajeIVA(articulo.getTipoIva());
                double precioConIVA = precioSinIVA * (1 + porcentajeIVA / 100);
                txtPrecioConIVA.setText(String.format("%.2f", precioConIVA));
            } catch (NumberFormatException ex) {
                txtPrecioConIVA.setText("");
            }
        }
    }

    private void generarNumeroRectificativa() {
        ejecutarConsulta("SELECT COALESCE(MAX(numeroRectificativaCliente), 0) as ultimo FROM rectificativasClientes",
                rs -> txtNumeroRectificativa.setText(String.valueOf(rs.getInt("ultimo") + 1)),
                "Error al generar número de rectificativa"
        );
    }

    private void agregarLineaRectificativa() {
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

            LineaRectificativa linea = new LineaRectificativa(
                    articulo.getCodigoArticulo(),
                    articulo.getDescripcionArticulo(),
                    cantidad,
                    precioSinIVA,
                    descuento,
                    importeSinIVA,
                    porcentajeIVA,
                    articulo.getProveedorArticulo()
            );

            lineasRectificativa.add(linea);
            calcularTotales();

            // Limpiar campos
            comboArticulos.setValue(null);
            txtCantidad.setText(CANTIDAD_POR_DEFECTO);
            txtPrecioSinIVA.setText("");
            txtPrecioConIVA.setText("");
            txtDescuento.setText("");

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
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("iva");
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al obtener IVA", e.getMessage());
        }
        return IVA_POR_DEFECTO;
    }

    private void eliminarLineaRectificativa() {
        LineaRectificativa lineaSeleccionada = tablaLineasRectificativa.getSelectionModel().getSelectedItem();
        if (lineaSeleccionada != null) {
            lineasRectificativa.remove(lineaSeleccionada);
            calcularTotales();
        } else {
            mostrarError("Error", "Debe seleccionar una línea para eliminar");
        }
    }

    private void calcularTotales() {
        double baseImponible = 0;
        double totalIVA = 0;

        for (LineaRectificativa linea : lineasRectificativa) {
            baseImponible += linea.getImporte();
            totalIVA += linea.getImporte() * linea.getPorcentajeIVA() / 100;
        }

        double totalRectificativa = baseImponible + totalIVA;

        txtBaseImponible.setText(String.format("%.2f", baseImponible));
        txtTotalIVA.setText(String.format("%.2f", totalIVA));
        txtTotalRectificativa.setText(String.format("%.2f", totalRectificativa));
    }

    private void guardarRectificativa() {
        if (!validarDatos()) {
            return;
        }

        try (Connection conn = DataBaseConnected.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idRectificativa = guardarCabeceraRectificativa(conn);
                guardarLineasRectificativa(conn, idRectificativa);
                conn.commit();
                mostrarInformacion("Éxito", "Rectificativa guardada correctamente");
                limpiarFormulario();
                generarNumeroRectificativa();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            mostrarError("Error al guardar rectificativa", e.getMessage());
        }
    }

    private boolean validarDatos() {
        StringBuilder errores = new StringBuilder();

        if (comboClientes.getValue() == null) errores.append("- Debe seleccionar un cliente\n");
        if (comboFacturasOriginal.getValue() == null) errores.append("- Debe seleccionar una factura original\n");
        if (comboMotivoRectificativa.getValue() == null) errores.append("- Debe seleccionar un motivo para la rectificativa\n");
        if (dateFechaRectificativa.getValue() == null) errores.append("- Debe seleccionar una fecha para la rectificativa\n");
        if (lineasRectificativa.isEmpty()) errores.append("- Debe agregar al menos una línea a la rectificativa\n");

        if (errores.length() > 0) {
            mostrarError("Validación", "Corrija los siguientes errores:\n" + errores);
            return false;
        }
        return true;
    }

    private int guardarCabeceraRectificativa(Connection conn) throws SQLException {
        String sql = "INSERT INTO rectificativasClientes (numeroRectificativaCliente, fechaRectificativaCliente, " +
                "idClienteRectificativaCliente, baseImponibleRectificativaCliente, ivaRectificativaCliente, " +
                "totalRectificativaCliente, observacionesRectificativaCliente) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, Integer.parseInt(txtNumeroRectificativa.getText()));
            pstmt.setDate(2, Date.valueOf(dateFechaRectificativa.getValue()));
            pstmt.setInt(3, obtenerIdCliente(conn, comboClientes.getValue().getNombreCliente()));
            pstmt.setDouble(4, parseDouble(txtBaseImponible.getText()));
            pstmt.setDouble(5, parseDouble(txtTotalIVA.getText()));
            pstmt.setDouble(6, parseDouble(txtTotalRectificativa.getText()));

            // Observaciones con el motivo y número de factura original
            String observaciones = String.format("Motivo: %s\nFactura original: %d\n%s",
                    comboMotivoRectificativa.getValue(),
                    comboFacturasOriginal.getValue(),
                    txtObservaciones.getText());

            pstmt.setString(7, observaciones);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se generó ID para la rectificativa");
                }
            }
        }
    }

    private void guardarLineasRectificativa(Connection conn, int idRectificativa) throws SQLException {
        String sql = "INSERT INTO lineasRectificativasClientes (idRectificativaCliente, idArticulo, " +
                "descripcionArticulo, codigoArticulo, pvpArticulo, ivaArticulo, " +
                "idProveedorArticulo, nombreProveedorArticulo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (LineaRectificativa linea : lineasRectificativa) {
                pstmt.setInt(1, idRectificativa);
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
        return obtenerIdGenerico(conn, SQL_ID_CLIENTE, nombreCliente, "Cliente no encontrado");
    }

    private int obtenerIdArticulo(Connection conn, String codigoArticulo) throws SQLException {
        return obtenerIdGenerico(conn, SQL_ID_ARTICULO, codigoArticulo, "Artículo no encontrado");
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
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_NOMBRE_PROVEEDOR)) {
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

        comboFacturasOriginal.setValue(null);
        txtFechaFacturaOriginal.clear();
        txtTotalFacturaOriginal.clear();

        comboMotivoRectificativa.setValue(null);
        dateFechaRectificativa.setValue(LocalDate.now());
        txtObservaciones.clear();

        comboArticulos.setValue(null);
        txtCantidad.setText(CANTIDAD_POR_DEFECTO);
        txtPrecioSinIVA.clear();
        txtPrecioConIVA.clear();
        txtDescuento.clear();

        lineasRectificativa.clear();

        txtBaseImponible.setText("0.00");
        txtTotalIVA.setText("0.00");
        txtTotalRectificativa.setText("0.00");
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
            if (mensajeError.contains("número de rectificativa")) {
                txtNumeroRectificativa.setText("1");
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

    // Clase para representar una línea de rectificativa
    public static class LineaRectificativa {
        private String codigoArticulo;
        private String descripcion;
        private double cantidad;
        private double precio;
        private double descuento;
        private double importe;
        private double porcentajeIVA;
        private int idProveedor;

        public LineaRectificativa(String codigoArticulo, String descripcion, double cantidad,
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