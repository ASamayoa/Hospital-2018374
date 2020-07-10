package org.andressamayoa.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.Especialidad;
import org.andressamayoa.bean.Horario;
import org.andressamayoa.bean.Medico;
import org.andressamayoa.bean.MedicoEspecialidad;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class MedicoEspecialidadController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    ObservableList<Medico> listaMedico;
    ObservableList<Horario> listaHorario;
    ObservableList<Especialidad> listaEspecialidad;
    
    private @FXML ComboBox cbmMedicos;
    private @FXML ComboBox cbmEspecialidades;
    private @FXML ComboBox cbmHorarios;
    private @FXML TableView tbnMedicosEspecialidades;
    private @FXML TableColumn colCodigo;
    private @FXML TableColumn colMedico;
    private @FXML TableColumn colHorario;
    private @FXML TableColumn colEspecialidad;
    private @FXML Button btnNuevo;
    private @FXML Button btnEliminar;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                activarControles();
                limpiarControles();
                tipoOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(cbmMedicos.getSelectionModel().getSelectedItem()!=null||cbmEspecialidades.getSelectionModel().getSelectedItem()!=null||cbmHorarios.getSelectionModel().getSelectedItem()!=null){
                    guardar();
                    cargarDatos();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    desactivarControles();
                    limpiarControles();
                    tipoOperacion = operaciones.NINGUNO;
                   break;
                }else
                    JOptionPane.showMessageDialog(null, "Llene todos los campos antes");
        }
    }
    
    public void eliminar(){
        switch (tipoOperacion){
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                desactivarControles();
                limpiarControles();
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()!=null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de eliminar este registro?", "Eliminar registro", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedicoEspecialidad(?)}");
                            procedimiento.setInt(1, ((MedicoEspecialidad) tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
                            procedimiento.execute();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else
                        limpiarControles();
                }else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro primero");
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()!=null){    
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    limpiarControles();
                    tipoOperacion = operaciones.EDITAR;
                }else
                    JOptionPane.showMessageDialog(null, "Seleccione un elemento primero");
                break;
            case EDITAR:
                actualizar();
                cargarDatos();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                desactivarControles();
                limpiarControles();
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case EDITAR:
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                desactivarControles();
                limpiarControles();
                tipoOperacion = operaciones.NINGUNO;
        }
    }

    public void cargarDatos(){
        tbnMedicosEspecialidades.setItems(getMedicoEspecialidad());
        colCodigo.setCellValueFactory(new PropertyValueFactory <MedicoEspecialidad,Integer> ("codigoMedicoEspecialidad"));
        colMedico.setCellValueFactory(new PropertyValueFactory <MedicoEspecialidad,Integer> ("codigoMedico"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory <MedicoEspecialidad,Integer> ("codigoEspecialidad"));
        colHorario.setCellValueFactory(new PropertyValueFactory <MedicoEspecialidad,Integer> ("codigoHorario"));
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cbmMedicos.setItems(getMedicos());
        cbmHorarios.setItems(getHorarios());
        cbmEspecialidades.setItems(getEspecialidad());
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void llamarMenuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }

    public void seleccionar(){
        if(tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()!=null){
            cbmMedicos.getSelectionModel().select(buscarMedico(((MedicoEspecialidad) tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            cbmEspecialidades.getSelectionModel().select(buscarEspecialidad(((MedicoEspecialidad) tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
            cbmHorarios.getSelectionModel().select(buscarHorario(((MedicoEspecialidad) tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()).getCodigoHorario()));
        }
    }
    
    public void guardar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedicoEspecialidad(?,?,?)}");
            procedimiento.setInt(1, ((Medico) cbmMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
            procedimiento.setInt(2, ((Especialidad) cbmEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
            procedimiento.setInt(3, ((Horario) cbmHorarios.getSelectionModel().getSelectedItem()).getCodigo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void actualizar(){
        MedicoEspecialidad registro = new MedicoEspecialidad();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("[call sp_EditarMedicoEspecialidad(?,?,?,?)]");
            registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad) tbnMedicosEspecialidades.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
            registro.setCodigoMedico(((Medico) cbmMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
            registro.setCodigoEspecialidad(((Especialidad) cbmEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
            registro.setCodigoHorario(((Horario) cbmHorarios.getSelectionModel().getSelectedItem()).getCodigo());
            procedimiento.setInt(1, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(2, registro.getCodigoMedico());
            procedimiento.setInt(3, registro.getCodigoEspecialidad());
            procedimiento.setInt(4, registro.getCodigoHorario());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void limpiarControles(){
        cbmMedicos.getSelectionModel().clearSelection();
        cbmHorarios.getSelectionModel().clearSelection();
        cbmEspecialidades.getSelectionModel().clearSelection();
        tbnMedicosEspecialidades.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        cbmMedicos.setDisable(false);
        cbmHorarios.setDisable(false);
        cbmEspecialidades.setDisable(false);
        tbnMedicosEspecialidades.setDisable(true);
    }
    
    public void desactivarControles(){
        cbmMedicos.setDisable(true);
        cbmHorarios.setDisable(true);
        cbmEspecialidades.setDisable(true);
        tbnMedicosEspecialidades.setDisable(false);
    }

    public Medico buscarMedico (int codigoMedico){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado =  new Medico(registro.getInt("codigoMedico"),
                                        registro.getInt("licenciaMedica"),
                                        registro.getString("nombres"),
                                        registro.getString("apellidos"),
                                        registro.getString("horaEntrada"),
                                        registro.getString("horaSalida"),
                                        registro.getInt("turnoMaximo"),
                                        registro.getString("sexo")
                
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Especialidad buscarEspecialidad(int codigo){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareStatement("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Especialidad(
                        registro.getInt("codigoEspecialidad"),
                        registro.getString("nombreEspecialidad")
                        );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Horario buscarHorario(int codigo){
        Horario resultado = new Horario();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
            procedimiento.setInt(1, codigo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Horario(registro.getInt("codigoHorario"),
                                        registro.getString("horaInicio"),
                                        registro.getString("horaSalida"),
                                        registro.getBoolean("lunes"),
                                        registro.getBoolean("martes"),
                                        registro.getBoolean("miercoles"),
                                        registro.getBoolean("jueves"),
                                        registro.getBoolean("viernes"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ObservableList getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>(); 
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedico()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                        resultado.getInt("licenciaMedica"),
                        resultado.getString("nombres"),
                        resultado.getString("apellidos"),
                        resultado.getString("horaEntrada"),
                        resultado.getString("horaSalida"),
                        resultado.getInt("turnoMaximo"),
                        resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedico = FXCollections.observableList(lista);
    } 

    public ObservableList getHorarios(){
        ArrayList<Horario> registro = new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorario()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                registro.add(new Horario(resultado.getInt("codigoHorario"),
                                         resultado.getString("horaInicio"),
                                         resultado.getString("horaSalida"),
                                         resultado.getBoolean("lunes"),
                                         resultado.getBoolean("martes"),
                                         resultado.getBoolean("miercoles"),
                                         resultado.getBoolean("jueves"),
                                         resultado.getBoolean("viernes")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaHorario = FXCollections.observableArrayList(registro);
    }

    public ObservableList getEspecialidad(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidad()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                    resultado.getString("nombreEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEspecialidad = FXCollections.observableArrayList(lista);
    }

    public ObservableList getMedicoEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicoEspecialidad()}");
                ResultSet resultado = procedimiento.executeQuery();
                 while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("codigoMedicoEspecialidad"),
                                                 resultado.getInt("codigoMedico"),
                                                 resultado.getInt("codigoHorario"),
                                                 resultado.getInt("codigoEspecialidad")));
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaMedicoEspecialidad = FXCollections.observableArrayList(lista);
    }
}
