package com.example.proyectofacturacion;

public class Proveedor {
    private String nombreProveedor;
    private String direccionProveedor;
    private String cpProveedor;
    private String poblacionProveedor;
    private String provinciaProveedor;
    private String paisProveedor;
    private String cifProveedor;
    private String telefonoProveedor;
    private String emailProveedor;
    private String ibanProveedor;
    private String contactoProveedor;
    private String observacionesProveedor;

    public Proveedor(String nombreProveedor, String direccionProveedor, String cpProveedor,
                     String poblacionProveedor, String provinciaProveedor, String paisProveedor,
                     String cifProveedor, String telefonoProveedor, String emailProveedor,
                     String ibanProveedor, String contactoProveedor, String observacionesProveedor) {
        this.nombreProveedor = nombreProveedor;
        this.direccionProveedor = direccionProveedor;
        this.cpProveedor = cpProveedor;
        this.poblacionProveedor = poblacionProveedor;
        this.provinciaProveedor = provinciaProveedor;
        this.paisProveedor = paisProveedor;
        this.cifProveedor = cifProveedor;
        this.telefonoProveedor = telefonoProveedor;
        this.emailProveedor = emailProveedor;
        this.ibanProveedor = ibanProveedor;
        this.contactoProveedor = contactoProveedor;
        this.observacionesProveedor = observacionesProveedor;
    }

    // Getters y Setters

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getDireccionProveedor() {
        return direccionProveedor;
    }

    public void setDireccionProveedor(String direccionProveedor) {
        this.direccionProveedor = direccionProveedor;
    }

    public String getCpProveedor() {
        return cpProveedor;
    }

    public void setCpProveedor(String cpProveedor) {
        this.cpProveedor = cpProveedor;
    }

    public String getPoblacionProveedor() {
        return poblacionProveedor;
    }

    public void setPoblacionProveedor(String poblacionProveedor) {
        this.poblacionProveedor = poblacionProveedor;
    }

    public String getProvinciaProveedor() {
        return provinciaProveedor;
    }

    public void setProvinciaProveedor(String provinciaProveedor) {
        this.provinciaProveedor = provinciaProveedor;
    }

    public String getPaisProveedor() {
        return paisProveedor;
    }

    public void setPaisProveedor(String paisProveedor) {
        this.paisProveedor = paisProveedor;
    }

    public String getCifProveedor() {
        return cifProveedor;
    }

    public void setCifProveedor(String cifProveedor) {
        this.cifProveedor = cifProveedor;
    }

    public String getTelefonoProveedor() {
        return telefonoProveedor;
    }

    public void setTelefonoProveedor(String telefonoProveedor) {
        this.telefonoProveedor = telefonoProveedor;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
    }

    public String getIbanProveedor() {
        return ibanProveedor;
    }

    public void setIbanProveedor(String ibanProveedor) {
        this.ibanProveedor = ibanProveedor;
    }

    public String getContactoProveedor() {
        return contactoProveedor;
    }

    public void setContactoProveedor(String contactoProveedor) {
        this.contactoProveedor = contactoProveedor;
    }

    public String getObservacionesProveedor() {
        return observacionesProveedor;
    }

    public void setObservacionesProveedor(String observacionesProveedor) {
        this.observacionesProveedor = observacionesProveedor;
    }
}
