package org.andressamayoa.bean;

public class ContactoUrgencia {
    int codigoContacto;
    int codigoPaciente;
    String nombre;
    String apellido;
    String numero;

    public ContactoUrgencia() {
    }

    public ContactoUrgencia(int codigoContacto, int codigoPaciente, String nombre, String apellido, String numero) {
        this.codigoContacto = codigoContacto;
        this.codigoPaciente = codigoPaciente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
    }

    public int getCodigoContacto() {
        return codigoContacto;
    }

    public void setCodigoContacto(int codigoContacto) {
        this.codigoContacto = codigoContacto;
    }

    public int getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(int codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    
}
