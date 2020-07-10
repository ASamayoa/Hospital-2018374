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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.Horario;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class HorariosController implements Initializable{
    private Principal escenarioPrincipal;
    private ObservableList<Horario> lista;
    public enum operacion{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    public operacion tipoOperacion = operacion.NINGUNO;
    
    @FXML CheckBox lunes;
    @FXML CheckBox martes;
    @FXML CheckBox miercoles;
    @FXML CheckBox jueves;
    @FXML CheckBox viernes;
    @FXML TextField txtEntrada;
    @FXML TextField txtSalida;
    @FXML TableView tbnHorarios;
    @FXML TableColumn colCodigo;
    @FXML TableColumn colEntrada;
    @FXML TableColumn colSalida;
    @FXML TableColumn colLunes;
    @FXML TableColumn colMartes;
    @FXML TableColumn colMiercoles;
    @FXML TableColumn colJueves;
    @FXML TableColumn colViernes;
    @FXML TableColumn colSabado;       
    @FXML TableColumn colDomingo;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    @FXML Button btnEditar;
    @FXML Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoOperacion = operacion.NUEVO;
                break;
            case NUEVO:
                if(txtEntrada.getText()!=null&&
                        txtSalida.getText()!=null&&
                        lunes.selectedProperty().getValue()!=null&&
                        martes.selectedProperty().getValue()!=null&&
                        miercoles.selectedProperty().getValue()!=null&&
                        jueves.selectedProperty().getValue()!=null&&
                        viernes.selectedProperty().getValue()!=null){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    cargar();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoOperacion = operacion.NINGUNO;
                }else
                    JOptionPane.showMessageDialog(null, "Llene todos los campos");
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoOperacion){
            case NUEVO:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operacion.NINGUNO;
                break;
            default:
                if(tbnHorarios.getSelectionModel().getSelectedItem()!=null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de eliminar este registro", "Seguro de eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                            procedimiento.setInt(1, ((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).getCodigo());
                            procedimiento.execute();
                            lista.remove(tbnHorarios.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargar();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tbnHorarios.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un elemento antes");
                    limpiarControles();
                }
        }
    }
    
    public void cargar(){
        tbnHorarios.setItems(listar());
        colCodigo.setCellValueFactory(new PropertyValueFactory  <Horario,Integer> ("codigo"));
        colEntrada.setCellValueFactory(new PropertyValueFactory <Horario,String> ("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory <Horario,String> ("horaSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory <Horario,Boolean> ("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory <Horario,Boolean> ("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory <Horario,Boolean> ("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory <Horario,Boolean> ("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory <Horario,Boolean> ("viernes"));
    }
    
    public void seleccionar(){
        if(tbnHorarios.getSelectionModel().getSelectedItem()!=null){
            txtEntrada.setText(((Horario)tbnHorarios.getSelectionModel().getSelectedItem()).getHoraEntrada());
            txtSalida.setText(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).getHoraSalida());
            lunes.setSelected(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).isLunes());
            martes.setSelected(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).isMartes());
            miercoles.setSelected(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).isMiercoles());
            jueves.setSelected(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).isJueves());
            viernes.setSelected(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).isViernes());
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnHorarios.getSelectionModel().getSelectedItem()!=null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    tipoOperacion = operacion.EDITAR;
                }else
                    JOptionPane.showMessageDialog(null, "Seleccione un registro primero");
                break;
            case EDITAR:
                actualizar();
                cargar();
                desactivarControles();
                limpiarControles();
                tbnHorarios.getSelectionModel().clearSelection();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operacion.NINGUNO;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case EDITAR:
                desactivarControles();
                limpiarControles();
                tbnHorarios.getSelectionModel().clearSelection();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operacion.NINGUNO;
        
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargar();
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
    
    public void limpiarControles(){
        txtEntrada.setText(null);
        txtSalida.setText(null);
        lunes.setSelected(false);
        martes.setSelected(false);
        miercoles.setSelected(false);
        jueves.setSelected(false);
        viernes.setSelected(false);
        tbnHorarios.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtEntrada.setEditable(true);
        txtSalida.setEditable(true);
        lunes.setDisable(false);
        martes.setDisable(false);
        miercoles.setDisable(false);
        jueves.setDisable(false);
        viernes.setDisable(false);
        tbnHorarios.setDisable(true);
    }
    
    public void desactivarControles(){
        txtEntrada.setEditable(false);
        txtSalida.setEditable(false);
        lunes.setDisable(true);
        martes.setDisable(true);
        miercoles.setDisable(true);
        jueves.setDisable(true);
        viernes.setDisable(true);
        tbnHorarios.setDisable(false);
    }
    
    public void guardar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, txtEntrada.getText());
            procedimiento.setString(2, txtSalida.getText());
            procedimiento.setBoolean(3, lunes.selectedProperty().getValue());
            procedimiento.setBoolean(4, martes.selectedProperty().getValue());
            procedimiento.setBoolean(5, miercoles.selectedProperty().getValue());
            procedimiento.setBoolean(6, jueves.selectedProperty().getValue());
            procedimiento.setBoolean(7, viernes.selectedProperty().getValue());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ObservableList listar(){
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
        return lista = FXCollections.observableArrayList(registro);
    }

    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarHorario(?,?,?,?,?,?,?,?)}");
            Horario registro = (Horario) tbnHorarios.getSelectionModel().getSelectedItem();
            registro.setCodigo(((Horario) tbnHorarios.getSelectionModel().getSelectedItem()).getCodigo());
            registro.setHoraEntrada(txtEntrada.getText());
            registro.setHoraSalida(txtSalida.getText());
            registro.setLunes(lunes.selectedProperty().getValue());
            registro.setMartes(martes.selectedProperty().getValue());
            registro.setMiercoles(miercoles.selectedProperty().getValue());
            registro.setJueves(jueves.selectedProperty().getValue());
            registro.setViernes(viernes.selectedProperty().getValue());
            procedimiento.setInt(1, registro.getCodigo());
            procedimiento.setString(2, registro.getHoraEntrada());
            procedimiento.setString(3, registro.getHoraSalida());
            procedimiento.setBoolean(4, registro.isLunes());
            procedimiento.setBoolean(5, registro.isMartes());
            procedimiento.setBoolean(6, registro.isMiercoles());
            procedimiento.setBoolean(7, registro.isJueves());
            procedimiento.setBoolean(8, registro.isViernes());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}