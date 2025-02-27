package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Predicate;

public class FichaArticulo {
    @FXML
    private TableView<Articulo> tableView;
    @FXML
    private TableColumn<Articulo, String> codigo_articulo;
    @FXML
    private TableColumn<Articulo, String> codigo_barras_articulo;
    @FXML
    private TableColumn<Articulo, String> descripcion_articulo;
    @FXML
    private TableColumn<Articulo, Double> pvp_articulo;
    @FXML
    private TableColumn<Articulo, Double> stock_articulo;
    @FXML
    private TableColumn<Articulo, String> familia_articulo;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private Button verDetalleButton;
    @FXML
    private Button crearNuevoButton;

    private ObservableList<Articulo> articulosList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configurar las columnas
        codigo_articulo.setCellValueFactory(new PropertyValueFactory<>("codigoArticulo"));
        codigo_barras_articulo.setCellValueFactory(new PropertyValueFactory<>("codigoBarrasArticulo"));
        descripcion_articulo.setCellValueFactory(new PropertyValueFactory<>("descripcionArticulo"));
        pvp_articulo.setCellValueFactory(new PropertyValueFactory<>("pvpArticulo"));
        stock_articulo.setCellValueFactory(new PropertyValueFactory<>("stockArticulo"));
        familia_articulo.setCellValueFactory(new PropertyValueFactory<>("nombreFamilia"));

        // Formatear las columnas numéricas para mostrar 2 decimales
        pvp_articulo.setCellFactory(col -> new TableCell<Articulo, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", price));
                }
            }
        });

        // Inicializar ComboBox
        searchTypeCombo.setItems(FXCollections.observableArrayList("Todos", "Código", "Código Barras", "Descripción", "Familia"));
        searchTypeCombo.setValue("Todos");

        // Cargar los datos
        cargarDatos();

        // Deshabilitar el botón de detalle hasta que se seleccione un artículo
        verDetalleButton.setDisable(true);

        // Escuchar cambios en la selección de la tabla
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            verDetalleButton.setDisable(newSelection == null);
        });
    }

    @FXML
    private void handleVerDetalle() {
        Articulo articuloSeleccionado = tableView.getSelectionModel().getSelectedItem();
        if (articuloSeleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detalle-articulo.fxml"));
                Parent root = loader.load();

                DetalleArticulo controller = loader.getController();
                controller.setArticulo(articuloSeleccionado);

                Stage stage = new Stage();
                stage.setTitle("Detalle del Artículo: " + articuloSeleccionado.getDescripcionArticulo());
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Recargar datos después de cerrar la ventana de detalle (por si hubo cambios)
                cargarDatos();

            } catch (IOException e) {
                mostrarAlerta("Error al abrir el detalle del artículo", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCrearNuevo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("crear-articulo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Crear Nuevo Artículo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Recargar datos después de cerrar la ventana de creación
            cargarDatos();

        } catch (IOException e) {
            mostrarAlerta("Error al abrir la ventana de creación", e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        String searchType = (searchTypeCombo.getValue() != null) ? searchTypeCombo.getValue() : "Todos";
        filterArticulos(searchText, searchType);
    }

    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        searchTypeCombo.setValue("Todos");
        tableView.setItems(articulosList);
    }

    public void cargarDatos() {
        articulosList.clear();
        String query = "SELECT a.*, f.denominacionFamilias as nombreFamilia " +
                "FROM articulos a " +
                "JOIN familiaArticulos f ON a.familiaArticulo = f.idFamiliaArticulos";

        try (Connection conn = DataBaseConnected.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Articulo articulo = new Articulo(
                        rs.getString("codigoArticulo"),
                        rs.getString("codigoBarrasArticulo"),
                        rs.getString("descripcionArticulo"),
                        rs.getInt("familiaArticulo"),
                        rs.getString("nombreFamilia"),
                        rs.getDouble("costeArticulo"),
                        rs.getDouble("margenComercialArticulo"),
                        rs.getDouble("pvpArticulo"),
                        rs.getInt("proveedorArticulo"),
                        rs.getDouble("stockArticulo"),
                        rs.getString("observacionesArticulo"),
                        rs.getInt("tipoIva"),
                        rs.getDouble("precioConIVA")
                );
                articulosList.add(articulo);
            }
            tableView.setItems(articulosList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar los artículos: " + e.getMessage());
        }
    }

    private void filterArticulos(String searchText, String searchType) {
        ObservableList<Articulo> filteredList = FXCollections.observableArrayList();
        Predicate<Articulo> predicate = articulo -> {
            if (searchText.isEmpty()) {
                return true;
            }

            if (searchType.equals("Todos")) {
                return articulo.getCodigoArticulo().toLowerCase().contains(searchText) ||
                        articulo.getCodigoBarrasArticulo().toLowerCase().contains(searchText) ||
                        articulo.getDescripcionArticulo().toLowerCase().contains(searchText) ||
                        articulo.getNombreFamilia().toLowerCase().contains(searchText);
            } else if (searchType.equals("Código")) {
                return articulo.getCodigoArticulo().toLowerCase().contains(searchText);
            } else if (searchType.equals("Código Barras")) {
                return articulo.getCodigoBarrasArticulo().toLowerCase().contains(searchText);
            } else if (searchType.equals("Descripción")) {
                return articulo.getDescripcionArticulo().toLowerCase().contains(searchText);
            } else if (searchType.equals("Familia")) {
                return articulo.getNombreFamilia().toLowerCase().contains(searchText);
            }
            return false;
        };

        for (Articulo articulo : articulosList) {
            if (predicate.test(articulo)) {
                filteredList.add(articulo);
            }
        }

        tableView.setItems(filteredList);
    }
}