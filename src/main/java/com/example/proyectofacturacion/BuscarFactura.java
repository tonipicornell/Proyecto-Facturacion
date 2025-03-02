package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;

public class BuscarFactura {

    // Criterios de búsqueda
    @FXML private TextField txtNumeroFactura;
    @FXML private DatePicker dateDesde, dateHasta;
    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private CheckBox checkSoloPendientes;
    @FXML private Button btnBuscar, btnLimpiar, btnVerDetalle;

    // Tabla de facturas
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Integer> colNumero;
    @FXML private TableColumn<Factura, LocalDate> colFecha;
    @FXML private TableColumn<Factura, String> colCliente;
    @FXML private TableColumn<Factura, String> colFormaPago;
    @FXML private TableColumn<Factura, Double> colTotal;
    @FXML private TableColumn<Factura, Boolean> colCobrada;
    @FXML private TableColumn<Factura, LocalDate> colFechaCobro;

    // Detalles de la factura seleccionada
    @FXML private Label lblNumFactura, lblFechaFactura, lblCliente, lblBaseImponible,
            lblIVA, lblTotal, lblFormaPago, lblCobrada, lblFechaCobro;
    @FXML private TextArea txtObservaciones;

    // Tabla de líneas de factura
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colCodigo, colDescripcion, colProveedor;
    @FXML private TableColumn<LineaFactura, Double> colCantidad, colPrecio, colIVA, colImporte;

