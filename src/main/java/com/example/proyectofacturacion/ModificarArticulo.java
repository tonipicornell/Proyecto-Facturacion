package com.example.proyectofacturacion;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private ComboBox<String> pais_iva;
    @FXML
    private ComboBox<String> tipo_iva_combobox;
    @FXML
    private ComboBox<String> familia_articulo;
    @FXML
    private ComboBox<String> proveedor_articulo;
    @FXML
    private TextArea observaciones_articulo;
    @FXML
    private Button boton_buscar_articulo;
    @FXML
    private Button boton_modificar_articulo;

    // Mapas para almacenar los IDs correspondientes a los nombres seleccionados en los ComboBox
    private Map<String, Integer> mapaFamilias = new HashMap<>();
    private Map<String, Integer> mapaProveedores = new HashMap<>();
    private Map<String, List<TipoIvaInfo>> mapaPaises = new HashMap<>();

    // Mapa inverso para obtener nombres a partir de IDs
    private Map<Integer, String> mapaFamiliasInverso = new HashMap<>();
    private Map<Integer, String> mapaProveedoresInverso = new HashMap<>();
    private Map<Integer, TipoIvaInfo> mapaTiposIvaInverso = new HashMap<>();

    // Clase interna para almacenar la información del IVA
    private static class TipoIvaInfo {
        private int id;
        private String pais;
        private String tipo;
        private double valor;

        public TipoIvaInfo(int id, String pais, String tipo, double valor) {
            this.id = id;
            this.pais = pais;
            this.tipo = tipo;
            this.valor = valor;
        }

        public int getId() { return id; }
        public String getPais() { return pais; }
        public String getTipo() { return tipo; }
        public double getValor() { return valor; }

        @Override
        public String toString() {
            return tipo + " (" + valor + "%)";
        }
    }

    @FXML
    private void initialize() {
        // Inicialmente ocultar todos los campos excepto código de artículo
        ocultarCampos(true);

        // Agregar validaciones numéricas
        agregarValidacionNumerica(coste_articulo, "Coste");
        agregarValidacionNumerica(margen_comercial_articulo, "Margen Comercial");
        agregarValidacionNumerica(stock_articulo, "Stock");

        // Cargar datos en los ComboBox
        cargarFamilias();
        cargarProveedores();
        cargarPaises();

        // Configurar acción del botón de búsqueda
        boton_buscar_articulo.setOnAction(event -> buscarArticuloPorCodigo());

        // Configurar acción del botón de modificación
        boton_modificar_articulo.setOnAction(event -> modificarArticulo());

        // Configurar cálculo automático del precio de venta basado en coste y margen
        configurarCalculoAutomatico();

        // Configurar el evento de selección del país
        pais_iva.setOnAction(event -> {
            String paisSeleccionado = pais_iva.getValue();
            if (paisSeleccionado != null) {
                cargarTiposIVAPorPais(paisSeleccionado);
            }
        });

        // Configurar el evento de selección del tipo de IVA
        tipo_iva_combobox.setOnAction(event -> {
            TipoIvaInfo tipoIvaSeleccionado = getTipoIvaSeleccionado();
            if (tipoIvaSeleccionado != null) {
                // Se podría usar aquí para mostrar información adicional si es necesario
                calcularPrecioVenta();
            }
        });
    }

    private void ocultarCampos(boolean ocultar) {
        // Lista de todos los campos que se deben ocultar/mostrar excepto el código y botón de búsqueda
        javafx.scene.Node[] campos = {
                codigo_barras_articulo,
                descripcion_articulo,
                coste_articulo,
                margen_comercial_articulo,
                precio_venta_articulo,
                stock_articulo,
                pais_iva,
                tipo_iva_combobox,
                familia_articulo,
                proveedor_articulo,
                observaciones_articulo,
                boton_modificar_articulo
        };

        for (javafx.scene.Node campo : campos) {
            campo.setVisible(!ocultar);
            campo.setManaged(!ocultar); // Esto evita que el espacio se reserve cuando está oculto
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
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Valor no válido");
                alert.setHeaderText(null);
                alert.setContentText("El campo '" + campo + "' debe ser un número válido.");
                alert.showAndWait();
                textField.setText(oldValue);
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
                    mapaFamiliasInverso.put(id, nombre);
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
                    mapaProveedoresInverso.put(id, nombre);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los proveedores: " + e.getMessage());
        }
    }

    private void cargarPaises() {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "SELECT DISTINCT pais FROM tiposIva ORDER BY pais";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String pais = resultSet.getString("pais");
                    pais_iva.getItems().add(pais);
                    // Precargar los tipos de IVA para cada país
                    cargarTiposIVAParaPais(pais);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los países: " + e.getMessage());
        }
    }

    private void cargarTiposIVAParaPais(String pais) {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "SELECT idTipoIva, pais, tipoIva, iva FROM tiposIva WHERE pais = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, pais);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<TipoIvaInfo> tiposIva = new ArrayList<>();
                    while (resultSet.next()) {
                        int id = resultSet.getInt("idTipoIva");
                        String tipo = resultSet.getString("tipoIva");
                        double valor = resultSet.getDouble("iva");
                        TipoIvaInfo tipoIvaInfo = new TipoIvaInfo(id, pais, tipo, valor);
                        tiposIva.add(tipoIvaInfo);
                        mapaTiposIvaInverso.put(id, tipoIvaInfo);
                    }
                    mapaPaises.put(pais, tiposIva);
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar los tipos de IVA para " + pais + ": " + e.getMessage());
        }
    }

    private void cargarTiposIVAPorPais(String pais) {
        tipo_iva_combobox.getItems().clear();
        if (mapaPaises.containsKey(pais)) {
            for (TipoIvaInfo tipoIva : mapaPaises.get(pais)) {
                tipo_iva_combobox.getItems().add(tipoIva.toString());
            }
        }
    }

    // Obtener el tipo de IVA seleccionado
    private TipoIvaInfo getTipoIvaSeleccionado() {
        String paisSeleccionado = pais_iva.getValue();
        String tipoIvaSeleccionado = tipo_iva_combobox.getValue();

        if (paisSeleccionado != null && tipoIvaSeleccionado != null && mapaPaises.containsKey(paisSeleccionado)) {
            List<TipoIvaInfo> tiposIva = mapaPaises.get(paisSeleccionado);
            for (TipoIvaInfo tipoIva : tiposIva) {
                if (tipoIva.toString().equals(tipoIvaSeleccionado)) {
                    return tipoIva;
                }
            }
        }
        return null;
    }

    @FXML
    private void buscarArticuloPorCodigo() {
        String codigo = codigo_articulo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Por favor, introduce un código de artículo.");
            return;
        }

        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "SELECT * FROM articulos WHERE codigoArticulo = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, codigo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Mostrar los campos primero
                        ocultarCampos(false);

                        // Llenar los campos con los datos del artículo
                        codigo_barras_articulo.setText(resultSet.getString("codigoBarrasArticulo"));
                        descripcion_articulo.setText(resultSet.getString("descripcionArticulo"));
                        coste_articulo.setText(String.format("%.2f", resultSet.getDouble("costeArticulo")));
                        margen_comercial_articulo.setText(String.format("%.2f", resultSet.getDouble("margenComercialArticulo")));
                        precio_venta_articulo.setText(String.format("%.2f", resultSet.getDouble("pvpArticulo")));
                        stock_articulo.setText(String.format("%.2f", resultSet.getDouble("stockArticulo")));
                        observaciones_articulo.setText(resultSet.getString("observacionesArticulo"));

                        // Seleccionar la familia del artículo
                        int familiaId = resultSet.getInt("familiaArticulo");
                        if (mapaFamiliasInverso.containsKey(familiaId)) {
                            familia_articulo.setValue(mapaFamiliasInverso.get(familiaId));
                        }

                        // Seleccionar el proveedor del artículo
                        int proveedorId = resultSet.getInt("proveedorArticulo");
                        if (mapaProveedoresInverso.containsKey(proveedorId)) {
                            proveedor_articulo.setValue(mapaProveedoresInverso.get(proveedorId));
                        }

                        // Seleccionar el tipo de IVA
                        int tipoIvaId = resultSet.getInt("tipoIva");
                        if (mapaTiposIvaInverso.containsKey(tipoIvaId)) {
                            TipoIvaInfo tipoIva = mapaTiposIvaInverso.get(tipoIvaId);
                            pais_iva.setValue(tipoIva.getPais());
                            // Cargar los tipos de IVA para el país seleccionado
                            cargarTiposIVAPorPais(tipoIva.getPais());
                            // Seleccionar el tipo de IVA específico
                            tipo_iva_combobox.setValue(tipoIva.toString());
                        }
                    } else {
                        mostrarError("No se encontró ningún artículo con el código " + codigo);
                        ocultarCampos(true);
                    }
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al buscar el artículo: " + e.getMessage());
        }
    }

    @FXML
    private void modificarArticulo() {
        if (validarCampos()) {
            try {
                String codigo = codigo_articulo.getText().trim();
                String codigoBarras = codigo_barras_articulo.getText().trim();
                String descripcion = descripcion_articulo.getText().trim();

                // Reemplazar comas por puntos para asegurar formato correcto en los números
                double coste = Double.parseDouble(coste_articulo.getText().replace(',', '.'));
                double margen = Double.parseDouble(margen_comercial_articulo.getText().replace(',', '.'));
                double pvp = Double.parseDouble(precio_venta_articulo.getText().replace(',', '.'));
                double stock = Double.parseDouble(stock_articulo.getText().replace(',', '.'));
                String observaciones = observaciones_articulo.getText().trim();

                // Obtener IDs de los ComboBox
                Integer familiaId = mapaFamilias.get(familia_articulo.getValue());
                Integer proveedorId = mapaProveedores.get(proveedor_articulo.getValue());

                // Obtener el tipo de IVA seleccionado
                TipoIvaInfo tipoIvaInfo = getTipoIvaSeleccionado();
                if (tipoIvaInfo == null) {
                    mostrarError("Seleccione un tipo de IVA válido.");
                    return;
                }

                int tipoIvaId = tipoIvaInfo.getId();
                double ivaValor = tipoIvaInfo.getValor();

                // Calcular precio con IVA
                double precioConIVA = pvp * (1 + (ivaValor / 100));

                // Actualizar el artículo en la base de datos
                actualizarArticulo(codigo, codigoBarras, descripcion, familiaId, coste, margen, pvp,
                        proveedorId, stock, observaciones, tipoIvaId, precioConIVA);
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
                pais_iva.getValue() == null ||
                tipo_iva_combobox.getValue() == null ||
                familia_articulo.getValue() == null ||
                proveedor_articulo.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo obligatorio");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, complete todos los campos obligatorios.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void actualizarArticulo(String codigo, String codigoBarras, String descripcion, Integer familiaId,
                                    double coste, double margen, double pvp, Integer proveedorId,
                                    double stock, String observaciones, Integer tipoIvaId, double precioConIVA) {
        try (Connection connection = DataBaseConnected.getConnection()) {
            String query = "UPDATE articulos SET codigoBarrasArticulo=?, descripcionArticulo=?, " +
                    "familiaArticulo=?, costeArticulo=?, margenComercialArticulo=?, pvpArticulo=?, " +
                    "proveedorArticulo=?, stockArticulo=?, observacionesArticulo=?, tipoIva=?, precioConIVA=? " +
                    "WHERE codigoArticulo=?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, codigoBarras);
                statement.setString(2, descripcion);
                statement.setInt(3, familiaId);
                statement.setDouble(4, coste);
                statement.setDouble(5, margen);
                statement.setDouble(6, pvp);
                statement.setInt(7, proveedorId);
                statement.setDouble(8, stock);
                statement.setString(9, observaciones);
                statement.setInt(10, tipoIvaId);
                statement.setDouble(11, precioConIVA);
                statement.setString(12, codigo);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Artículo Actualizado");
                    alert.setHeaderText(null);
                    alert.setContentText("El artículo ha sido actualizado exitosamente.");
                    alert.showAndWait();

                    // Ocultar los campos después de actualizar el artículo
                    ocultarCampos(true);
                    codigo_articulo.clear();
                } else {
                    mostrarError("No se pudo actualizar el artículo.");
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al actualizar el artículo: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}