package com.example.proyectofacturacion;

import javafx.event.ActionEvent;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ManualUsuario {

    /**
     * Maneja la acción de imprimir el manual de usuario.
     * Muestra un diálogo de impresión del sistema.
     *
     * @param event El evento de acción del botón
     */
    public void imprimirManual(ActionEvent event) {
        try {
            // Obtener el nodo padre (posiblemente BorderPane) desde la fuente del evento
            Node source = (Node) event.getSource();
            Node root = source.getScene().getRoot();

            // Crear un trabajo de impresión
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                // Mostrar diálogo de configuración de impresión
                boolean showDialog = job.showPrintDialog(source.getScene().getWindow());

                if (showDialog) {
                    // Obtener el diseño de página
                    PageLayout pageLayout = job.getJobSettings().getPageLayout();

                    // Escalar si es necesario
                    double scaleX = pageLayout.getPrintableWidth() / root.getBoundsInParent().getWidth();
                    double scaleY = pageLayout.getPrintableHeight() / root.getBoundsInParent().getHeight();
                    double scale = Math.min(scaleX, scaleY);

                    // Imprimir
                    boolean success = job.printPage(root);
                    if (success) {
                        job.endJob();
                        mostrarAlerta("Impresión iniciada", "El manual de usuario se ha enviado a la impresora.", Alert.AlertType.INFORMATION);
                    } else {
                        job.cancelJob();
                        mostrarAlerta("Error de impresión", "No se pudo imprimir el manual de usuario.", Alert.AlertType.ERROR);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al imprimir: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Maneja la acción de descargar el manual de usuario como PDF.
     * Permite al usuario seleccionar la ubicación para guardar el archivo PDF.
     *
     * @param event El evento de acción del botón
     */
    public void descargarPDF(ActionEvent event) {
        try {
            // Configurar el selector de archivos
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Manual de Usuario");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            fileChooser.setInitialFileName("Manual_de_Usuario_Facturation_Toni.pdf");

            // Mostrar el diálogo de guardar
            Node source = (Node) event.getSource();
            File file = fileChooser.showSaveDialog(source.getScene().getWindow());

            if (file != null) {
                // Obtener la ruta al PDF incluido en los recursos
                URL pdfResource = getClass().getResource("/com/example/proyectofacturacion/Manual de usuario.pdf");

                if (pdfResource != null) {
                    try (InputStream inputStream = pdfResource.openStream();
                         FileOutputStream outputStream = new FileOutputStream(file)) {

                        // Copiar el contenido del archivo
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        mostrarAlerta("Descarga completada",
                                "El manual de usuario se ha guardado correctamente en:\n" + file.getAbsolutePath(),
                                Alert.AlertType.INFORMATION);
                    }
                } else {
                    // Intentar una alternativa si el recurso no se encuentra
                    generarPDFAlternativo(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al descargar el PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Método alternativo para generar el PDF si no se encuentra el recurso incluido.
     * Podría implementarse con librerías como iText, Apache PDFBox, etc.
     */
    private void generarPDFAlternativo(File file) {
        try {
            // Esta es una implementación de respaldo simple que copia un archivo temporal
            // En una implementación completa, usarías una librería para generar el PDF programáticamente

            // Crear contenido temporal
            Path tempFile = Files.createTempFile("manual_usuario_", ".pdf");

            // Escribir contenido básico (en una implementación real, usarías una librería PDF)
            String mensaje = "Este es un PDF generado automáticamente. " +
                    "El manual de usuario completo no se pudo encontrar en los recursos del sistema.";
            Files.writeString(tempFile, mensaje);

            // Copiar al destino final
            Files.copy(tempFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            mostrarAlerta("Descarga completada (versión alternativa)",
                    "Se ha generado una versión básica del manual de usuario en:\n" + file.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo generar el PDF alternativo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra una alerta con el mensaje y tipo especificados.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}