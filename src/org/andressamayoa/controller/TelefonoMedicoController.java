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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.Medico;
import org.andressamayoa.bean.TelefonoMedico;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class TelefonoMedicoController implements Initializable{
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private ObservableList<TelefonoMedico> listaTelefonoMedico;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaCodigoMedico;
    boolean validacion = false;
    
    @FXML private TextField txtTelefonoProfecional;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private ComboBox cbmCodigoMedico;
    @FXML private TableView tbnTelefonosMedicos;
    @FXML private TableColumn colCodigoTelefonoMedico;
    @FXML private TableColumn colMedico;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colTelefonoProfecional;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                tbnTelefonosMedicos.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtTelefonoPersonal.getText() != null&& cbmCodigoMedico.getSelectionModel().getSelectedItem()!= null){
                    if(txtTelefonoProfecional.getText() == null)
                        txtTelefonoProfecional.setText("N/A");
                    guardar();
                    limpiarControles();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    cargarDatos();
                    tipoOperacion = operaciones.NINGUNO;
                }else{
                    JOptionPane.showMessageDialog(null, "Llene todos los campos");
                }
                break;
        }
    }
    
    public void eliminar(){
        switch (tipoOperacion){
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnTelefonosMedicos.getSelectionModel().getSelectedItem()!=null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de eliminar este registro","Eliminar telefono medico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
                        procedimiento.setInt(1, ((TelefonoMedico) tbnTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
                        procedimiento.execute();
                        listaTelefonoMedico.remove(tbnTelefonosMedicos.getSelectionModel().getSelectedIndex());
                        limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tbnTelefonosMedicos.getSelectionModel().clearSelection();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento primero");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnTelefonosMedicos.getSelectionModel().getSelectedItem()!=null){
                    tbnTelefonosMedicos.getSelectionModel().clearSelection();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    cbmCodigoMedico.setDisable(true);
                    tipoOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro primero");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                limpiarControles();
                desactivarControles();
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
            break;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                limpiarControles();
                tipoOperacion = operaciones.NINGUNO;
        }
    
    }
    
    public ObservableList<TelefonoMedico> getTelefonosMedicos(){
        ArrayList<TelefonoMedico> lista = new ArrayList<TelefonoMedico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTelefonoMedico()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new TelefonoMedico(resultado.getInt("codigoTelefonoMedico"),
                                             resultado.getString("telefonoPersonal"),
                                             resultado.getString("telefonoTrabajo"),
                                             resultado.getInt("codigoMedico")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTelefonoMedico = FXCollections.observableList(lista);
    }
    
    public void cargarDatos(){
        tbnTelefonosMedicos.setItems(getTelefonosMedicos());
        colCodigoTelefonoMedico.setCellValueFactory(new PropertyValueFactory <TelefonoMedico,Integer>("codigoTelefonoMedico"));
        colMedico.setCellValueFactory(new PropertyValueFactory <TelefonoMedico,Integer> ("codigoMedico"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory <TelefonoMedico,String> ("telefonoPersonal"));
        colTelefonoProfecional.setCellValueFactory(new PropertyValueFactory <TelefonoMedico,String>("telefonoTrabajo"));
    }
    
    public void seleccionarElementos(){
        if(tbnTelefonosMedicos.getSelectionModel().getSelectedIndex()>=0){
            txtTelefonoPersonal.setText(((TelefonoMedico)tbnTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            txtTelefonoProfecional.setText(((TelefonoMedico) tbnTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());
            cbmCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonoMedico)tbnTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        }else{
            limpiarControles();
            tbnTelefonosMedicos.getSelectionModel().clearSelection();
        }
    }   
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cbmCodigoMedico.setItems(getMedicos());
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void llamarMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }

    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoProfecional.setEditable(true);
        cbmCodigoMedico.setDisable(false);
        tbnTelefonosMedicos.setDisable(true);
    }
    
    public void desactivarControles(){
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoProfecional.setEditable(false);
        cbmCodigoMedico.setDisable(true);
        tbnTelefonosMedicos.setDisable(false);
    }
    
    public void limpiarControles(){
        txtTelefonoPersonal.setText(null);
        txtTelefonoProfecional.setText(null);
        cbmCodigoMedico.setValue(null);
        tbnTelefonosMedicos.getSelectionModel().clearSelection();
    }
    
    public void guardar(){
        TelefonoMedico resultado = new TelefonoMedico();
        resultado.setTelefonoPersonal(txtTelefonoPersonal.getText());
        resultado.setTelefonoTrabajo(txtTelefonoProfecional.getText());
        resultado.setCodigoMedico(((Medico)cbmCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
            procedimiento.setString(1, resultado.getTelefonoPersonal());
            procedimiento.setString(2, resultado.getTelefonoTrabajo());
            procedimiento.setInt(3, resultado.getCodigoMedico());
            procedimiento.execute();
            listaTelefonoMedico.add(resultado);
        }catch(Exception e){
         e.printStackTrace();
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTelefonoMedico(?,?,?,?)}");
            TelefonoMedico registro = (TelefonoMedico) tbnTelefonosMedicos.getSelectionModel().getSelectedItem();
            registro.setCodigoTelefonoMedico(((TelefonoMedico) tbnTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
            registro.setCodigoMedico(((Medico) cbmCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoProfecional.getText());
            procedimiento.setInt(1, registro.getCodigoTelefonoMedico());
            procedimiento.setString(2, registro.getTelefonoPersonal());
            procedimiento.setString(3, registro.getTelefonoTrabajo());
            procedimiento.setInt(4, registro.getCodigoMedico());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ObservableList<Medico> getMedicos(){
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

        return listaCodigoMedico = FXCollections.observableList(lista);
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

    public void validarNumeroPersonal(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isDigit(key)){
               if(txtTelefonoPersonal.getText().length()==8)
                   KeyEvent.consume();
            }else
                KeyEvent.consume();
        }catch(Exception e){
            //e.printStackTrace();
        }
    }
   
    public void validarNumeroProfecional(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isDigit(key)){
               if(txtTelefonoProfecional.getText().length()==8)
                   KeyEvent.consume();
            }else
                KeyEvent.consume();
        }catch(Exception e){
            //e.printStackTrace();
        }
    }
}
