package com.example.proyectofacturacion;

import java.time.LocalDate;

public class Comercial {
    private int id;
    private String nombre;
    private String apellidos;
    private String nif;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private String puestoTrabajo;
    private String zonaActividad;
    private String historialVentas;
    private double comisiones;
    private double incentivos;
    private String objetivosAlcanzados;
    private LocalDate fechaContratacion;

    public Comercial(int id, String nombre, String apellidos, String nif, String direccion, String telefono,
                     String correoElectronico, String puestoTrabajo, String zonaActividad, String historialVentas,
                     double comisiones, double incentivos, String objetivosAlcanzados, LocalDate fechaContratacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nif = nif;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.puestoTrabajo = puestoTrabajo;
        this.zonaActividad = zonaActividad;
        this.historialVentas = historialVentas;
        this.comisiones = comisiones;
        this.incentivos = incentivos;
        this.objetivosAlcanzados = objetivosAlcanzados;
        this.fechaContratacion = fechaContratacion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getPuestoTrabajo() {
        return puestoTrabajo;
    }

    public void setPuestoTrabajo(String puestoTrabajo) {
        this.puestoTrabajo = puestoTrabajo;
    }

    public String getZonaActividad() {
        return zonaActividad;
    }

    public void setZonaActividad(String zonaActividad) {
        this.zonaActividad = zonaActividad;
    }

    public String getHistorialVentas() {
        return historialVentas;
    }

    public void setHistorialVentas(String historialVentas) {
        this.historialVentas = historialVentas;
    }

    public double getComisiones() {
        return comisiones;
    }

    public void setComisiones(double comisiones) {
        this.comisiones = comisiones;
    }

    public double getIncentivos() {
        return incentivos;
    }

    public void setIncentivos(double incentivos) {
        this.incentivos = incentivos;
    }

    public String getObjetivosAlcanzados() {
        return objetivosAlcanzados;
    }

    public void setObjetivosAlcanzados(String objetivosAlcanzados) {
        this.objetivosAlcanzados = objetivosAlcanzados;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    // Método para obtener el nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}