package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;

public class VerFactura {

    // Criterios de búsqueda
    @FXML private TextField txtNumeroFactura;
    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private DatePicker dateFechaDesde, dateFechaHasta;
    @FXML private CheckBox checkCobradas, checkPendientes;
    @FXML private ComboBox<String> comboFormaPago;

    // Tabla de facturas
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, Integer> colNumero;
    @FXML private TableColumn<Factura, String> colFecha, colCliente, colFormaPago;
    @FXML private TableColumn<Factura, Double> colBase, colIVA, colTotal;
    @FXML private TableColumn<Factura, Boolean> colCobrada;

    // Detalles de la factura seleccionada
    @FXML private Label lblNumeroFactura, lblFechaFactura, lblCliente, lblDireccion;
    @FXML private Label lblCIF, lblPoblacion, lblProvincia, lblPais;
    @FXML private Label lblFormaPago, lblFechaCobro, lblEstado, lblObservaciones;
    @FXML private Label lblBaseImponible, lblTotalIVA, lblTotalFactura;

    // Tabla de líneas de factura
    @FXML private TableView<CrearFactura.LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<CrearFactura.LineaFactura, String> colCodigo, colDescripcion;
    @FXML private TableColumn<CrearFactura.LineaFactura, Double> colCantidad, colPrecio, colDescuento, colImporte, colIVALinea;

    // Botones de acción
    @FXML private Button btnBuscar, btnLimpiarFiltros, btnVerDetalle, btnImprimir, btnExportar;

