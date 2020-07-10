package org.andressamayoa.sistema;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.andressamayoa.controller.AcercaDeController;
import org.andressamayoa.controller.AreasController;
import org.andressamayoa.controller.CargosController;
import org.andressamayoa.controller.ContactoUrgenciasController;
import org.andressamayoa.controller.EspecialidadesController;
import org.andressamayoa.controller.HorariosController;
import org.andressamayoa.controller.MedicoController;
import org.andressamayoa.controller.MedicoEspecialidadController;
import org.andressamayoa.controller.MenuPrincipalController;
import org.andressamayoa.controller.PacientesController;
import org.andressamayoa.controller.ResponsableTurnoController;
import org.andressamayoa.controller.TelefonoMedicoController;
import org.andressamayoa.controller.TurnosController;

public class Principal extends Application {
     private final String paqueteVistas = "/org/andressamayoa/view/"; 
     private Stage escenarioPrincipal;
     private Scene escena;
     
   public void menuPrincipal(){
       try{
           MenuPrincipalController menuPrincipal = (MenuPrincipalController) cambiarEscena("MenuPrincipalView.fxml",460,360);
           menuPrincipal.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void acercaDe(){
       try{
           AcercaDeController acercaDe = (AcercaDeController) cambiarEscena("AcercaDeView.fxml",350,450);
           acercaDe.setAcercaDe(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void ventanaMedicos(){
       try{
           MedicoController medico = (MedicoController) cambiarEscena("MedicoView.fxml",800,600);
           medico.setMedico(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void especialidades(){
       try{
           EspecialidadesController especialidades = (EspecialidadesController) cambiarEscena("EspecialidadesView.fxml",600,400);
           especialidades.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void cargos(){
       try{
           CargosController cargos = (CargosController) cambiarEscena("CargosView.fxml",600,400);
           cargos.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void areas(){
       try{
           AreasController areas = (AreasController) cambiarEscena("AreasView.fxml",600,400);
           areas.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void pacientes(){
       try{
           PacientesController pacientes = (PacientesController) cambiarEscena("PacientesView.fxml",800,600);
           pacientes.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void contactoUrgencias(){
       try{
           ContactoUrgenciasController contactoUrgencias = (ContactoUrgenciasController) cambiarEscena("ContactoUrgenciasView.fxml",600,400);
           contactoUrgencias.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void telefonoMedicos(){
       try{
           TelefonoMedicoController telefonoMedico = (TelefonoMedicoController) cambiarEscena("TelefonoMedicoView.fxml",600,400);
           telefonoMedico.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void horarios(){
       try{
           HorariosController horario = (HorariosController) cambiarEscena("HorarioView.fxml",600,400);
           horario.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void responsableTurno(){
       try{
           ResponsableTurnoController responsableTurno = (ResponsableTurnoController) cambiarEscena("ResponsableTurnoView.fxml",600,400);
           responsableTurno.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void turno(){
       try{
           TurnosController turnos = (TurnosController) cambiarEscena("TurnosView.fxml",600,400);
           turnos.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
   public void medicoEspecialidad(){
       try{
           MedicoEspecialidadController medicoEspecialidad = (MedicoEspecialidadController) cambiarEscena("MedicoEspecialidadView.fxml",547,400);
           medicoEspecialidad.setEscenarioPrincipal(this);
       }catch(Exception e){
           e.printStackTrace();
       }
   }
   
    public Initializable cambiarEscena(String fxml, int ancho, int alto)throws Exception{
            Initializable resultado= null;
            FXMLLoader cargadorFXML = new FXMLLoader();
            InputStream archivo = Principal.class.getResourceAsStream(paqueteVistas+fxml);
            cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
            cargadorFXML.setLocation(Principal.class.getResource(paqueteVistas+fxml));
            escena = new Scene(( AnchorPane) cargadorFXML.load(archivo),ancho,alto);
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.sizeToScene();
            resultado = (Initializable) cargadorFXML.getController();
            return  resultado;
    } 
     @Override
    public void start(Stage escenarioPrincipal) {
            this.escenarioPrincipal = escenarioPrincipal;
            escenarioPrincipal.setTitle("Hospital Infectologia");
            menuPrincipal();
            escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
