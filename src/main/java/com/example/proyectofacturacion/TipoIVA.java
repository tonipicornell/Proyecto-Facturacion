package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoIVA {

    @FXML
    private ComboBox<String> elegir_pais;
    @FXML
    private Label iva_general;
    @FXML
    private Label iva_reducido;
    @FXML
    private Label iva_superreducido;

    // Método para llenar el ComboBox con los países
    public void initialize() {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String sql = "SELECT DISTINCT pais FROM tiposIva";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<String> paises = new ArrayList<>();
            while (resultSet.next()) {
                paises.add(resultSet.getString("pais"));
            }
            elegir_pais.getItems().addAll(paises);

            // Si se selecciona un país, actualiza los IVA en los Labels
            elegir_pais.setOnAction(event -> updateIVA());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar los valores del IVA según el país seleccionado
    private void updateIVA() {
        String paisSeleccionado = elegir_pais.getValue();
        if (paisSeleccionado != null) {
            try (Connection connection = DataBaseConnected.getConnection()) {
                String sql = "SELECT tipoIva, iva FROM tiposIva WHERE pais = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, paisSeleccionado);
                ResultSet resultSet = statement.executeQuery();

                // Restablecer los valores de IVA a 0%
                iva_general.setText("0 %");
                iva_reducido.setText("0 %");
                iva_superreducido.setText("0 %");

                // Iterar sobre los resultados y actualizar los Labels
                while (resultSet.next()) {
                    String tipoIva = resultSet.getString("tipoIva");
                    double iva = resultSet.getDouble("iva");
                    switch (tipoIva) {
                        case "General":
                            iva_general.setText(iva + " %");
                            break;
                        case "Reducido":
                            iva_reducido.setText(iva + " %");
                            break;
                        case "Superreducido":
                            iva_superreducido.setText(iva + " %");
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
