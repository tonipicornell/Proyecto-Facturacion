package com.example.proyectofacturacion;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class DatosEmpresaControlador implements Initializable {

    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtNif;
    @FXML private ComboBox<String> cmbTipoEmpresa;
    @FXML private TextField txtCif;
    @FXML private TextField txtDireccionSocial;
    @FXML private TextField txtActividadEconomica;
    @FXML private DatePicker dateFechaConstitucion;
    @FXML private TextField txtCapitalSocial;
    @FXML private TextField txtEstatutosSociales;
    @FXML private CheckBox chkLibroActas;
    @FXML private TextField txtNumeroTrabajadores;
    @FXML private TextField txtNumeroComerciales;
    @FXML private TextField txtNumeroDistribuidores;
    @FXML private TextField txtNumeroProveedores;
    @FXML private TextField txtNumeroClientes;
    @FXML private TextField txtConvenioColectivo;
    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private DatosEmpresa datosEmpresaOriginal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar los tipos de empresa en el ComboBox
        cmbTipoEmpresa.setItems(FXCollections.observableArrayList(
                "Empresario individual",
                "Sociedad Limitada (S.L.)",
                "Sociedad Anónima (S.A.)",
                "Asociaciones sin ánimo de lucro",
                "Sociedad Colectiva",
                "Sociedad Comanditaria",
                "Comunidad de Bienes",
                "Sociedad Cooperativa"
        ));

        // Cargar datos iniciales
        cargarDatosEmpresa();
    }

    /**
     * Carga los datos de la empresa desde la base de datos
     */
    private void cargarDatosEmpresa() {
        try (Connection conn = DataBaseConnected.getConnection()) {
            // Consulta principal para datos de la empresa
            String query = "SELECT * FROM empresa WHERE id_empresa = 1";
            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    // Cargar los datos básicos de la empresa
                    datosEmpresaOriginal = new DatosEmpresa();
                    datosEmpresaOriginal.setIdEmpresa(rs.getInt("id_empresa"));
                    datosEmpresaOriginal.setRazonSocial(rs.getString("razon_social"));
                    datosEmpresaOriginal.setNif(rs.getString("nif"));
                    datosEmpresaOriginal.setTipoEmpresa(rs.getString("tipo_empresa"));
                    datosEmpresaOriginal.setCif(rs.getString("cif"));
                    datosEmpresaOriginal.setDireccionSocial(rs.getString("direccion_social"));
                    datosEmpresaOriginal.setActividadEconomica(rs.getString("actividad_economica"));

                    // Convertir java.sql.Date a LocalDate
                    Date fechaSQL = rs.getDate("fecha_constitucion");
                    if (fechaSQL != null) {
                        datosEmpresaOriginal.setFechaConstitucion(fechaSQL.toLocalDate());
                    }

                    datosEmpresaOriginal.setCapitalSocial(rs.getDouble("capital_social"));
                    datosEmpresaOriginal.setEstatutosSociales(rs.getString("estatutos_sociales"));
                    datosEmpresaOriginal.setLibroActas(rs.getBoolean("libro_actas"));

                    // Para estos campos, verificaremos primero si están en la base de datos
                    // o si debemos contarlos manualmente
                    int numTrabajadores = rs.getInt("numero_trabajadores");
                    int numComerciales = rs.getInt("numero_comerciales");
                    int numDistribuidores = rs.getInt("numero_distribuidores");
                    int numProveedores = rs.getInt("numero_proveedores");
                    int numClientes = rs.getInt("numero_clientes");

                    // Si los valores son 0, consultaremos las tablas relacionadas
                    if (numTrabajadores == 0) {
                        numTrabajadores = contarRegistros(conn, "trabajadores");
                    }
                    if (numComerciales == 0) {
                        numComerciales = contarRegistros(conn, "comerciales");
                    }
                    if (numDistribuidores == 0) {
                        numDistribuidores = contarRegistros(conn, "distribuidores");
                    }
                    if (numProveedores == 0) {
                        numProveedores = contarRegistros(conn, "proveedores");
                    }
                    if (numClientes == 0) {
                        numClientes = contarRegistros(conn, "clientes");
                    }

                    datosEmpresaOriginal.setNumeroTrabajadores(numTrabajadores);
                    datosEmpresaOriginal.setNumeroComerciales(numComerciales);
                    datosEmpresaOriginal.setNumeroDistribuidores(numDistribuidores);
                    datosEmpresaOriginal.setNumeroProveedores(numProveedores);
                    datosEmpresaOriginal.setNumeroClientes(numClientes);
                    datosEmpresaOriginal.setConvenioColectivo(rs.getString("convenio_colectivo"));

                    // Mostrar datos en la interfaz
                    mostrarDatosEmpresa(datosEmpresaOriginal);
                } else {
                    // Si no existe el registro, creamos uno nuevo
                    mostrarAlerta("No existe registro de empresa",
                            "No se encontró ningún registro de empresa. Se creará uno nuevo.",
                            AlertType.INFORMATION);
                    datosEmpresaOriginal = new DatosEmpresa();
                    datosEmpresaOriginal.setIdEmpresa(1);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de base de datos",
                    "Error al cargar datos de la empresa: " + e.getMessage(),
                    AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Cuenta el número de registros en una tabla
     */
    private int contarRegistros(Connection conn, String tabla) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tabla;
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Muestra los datos de la empresa en la interfaz
     */
    private void mostrarDatosEmpresa(DatosEmpresa datos) {
        txtRazonSocial.setText(datos.getRazonSocial());
        txtNif.setText(datos.getNif());
        cmbTipoEmpresa.setValue(datos.getTipoEmpresa());
        txtCif.setText(datos.getCif());
        txtDireccionSocial.setText(datos.getDireccionSocial());
        txtActividadEconomica.setText(datos.getActividadEconomica());
        dateFechaConstitucion.setValue(datos.getFechaConstitucion());
        txtCapitalSocial.setText(datos.getCapitalSocial() > 0 ? String.valueOf(datos.getCapitalSocial()) : "");
        txtEstatutosSociales.setText(datos.getEstatutosSociales());
        chkLibroActas.setSelected(datos.isLibroActas());
        txtNumeroTrabajadores.setText(String.valueOf(datos.getNumeroTrabajadores()));
        txtNumeroComerciales.setText(String.valueOf(datos.getNumeroComerciales()));
        txtNumeroDistribuidores.setText(String.valueOf(datos.getNumeroDistribuidores()));
        txtNumeroProveedores.setText(String.valueOf(datos.getNumeroProveedores()));
        txtNumeroClientes.setText(String.valueOf(datos.getNumeroClientes()));
        txtConvenioColectivo.setText(datos.getConvenioColectivo());
    }

    /**
     * Maneja el evento del botón Editar
     */
    @FXML
    void handleEditar(ActionEvent event) {
        // Mostrar confirmación antes de habilitar la edición
        boolean confirmar = mostrarConfirmacion("Confirmación",
                "¿Está seguro que desea editar los datos de la empresa?");

        if (confirmar) {
            // Habilitar la edición de campos
            setEdicionHabilitada(true);

            // Cambiar visibilidad de botones
            btnEditar.setVisible(false);
            btnGuardar.setVisible(true);
            btnCancelar.setVisible(true);
        }
    }

    /**
     * Maneja el evento del botón Guardar
     */
    @FXML
    void handleGuardar(ActionEvent event) {
        // Validar campos obligatorios
        if (txtRazonSocial.getText().trim().isEmpty() || txtNif.getText().trim().isEmpty()) {
            mostrarAlerta("Datos incompletos",
                    "Los campos Razón Social y NIF son obligatorios.",
                    AlertType.WARNING);
            return;
        }

        // Obtener datos del formulario
        DatosEmpresa datosActualizados = obtenerDatosFormulario();

        // Guardar en la base de datos
        if (guardarDatosEmpresa(datosActualizados)) {
            // Actualizar el objeto original
            datosEmpresaOriginal = datosActualizados;

            // Deshabilitar edición
            setEdicionHabilitada(false);

            // Cambiar visibilidad de botones
            btnEditar.setVisible(true);
            btnGuardar.setVisible(false);
            btnCancelar.setVisible(false);

            mostrarAlerta("Datos guardados",
                    "Los datos de la empresa se han guardado correctamente.",
                    AlertType.INFORMATION);
        }
    }

    /**
     * Maneja el evento del botón Cancelar
     */
    @FXML
    void handleCancelar(ActionEvent event) {
        // Restaurar datos originales
        mostrarDatosEmpresa(datosEmpresaOriginal);

        // Deshabilitar edición
        setEdicionHabilitada(false);

        // Cambiar visibilidad de botones
        btnEditar.setVisible(true);
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);
    }

    /**
     * Obtiene los datos del formulario
     */
    private DatosEmpresa obtenerDatosFormulario() {
        DatosEmpresa datos = new DatosEmpresa();
        datos.setIdEmpresa(1); // Siempre es 1 según la restricción de la tabla
        datos.setRazonSocial(txtRazonSocial.getText().trim());
        datos.setNif(txtNif.getText().trim());
        datos.setTipoEmpresa(cmbTipoEmpresa.getValue());
        datos.setCif(txtCif.getText().trim());
        datos.setDireccionSocial(txtDireccionSocial.getText().trim());
        datos.setActividadEconomica(txtActividadEconomica.getText().trim());
        datos.setFechaConstitucion(dateFechaConstitucion.getValue());

        try {
            if (!txtCapitalSocial.getText().trim().isEmpty()) {
                datos.setCapitalSocial(Double.parseDouble(txtCapitalSocial.getText().trim()));
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato",
                    "El capital social debe ser un número válido.",
                    AlertType.WARNING);
        }

        datos.setEstatutosSociales(txtEstatutosSociales.getText().trim());
        datos.setLibroActas(chkLibroActas.isSelected());

        // Los números se mantienen como los que vienen de la base de datos
        datos.setNumeroTrabajadores(datosEmpresaOriginal.getNumeroTrabajadores());
        datos.setNumeroComerciales(datosEmpresaOriginal.getNumeroComerciales());
        datos.setNumeroDistribuidores(datosEmpresaOriginal.getNumeroDistribuidores());
        datos.setNumeroProveedores(datosEmpresaOriginal.getNumeroProveedores());
        datos.setNumeroClientes(datosEmpresaOriginal.getNumeroClientes());

        datos.setConvenioColectivo(txtConvenioColectivo.getText().trim());

        return datos;
    }

    /**
     * Guarda los datos de la empresa en la base de datos
     */
    private boolean guardarDatosEmpresa(DatosEmpresa datos) {
        try (Connection conn = DataBaseConnected.getConnection()) {
            // Primero verificamos si existe el registro
            String checkQuery = "SELECT id_empresa FROM empresa WHERE id_empresa = 1";
            boolean registroExiste = false;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                 ResultSet rs = checkStmt.executeQuery()) {
                registroExiste = rs.next();
            }

            String query;
            if (registroExiste) {
                // UPDATE
                query = "UPDATE empresa SET razon_social=?, nif=?, tipo_empresa=?, cif=?, " +
                        "direccion_social=?, actividad_economica=?, fecha_constitucion=?, " +
                        "capital_social=?, estatutos_sociales=?, libro_actas=?, " +
                        "numero_trabajadores=?, numero_comerciales=?, numero_distribuidores=?, " +
                        "numero_proveedores=?, numero_clientes=?, convenio_colectivo=? " +
                        "WHERE id_empresa=1";
            } else {
                // INSERT
                query = "INSERT INTO empresa (razon_social, nif, tipo_empresa, cif, " +
                        "direccion_social, actividad_economica, fecha_constitucion, " +
                        "capital_social, estatutos_sociales, libro_actas, " +
                        "numero_trabajadores, numero_comerciales, numero_distribuidores, " +
                        "numero_proveedores, numero_clientes, convenio_colectivo, id_empresa) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, datos.getRazonSocial());
                pstmt.setString(2, datos.getNif());
                pstmt.setString(3, datos.getTipoEmpresa());
                pstmt.setString(4, datos.getCif());
                pstmt.setString(5, datos.getDireccionSocial());
                pstmt.setString(6, datos.getActividadEconomica());

                // Convertir LocalDate a java.sql.Date
                if (datos.getFechaConstitucion() != null) {
                    pstmt.setDate(7, Date.valueOf(datos.getFechaConstitucion()));
                } else {
                    pstmt.setNull(7, Types.DATE);
                }

                pstmt.setDouble(8, datos.getCapitalSocial());
                pstmt.setString(9, datos.getEstatutosSociales());
                pstmt.setBoolean(10, datos.isLibroActas());
                pstmt.setInt(11, datos.getNumeroTrabajadores());
                pstmt.setInt(12, datos.getNumeroComerciales());
                pstmt.setInt(13, datos.getNumeroDistribuidores());
                pstmt.setInt(14, datos.getNumeroProveedores());
                pstmt.setInt(15, datos.getNumeroClientes());
                pstmt.setString(16, datos.getConvenioColectivo());

                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al guardar",
                    "Error al guardar los datos de la empresa: " + e.getMessage(),
                    AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Habilita o deshabilita la edición de campos
     */
    private void setEdicionHabilitada(boolean editable) {
        txtRazonSocial.setEditable(editable);
        txtNif.setEditable(editable);
        cmbTipoEmpresa.setDisable(!editable);
        txtCif.setEditable(editable);
        txtDireccionSocial.setEditable(editable);
        txtActividadEconomica.setEditable(editable);
        dateFechaConstitucion.setDisable(!editable);
        txtCapitalSocial.setEditable(editable);
        txtEstatutosSociales.setEditable(editable);
        chkLibroActas.setDisable(!editable);
        // Los campos de números no son editables aquí
        txtNumeroTrabajadores.setEditable(false);
        txtNumeroComerciales.setEditable(false);
        txtNumeroDistribuidores.setEditable(false);
        txtNumeroProveedores.setEditable(false);
        txtNumeroClientes.setEditable(false);
        txtConvenioColectivo.setEditable(editable);
    }

    /**
     * Muestra un diálogo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación
     */
    private boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnSi, btnNo);

        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(btnNo) == btnSi;
    }
}