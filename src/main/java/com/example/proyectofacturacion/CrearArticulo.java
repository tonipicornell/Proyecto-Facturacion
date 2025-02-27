package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CrearArticulo {

    @FXML
    private TextField codigo_articulo;
    @FXML
    private TextField codigo_barras_articulo;
    @FXML
    private TextField descripcion_articulo;
    @FXML
    private TextField coste_articulo;
    @FXML
    private TextField margen_comercial_articulo;
    @FXML
    private TextField precio_venta_articulo;
    @FXML
    private TextField stock_articulo;
    @FXML
    private TextField tipo_iva_articulo;
    @FXML
    private ComboBox<String> familia_articulo;
    @FXML
    private ComboBox<String> proveedor_articulo;
    @FXML
    private TextArea observaciones_articulo;
    @FXML
    private Button boton_crear_articulo;

    // Mapas para almacenar los IDs correspondientes a los nombres seleccionados en los ComboBox
    private Map<String, Integer> mapaFamilias = new HashMap<>();
    private Map<String, Integer> mapaProveedores = new HashMap<>();
    private Map<String, Integer> mapaTiposIva = new HashMap<>();

    @FXML
    private void initialize() {
        // Agregar validaciones numéricas (eliminada la del precio de venta)
        agregarValidacionNumerica(coste_articulo, "Coste");
        agregarValidacionNumerica(margen_comercial_articulo, "Margen Comercial");
        agregarValidacionNumerica(stock_articulo, "Stock");
        agregarValidacionNumerica(tipo_iva_articulo, "Tipo IVA");

        // Cargar datos en los ComboBox
        cargarFamilias();
        cargarProveedores();
        cargarTiposIVA();

        // Configurar acción del botón
        boton_crear_articulo.setOnAction(event -> crearArticulo());

        // Configurar cálculo automático del precio de venta basado en coste y margen
        configurarCalculoAutomatico();
    }

    private void configurarCalculoAutomatico() {
        // Calcular precio de venta cuando cambia el coste o el margen
        coste_articulo.textProperty().addListener((observable, oldValue, newValue) -> {
            calcularPrecioVenta();
        });

        margen_comercial_articulo.textProperty().addListener((observable, oldValue, newValue) -> {
            calcularPrecioVenta();
        });
    }

    private void calcularPrecioVenta() {
        try {
            if (!coste_articulo.getText().isEmpty() && !margen_comercial_articulo.getText().isEmpty()) {
                double coste = Double.parseDouble(coste_articulo.getText().replace(',', '.'));
                double margen = Double.parseDouble(margen_comercial_articulo.getText().replace(',', '.'));
                double precioVenta = coste * (1 + margen / 100);
                precio_venta_articulo.setText(String.format("%.2f", precioVenta));
            }
        } catch (NumberFormatException e) {
            // Ignorar si los valores no son numéricos
        }
    }

    private void agregarValidacionNumerica(TextField textField, String campo) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Patrón mejorado que acepta números enteros, números con punto decimal y números con coma decimal
            if (!newValue.isEmpty() && !newValue.matches("\\d*([.,]\\d*)?")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Valor no válido");
                alert.setHeaderText(null);
                alert.setContentText("El campo '" + campo + "' debe ser un número válido.");
                alert.showAndWait();
                textField.setText(oldValue);
            } else if (campo.equals("Tipo IVA") && !newValue.isEmpty()) {
                try {
                    // Reemplaza comas por puntos para asegurar el formato correcto para Double.parseDouble
                    double iva = Double.parseDouble(newValue.replace(',', '.'));
                    if (iva > 27) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Valor no válido");
                        alert.setHeaderText(null);
                        alert.setContentText("El Tipo IVA no puede superar el 27%.");
                        alert.showAndWait();
                        textField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    // Ignorar si el valor no es numérico
                }
            }
        });
    }

    private void cargarFamilias() {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "SELECT idFamiliaArticulos, denominacionFamilias FROM familiaArticulos ORDER BY denominacionFamilias";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idFamiliaArticulos");
                    String nombre = resultSet.getString("denominacionFamilias");
                    familia_articulo.getItems().add(nombre);
                    mapaFamilias.put(nombre, id);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar las familias de artículos: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "SELECT idProveedor, nombreProveedor FROM proveedores ORDER BY nombreProveedor";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idProveedor");
                    String nombre = resultSet.getString("nombreProveedor");
                    proveedor_articulo.getItems().add(nombre);
                    mapaProveedores.put(nombre, id);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los proveedores: " + e.getMessage());
        }
    }

    private void cargarTiposIVA() {
        try (Connection connection = DataBaseConnected.getConnection()) {
            // Cargar los tipos de IVA de España como predeterminado
            String query = "SELECT idTipoIva, tipoIva, iva FROM tiposIva WHERE pais = 'España'";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idTipoIva");
                    double iva = resultSet.getDouble("iva");
                    String tipoIva = resultSet.getString("tipoIva");
                    mapaTiposIva.put(String.valueOf(iva), id);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los tipos de IVA: " + e.getMessage());
        }
    }

    @FXML
    private void crearArticulo() {
        if (validarCampos()) {
            try {
                String codigo = codigo_articulo.getText();
                String codigoBarras = codigo_barras_articulo.getText();
                String descripcion = descripcion_articulo.getText();
                // Reemplazar comas por puntos para asegurar formato correcto en los números
                double coste = Double.parseDouble(coste_articulo.getText().replace(',', '.'));
                double margen = Double.parseDouble(margen_comercial_articulo.getText().replace(',', '.'));
                double pvp = Double.parseDouble(precio_venta_articulo.getText().replace(',', '.'));
                double stock = Double.parseDouble(stock_articulo.getText().replace(',', '.'));
                double ivaValor = Double.parseDouble(tipo_iva_articulo.getText().replace(',', '.'));
                String observaciones = observaciones_articulo.getText();

                // Validar el IVA
                if (ivaValor < 0 || ivaValor > 27) {
                    mostrarError("El valor de IVA debe estar entre 0 y 27.");
                    return;
                }

                // Obtener IDs de los ComboBox
                Integer familiaId = mapaFamilias.get(familia_articulo.getValue());
                Integer proveedorId = mapaProveedores.get(proveedor_articulo.getValue());

                // Calcular precio con IVA
                double precioConIVA = pvp * (1 + (ivaValor / 100));

                // Almacenar el artículo en la base de datos
                guardarArticulo(codigo, codigoBarras, descripcion, familiaId, coste, margen, pvp,
                        proveedorId, stock, observaciones, (int) ivaValor, precioConIVA);
            } catch (NumberFormatException e) {
                mostrarError("Por favor, asegúrese de que todos los campos numéricos contienen valores válidos.");
            }
        }
    }

    private boolean validarCampos() {
        if (codigo_articulo.getText().isEmpty() ||
                descripcion_articulo.getText().isEmpty() ||
                coste_articulo.getText().isEmpty() ||
                margen_comercial_articulo.getText().isEmpty() ||
                precio_venta_articulo.getText().isEmpty() ||
                stock_articulo.getText().isEmpty() ||
                tipo_iva_articulo.getText().isEmpty() ||
                familia_articulo.getValue() == null ||
                proveedor_articulo.getValue() == null) {

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Campo obligatorio");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, complete todos los campos obligatorios.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void guardarArticulo(String codigo, String codigoBarras, String descripcion, Integer familiaId,
                                 double coste, double margen, double pvp, Integer proveedorId,
                                 double stock, String observaciones, Integer tipoIvaId, double precioConIVA) {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "INSERT INTO articulos (codigoArticulo, codigoBarrasArticulo, descripcionArticulo, " +
                    "familiaArticulo, costeArticulo, margenComercialArticulo, pvpArticulo, " +
                    "proveedorArticulo, stockArticulo, observacionesArticulo, tipoIva, precioConIVA) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, codigo);
                statement.setString(2, codigoBarras);
                statement.setString(3, descripcion);
                statement.setInt(4, familiaId);
                statement.setDouble(5, coste);
                statement.setDouble(6, margen);
                statement.setDouble(7, pvp);
                statement.setInt(8, proveedorId);
                statement.setDouble(9, stock);
                statement.setString(10, observaciones);
                statement.setInt(11, tipoIvaId);
                statement.setDouble(12, precioConIVA);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Artículo Creado");
                    alert.setHeaderText(null);
                    alert.setContentText("El artículo ha sido creado exitosamente.");
                    alert.showAndWait();

                    // Limpiar los campos después de crear el artículo
                    limpiarCampos();
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al guardar el artículo: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        codigo_articulo.clear();
        codigo_barras_articulo.clear();
        descripcion_articulo.clear();
        coste_articulo.clear();
        margen_comercial_articulo.clear();
        precio_venta_articulo.clear();
        stock_articulo.clear();
        tipo_iva_articulo.clear();
        familia_articulo.setValue(null);
        proveedor_articulo.setValue(null);
        observaciones_articulo.clear();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}