    // Listas para los ComboBox
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final ObservableList<Factura> listaFacturas = FXCollections.observableArrayList();
    private final ObservableList<LineaFactura> lineasFactura = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTablaFacturas();
        configurarTablaLineas();
        cargarClientes();
        configurarListeners();
        dateDesde.setValue(LocalDate.now().minusMonths(1));
        dateHasta.setValue(LocalDate.now());
    }

    private void configurarTablaFacturas() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFormaPago.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalFactura"));
        colCobrada.setCellValueFactory(new PropertyValueFactory<>("cobrada"));
        colFechaCobro.setCellValueFactory(new PropertyValueFactory<>("fechaCobro"));

        tablaFacturas.setItems(listaFacturas);
    }

    private void configurarTablaLineas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colIVA.setCellValueFactory(new PropertyValueFactory<>("porcentajeIVA"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));

        tablaLineasFactura.setItems(lineasFactura);
    }

    private void cargarClientes() {
        ejecutarConsulta("SELECT * FROM clientes ORDER BY nombreCliente",
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

    private void configurarListeners() {
        btnBuscar.setOnAction(e -> buscarFacturas());
        btnLimpiar.setOnAction(e -> limpiarFiltros());
        btnVerDetalle.setOnAction(e -> verDetalleFactura());

        tablaFacturas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> cargarDetalleFactura(newValue));
    }

    private void buscarFacturas() {
        listaFacturas.clear();

        StringBuilder sql = new StringBuilder(
                "SELECT f.*, c.nombreCliente, fp.tipoFormaPago " +
                        "FROM facturasClientes f " +
                        "JOIN clientes c ON f.idClienteFactura = c.id " +
                        "JOIN formaPago fp ON f.formaCobroFactura = fp.idFormaPago " +
                        "WHERE 1=1 ");

        if (txtNumeroFactura.getText() != null && !txtNumeroFactura.getText().isEmpty()) {
            sql.append("AND f.numeroFacturaCliente = ").append(txtNumeroFactura.getText()).append(" ");
        }

        if (dateDesde.getValue() != null) {
            sql.append("AND f.fechaFacturaCliente >= '").append(dateDesde.getValue()).append("' ");
        }

        if (dateHasta.getValue() != null) {
            sql.append("AND f.fechaFacturaCliente <= '").append(dateHasta.getValue()).append("' ");
        }

        if (comboClientes.getValue() != null) {
            sql.append("AND c.nombreCliente = '").append(comboClientes.getValue().getNombreCliente().replace("'", "''")).append("' ");
        }

        if (checkSoloPendientes.isSelected()) {
            sql.append("AND f.cobradaFactura = 0 ");
        }

        sql.append("ORDER BY f.fechaFacturaCliente DESC, f.numeroFacturaCliente DESC");

        ejecutarConsulta(sql.toString(),
                rs -> {
                    Factura factura = new Factura(
                            rs.getInt("numeroFacturaCliente"),
                            rs.getDate("fechaFacturaCliente").toLocalDate(),
                            rs.getString("nombreCliente"),
                            rs.getString("tipoFormaPago"),
                            rs.getDouble("totalFacturaCliente"),
                            rs.getBoolean("cobradaFactura"),
                            rs.getDate("fechaCobroFactura") != null ? rs.getDate("fechaCobroFactura").toLocalDate() : null,
                            rs.getDouble("baseImponibleFacturaCliente"),
                            rs.getDouble("ivaFacturaCliente"),
                            rs.getString("observacionesFacturaClientes")
                    );
                    listaFacturas.add(factura);
                },
                "Error al buscar facturas"
        );
    }

    private void limpiarFiltros() {
        txtNumeroFactura.clear();
        dateDesde.setValue(LocalDate.now().minusMonths(1));
        dateHasta.setValue(LocalDate.now());
        comboClientes.setValue(null);
        checkSoloPendientes.setSelected(false);
        listaFacturas.clear();
        lineasFactura.clear();
        limpiarDetalleFactura();
    }

    private void verDetalleFactura() {
        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada == null) {
            mostrarError("Error", "Debe seleccionar una factura para ver sus detalles");
            return;
        }

        // Aquí podría abrirse una nueva ventana con más detalles o imprimir la factura
        mostrarInformacion("Detalle Factura", "Función para ver/imprimir detalle completo de la factura "
                + facturaSeleccionada.getNumeroFactura());
    }

    private void cargarDetalleFactura(Factura factura) {
        lineasFactura.clear();

        if (factura == null) {
            limpiarDetalleFactura();
            return;
        }

        // Mostrar información de la factura
        lblNumFactura.setText(String.valueOf(factura.getNumeroFactura()));
        lblFechaFactura.setText(factura.getFechaFactura().toString());
        lblCliente.setText(factura.getNombreCliente());
        lblBaseImponible.setText(String.format("%.2f €", factura.getBaseImponible()));
        lblIVA.setText(String.format("%.2f €", factura.getIvaFactura()));
        lblTotal.setText(String.format("%.2f €", factura.getTotalFactura()));
        lblFormaPago.setText(factura.getFormaPago());
        lblCobrada.setText(factura.isCobrada() ? "Sí" : "No");
        lblFechaCobro.setText(factura.getFechaCobro() != null ? factura.getFechaCobro().toString() : "Pendiente");
        txtObservaciones.setText(factura.getObservaciones());

        // Cargar líneas de la factura
        String sql = "SELECT l.*, p.nombreProveedor " +
                "FROM lineasFacturasClientes l " +
                "LEFT JOIN proveedores p ON l.idProveedorArticulo = p.idProveedor " +
                "WHERE l.numeroFacturaCliente = " + factura.getNumeroFactura();

        ejecutarConsulta(sql,
                rs -> {
                    double cantidad = 1.0; // Cantidad por defecto si no está disponible
                    double precioSinIVA = rs.getDouble("pvpArticulo");
                    double porcentajeIVA = rs.getDouble("ivaArticulo");
                    double importe = precioSinIVA; // Importe calculado según la información disponible

                    LineaFactura linea = new LineaFactura(
                            rs.getString("codigoArticulo"),
                            rs.getString("descripcionArticulo"),
                            cantidad,
                            precioSinIVA,
                            0.0, // No tenemos el campo de descuento en la tabla
                            importe,
                            porcentajeIVA,
                            rs.getInt("idProveedorArticulo"),
                            rs.getString("nombreProveedorArticulo")
                    );
                    lineasFactura.add(linea);
                },
                "Error al cargar líneas de factura"
        );
    }

    private void limpiarDetalleFactura() {
        lblNumFactura.setText("");
        lblFechaFactura.setText("");
        lblCliente.setText("");
        lblBaseImponible.setText("");
        lblIVA.setText("");
        lblTotal.setText("");
        lblFormaPago.setText("");
        lblCobrada.setText("");
        lblFechaCobro.setText("");
        txtObservaciones.setText("");
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

    // Clase para representar una Factura
    public static class Factura {
        private int numeroFactura;
        private LocalDate fechaFactura;
        private String nombreCliente;
        private String formaPago;
        private double totalFactura;
        private boolean cobrada;
        private LocalDate fechaCobro;
        private double baseImponible;
        private double ivaFactura;
        private String observaciones;

        public Factura(int numeroFactura, LocalDate fechaFactura, String nombreCliente,
                       String formaPago, double totalFactura, boolean cobrada,
                       LocalDate fechaCobro, double baseImponible, double ivaFactura,
                       String observaciones) {
            this.numeroFactura = numeroFactura;
            this.fechaFactura = fechaFactura;
            this.nombreCliente = nombreCliente;
            this.formaPago = formaPago;
            this.totalFactura = totalFactura;
            this.cobrada = cobrada;
            this.fechaCobro = fechaCobro;
            this.baseImponible = baseImponible;
            this.ivaFactura = ivaFactura;
            this.observaciones = observaciones;
        }

        // Getters y setters
        public int getNumeroFactura() { return numeroFactura; }
        public void setNumeroFactura(int numeroFactura) { this.numeroFactura = numeroFactura; }

        public LocalDate getFechaFactura() { return fechaFactura; }
        public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getFormaPago() { return formaPago; }
        public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

        public double getTotalFactura() { return totalFactura; }
        public void setTotalFactura(double totalFactura) { this.totalFactura = totalFactura; }

        public boolean isCobrada() { return cobrada; }
        public void setCobrada(boolean cobrada) { this.cobrada = cobrada; }

        public LocalDate getFechaCobro() { return fechaCobro; }
        public void setFechaCobro(LocalDate fechaCobro) { this.fechaCobro = fechaCobro; }

        public double getBaseImponible() { return baseImponible; }
        public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }

        public double getIvaFactura() { return ivaFactura; }
        public void setIvaFactura(double ivaFactura) { this.ivaFactura = ivaFactura; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }

    // Clase para representar una línea de factura (adaptada de CrearFactura)
    public static class LineaFactura {
        private String codigoArticulo;
        private String descripcion;
        private double cantidad;
        private double precio;
        private double descuento;
        private double importe;
        private double porcentajeIVA;
        private int idProveedor;
        private String nombreProveedor;

        public LineaFactura(String codigoArticulo, String descripcion, double cantidad,
                            double precio, double descuento, double importe,
                            double porcentajeIVA, int idProveedor, String nombreProveedor) {
            this.codigoArticulo = codigoArticulo;
            this.descripcion = descripcion;
            this.cantidad = cantidad;
            this.precio = precio;
            this.descuento = descuento;
            this.importe = importe;
            this.porcentajeIVA = porcentajeIVA;
            this.idProveedor = idProveedor;
            this.nombreProveedor = nombreProveedor;
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

        public String getNombreProveedor() { return nombreProveedor; }
        public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    }
}