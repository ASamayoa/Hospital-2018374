package org.andressamayoa.bean;

public class ResponsableTurno {
    private int codigoResponsableTurno;
    private String nombreResponsable;
    private String telefonoResponsable;
    private int codigoArea;
    private int codigoCargo;

    public ResponsableTurno() {
    }

    public ResponsableTurno(int codigoResponsableTurno, String nombreResponsable, String telefonoResponsable, int codigoArea, int codigoCargo) {
        this.codigoResponsableTurno = codigoResponsableTurno;
        this.nombreResponsable = nombreResponsable;
        this.telefonoResponsable = telefonoResponsable;
        this.codigoArea = codigoArea;
        this.codigoCargo = codigoCargo;
    }

    public int getCodigoResponsableTurno() {
        return codigoResponsableTurno;
    }

    public void setCodigoResponsableTurno(int codigoResponsableTurno) {
        this.codigoResponsableTurno = codigoResponsableTurno;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getTelefonoResponsable() {
        return telefonoResponsable;
    }

    public void setTelefonoResponsable(String telefonoResponsable) {
        this.telefonoResponsable = telefonoResponsable;
    }

    public int getCodigoArea() {
        return codigoArea;
    }

    public void setCodigoArea(int codigoArea) {
        this.codigoArea = codigoArea;
    }

    public int getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(int codigoCargo) {
        this.codigoCargo = codigoCargo;
    }
    
    public String toString(){
        return getCodigoResponsableTurno()+"|"+getNombreResponsable()+", "+getTelefonoResponsable();
    }
}
