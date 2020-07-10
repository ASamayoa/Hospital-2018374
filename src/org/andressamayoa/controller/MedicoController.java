package org.andressamayoa.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.Medico;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.report.GenerarReporte;
import org.andressamayoa.sistema.Principal;

public class MedicoController implements Initializable{
    private final String imagen = "/org/andressamayoa/images/";
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
     ObservableList<Medico> listaMedico;
    
    @FXML private TextField txtLicenciaMedica;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtHoraEntrada;
    @FXML private TextField txtHoraSalida;
    @FXML private TextField txtTurno;
    @FXML private TextField txtSexo;
    @FXML private TableView tbnMedicos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colTurno;
    @FXML private TableColumn colSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                    tbnMedicos.getSelectionModel().clearSelection();
                    limpiarControles();
                    activarControles();
                    btnNuevo.setText("Guardar");
                    btnEliminar.setText("Cancelar");
                    btnEditar.setDisable(true);
                    btnReporte.setDisable(true);
                    tipoOperacion = operaciones.GUARDAR;
                    break;
            case GUARDAR:
                if(txtLicenciaMedica.getText()!=null&&txtNombres.getText()!=null&&txtApellidos.getText()!=null&&txtHoraEntrada.getText()!=null&&txtHoraSalida!=null&&txtSexo.getText()!=null){
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                }else
                    JOptionPane.showMessageDialog(null,"Llene todos los campos antes de guardar");
                break;
        }
    }
    
    public void seleccionarElemento(){
        if(tbnMedicos.getSelectionModel().getSelectedItem()!=null){
        txtLicenciaMedica.setText(String.valueOf(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
        txtNombres.setText(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getApellidos());
        txtHoraEntrada.setText(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada());
        txtHoraSalida.setText(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getHoraSalida());
        txtTurno.setText(String.valueOf(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getTurnoMaximo()));
        txtSexo.setText(((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getSexo());
        }
    }
    
    public void eliminar(){
        switch(tipoOperacion){
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
                
                if(tbnMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de eliminar el registro?", "Eliminar Medico", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                            procedimiento.setInt(1, ((Medico) tbnMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico() );
                            procedimiento.execute();
                            listaMedico.remove(tbnMedicos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tbnMedicos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tbnMedicos.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                
        }
    }
    
    public void editar(){
        switch (tipoOperacion){
            case NINGUNO:
                if(tbnMedicos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoOperacion =operaciones.EDITAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro primero");
                }
                break;
            case EDITAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion=operaciones.NINGUNO;
                cargarDatos();
                limpiarControles();
                desactivarControles();
                tbnMedicos.getSelectionModel().clearSelection();
                break;
        }
    }
   
    public void reporte(){
        switch(tipoOperacion){
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                break;
            case EDITAR:
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoOperacion=operaciones.NINGUNO;
                limpiarControles();
                desactivarControles();
                tbnMedicos.getSelectionModel().clearSelection();
                break;
        }
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tbnMedicos.setItems(getMedicos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("licenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory <Medico,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico,String>("apellidos"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Medico,String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Medico,String>("horaSalida"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("turnoMaximo"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico,String>("sexo"));
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
    
    public Principal getMedico() {
        return escenarioPrincipal;
    }

    public void setMedico(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void llamarMenuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void llamarTelefonoMedico(){
        escenarioPrincipal.telefonoMedicos();
    }
    
    public void desactivarControles(){
        txtLicenciaMedica.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtHoraEntrada.setEditable(false);
        txtHoraSalida.setEditable(false);
        txtTurno.setEditable(false);
        txtSexo.setEditable(false);
        tbnMedicos.setDisable(false);
    }
    
    public void activarControles(){
        txtLicenciaMedica.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtHoraEntrada.setEditable(true);
        txtHoraSalida.setEditable(true);
        txtTurno.setEditable(false);
        txtSexo.setEditable(true);
        tbnMedicos.setDisable(true);
    }
   
    public void limpiarControles(){
        txtLicenciaMedica.setText(null);
        txtNombres.setText(null);
        txtApellidos.setText(null);
        txtHoraEntrada.setText(null);
        txtHoraSalida.setText(null);
        txtTurno.setText(null);
        txtSexo.setText(null);
        tbnMedicos.getSelectionModel().clearSelection();
    }
    
    public void guardar(){
        Medico registro = new Medico();
        registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setHoraEntrada(txtHoraEntrada.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        registro.setSexo(txtSexo.getText());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico(?,?,?,?,?,?)}");
            procedimiento.setInt(1,registro.getLicenciaMedica());
            procedimiento.setString(2,registro.getNombres());
            procedimiento.setString(3,registro.getApellidos());
            procedimiento.setString(4,registro.getHoraEntrada());
            procedimiento.setString(5,registro.getHoraSalida());
            procedimiento.setString(6,registro.getSexo());
            procedimiento.execute();
            
            listaMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Medico(
                            registro.getInt("codigoMedico"),
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
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarMedico(?,?,?,?,?,?,?)}");
            Medico registro = (Medico)tbnMedicos.getSelectionModel().getSelectedItem();
            registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setHoraEntrada(txtHoraEntrada.getText());
            registro.setHoraSalida(txtHoraEntrada.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getLicenciaMedica());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setString(4, registro.getApellidos());
            procedimiento.setString(5, registro.getHoraEntrada());
            procedimiento.setString(6, registro.getHoraSalida());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("ReporteMedicos.jasper", null);
        parametros.put("Ruta", this.getClass().getResourceAsStream(imagen+"logo temporal.jpg"));
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper", "Reporte de medicos", parametros);
    }
    
    public void validarNumeros(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isDigit(key)){
                if(txtLicenciaMedica.getText().length()>8){
                    KeyEvent.consume();
            }
            }else
                KeyEvent.consume();
        }catch(Exception e){
           // e.printStackTrace();
        }
    }
    
    public void validarLetras(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(!Character.isLetter(key)&&!Character.isSpaceChar(key)){
                KeyEvent.consume();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
