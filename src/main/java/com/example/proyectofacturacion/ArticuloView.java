package com.example.proyectofacturacion;

public class ArticuloView {
    private String codigoArticulo;
    private String codigoBarrasArticulo;
    private String descripcionArticulo;
    private int familiaArticulo;
    private String nombreFamilia;
    private double costeArticulo;
    private double margenComercialArticulo;
    private double pvpArticulo;
    private int proveedorArticulo;
    private String nombreProveedor;
    private double stockArticulo;
    private String observacionesArticulo;
    private int tipoIva;
    private String nombreTipoIva;
    private String paisIva;
    private double porcentajeIva;
    private double precioConIVA;

    public ArticuloView(String codigoArticulo, String codigoBarrasArticulo, String descripcionArticulo,
                        int familiaArticulo, String nombreFamilia, double costeArticulo, double margenComercialArticulo,
                        double pvpArticulo, int proveedorArticulo, String nombreProveedor, double stockArticulo,
                        String observacionesArticulo, int tipoIva, String nombreTipoIva, String paisIva,
                        double porcentajeIva, double precioConIVA) {
        this.codigoArticulo = codigoArticulo;
        this.codigoBarrasArticulo = codigoBarrasArticulo;
        this.descripcionArticulo = descripcionArticulo;
        this.familiaArticulo = familiaArticulo;
        this.nombreFamilia = nombreFamilia;
        this.costeArticulo = costeArticulo;
        this.margenComercialArticulo = margenComercialArticulo;
        this.pvpArticulo = pvpArticulo;
        this.proveedorArticulo = proveedorArticulo;
        this.nombreProveedor = nombreProveedor;
        this.stockArticulo = stockArticulo;
        this.observacionesArticulo = observacionesArticulo;
        this.tipoIva = tipoIva;
        this.nombreTipoIva = nombreTipoIva;
        this.paisIva = paisIva;
        this.porcentajeIva = porcentajeIva;
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

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
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

    public String getNombreTipoIva() {
        return nombreTipoIva;
    }

    public void setNombreTipoIva(String nombreTipoIva) {
        this.nombreTipoIva = nombreTipoIva;
    }

    public String getPaisIva() {
        return paisIva;
    }

    public void setPaisIva(String paisIva) {
        this.paisIva = paisIva;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public double getPrecioConIVA() {
        return precioConIVA;
    }

    public void setPrecioConIVA(double precioConIVA) {
        this.precioConIVA = precioConIVA;
    }
}