    // Listas para los ComboBox
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final ObservableList<String> listaFormasPago = FXCollections.observableArrayList();
    private final ObservableList<Factura> listaFacturas = FXCollections.observableArrayList();
    private final ObservableList<CrearFactura.LineaFactura> lineasFacturaSeleccionada = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTablas();
        cargarDatosIniciales();
        configurarListeners();
        configurarFiltrosIniciales();
    }

    private void configurarTablas() {
        // Configurar tabla de facturas
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFormaPago.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
        colBase.setCellValueFactory(new PropertyValueFactory<>("baseImponible"));
        colIVA.setCellValueFactory(new PropertyValueFactory<>("totalIVA"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalFactura"));
        colCobrada.setCellValueFactory(new PropertyValueFactory<>("cobrada"));

        // Configurar la forma en que se muestra el valor booleano de "cobrada"
        colCobrada.setCellFactory(column -> new TableCell<Factura, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Sí" : "No");
                }
            }
        });

        tablaFacturas.setItems(listaFacturas);

        // Configurar tabla de líneas de factura
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colIVALinea.setCellValueFactory(new PropertyValueFactory<>("porcentajeIVA"));
        tablaLineasFactura.setItems(lineasFacturaSeleccionada);
    }

    private void cargarDatosIniciales() {
        cargarClientes();
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

    private void cargarFormasPago() {
        ejecutarConsulta("SELECT * FROM formaPago",
                rs -> listaFormasPago.add(rs.getString("tipoFormaPago")),
                "Error al cargar formas de pago"
        );
        comboFormaPago.setItems(listaFormasPago);
    }

    private void configurarListeners() {
        btnBuscar.setOnAction(e -> buscarFacturas());
        btnLimpiarFiltros.setOnAction(e -> limpiarFiltros());
        btnVerDetalle.setOnAction(e -> mostrarDetalleFactura());
        btnImprimir.setOnAction(e -> imprimirFactura());
        btnExportar.setOnAction(e -> exportarFactura());

        // Listener para mostrar detalles al seleccionar una factura
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarDetalleFactura(newValue);
                    } else {
                        limpiarDetalleFactura();
                    }
                });
    }

    private void configurarFiltrosIniciales() {
        // Configurar fechas por defecto (último mes)
        dateFechaHasta.setValue(LocalDate.now());
        dateFechaDesde.setValue(LocalDate.now().minusMonths(1));

        // Marcar ambas opciones de estado por defecto
        checkCobradas.setSelected(true);
        checkPendientes.setSelected(true);
    }

    private void buscarFacturas() {
        listaFacturas.clear();

        StringBuilder sql = new StringBuilder(
                "SELECT f.*, c.nombreCliente, c.direccionCliente, c.cifCliente, " +
                        "c.poblacionCliente, c.provinciaCliente, c.paisCliente, " +
                        "fp.tipoFormaPago " +
                        "FROM facturasClientes f " +
                        "LEFT JOIN clientes c ON f.idClienteFactura = c.id " +
                        "LEFT JOIN formaPago fp ON f.formaCobroFactura = fp.idFormaPago " +
                        "WHERE 1=1");

        // Añadir condiciones según los filtros
        if (txtNumeroFactura.getText() != null && !txtNumeroFactura.getText().isEmpty()) {
            sql.append(" AND f.numeroFacturaCliente = ").append(txtNumeroFactura.getText());
        }

        if (comboClientes.getValue() != null) {
            sql.append(" AND c.nombreCliente = '").append(comboClientes.getValue().getNombreCliente()).append("'");
        }

        if (dateFechaDesde.getValue() != null) {
            sql.append(" AND f.fechaFacturaCliente >= '").append(dateFechaDesde.getValue()).append("'");
        }

        if (dateFechaHasta.getValue() != null) {
            sql.append(" AND f.fechaFacturaCliente <= '").append(dateFechaHasta.getValue()).append("'");
        }

        if (comboFormaPago.getValue() != null) {
            sql.append(" AND fp.tipoFormaPago = '").append(comboFormaPago.getValue()).append("'");
        }

        // Filtrar por estado (cobrada/pendiente)
        if (checkCobradas.isSelected() && !checkPendientes.isSelected()) {
            sql.append(" AND f.cobradaFactura = 1");
        } else if (!checkCobradas.isSelected() && checkPendientes.isSelected()) {
            sql.append(" AND f.cobradaFactura = 0");
        } else if (!checkCobradas.isSelected() && !checkPendientes.isSelected()) {
            // Si no se selecciona ninguno, no mostrar resultados
            sql.append(" AND 1=0");
        }

        sql.append(" ORDER BY f.fechaFacturaCliente DESC, f.numeroFacturaCliente DESC");

        ejecutarConsulta(sql.toString(),
                rs -> {
                    Factura factura = new Factura(
                            rs.getInt("numeroFacturaCliente"),
                            rs.getDate("fechaFacturaCliente").toLocalDate(),
                            rs.getString("nombreCliente"),
                            rs.getString("direccionCliente"),
                            rs.getString("cifCliente"),
                            rs.getString("poblacionCliente"),
                            rs.getString("provinciaCliente"),
                            rs.getString("paisCliente"),
                            rs.getDouble("baseImponibleFacturaCliente"),
                            rs.getDouble("ivaFacturaCliente"),
                            rs.getDouble("totalFacturaCliente"),
                            rs.getBoolean("cobradaFactura"),
                            rs.getString("tipoFormaPago"),
                            rs.getDate("fechaCobroFactura") != null ? rs.getDate("fechaCobroFactura").toLocalDate() : null,
                            rs.getString("observacionesFacturaClientes")
                    );
                    listaFacturas.add(factura);
                },
                "Error al buscar facturas"
        );

        if (listaFacturas.isEmpty()) {
            mostrarInformacion("Búsqueda", "No se encontraron facturas con los criterios especificados.");
        }
    }

    private void limpiarFiltros() {
        txtNumeroFactura.clear();
        comboClientes.setValue(null);
        dateFechaDesde.setValue(LocalDate.now().minusMonths(1));
        dateFechaHasta.setValue(LocalDate.now());
        checkCobradas.setSelected(true);
        checkPendientes.setSelected(true);
        comboFormaPago.setValue(null);

        listaFacturas.clear();
        limpiarDetalleFactura();
    }

    private void cargarDetalleFactura(Factura factura) {
        // Mostrar datos de la factura en los labels
        lblNumeroFactura.setText(String.valueOf(factura.getNumeroFactura()));
        lblFechaFactura.setText(factura.getFechaFactura().toString());
        lblCliente.setText(factura.getNombreCliente());
        lblDireccion.setText(factura.getDireccionCliente());
        lblCIF.setText(factura.getCifCliente());
        lblPoblacion.setText(factura.getPoblacionCliente());
        lblProvincia.setText(factura.getProvinciaCliente());
        lblPais.setText(factura.getPaisCliente());
        lblFormaPago.setText(factura.getFormaPago());
        lblFechaCobro.setText(factura.getFechaCobro() != null ? factura.getFechaCobro().toString() : "Pendiente");
        lblEstado.setText(factura.isCobrada() ? "Cobrada" : "Pendiente");
        lblObservaciones.setText(factura.getObservaciones());
        lblBaseImponible.setText(String.format("%.2f €", factura.getBaseImponible()));
        lblTotalIVA.setText(String.format("%.2f €", factura.getTotalIVA()));
        lblTotalFactura.setText(String.format("%.2f €", factura.getTotalFactura()));

        // Cargar líneas de factura
        cargarLineasFactura(factura.getNumeroFactura());

        // Habilitar botones que dependen de la selección
        btnVerDetalle.setDisable(false);
        btnImprimir.setDisable(false);
        btnExportar.setDisable(false);
    }

    private void limpiarDetalleFactura() {
        // Limpiar labels
        lblNumeroFactura.setText("");
        lblFechaFactura.setText("");
        lblCliente.setText("");
        lblDireccion.setText("");
        lblCIF.setText("");
        lblPoblacion.setText("");
        lblProvincia.setText("");
        lblPais.setText("");
        lblFormaPago.setText("");
        lblFechaCobro.setText("");
        lblEstado.setText("");
        lblObservaciones.setText("");
        lblBaseImponible.setText("");
        lblTotalIVA.setText("");
        lblTotalFactura.setText("");

        // Limpiar tabla de líneas
        lineasFacturaSeleccionada.clear();

        // Deshabilitar botones que dependen de la selección
        btnVerDetalle.setDisable(true);
        btnImprimir.setDisable(true);
        btnExportar.setDisable(true);
    }

    private void cargarLineasFactura(int numeroFactura) {
        lineasFacturaSeleccionada.clear();

        String sql = "SELECT l.*, a.descripcionArticulo " +
                "FROM lineasFacturasClientes l " +
                "LEFT JOIN articulos a ON l.codigoArticulo = a.codigoArticulo " +
                "WHERE l.numeroFacturaCliente = " + numeroFactura;

        ejecutarConsulta(sql,
                rs -> {
                    CrearFactura.LineaFactura linea = new CrearFactura.LineaFactura(
                            rs.getString("codigoArticulo"),
                            rs.getString("descripcionArticulo"),
                            rs.getDouble("cantidad"),
                            rs.getDouble("pvpArticulo"),
                            rs.getDouble("descuento"),
                            rs.getDouble("importe"),
                            rs.getDouble("ivaArticulo"),
                            rs.getInt("idProveedorArticulo")
                    );
                    lineasFacturaSeleccionada.add(linea);
                },
                "Error al cargar líneas de factura"
        );
    }

    private void mostrarDetalleFactura() {
        Factura factura = tablaFacturas.getSelectionModel().getSelectedItem();
        if (factura != null) {
            // Aquí se podría abrir una ventana con todos los detalles
            // Por ahora simplemente mostramos una alerta con los datos básicos
            mostrarInformacion("Detalle de Factura",
                    "Número: " + factura.getNumeroFactura() + "\n" +
                            "Fecha: " + factura.getFechaFactura() + "\n" +
                            "Cliente: " + factura.getNombreCliente() + "\n" +
                            "Total: " + String.format("%.2f €", factura.getTotalFactura()) + "\n" +
                            "Estado: " + (factura.isCobrada() ? "Cobrada" : "Pendiente"));
        }
    }

    private void imprimirFactura() {
        Factura factura = tablaFacturas.getSelectionModel().getSelectedItem();
        if (factura != null) {
            mostrarInformacion("Imprimir Factura",
                    "La factura " + factura.getNumeroFactura() + " se está preparando para imprimir.");
            // Aquí iría el código para generar e imprimir la factura
        }
    }

    private void exportarFactura() {
        Factura factura = tablaFacturas.getSelectionModel().getSelectedItem();
        if (factura != null) {
            mostrarInformacion("Exportar Factura",
                    "La factura " + factura.getNumeroFactura() + " se está preparando para exportar.");
            // Aquí iría el código para exportar la factura (PDF, Excel, etc.)
        }
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

    // Clase para representar una factura
    public static class Factura {
        private int numeroFactura;
        private LocalDate fechaFactura;
        private String nombreCliente;
        private String direccionCliente;
        private String cifCliente;
        private String poblacionCliente;
        private String provinciaCliente;
        private String paisCliente;
        private double baseImponible;
        private double totalIVA;
        private double totalFactura;
        private boolean cobrada;
        private String formaPago;
        private LocalDate fechaCobro;
        private String observaciones;

        public Factura(int numeroFactura, LocalDate fechaFactura, String nombreCliente,
                       String direccionCliente, String cifCliente, String poblacionCliente,
                       String provinciaCliente, String paisCliente, double baseImponible,
                       double totalIVA, double totalFactura, boolean cobrada,
                       String formaPago, LocalDate fechaCobro, String observaciones) {
            this.numeroFactura = numeroFactura;
            this.fechaFactura = fechaFactura;
            this.nombreCliente = nombreCliente;
            this.direccionCliente = direccionCliente;
            this.cifCliente = cifCliente;
            this.poblacionCliente = poblacionCliente;
            this.provinciaCliente = provinciaCliente;
            this.paisCliente = paisCliente;
            this.baseImponible = baseImponible;
            this.totalIVA = totalIVA;
            this.totalFactura = totalFactura;
            this.cobrada = cobrada;
            this.formaPago = formaPago;
            this.fechaCobro = fechaCobro;
            this.observaciones = observaciones;
        }

        // Getters y setters
        public int getNumeroFactura() { return numeroFactura; }
        public void setNumeroFactura(int numeroFactura) { this.numeroFactura = numeroFactura; }

        public LocalDate getFechaFactura() { return fechaFactura; }
        public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public String getDireccionCliente() { return direccionCliente; }
        public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

        public String getCifCliente() { return cifCliente; }
        public void setCifCliente(String cifCliente) { this.cifCliente = cifCliente; }

        public String getPoblacionCliente() { return poblacionCliente; }
        public void setPoblacionCliente(String poblacionCliente) { this.poblacionCliente = poblacionCliente; }

        public String getProvinciaCliente() { return provinciaCliente; }
        public void setProvinciaCliente(String provinciaCliente) { this.provinciaCliente = provinciaCliente; }

        public String getPaisCliente() { return paisCliente; }
        public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente; }

        public double getBaseImponible() { return baseImponible; }
        public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }

        public double getTotalIVA() { return totalIVA; }
        public void setTotalIVA(double totalIVA) { this.totalIVA = totalIVA; }

        public double getTotalFactura() { return totalFactura; }
        public void setTotalFactura(double totalFactura) { this.totalFactura = totalFactura; }

        public boolean isCobrada() { return cobrada; }
        public void setCobrada(boolean cobrada) { this.cobrada = cobrada; }

        public String getFormaPago() { return formaPago; }
        public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

        public LocalDate getFechaCobro() { return fechaCobro; }
        public void setFechaCobro(LocalDate fechaCobro) { this.fechaCobro = fechaCobro; }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    }
}