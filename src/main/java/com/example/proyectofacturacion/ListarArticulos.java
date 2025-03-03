package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ListarArticulos implements Initializable {

    @FXML
    private TableView<ArticuloView> tablaArticulos;

    @FXML
    private TableColumn<ArticuloView, String> colCodigo;

    @FXML
    private TableColumn<ArticuloView, String> colCodigoBarras;

    @FXML
    private TableColumn<ArticuloView, String> colDescripcion;

    @FXML
    private TableColumn<ArticuloView, String> colFamilia;

    @FXML
    private TableColumn<ArticuloView, Double> colCoste;

    @FXML
    private TableColumn<ArticuloView, Double> colMargen;

    @FXML
    private TableColumn<ArticuloView, Double> colPVP;

    @FXML
    private TableColumn<ArticuloView, String> colProveedor;

    @FXML
    private TableColumn<ArticuloView, Double> colStock;

    @FXML
    private TableColumn<ArticuloView, String> colTipoIva;

    @FXML
    private TableColumn<ArticuloView, String> colPaisIva;

    @FXML
    private TableColumn<ArticuloView, Double> colPrecioConIVA;

    private ObservableList<ArticuloView> listaArticulos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarDatos();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigoBarrasArticulo"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionArticulo"));
        colFamilia.setCellValueFactory(new PropertyValueFactory<>("nombreFamilia"));
        colCoste.setCellValueFactory(new PropertyValueFactory<>("costeArticulo"));
        colMargen.setCellValueFactory(new PropertyValueFactory<>("margenComercialArticulo"));
        colPVP.setCellValueFactory(new PropertyValueFactory<>("pvpArticulo"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("nombreProveedor"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockArticulo"));
        colTipoIva.setCellValueFactory(new PropertyValueFactory<>("nombreTipoIva"));
        colPaisIva.setCellValueFactory(new PropertyValueFactory<>("paisIva"));
        colPrecioConIVA.setCellValueFactory(new PropertyValueFactory<>("precioConIVA"));
    }

    private void cargarDatos() {
        listaArticulos.clear();

        String query = "SELECT a.*, " +
                "f.denominacionFamilias as nombreFamilia, " +
                "p.nombreProveedor, " +
                "t.tipoIva as nombreTipoIva, " +
                "t.iva as porcentajeIva, " +
                "t.pais as paisIva " +
                "FROM articulos a " +
                "LEFT JOIN familiaArticulos f ON a.familiaArticulo = f.idFamiliaArticulos " +
                "LEFT JOIN proveedores p ON a.proveedorArticulo = p.idProveedor " +
                "LEFT JOIN tiposIva t ON a.tipoIva = t.idTipoIva";

        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ArticuloView articulo = new ArticuloView(
                        rs.getString("codigoArticulo"),
                        rs.getString("codigoBarrasArticulo"),
                        rs.getString("descripcionArticulo"),
                        rs.getInt("familiaArticulo"),
                        rs.getString("nombreFamilia"),
                        rs.getDouble("costeArticulo"),
                        rs.getDouble("margenComercialArticulo"),
                        rs.getDouble("pvpArticulo"),
                        rs.getInt("proveedorArticulo"),
                        rs.getString("nombreProveedor"),
                        rs.getDouble("stockArticulo"),
                        rs.getString("observacionesArticulo"),
                        rs.getInt("tipoIva"),
                        rs.getString("nombreTipoIva"),
                        rs.getString("paisIva"),
                        rs.getDouble("porcentajeIva"),
                        rs.getDouble("precioConIVA")
                );

                listaArticulos.add(articulo);
            }

            tablaArticulos.setItems(listaArticulos);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarLista() {
        cargarDatos();
    }
}