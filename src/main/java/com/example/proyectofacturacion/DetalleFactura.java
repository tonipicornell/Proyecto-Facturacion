package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;

public class DetalleFactura {

    // Cliente
    @FXML private TextField txtCliente;
    @FXML private TextField txtDireccion, txtCIF, txtPoblacion, txtProvincia, txtPais;

    // Factura
    @FXML private TextField txtNumeroFactura;
    @FXML private TextField txtFechaFactura, txtFechaCobro;
    @FXML private TextField txtFormaPago;
    @FXML private CheckBox checkCobrada;
    @FXML private TextArea txtObservaciones;

    // Tabla de líneas de factura
    @FXML private TableView<CrearFactura.LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<CrearFactura.LineaFactura, String> colCodigo, colDescripcion;
    @FXML private TableColumn<CrearFactura.LineaFactura, Double> colCantidad, colPrecio, colDescuento, colImporte, colIVA;

    // Totales
    @FXML private TextField txtBaseImponible, txtTotalIVA, txtTotalFactura;

    // Botones de acción
    @FXML private Button btnVolver, btnImprimir;

    // Lista para las líneas de factura
    private final ObservableList<CrearFactura.LineaFactura> lineasFactura = FXCollections.observableArrayList();

    // ID de la factura a mostrar
    private int idFactura;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarCamposNoEditables();
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
        cargarDatosFactura();
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

    private void configurarCamposNoEditables() {
        // Cliente
        txtCliente.setEditable(false);
        txtDireccion.setEditable(false);
        txtCIF.setEditable(false);
        txtPoblacion.setEditable(false);
        txtProvincia.setEditable(false);
        txtPais.setEditable(false);

        // Factura
        txtNumeroFactura.setEditable(false);
        txtFechaFactura.setEditable(false);
        txtFechaCobro.setEditable(false);
        txtFormaPago.setEditable(false);
        checkCobrada.setDisable(true);
        txtObservaciones.setEditable(false);

        // Totales
        txtBaseImponible.setEditable(false);
        txtTotalIVA.setEditable(false);
        txtTotalFactura.setEditable(false);

        // Configurar estilos para campos de solo lectura
        String readOnlyStyle = "-fx-control-inner-background: #F0F0F0; -fx-opacity: 1.0;";
        txtCliente.setStyle(readOnlyStyle);
        txtDireccion.setStyle(readOnlyStyle);
        txtCIF.setStyle(readOnlyStyle);
        txtPoblacion.setStyle(readOnlyStyle);
        txtProvincia.setStyle(readOnlyStyle);
        txtPais.setStyle(readOnlyStyle);
        txtNumeroFactura.setStyle(readOnlyStyle);
        txtFechaFactura.setStyle(readOnlyStyle);
        txtFechaCobro.setStyle(readOnlyStyle);
        txtFormaPago.setStyle(readOnlyStyle);
        txtObservaciones.setStyle(readOnlyStyle);
        txtBaseImponible.setStyle(readOnlyStyle);
        txtTotalIVA.setStyle(readOnlyStyle);
        txtTotalFactura.setStyle(readOnlyStyle);
    }

    private void cargarDatosFactura() {
        String sql = "SELECT fc.*, c.*, fp.tipoFormaPago " +
                "FROM facturasClientes fc " +
                "JOIN clientes c ON fc.idClienteFactura = c.id " +
                "JOIN formaPago fp ON fc.formaCobroFactura = fp.idFormaPago " +
                "WHERE fc.idFacturaCliente = ?";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFactura);
            int numeroFactura = 0;

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Cargar datos de factura
                    numeroFactura = rs.getInt("numeroFacturaCliente");
                    txtNumeroFactura.setText(String.valueOf(numeroFactura));

                    Date fechaFactura = rs.getDate("fechaFacturaCliente");
                    txtFechaFactura.setText(fechaFactura != null ? fechaFactura.toLocalDate().toString() : "");

                    Date fechaCobro = rs.getDate("fechaCobroFactura");
                    txtFechaCobro.setText(fechaCobro != null ? fechaCobro.toLocalDate().toString() : "");

                    txtFormaPago.setText(rs.getString("tipoFormaPago"));
                    checkCobrada.setSelected(rs.getBoolean("cobradaFactura"));
                    txtObservaciones.setText(rs.getString("observacionesFacturaClientes"));

                    // Cargar datos del cliente
                    txtCliente.setText(rs.getString("nombreCliente"));
                    txtDireccion.setText(rs.getString("direccionCliente"));
                    txtCIF.setText(rs.getString("cifCliente"));
                    txtPoblacion.setText(rs.getString("poblacionCliente"));
                    txtProvincia.setText(rs.getString("provinciaCliente"));
                    txtPais.setText(rs.getString("paisCliente"));

                    // Cargar totales
                    double baseImponible = rs.getDouble("baseImponibleFacturaCliente");
                    double totalIVA = rs.getDouble("ivaFacturaCliente");
                    double totalFactura = rs.getDouble("totalFacturaCliente");

                    txtBaseImponible.setText(String.format("%.2f", baseImponible));
                    txtTotalIVA.setText(String.format("%.2f", totalIVA));
                    txtTotalFactura.setText(String.format("%.2f", totalFactura));
                }
            }

            // Cargar líneas de factura solo si hemos obtenido un número de factura válido
            if (numeroFactura > 0) {
                cargarLineasFactura(conn, numeroFactura);
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar factura", e.getMessage());
        }
    }

    private void cargarLineasFactura(Connection conn, int numeroFactura) throws SQLException {
        String sql = "SELECT * FROM lineasFacturasClientes WHERE numeroFacturaCliente = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, numeroFactura);

            try (ResultSet rs = pstmt.executeQuery()) {
                lineasFactura.clear();

                while (rs.next()) {
                    // Para cada línea, calculamos el importe
                    double precio = rs.getDouble("pvpArticulo");
                    double cantidad = rs.getDouble("cantidadArticulo");
                    double descuento = rs.getDouble("descuentoArticulo");
                    double importe = precio * cantidad * (1 - descuento / 100);

                    CrearFactura.LineaFactura linea = new CrearFactura.LineaFactura(
                            rs.getString("codigoArticulo"),
                            rs.getString("descripcionArticulo"),
                            cantidad,
                            precio,
                            descuento,
                            importe,
                            rs.getDouble("ivaArticulo"),
                            rs.getInt("idProveedorArticulo")
                    );

                    lineasFactura.add(linea);
                }
            }
        }
    }

    @FXML
    private void volverAtras() {
        // Aquí iría el código para volver a la pantalla anterior
        // Por ejemplo:
        // Stage stage = (Stage) btnVolver.getScene().getWindow();
        // stage.close();
    }

    @FXML
    private void imprimirFactura() {
        // Aquí iría el código para imprimir la factura
        // Este método se conectaría con la funcionalidad de impresión
        mostrarInformacion("Imprimir", "Función de impresión en desarrollo");
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
}