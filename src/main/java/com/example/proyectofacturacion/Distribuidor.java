package com.example.proyectofacturacion;

import java.time.LocalDate;

public class Distribuidor {
    private int id;
    private String nombreEmpresa;
    private String direccionFisica;
    private String telefono;
    private String correoElectronico;
    private String paginaWeb;
    private String condicionesPago;
    private double precioCompra;
    private double margenGanancia;
    private int volumenCompra;
    private String condicionesEntrega;
    private LocalDate contratoInicio;
    private LocalDate contratoTermino;
    private String territorioAsignado;
    private String metodosEnvio;
    private boolean soportePostventa;

    public Distribuidor(int id, String nombreEmpresa, String direccionFisica, String telefono,
                        String correoElectronico, String paginaWeb, String condicionesPago,
                        double precioCompra, double margenGanancia, int volumenCompra,
                        String condicionesEntrega, LocalDate contratoInicio, LocalDate contratoTermino,
                        String territorioAsignado, String metodosEnvio, boolean soportePostventa) {
        this.id = id;
        this.nombreEmpresa = nombreEmpresa;
        this.direccionFisica = direccionFisica;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.paginaWeb = paginaWeb;
        this.condicionesPago = condicionesPago;
        this.precioCompra = precioCompra;
        this.margenGanancia = margenGanancia;
        this.volumenCompra = volumenCompra;
        this.condicionesEntrega = condicionesEntrega;
        this.contratoInicio = contratoInicio;
        this.contratoTermino = contratoTermino;
        this.territorioAsignado = territorioAsignado;
        this.metodosEnvio = metodosEnvio;
        this.soportePostventa = soportePostventa;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDireccionFisica() {
        return direccionFisica;
    }

    public void setDireccionFisica(String direccionFisica) {
        this.direccionFisica = direccionFisica;
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

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public String getCondicionesPago() {
        return condicionesPago;
    }

    public void setCondicionesPago(String condicionesPago) {
        this.condicionesPago = condicionesPago;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getMargenGanancia() {
        return margenGanancia;
    }

    public void setMargenGanancia(double margenGanancia) {
        this.margenGanancia = margenGanancia;
    }

    public int getVolumenCompra() {
        return volumenCompra;
    }

    public void setVolumenCompra(int volumenCompra) {
        this.volumenCompra = volumenCompra;
    }

    public String getCondicionesEntrega() {
        return condicionesEntrega;
    }

    public void setCondicionesEntrega(String condicionesEntrega) {
        this.condicionesEntrega = condicionesEntrega;
    }

    public LocalDate getContratoInicio() {
        return contratoInicio;
    }

    public void setContratoInicio(LocalDate contratoInicio) {
        this.contratoInicio = contratoInicio;
    }

    public LocalDate getContratoTermino() {
        return contratoTermino;
    }

    public void setContratoTermino(LocalDate contratoTermino) {
        this.contratoTermino = contratoTermino;
    }

    public String getTerritorioAsignado() {
        return territorioAsignado;
    }

    public void setTerritorioAsignado(String territorioAsignado) {
        this.territorioAsignado = territorioAsignado;
    }

    public String getMetodosEnvio() {
        return metodosEnvio;
    }

    public void setMetodosEnvio(String metodosEnvio) {
        this.metodosEnvio = metodosEnvio;
    }

    public boolean isSoportePostventa() {
        return soportePostventa;
    }

    public void setSoportePostventa(boolean soportePostventa) {
        this.soportePostventa = soportePostventa;
    }

    // Método para obtener el texto de soporte postventa (Sí/No)
    public String getSoportePostventaTexto() {
        return soportePostventa ? "Sí" : "No";
    }
}