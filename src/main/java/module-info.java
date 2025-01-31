module com.example.proyectofacturacion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.proyectofacturacion to javafx.fxml;
    exports com.example.proyectofacturacion;
}