package org.andressamayoa.bean;

public class MedicoEspecialidad {
    int codigoMedicoEspecialidad;
    int codigoMedico;
    int codigoHorario;
    int codigoEspecialidad;

    public MedicoEspecialidad() {
    }

    public MedicoEspecialidad(int codigoMedicoEspecialidad, int codigoMedico, int codigoHorario, int codigoEspecialidad) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
        this.codigoMedico = codigoMedico;
        this.codigoHorario = codigoHorario;
        this.codigoEspecialidad = codigoEspecialidad;
    }

    public int getCodigoMedicoEspecialidad() {
        return codigoMedicoEspecialidad;
    }

    public void setCodigoMedicoEspecialidad(int codigoMedicoEspecialidad) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
    }

    public int getCodigoMedico() {
        return codigoMedico;
    }

    public void setCodigoMedico(int codigoMedico) {
        this.codigoMedico = codigoMedico;
    }

    public int getCodigoHorario() {
        return codigoHorario;
    }

    public void setCodigoHorario(int codigoHorario) {
        this.codigoHorario = codigoHorario;
    }

    public int getCodigoEspecialidad() {
        return codigoEspecialidad;
    }

    public void setCodigoEspecialidad(int codigoEspecialidad) {
        this.codigoEspecialidad = codigoEspecialidad;
    }
    
    public String toString(){
        return getCodigoMedicoEspecialidad()+"|"+getCodigoMedico()+", "+getCodigoEspecialidad()+", "+getCodigoMedico();
    }
}
