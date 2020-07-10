
package org.andressamayoa.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.andressamayoa.sistema.Principal;

public class MenuPrincipalController implements Initializable {
    private Principal escenarioPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void llamarAcercaDe(){
        escenarioPrincipal.acercaDe();
    }
    
    public void llamarMedico(){
        escenarioPrincipal.ventanaMedicos();
    }
 
    public void llamarEspecialidades(){
        escenarioPrincipal.especialidades();
    }
    
    public void llamarCargos(){
        escenarioPrincipal.cargos();
    }
    
    public void llamarAreas(){
        escenarioPrincipal.areas();
    }
    
    public void llamarPacientes(){
        escenarioPrincipal.pacientes();
    }
    
    public void llamarHorarios(){
        escenarioPrincipal.horarios();
    }
    
    public void llamarResponsableTurno(){
        escenarioPrincipal.responsableTurno();
    }

    public void llamarMedicoEspecialidad(){
        escenarioPrincipal.medicoEspecialidad();
    }

    public void llamarTurno(){
        escenarioPrincipal.turno();
    }
}
