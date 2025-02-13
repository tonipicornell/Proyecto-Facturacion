package com.example.proyectofacturacion;

public class Cliente {
    private String nombreCliente;
    private String direccionCliente;
    private String cpCliente;
    private String poblacionCliente;
    private String provinciaCliente;
    private String paisCliente;
    private String cifCliente;
    private String telCliente;
    private String emailCliente;
    private String ibanCliente;
    private double riesgoCliente;
    private double descuentoCliente;
    private String observacionesCliente;

    public Cliente(String nombreCliente, String direccionCliente, String cpCliente,
                   String poblacionCliente, String provinciaCliente, String paisCliente,
                   String cifCliente, String telCliente, String emailCliente,
                   String ibanCliente, double riesgoCliente, double descuentoCliente,
                   String observacionesCliente) {
        this.nombreCliente = nombreCliente;
        this.direccionCliente = direccionCliente;
        this.cpCliente = cpCliente;
        this.poblacionCliente = poblacionCliente;
        this.provinciaCliente = provinciaCliente;
        this.paisCliente = paisCliente;
        this.cifCliente = cifCliente;
        this.telCliente = telCliente;
        this.emailCliente = emailCliente;
        this.ibanCliente = ibanCliente;
        this.riesgoCliente = riesgoCliente;
        this.descuentoCliente = descuentoCliente;
        this.observacionesCliente = observacionesCliente;
    }

    // Getters
    public String getNombreCliente() { return nombreCliente; }
    public String getDireccionCliente() { return direccionCliente; }
    public String getCpCliente() { return cpCliente; }
    public String getPoblacionCliente() { return poblacionCliente; }
    public String getProvinciaCliente() { return provinciaCliente; }
    public String getPaisCliente() { return paisCliente; }
    public String getCifCliente() { return cifCliente; }
    public String getTelCliente() { return telCliente; }
    public String getEmailCliente() { return emailCliente; }
    public String getIbanCliente() { return ibanCliente; }
    public double getRiesgoCliente() { return riesgoCliente; }
    public double getDescuentoCliente() { return descuentoCliente; }
    public String getObservacionesCliente() { return observacionesCliente; }

    // Setters
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }
    public void setCpCliente(String cpCliente) { this.cpCliente = cpCliente; }
    public void setPoblacionCliente(String poblacionCliente) { this.poblacionCliente = poblacionCliente; }
    public void setProvinciaCliente(String provinciaCliente) { this.provinciaCliente = provinciaCliente; }
    public void setPaisCliente(String paisCliente) { this.paisCliente = paisCliente; }
    public void setCifCliente(String cifCliente) { this.cifCliente = cifCliente; }
    public void setTelCliente(String telCliente) { this.telCliente = telCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public void setIbanCliente(String ibanCliente) { this.ibanCliente = ibanCliente; }
    public void setRiesgoCliente(double riesgoCliente) { this.riesgoCliente = riesgoCliente; }
    public void setDescuentoCliente(double descuentoCliente) { this.descuentoCliente = descuentoCliente; }
    public void setObservacionesCliente(String observacionesCliente) { this.observacionesCliente = observacionesCliente; }
}
