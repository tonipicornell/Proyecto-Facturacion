package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class BuscarRectificativa {

    // Campos de búsqueda
    @FXML private TextField txtNumeroRectificativa;
    @FXML private ComboBox<Cliente> comboClientes;
    @FXML private DatePicker dateFechaDesde;
    @FXML private DatePicker dateFechaHasta;
    @FXML private Button btnBuscar, btnLimpiar, btnEditar, btnImprimir;

    // Tabla de resultados
    @FXML private TableView<RectificativaResumen> tablaRectificativas;
    @FXML private TableColumn<RectificativaResumen, Integer> colNumero;
    @FXML private TableColumn<RectificativaResumen, Date> colFecha;
    @FXML private TableColumn<RectificativaResumen, String> colCliente;
    @FXML private TableColumn<RectificativaResumen, Integer> colFacturaOriginal;
    @FXML private TableColumn<RectificativaResumen, String> colMotivo;
    @FXML private TableColumn<RectificativaResumen, Double> colBaseImponible, colIVA, colTotal;

    // Detalle de la rectificativa seleccionada
    @FXML private TextArea txtObservaciones;

    // Tabla de líneas de la rectificativa seleccionada
    @FXML private TableView<LineaRectificativa> tablaLineasRectificativa;
    @FXML private TableColumn<LineaRectificativa, String> colCodigo, colDescripcion;
    @FXML private TableColumn<LineaRectificativa, Double> colCantidad, colPrecio, colDescuento, colImporte, colIVALinea;

    // Listas observables
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private final ObservableList<RectificativaResumen> listaRectificativas = FXCollections.observableArrayList();
    private final ObservableList<LineaRectificativa> lineasRectificativa = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTablas();
        cargarClientes();
        configurarListeners();
        configurarFechas();
    }

    private void configurarTablas() {
        // Configurar tabla de rectificativas
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroRectificativa"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRectificativa"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFacturaOriginal.setCellValueFactory(new PropertyValueFactory<>("facturaOriginal"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colBaseImponible.setCellValueFactory(new PropertyValueFactory<>("baseImponible"));
        colIVA.setCellValueFactory(new PropertyValueFactory<>("iva"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tablaRectificativas.setItems(listaRectificativas);

        // Configurar tabla de líneas de rectificativa
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));
        colIVALinea.setCellValueFactory(new PropertyValueFactory<>("porcentajeIVA"));
        tablaLineasRectificativa.setItems(lineasRectificativa);
    }

    private void configurarFechas() {
        // Inicializar fechas con el primer día del mes actual y hoy
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaMes = LocalDate.of(hoy.getYear(), hoy.getMonth(), 1);

        dateFechaDesde.setValue(primerDiaMes);
        dateFechaHasta.setValue(hoy);
    }

    private void cargarClientes() {
        String sql = "SELECT * FROM clientes ORDER BY nombreCliente";
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

    private void configurarListeners() {
        btnBuscar.setOnAction(e -> buscarRectificativas());
        btnLimpiar.setOnAction(e -> limpiarFiltros());
        btnImprimir.setOnAction(e -> imprimirRectificativa());
        btnEditar.setOnAction(e -> editarRectificativa());

        tablaRectificativas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarDetalleRectificativa(newValue.getIdRectificativa());
                    } else {
                        limpiarDetalle();
                    }
                });
    }

    @FXML
    private void buscarRectificativas() {
        listaRectificativas.clear();

        StringBuilder sql = new StringBuilder(
                "SELECT r.idRectificativaCliente, r.numeroRectificativaCliente, r.fechaRectificativaCliente, " +
                        "c.nombreCliente, r.baseImponibleRectificativaCliente, r.ivaRectificativaCliente, " +
                        "r.totalRectificativaCliente, r.observacionesRectificativaCliente " +
                        "FROM rectificativasClientes r " +
                        "INNER JOIN clientes c ON r.idClienteRectificativaCliente = c.id " +
                        "WHERE 1=1 ");

        if (!txtNumeroRectificativa.getText().isEmpty()) {
            sql.append("AND r.numeroRectificativaCliente = ").append(txtNumeroRectificativa.getText()).append(" ");
        }

        if (comboClientes.getValue() != null) {
            try (Connection conn = DataBaseConnected.getConnection()) {
                int idCliente = obtenerIdCliente(conn, comboClientes.getValue().getNombreCliente());
                sql.append("AND r.idClienteRectificativaCliente = ").append(idCliente).append(" ");
            } catch (SQLException ex) {
                mostrarError("Error al obtener ID del cliente", ex.getMessage());
                return;
            }
        }

        if (dateFechaDesde.getValue() != null) {
            sql.append("AND r.fechaRectificativaCliente >= '").append(dateFechaDesde.getValue()).append("' ");
        }

        if (dateFechaHasta.getValue() != null) {
            sql.append("AND r.fechaRectificativaCliente <= '").append(dateFechaHasta.getValue()).append("' ");
        }

        sql.append("ORDER BY r.numeroRectificativaCliente DESC");

        ejecutarConsulta(sql.toString(), rs -> {
            int idRectificativa = rs.getInt("idRectificativaCliente");
            int numeroRectificativa = rs.getInt("numeroRectificativaCliente");
            Date fechaRectificativa = rs.getDate("fechaRectificativaCliente");
            String nombreCliente = rs.getString("nombreCliente");
            double baseImponible = rs.getDouble("baseImponibleRectificativaCliente");
            double iva = rs.getDouble("ivaRectificativaCliente");
            double total = rs.getDouble("totalRectificativaCliente");
            String observaciones = rs.getString("observacionesRectificativaCliente");

            // Extraer número de factura original y motivo de las observaciones
            int facturaOriginal = 0;
            String motivo = "";

            if (observaciones != null) {
                // Buscar "Factura original: X" en observaciones
                String[] lineas = observaciones.split("\n");
                for (String linea : lineas) {
                    if (linea.startsWith("Factura original:")) {
                        try {
                            facturaOriginal = Integer.parseInt(linea.substring(linea.indexOf(":") + 1).trim());
                        } catch (NumberFormatException e) {
                            // Ignorar error de parseo
                        }
                    } else if (linea.startsWith("Motivo:")) {
                        motivo = linea.substring(linea.indexOf(":") + 1).trim();
                    }
                }
            }

            RectificativaResumen resumen = new RectificativaResumen(
                    idRectificativa,
                    numeroRectificativa,
                    fechaRectificativa,
                    nombreCliente,
                    facturaOriginal,
                    motivo,
                    baseImponible,
                    iva,
                    total
            );

            listaRectificativas.add(resumen);
        }, "Error al buscar rectificativas");

        // Habilitar/deshabilitar botones según resultados
        boolean hayResultados = !listaRectificativas.isEmpty();
        btnImprimir.setDisable(!hayResultados);
        btnEditar.setDisable(!hayResultados);

        if (!hayResultados) {
            mostrarInformacion("Búsqueda", "No se encontraron rectificativas con los criterios especificados.");
        }
    }

    private void cargarDetalleRectificativa(int idRectificativa) {
        // Cargar observaciones
        String sqlObservaciones = "SELECT observacionesRectificativaCliente FROM rectificativasClientes " +
                "WHERE idRectificativaCliente = " + idRectificativa;

        ejecutarConsulta(sqlObservaciones, rs -> {
            txtObservaciones.setText(rs.getString("observacionesRectificativaCliente"));
        }, "Error al cargar observaciones");

        // Cargar líneas de la rectificativa
        lineasRectificativa.clear();
        String sqlLineas = "SELECT * FROM lineasRectificativasClientes WHERE idRectificativaCliente = " + idRectificativa;

        ejecutarConsulta(sqlLineas, rs -> {
            String codigoArticulo = rs.getString("codigoArticulo");
            String descripcionArticulo = rs.getString("descripcionArticulo");
            double cantidad = 1.0; // Por defecto, ya que no se especifica en la BD
            double precio = rs.getDouble("pvpArticulo");
            double descuento = 0.0; // Por defecto, ya que no se especifica en la BD
            double importe = precio; // Cálculo simplificado
            double porcentajeIVA = rs.getDouble("ivaArticulo");
            int idProveedor = rs.getInt("idProveedorArticulo");

            LineaRectificativa linea = new LineaRectificativa(
                    codigoArticulo,
                    descripcionArticulo,
                    cantidad,
                    precio,
                    descuento,
                    importe,
                    porcentajeIVA,
                    idProveedor
            );

            lineasRectificativa.add(linea);
        }, "Error al cargar líneas de rectificativa");
    }

    private void limpiarFiltros() {
        txtNumeroRectificativa.clear();
        comboClientes.setValue(null);
        configurarFechas(); // Restablecer fechas por defecto
        listaRectificativas.clear();
        limpiarDetalle();
        btnImprimir.setDisable(true);
        btnEditar.setDisable(true);
    }

    private void limpiarDetalle() {
        txtObservaciones.clear();
        lineasRectificativa.clear();
    }

    private void imprimirRectificativa() {
        RectificativaResumen seleccionada = tablaRectificativas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            // Implementar lógica de impresión
            mostrarInformacion("Imprimir", "Preparando impresión de la rectificativa #" + seleccionada.getNumeroRectificativa());
            // Aquí iría la lógica de impresión real
        } else {
            mostrarError("Error", "Debe seleccionar una rectificativa para imprimir");
        }
    }

    private void editarRectificativa() {
        RectificativaResumen seleccionada = tablaRectificativas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            // Implementar lógica para abrir el formulario de edición
            mostrarInformacion("Editar", "Abriendo formulario de edición para la rectificativa #" + seleccionada.getNumeroRectificativa());
            // Aquí iría la lógica para abrir el formulario de edición
        } else {
            mostrarError("Error", "Debe seleccionar una rectificativa para editar");
        }
    }

    private int obtenerIdCliente(Connection conn, String nombreCliente) throws SQLException {
        String sql = "SELECT id FROM clientes WHERE nombreCliente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Cliente no encontrado: " + nombreCliente);
                }
            }
        }
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

    // Interfaz funcional para callback de consultas SQL
    @FunctionalInterface
    private interface ConsultaCallback {
        void procesar(ResultSet rs) throws SQLException;
    }

    // Clase para representar el resumen de una rectificativa en la tabla principal
    public static class RectificativaResumen {
        private int idRectificativa;
        private int numeroRectificativa;
        private Date fechaRectificativa;
        private String nombreCliente;
        private int facturaOriginal;
        private String motivo;
        private double baseImponible;
        private double iva;
        private double total;

        public RectificativaResumen(int idRectificativa, int numeroRectificativa, Date fechaRectificativa,
                                    String nombreCliente, int facturaOriginal, String motivo,
                                    double baseImponible, double iva, double total) {
            this.idRectificativa = idRectificativa;
            this.numeroRectificativa = numeroRectificativa;
            this.fechaRectificativa = fechaRectificativa;
            this.nombreCliente = nombreCliente;
            this.facturaOriginal = facturaOriginal;
            this.motivo = motivo;
            this.baseImponible = baseImponible;
            this.iva = iva;
            this.total = total;
        }

        // Getters y setters
        public int getIdRectificativa() { return idRectificativa; }
        public void setIdRectificativa(int idRectificativa) { this.idRectificativa = idRectificativa; }

        public int getNumeroRectificativa() { return numeroRectificativa; }
        public void setNumeroRectificativa(int numeroRectificativa) { this.numeroRectificativa = numeroRectificativa; }

        public Date getFechaRectificativa() { return fechaRectificativa; }
        public void setFechaRectificativa(Date fechaRectificativa) { this.fechaRectificativa = fechaRectificativa; }

        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

        public int getFacturaOriginal() { return facturaOriginal; }
        public void setFacturaOriginal(int facturaOriginal) { this.facturaOriginal = facturaOriginal; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }

        public double getBaseImponible() { return baseImponible; }
        public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }

        public double getIva() { return iva; }
        public void setIva(double iva) { this.iva = iva; }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
    }

    // Clase para representar una línea de rectificativa en la tabla de detalle
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

    // Conversor para ComboBox de clientes
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
}