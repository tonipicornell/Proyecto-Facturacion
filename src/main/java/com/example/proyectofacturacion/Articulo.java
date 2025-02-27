package com.example.proyectofacturacion;

public class Articulo {
    private String codigoArticulo;
    private String codigoBarrasArticulo;
    private String descripcionArticulo;
    private int familiaArticulo;
    private String nombreFamilia;
    private double costeArticulo;
    private double margenComercialArticulo;
    private double pvpArticulo;
    private int proveedorArticulo;
    private double stockArticulo;
    private String observacionesArticulo;
    private int tipoIva;
    private double precioConIVA;

    public Articulo(String codigoArticulo, String codigoBarrasArticulo, String descripcionArticulo,
                    int familiaArticulo, String nombreFamilia, double costeArticulo, double margenComercialArticulo,
                    double pvpArticulo, int proveedorArticulo, double stockArticulo,
                    String observacionesArticulo, int tipoIva, double precioConIVA) {
        this.codigoArticulo = codigoArticulo;
        this.codigoBarrasArticulo = codigoBarrasArticulo;
        this.descripcionArticulo = descripcionArticulo;
        this.familiaArticulo = familiaArticulo;
        this.nombreFamilia = nombreFamilia;
        this.costeArticulo = costeArticulo;
        this.margenComercialArticulo = margenComercialArticulo;
        this.pvpArticulo = pvpArticulo;
        this.proveedorArticulo = proveedorArticulo;
        this.stockArticulo = stockArticulo;
        this.observacionesArticulo = observacionesArticulo;
        this.tipoIva = tipoIva;
        this.precioConIVA = precioConIVA;
    }

    // Getters y Setters
    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public String getCodigoBarrasArticulo() {
        return codigoBarrasArticulo;
    }

    public void setCodigoBarrasArticulo(String codigoBarrasArticulo) {
        this.codigoBarrasArticulo = codigoBarrasArticulo;
    }

    public String getDescripcionArticulo() {
        return descripcionArticulo;
    }

    public void setDescripcionArticulo(String descripcionArticulo) {
        this.descripcionArticulo = descripcionArticulo;
    }

    public int getFamiliaArticulo() {
        return familiaArticulo;
    }

    public void setFamiliaArticulo(int familiaArticulo) {
        this.familiaArticulo = familiaArticulo;
    }

    public String getNombreFamilia() {
        return nombreFamilia;
    }

    public void setNombreFamilia(String nombreFamilia) {
        this.nombreFamilia = nombreFamilia;
    }

    public double getCosteArticulo() {
        return costeArticulo;
    }

    public void setCosteArticulo(double costeArticulo) {
        this.costeArticulo = costeArticulo;
    }

    public double getMargenComercialArticulo() {
        return margenComercialArticulo;
    }

    public void setMargenComercialArticulo(double margenComercialArticulo) {
        this.margenComercialArticulo = margenComercialArticulo;
    }

    public double getPvpArticulo() {
        return pvpArticulo;
    }

    public void setPvpArticulo(double pvpArticulo) {
        this.pvpArticulo = pvpArticulo;
    }

    public int getProveedorArticulo() {
        return proveedorArticulo;
    }

    public void setProveedorArticulo(int proveedorArticulo) {
        this.proveedorArticulo = proveedorArticulo;
    }

    public double getStockArticulo() {
        return stockArticulo;
    }

    public void setStockArticulo(double stockArticulo) {
        this.stockArticulo = stockArticulo;
    }

    public String getObservacionesArticulo() {
        return observacionesArticulo;
    }

    public void setObservacionesArticulo(String observacionesArticulo) {
        this.observacionesArticulo = observacionesArticulo;
    }

    public int getTipoIva() {
        return tipoIva;
    }

    public void setTipoIva(int tipoIva) {
        this.tipoIva = tipoIva;
    }

    public double getPrecioConIVA() {
        return precioConIVA;
    }

    public void setPrecioConIVA(double precioConIVA) {
        this.precioConIVA = precioConIVA;
    }
}