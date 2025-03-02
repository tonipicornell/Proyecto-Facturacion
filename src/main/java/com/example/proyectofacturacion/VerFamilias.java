package com.example.proyectofacturacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerFamilias {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<String> tableViewFamilias;
    @FXML
    private TableColumn<String, String> codigoFamiliaColumn;
    @FXML
    private TableColumn<String, String> denominacionFamiliaColumn;

    private ObservableList<String> familiasData;

    public void initialize() {
        // Configurar las columnas
        codigoFamiliaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split(" - ")[0]));
        denominacionFamiliaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().split(" - ")[1]));

        // Inicializar lista de familias
        familiasData = FXCollections.observableArrayList();
        tableViewFamilias.setItems(familiasData);

        // Cargar todas las familias al inicio
        loadFamilias("");
    }

    private void loadFamilias(String searchQuery) {
        String query = "SELECT codigoFamiliaArticulos, denominacionFamilias FROM familiaArticulos WHERE codigoFamiliaArticulos LIKE ? OR denominacionFamilias LIKE ?";
        try (Connection conn = DataBaseConnected.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                familiasData.clear();
                while (rs.next()) {
                    String familia = rs.getString("codigoFamiliaArticulos") + " - " + rs.getString("denominacionFamilias");
                    familiasData.add(familia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al cargar las familias: " + e.getMessage());
        }
    }

    // Método para manejar la búsqueda
    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText().trim();
        loadFamilias(searchQuery);
    }

    // Método para manejar el botón de búsqueda
    @FXML
    private void handleSearchButton() {
        handleSearch();
    }

    // Método para limpiar la búsqueda
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadFamilias("");
    }
}