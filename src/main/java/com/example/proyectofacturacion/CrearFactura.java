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
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCIF;
    @FXML private TextField txtPoblacion;
    @FXML private TextField txtProvincia;
    @FXML private TextField txtPais;

    // Factura
    @FXML private TextField txtNumeroFactura;
    @FXML private DatePicker dateFechaFactura;
    @FXML private ComboBox<String> comboFormaPago;
    @FXML private DatePicker dateFechaCobro;
    @FXML private CheckBox checkCobrada;
    @FXML private TextArea txtObservaciones;

    // Artículos
    @FXML private ComboBox<Articulo> comboArticulos;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioSinIVA;  // Cambiado de txtPrecio a txtPrecioSinIVA
    @FXML private TextField txtPrecioConIVA;  // Nuevo campo para mostrar precio con IVA
    @FXML private TextField txtDescuento;
    @FXML private Button btnAgregarLinea;
    @FXML private Button btnEliminarLinea;

    // Tabla de líneas de factura
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colCodigo;
    @FXML private TableColumn<LineaFactura, String> colDescripcion;
    @FXML private TableColumn<LineaFactura, Double> colCantidad;
    @FXML private TableColumn<LineaFactura, Double> colPrecio;
    @FXML private TableColumn<LineaFactura, Double> colDescuento;
    @FXML private TableColumn<LineaFactura, Double> colImporte;
    @FXML private TableColumn<LineaFactura, Double> colIVA;

    // Totales
    @FXML private TextField txtBaseImponible;
    @FXML private TextField txtTotalIVA;
    @FXML private TextField txtTotalFactura;

    // Botones de acción
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Listas para los ComboBox
    private ObservableList<Cliente> listaClientes;
    private ObservableList<Articulo> listaArticulos;
    private ObservableList<String> listaFormasPago;
    private ObservableList<LineaFactura> lineasFactura;

    // Inicialización
    @FXML
    public void initialize() {
        // Inicialización de listas
        listaClientes = FXCollections.observableArrayList();
        listaArticulos = FXCollections.observableArrayList();
        listaFormasPago = FXCollections.observableArrayList();
        lineasFactura = FXCollections.observableArrayList();

        // Configurar fecha actual
        dateFechaFactura.setValue(LocalDate.now());

        // Configurar tabla
        configurarTabla();

        // Cargar datos
        cargarClientes();
        cargarArticulos();
        cargarFormasPago();

        // Configurar listeners
        configurarListeners();

        // Generar número de factura
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

    private void cargarClientes() {
        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clientes")) {

            while (rs.next()) {
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
            }

            comboClientes.setItems(listaClientes);
            comboClientes.setConverter(new ClienteStringConverter());

        } catch (SQLException e) {
            mostrarError("Error al cargar clientes", e.getMessage());
        }
    }

    private void cargarArticulos() {
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT a.*, f.denominacionFamilias, t.iva " +
                             "FROM articulos a " +
                             "LEFT JOIN familiaArticulos f ON a.familiaArticulo = f.idFamiliaArticulos " +
                             "LEFT JOIN tiposIva t ON a.tipoIva = t.idTipoIva")) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
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
                        precioSinIVA,  // Precio sin IVA
                        rs.getInt("proveedorArticulo"),
                        rs.getDouble("stockArticulo"),
                        rs.getString("observacionesArticulo"),
                        rs.getInt("tipoIva"),
                        precioConIVA  // Precio con IVA calculado
                );
                listaArticulos.add(articulo);
            }

            comboArticulos.setItems(listaArticulos);
            comboArticulos.setConverter(new ArticuloStringConverter());

        } catch (SQLException e) {
            mostrarError("Error al cargar artículos", e.getMessage());
        }
    }

    private void cargarFormasPago() {
        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM formaPago")) {

            while (rs.next()) {
                listaFormasPago.add(rs.getString("tipoFormaPago"));
            }

            comboFormaPago.setItems(listaFormasPago);

        } catch (SQLException e) {
            mostrarError("Error al cargar formas de pago", e.getMessage());
        }
    }

    private void configurarListeners() {
        // Cliente seleccionado
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

        // Artículo seleccionado
        comboArticulos.setOnAction(e -> {
            Articulo articulo = comboArticulos.getValue();
            if (articulo != null) {
                txtPrecioSinIVA.setText(String.format("%.2f", articulo.getPvpArticulo()));
                txtPrecioConIVA.setText(String.format("%.2f", articulo.getPrecioConIVA()));
            }
        });

        // Actualización de precio con IVA cuando cambia el precio sin IVA
        txtPrecioSinIVA.textProperty().addListener((observable, oldValue, newValue) -> {
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
        });

        // Botón para agregar línea
        btnAgregarLinea.setOnAction(e -> agregarLineaFactura());

        // Botón para eliminar línea
        btnEliminarLinea.setOnAction(e -> eliminarLineaFactura());

        // Botón guardar
        btnGuardar.setOnAction(e -> guardarFactura());

        // Botón cancelar
        btnCancelar.setOnAction(e -> limpiarFormulario());
    }

    private void generarNumeroFactura() {
        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(numeroFacturaCliente) as ultimo FROM facturasClientes")) {

            if (rs.next()) {
                int ultimoNumero = rs.getInt("ultimo");
                txtNumeroFactura.setText(String.valueOf(ultimoNumero + 1));
            } else {
                txtNumeroFactura.setText("1");
            }

        } catch (SQLException e) {
            mostrarError("Error al generar número de factura", e.getMessage());
            txtNumeroFactura.setText("1");
        }
    }

    private void agregarLineaFactura() {
        Articulo articulo = comboArticulos.getValue();
        if (articulo == null) {
            mostrarError("Error", "Debe seleccionar un artículo");
            return;
        }

        try {
            // Verificar que la cantidad no esté vacía
            if (txtCantidad.getText().isEmpty()) {
                txtCantidad.setText("1"); // Valor por defecto
            }

            // Verificar que el precio sin IVA no esté vacío
            if (txtPrecioSinIVA.getText().isEmpty()) {
                txtPrecioSinIVA.setText(String.format("%.2f", articulo.getPvpArticulo()));
            }

            // Reemplazar comas por puntos para manejar diferentes formatos numéricos
            String cantidadStr = txtCantidad.getText().replace(',', '.');
            String precioStr = txtPrecioSinIVA.getText().replace(',', '.');
            String descuentoStr = txtDescuento.getText().replace(',', '.');

            double cantidad = Double.parseDouble(cantidadStr);
            double precioSinIVA = Double.parseDouble(precioStr);
            double descuento = 0;

            if (!descuentoStr.isEmpty()) {
                descuento = Double.parseDouble(descuentoStr);
            }

            // El resto de tu código para agregar la línea...
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

            // Limpiar campos
            comboArticulos.setValue(null);
            txtCantidad.setText("1");
            txtPrecioSinIVA.setText("");
            txtPrecioConIVA.setText("");

        } catch (NumberFormatException e) {
            mostrarError("Error de formato", "Los valores numéricos no son válidos: " + e.getMessage());
        }
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

        return 21.0; // Valor por defecto
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
            // Desactivar auto-commit para manejar transacción
            conn.setAutoCommit(false);

            try {
                // Guardar cabecera de factura
                int idFactura = guardarCabeceraFactura(conn);

                // Guardar líneas de factura
                guardarLineasFactura(conn, idFactura);

                // Confirmar transacción
                conn.commit();

                mostrarInformacion("Éxito", "Factura guardada correctamente");
                limpiarFormulario();
                generarNumeroFactura();

            } catch (SQLException e) {
                // Deshacer transacción en caso de error
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

            // Obtener ID del cliente seleccionado
            pstmt.setInt(3, obtenerIdCliente(conn, comboClientes.getValue().getNombreCliente()));

            // Reemplazar comas por puntos antes de parsear
            String baseImponible = txtBaseImponible.getText().replace(',', '.');
            String totalIVA = txtTotalIVA.getText().replace(',', '.');
            String totalFactura = txtTotalFactura.getText().replace(',', '.');

            pstmt.setDouble(4, Double.parseDouble(baseImponible));
            pstmt.setDouble(5, Double.parseDouble(totalIVA));
            pstmt.setDouble(6, Double.parseDouble(totalFactura));
            pstmt.setBoolean(7, checkCobrada.isSelected());
            pstmt.setInt(8, formaPagoId);

            // Fecha de cobro
            if (dateFechaCobro.getValue() != null) {
                pstmt.setDate(9, Date.valueOf(dateFechaCobro.getValue()));
            } else if (checkCobrada.isSelected()) {
                pstmt.setDate(9, Date.valueOf(LocalDate.now()));
            } else {
                pstmt.setDate(9, null);
            }

            pstmt.setString(10, txtObservaciones.getText());

            pstmt.executeUpdate();

            // Obtener ID generado
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
                pstmt.setDouble(5, linea.getPrecio());  // Guardamos el precio sin IVA
                pstmt.setDouble(6, linea.getPorcentajeIVA());
                pstmt.setInt(7, linea.getIdProveedor());
                pstmt.setString(8, obtenerNombreProveedor(conn, linea.getIdProveedor()));

                pstmt.executeUpdate();
            }
        }
    }

    private int obtenerIdCliente(Connection conn, String nombreCliente) throws SQLException {
        String sql = "SELECT id FROM clientes WHERE nombreCliente = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreCliente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Cliente no encontrado: " + nombreCliente);
                }
            }
        }
    }

    private int obtenerIdArticulo(Connection conn, String codigoArticulo) throws SQLException {
        String sql = "SELECT idArticulo FROM articulos WHERE codigoArticulo = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigoArticulo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idArticulo");
                } else {
                    throw new SQLException("Artículo no encontrado: " + codigoArticulo);
                }
            }
        }
    }

    private String obtenerNombreProveedor(Connection conn, int idProveedor) throws SQLException {
        String sql = "SELECT nombreProveedor FROM proveedores WHERE idProveedor = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProveedor);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombreProveedor");
                } else {
                    return "";
                }
            }
        }
    }

    private int obtenerIdFormaPago(Connection conn, String tipoFormaPago) throws SQLException {
        String sql = "SELECT idFormaPago FROM formaPago WHERE tipoFormaPago = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipoFormaPago);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idFormaPago");
                } else {
                    throw new SQLException("Forma de pago no encontrada: " + tipoFormaPago);
                }
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
        txtCantidad.setText("1");
        txtPrecioSinIVA.clear();
        txtPrecioConIVA.clear();
        txtDescuento.clear();

        lineasFactura.clear();

        txtBaseImponible.setText("0.00");
        txtTotalIVA.setText("0.00");
        txtTotalFactura.setText("0.00");
    }

    private void mostrarError(String titulo, String mensaje) {
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

    // Clase para manejar conversión Cliente <-> String en ComboBox
    private class ClienteStringConverter extends javafx.util.StringConverter<Cliente> {
        @Override
        public String toString(Cliente cliente) {
            return cliente == null ? "" : cliente.getNombreCliente();
        }

        @Override
        public Cliente fromString(String string) {
            return null; // No es necesario para este caso
        }
    }

    // Clase para manejar conversión Articulo <-> String en ComboBox
    private class ArticuloStringConverter extends javafx.util.StringConverter<Articulo> {
        @Override
        public String toString(Articulo articulo) {
            return articulo == null ? "" : articulo.getCodigoArticulo() + " - " + articulo.getDescripcionArticulo();
        }

        @Override
        public Articulo fromString(String string) {
            return null; // No es necesario para este caso
        }
    }

    // Clase para representar una línea de factura en la tabla
    public static class LineaFactura {
        private String codigoArticulo;
        private String descripcion;
        private double cantidad;
        private double precio; // Ahora este precio es sin IVA
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