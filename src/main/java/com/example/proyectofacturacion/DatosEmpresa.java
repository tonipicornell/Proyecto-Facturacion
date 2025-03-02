package com.example.proyectofacturacion;

import java.sql.Date;
import java.time.LocalDate;

public class DatosEmpresa {
    private int idEmpresa;
    private String razonSocial;
    private String nif;
    private String tipoEmpresa;
    private String cif;
    private String direccionSocial;
    private String actividadEconomica;
    private LocalDate fechaConstitucion;
    private double capitalSocial;
    private String estatutosSociales;
    private boolean libroActas;
    private int numeroTrabajadores;
    private int numeroComerciales;
    private int numeroDistribuidores;
    private int numeroProveedores;
    private int numeroClientes;
    private String convenioColectivo;

    // Constructor vacío
    public DatosEmpresa() {
    }

    // Constructor completo
    public DatosEmpresa(int idEmpresa, String razonSocial, String nif, String tipoEmpresa, String cif,
                        String direccionSocial, String actividadEconomica, LocalDate fechaConstitucion,
                        double capitalSocial, String estatutosSociales, boolean libroActas,
                        int numeroTrabajadores, int numeroComerciales, int numeroDistribuidores,
                        int numeroProveedores, int numeroClientes, String convenioColectivo) {
        this.idEmpresa = idEmpresa;
        this.razonSocial = razonSocial;
        this.nif = nif;
        this.tipoEmpresa = tipoEmpresa;
        this.cif = cif;
        this.direccionSocial = direccionSocial;
        this.actividadEconomica = actividadEconomica;
        this.fechaConstitucion = fechaConstitucion;
        this.capitalSocial = capitalSocial;
        this.estatutosSociales = estatutosSociales;
        this.libroActas = libroActas;
        this.numeroTrabajadores = numeroTrabajadores;
        this.numeroComerciales = numeroComerciales;
        this.numeroDistribuidores = numeroDistribuidores;
        this.numeroProveedores = numeroProveedores;
        this.numeroClientes = numeroClientes;
        this.convenioColectivo = convenioColectivo;
    }

    // Getters y Setters
    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getDireccionSocial() {
        return direccionSocial;
    }

    public void setDireccionSocial(String direccionSocial) {
        this.direccionSocial = direccionSocial;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(String actividadEconomica) {
        this.actividadEconomica = actividadEconomica;
    }

    public LocalDate getFechaConstitucion() {
        return fechaConstitucion;
    }

    public void setFechaConstitucion(LocalDate fechaConstitucion) {
        this.fechaConstitucion = fechaConstitucion;
    }

    public double getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(double capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    public String getEstatutosSociales() {
        return estatutosSociales;
    }

    public void setEstatutosSociales(String estatutosSociales) {
        this.estatutosSociales = estatutosSociales;
    }

    public boolean isLibroActas() {
        return libroActas;
    }

    public void setLibroActas(boolean libroActas) {
        this.libroActas = libroActas;
    }

    public int getNumeroTrabajadores() {
        return numeroTrabajadores;
    }

    public void setNumeroTrabajadores(int numeroTrabajadores) {
        this.numeroTrabajadores = numeroTrabajadores;
    }

    public int getNumeroComerciales() {
        return numeroComerciales;
    }

    public void setNumeroComerciales(int numeroComerciales) {
        this.numeroComerciales = numeroComerciales;
    }

    public int getNumeroDistribuidores() {
        return numeroDistribuidores;
    }

    public void setNumeroDistribuidores(int numeroDistribuidores) {
        this.numeroDistribuidores = numeroDistribuidores;
    }

    public int getNumeroProveedores() {
        return numeroProveedores;
    }

    public void setNumeroProveedores(int numeroProveedores) {
        this.numeroProveedores = numeroProveedores;
    }

    public int getNumeroClientes() {
        return numeroClientes;
    }

    public void setNumeroClientes(int numeroClientes) {
        this.numeroClientes = numeroClientes;
    }

    public String getConvenioColectivo() {
        return convenioColectivo;
    }

    public void setConvenioColectivo(String convenioColectivo) {
        this.convenioColectivo = convenioColectivo;
    }
}