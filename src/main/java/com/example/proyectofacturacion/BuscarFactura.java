package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class BuscarFactura {

    @FXML private TableView<FacturaResumen> tablaFacturas;
    @FXML private TableColumn<FacturaResumen, Integer> colNumeroFactura;
    @FXML private TableColumn<FacturaResumen, String> colCliente;
    @FXML private TableColumn<FacturaResumen, LocalDate> colFechaCobro;
    @FXML private TableColumn<FacturaResumen, Boolean> colCobrada;
    @FXML private TableColumn<FacturaResumen, Double> colTotal;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;

    private final ObservableList<FacturaResumen> listaFacturas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarFacturas();

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList("Todos", "Cliente"));
        searchTypeCombo.setValue("Todos");
    }

    private void configurarTabla() {
        colNumeroFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFechaCobro.setCellValueFactory(new PropertyValueFactory<>("fechaCobro"));
        colCobrada.setCellValueFactory(new PropertyValueFactory<>("cobrada"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalFactura"));

        // Configurar formato de fecha
        colFechaCobro.setCellFactory(column -> new TableCell<FacturaResumen, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(formatter.format(fecha));
                }
            }
        });

        // Configurar columna de estado de cobro
        colCobrada.setCellFactory(column -> new TableCell<FacturaResumen, Boolean>() {
            @Override
            protected void updateItem(Boolean cobrada, boolean empty) {
                super.updateItem(cobrada, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(cobrada ? "Sí" : "No");
                }
            }
        });

        // Configurar formato de totales
        colTotal.setCellFactory(column -> new TableCell<FacturaResumen, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", total));
                }
            }
        });

        tablaFacturas.setItems(listaFacturas);
    }

    private void cargarFacturas() {
        String sql = "SELECT f.numeroFacturaCliente, f.fechaFacturaCliente, c.nombreCliente, " +
                "f.cobradaFactura, f.fechaCobroFactura, f.totalFacturaCliente " +
                "FROM facturasClientes f " +
                "JOIN clientes c ON f.idClienteFactura = c.id " +
                "ORDER BY f.numeroFacturaCliente DESC";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Date fechaCobroSQL = rs.getDate("fechaCobroFactura");
                LocalDate fechaCobro = fechaCobroSQL != null ? fechaCobroSQL.toLocalDate() : null;

                FacturaResumen factura = new FacturaResumen(
                        rs.getInt("numeroFacturaCliente"),
                        rs.getString("nombreCliente"),
                        fechaCobro,
                        rs.getBoolean("cobradaFactura"),
                        rs.getDouble("totalFacturaCliente")
                );
                listaFacturas.add(factura);
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar facturas", e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = (searchTypeCombo.getValue() != null) ? searchTypeCombo.getValue() : "Todos";
        filterFacturas(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tablaFacturas.setItems(listaFacturas);
    }

    private void filterFacturas(String searchText, String searchType) {
        ObservableList<FacturaResumen> filteredList = FXCollections.observableArrayList();
        Predicate<FacturaResumen> predicate = factura -> {
            if (searchType.equals("Todos")) {
                return factura.getNombreCliente().toLowerCase().contains(searchText);
            } else if (searchType.equals("Cliente")) {
                return factura.getNombreCliente().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (FacturaResumen factura : listaFacturas) {
            if (predicate.test(factura)) {
                filteredList.add(factura);
            }
        }

        tablaFacturas.setItems(filteredList);
    }

    // Clase para almacenar los datos resumidos de las facturas
    public static class FacturaResumen {
        private final int numeroFactura;
        private final String nombreCliente;
        private final LocalDate fechaCobro;
        private final boolean cobrada;
        private final double totalFactura;

        public FacturaResumen(int numeroFactura, String nombreCliente, LocalDate fechaCobro,
                              boolean cobrada, double totalFactura) {
            this.numeroFactura = numeroFactura;
            this.nombreCliente = nombreCliente;
            this.fechaCobro = fechaCobro;
            this.cobrada = cobrada;
            this.totalFactura = totalFactura;
        }

        // Getters
        public int getNumeroFactura() { return numeroFactura; }
        public String getNombreCliente() { return nombreCliente; }
        public LocalDate getFechaCobro() { return fechaCobro; }
        public boolean isCobrada() { return cobrada; }
        public double getTotalFactura() { return totalFactura; }
    }
}
