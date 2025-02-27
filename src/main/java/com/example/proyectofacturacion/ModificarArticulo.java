package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ModificarArticulo {

    @FXML
    private AnchorPane rootPane;

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

    private Connection connection;

    // Mapas para almacenar los IDs correspondientes a los nombres seleccionados en los ComboBox
    private Map<String, Integer> mapaFamilias = new HashMap<>();
    private Map<String, Integer> mapaProveedores = new HashMap<>();
    private Map<String, Integer> mapaTiposIva = new HashMap<>();

    // Mapas inversos para buscar nombres por ID
    private Map<Integer, String> mapaFamiliasInverso = new HashMap<>();
    private Map<Integer, String> mapaProveedoresInverso = new HashMap<>();

    @FXML
    public void initialize() {
        try {
            connection = DataBaseConnected.getConnection();

            // Inicialmente ocultar todos los campos excepto el código del artículo
            ocultarCampos(true);

            // Cargar datos en los ComboBox
            cargarFamilias();
            cargarProveedores();
            cargarTiposIVA();

            // Configurar el evento Enter en el campo código artículo
            codigo_articulo.setOnAction(event -> buscarArticuloPorCodigo());

            // Agregar validaciones numéricas
            agregarValidacionNumerica(coste_articulo, "Coste");
            agregarValidacionNumerica(margen_comercial_articulo, "Margen Comercial");
            agregarValidacionNumerica(stock_articulo, "Stock");
            agregarValidacionNumerica(tipo_iva_articulo, "Tipo IVA");

            // Configurar cálculo automático del precio de venta
            configurarCalculoAutomatico();

            // Configurar el botón modificar
            boton_crear_articulo.setOnAction(event -> modificarArticulo());

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos.");
        }
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
                mostrarAlerta(Alert.AlertType.WARNING, "Valor no válido",
                        "El campo '" + campo + "' debe ser un número válido.");
                textField.setText(oldValue);
            } else if (campo.equals("Tipo IVA") && !newValue.isEmpty()) {
                try {
                    // Reemplaza comas por puntos para asegurar el formato correcto
                    double iva = Double.parseDouble(newValue.replace(',', '.'));
                    if (iva > 27) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Valor no válido",
                                "El Tipo IVA no puede superar el 27%.");
                        textField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    // Ignorar si el valor no es numérico
                }
            }
        });
    }

    private void ocultarCampos(boolean ocultar) {
        // Lista de todos los campos que se deben ocultar/mostrar
        javafx.scene.Node[] campos = {
                codigo_barras_articulo,
                descripcion_articulo,
                coste_articulo,
                margen_comercial_articulo,
                precio_venta_articulo,
                stock_articulo,
                tipo_iva_articulo,
                familia_articulo,
                proveedor_articulo,
                observaciones_articulo,
                boton_crear_articulo
        };

        for (javafx.scene.Node campo : campos) {
            campo.setVisible(!ocultar);
            campo.setManaged(!ocultar); // Esto evita que el espacio se reserve cuando está oculto
        }
    }

    private void cargarFamilias() {
        try {
            String query = "SELECT idFamiliaArticulos, denominacionFamilias FROM familiaArticulos ORDER BY denominacionFamilias";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idFamiliaArticulos");
                    String nombre = resultSet.getString("denominacionFamilias");
                    familia_articulo.getItems().add(nombre);
                    mapaFamilias.put(nombre, id);
                    mapaFamiliasInverso.put(id, nombre);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar las familias de artículos: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        try {
            String query = "SELECT idProveedor, nombreProveedor FROM proveedores ORDER BY nombreProveedor";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idProveedor");
                    String nombre = resultSet.getString("nombreProveedor");
                    proveedor_articulo.getItems().add(nombre);
                    mapaProveedores.put(nombre, id);
                    mapaProveedoresInverso.put(id, nombre);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los proveedores: " + e.getMessage());
        }
    }

    private void cargarTiposIVA() {
        try {
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los tipos de IVA: " + e.getMessage());
        }
    }

    @FXML
    private void buscarArticuloPorCodigo() {
        String codigo = codigo_articulo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código vacío", "Por favor, introduce un código de artículo.");
            return;
        }

        String query = "SELECT * FROM articulos WHERE codigoArticulo = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, codigo);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Mostrar los campos primero
                ocultarCampos(false);

                // Luego rellenar con los datos
                codigo_barras_articulo.setText(resultSet.getString("codigoBarrasArticulo"));
                descripcion_articulo.setText(resultSet.getString("descripcionArticulo"));
                coste_articulo.setText(String.format("%.2f", resultSet.getDouble("costeArticulo")));
                margen_comercial_articulo.setText(String.format("%.2f", resultSet.getDouble("margenComercialArticulo")));
                precio_venta_articulo.setText(String.format("%.2f", resultSet.getDouble("pvpArticulo")));
                stock_articulo.setText(String.format("%.2f", resultSet.getDouble("stockArticulo")));

                // Recuperar el valor del IVA desde la base de datos
                int tipoIvaId = resultSet.getInt("tipoIva");
                String queryIva = "SELECT iva FROM tiposIva WHERE idTipoIva = ?";
                try (PreparedStatement stmtIva = connection.prepareStatement(queryIva)) {
                    stmtIva.setInt(1, tipoIvaId);
                    ResultSet rsIva = stmtIva.executeQuery();
                    if (rsIva.next()) {
                        tipo_iva_articulo.setText(String.format("%.0f", rsIva.getDouble("iva")));
                    }
                }

                observaciones_articulo.setText(resultSet.getString("observacionesArticulo"));

                // Establecer los valores de los ComboBox
                int familiaId = resultSet.getInt("familiaArticulo");
                int proveedorId = resultSet.getInt("proveedorArticulo");

                familia_articulo.setValue(mapaFamiliasInverso.get(familiaId));
                proveedor_articulo.setValue(mapaProveedoresInverso.get(proveedorId));

            } else {
                ocultarCampos(true);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Artículo no encontrado",
                        "No se encontró un artículo con ese código.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la búsqueda",
                    "Ocurrió un error al buscar el artículo: " + e.getMessage());
        }
    }

    @FXML
    private void modificarArticulo() {
        try {
            // Validaciones básicas
            if (codigo_articulo.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error", "El código del artículo es obligatorio.");
                return;
            }

            if (validarCampos()) {
                String query = "UPDATE articulos SET codigoBarrasArticulo=?, descripcionArticulo=?, " +
                        "familiaArticulo=?, costeArticulo=?, margenComercialArticulo=?, pvpArticulo=?, " +
                        "proveedorArticulo=?, stockArticulo=?, observacionesArticulo=?, tipoIva=?, precioConIVA=? " +
                        "WHERE codigoArticulo=?";

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, codigo_barras_articulo.getText().trim());
                    stmt.setString(2, descripcion_articulo.getText().trim());

                    // Obtener IDs de los ComboBox
                    Integer familiaId = mapaFamilias.get(familia_articulo.getValue());
                    Integer proveedorId = mapaProveedores.get(proveedor_articulo.getValue());

                    stmt.setInt(3, familiaId);

                    // Convertir valores numéricos con manejo de errores
                    double coste = Double.parseDouble(coste_articulo.getText().trim().replace(',', '.'));
                    double margen = Double.parseDouble(margen_comercial_articulo.getText().trim().replace(',', '.'));
                    double pvp = Double.parseDouble(precio_venta_articulo.getText().trim().replace(',', '.'));
                    double stock = Double.parseDouble(stock_articulo.getText().trim().replace(',', '.'));
                    double ivaValor = Double.parseDouble(tipo_iva_articulo.getText().trim().replace(',', '.'));

                    // Calcular precio con IVA
                    double precioConIVA = pvp * (1 + (ivaValor / 100));

                    stmt.setDouble(4, coste);
                    stmt.setDouble(5, margen);
                    stmt.setDouble(6, pvp);
                    stmt.setInt(7, proveedorId);
                    stmt.setDouble(8, stock);
                    stmt.setString(9, observaciones_articulo.getText().trim());
                    stmt.setInt(10, (int)ivaValor);
                    stmt.setDouble(11, precioConIVA);
                    stmt.setString(12, codigo_articulo.getText().trim());

                    int filasActualizadas = stmt.executeUpdate();
                    if (filasActualizadas > 0) {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Artículo actualizado correctamente.");
                        ocultarCampos(true);
                        codigo_articulo.clear();
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el artículo.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la actualización",
                    "Ocurrió un error al modificar el artículo: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Por favor, asegúrese de que todos los campos numéricos contienen valores válidos.");
        }
    }

    private boolean validarCampos() {
        if (descripcion_articulo.getText().isEmpty() ||
                coste_articulo.getText().isEmpty() ||
                margen_comercial_articulo.getText().isEmpty() ||
                precio_venta_articulo.getText().isEmpty() ||
                stock_articulo.getText().isEmpty() ||
                tipo_iva_articulo.getText().isEmpty() ||
                familia_articulo.getValue() == null ||
                proveedor_articulo.getValue() == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Campo obligatorio",
                    "Por favor, complete todos los campos obligatorios.");
            return false;
        }

        // Validar el IVA
        try {
            double ivaValor = Double.parseDouble(tipo_iva_articulo.getText().replace(',', '.'));
            if (ivaValor < 0 || ivaValor > 27) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El valor de IVA debe estar entre 0 y 27.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El campo de IVA debe ser un número válido.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}