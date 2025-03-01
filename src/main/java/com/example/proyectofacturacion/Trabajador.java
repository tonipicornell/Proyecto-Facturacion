package com.example.proyectofacturacion;

import java.time.LocalDate;

public class Trabajador {
    private int id;
    private String nombreCompleto;
    private String dniNie;
    private LocalDate fechaNacimiento;
    private String direccionResidencia;
    private String telefonoContacto;
    private String correoElectronico;
    private String numeroAfiliacionSeguridadSocial;
    private LocalDate fechaInicioContrato;
    private String tipoContrato;
    private String puestoTrabajo;
    private String centroTrabajo;
    private String grupoProfesional;
    private String jornadaLaboral;
    private double tipoRetencionIrpf;
    private String datosBancarios;
    private String regimenSeguridadSocial;
    private String titulosAcademicos;
    private String cursosFormacion;
    private String experienciaLaboral;
    private boolean antecedentesPenales;

    public Trabajador(int id, String nombreCompleto, String dniNie, LocalDate fechaNacimiento,
                      String direccionResidencia, String telefonoContacto, String correoElectronico,
                      String numeroAfiliacionSeguridadSocial, LocalDate fechaInicioContrato,
                      String tipoContrato, String puestoTrabajo, String centroTrabajo,
                      String grupoProfesional, String jornadaLaboral, double tipoRetencionIrpf,
                      String datosBancarios, String regimenSeguridadSocial, String titulosAcademicos,
                      String cursosFormacion, String experienciaLaboral, boolean antecedentesPenales) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dniNie = dniNie;
        this.fechaNacimiento = fechaNacimiento;
        this.direccionResidencia = direccionResidencia;
        this.telefonoContacto = telefonoContacto;
        this.correoElectronico = correoElectronico;
        this.numeroAfiliacionSeguridadSocial = numeroAfiliacionSeguridadSocial;
        this.fechaInicioContrato = fechaInicioContrato;
        this.tipoContrato = tipoContrato;
        this.puestoTrabajo = puestoTrabajo;
        this.centroTrabajo = centroTrabajo;
        this.grupoProfesional = grupoProfesional;
        this.jornadaLaboral = jornadaLaboral;
        this.tipoRetencionIrpf = tipoRetencionIrpf;
        this.datosBancarios = datosBancarios;
        this.regimenSeguridadSocial = regimenSeguridadSocial;
        this.titulosAcademicos = titulosAcademicos;
        this.cursosFormacion = cursosFormacion;
        this.experienciaLaboral = experienciaLaboral;
        this.antecedentesPenales = antecedentesPenales;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDniNie() {
        return dniNie;
    }

    public void setDniNie(String dniNie) {
        this.dniNie = dniNie;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccionResidencia() {
        return direccionResidencia;
    }

    public void setDireccionResidencia(String direccionResidencia) {
        this.direccionResidencia = direccionResidencia;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getNumeroAfiliacionSeguridadSocial() {
        return numeroAfiliacionSeguridadSocial;
    }

    public void setNumeroAfiliacionSeguridadSocial(String numeroAfiliacionSeguridadSocial) {
        this.numeroAfiliacionSeguridadSocial = numeroAfiliacionSeguridadSocial;
    }

    public LocalDate getFechaInicioContrato() {
        return fechaInicioContrato;
    }

    public void setFechaInicioContrato(LocalDate fechaInicioContrato) {
        this.fechaInicioContrato = fechaInicioContrato;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getPuestoTrabajo() {
        return puestoTrabajo;
    }

    public void setPuestoTrabajo(String puestoTrabajo) {
        this.puestoTrabajo = puestoTrabajo;
    }

    public String getCentroTrabajo() {
        return centroTrabajo;
    }

    public void setCentroTrabajo(String centroTrabajo) {
        this.centroTrabajo = centroTrabajo;
    }

    public String getGrupoProfesional() {
        return grupoProfesional;
    }

    public void setGrupoProfesional(String grupoProfesional) {
        this.grupoProfesional = grupoProfesional;
    }

    public String getJornadaLaboral() {
        return jornadaLaboral;
    }

    public void setJornadaLaboral(String jornadaLaboral) {
        this.jornadaLaboral = jornadaLaboral;
    }

    public double getTipoRetencionIrpf() {
        return tipoRetencionIrpf;
    }

    public void setTipoRetencionIrpf(double tipoRetencionIrpf) {
        this.tipoRetencionIrpf = tipoRetencionIrpf;
    }

    public String getDatosBancarios() {
        return datosBancarios;
    }

    public void setDatosBancarios(String datosBancarios) {
        this.datosBancarios = datosBancarios;
    }

    public String getRegimenSeguridadSocial() {
        return regimenSeguridadSocial;
    }

    public void setRegimenSeguridadSocial(String regimenSeguridadSocial) {
        this.regimenSeguridadSocial = regimenSeguridadSocial;
    }

    public String getTitulosAcademicos() {
        return titulosAcademicos;
    }

    public void setTitulosAcademicos(String titulosAcademicos) {
        this.titulosAcademicos = titulosAcademicos;
    }

    public String getCursosFormacion() {
        return cursosFormacion;
    }

    public void setCursosFormacion(String cursosFormacion) {
        this.cursosFormacion = cursosFormacion;
    }

    public String getExperienciaLaboral() {
        return experienciaLaboral;
    }

    public void setExperienciaLaboral(String experienciaLaboral) {
        this.experienciaLaboral = experienciaLaboral;
    }

    public boolean isAntecedentesPenales() {
        return antecedentesPenales;
    }

    public void setAntecedentesPenales(boolean antecedentesPenales) {
        this.antecedentesPenales = antecedentesPenales;
    }
}