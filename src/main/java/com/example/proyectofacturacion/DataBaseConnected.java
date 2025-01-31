package com.example.proyectofacturacion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnected {

    private static final String URL = "jdbc:mysql://localhost:3306/gestion";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "123456789";

    // Método para establecer la conexión
    public static Connection getConnection() throws SQLException {
        try  {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el driver: " + e.getMessage());
            throw new SQLException("No se ha podido cargar el driver del MySQL");
        }
    }
}
