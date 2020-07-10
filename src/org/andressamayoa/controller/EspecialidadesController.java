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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.Especialidad;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class EspecialidadesController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Especialidad> listaEspecialidad;
    
    @FXML private TextField txtNombre;
    @FXML private TableView tbnEspecialidades;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombre;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
     public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                    activarControles();
                    btnNuevo.setText("Guardar");
                    btnEliminar.setText("Cancelar");
                    btnEditar.setDisable(true);
                    btnReporte.setDisable(true);
                    tipoOperacion = operaciones.GUARDAR;
                    break;
            case GUARDAR:
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
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
                if(tbnEspecialidades.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de eliminar el registro", "Eliminar Especialidad", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarEspecialidad(?)}");
                            procedimiento.setInt(1, ((Especialidad) tbnEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
                            procedimiento.execute();
                            listaEspecialidad.remove(tbnEspecialidades.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else
                        tbnEspecialidades.getSelectionModel().clearSelection();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
        }
    }
   
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnEspecialidades.getSelectionModel().getSelectedItem()!=null){
                activarControles();
                btnNuevo.setDisable(true);
                btnEliminar.setDisable(true);
                btnEditar.setText("Guardar");
                btnReporte.setText("Cancelar");
                tipoOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro antes");
                break;
            case ACTUALIZAR:
                actualizar();
                desactivarControles();
                limpiarControles();
                cargarDatos();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tbnEspecialidades.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tbnEspecialidades.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void seleccionarElemento(){
        if(tbnEspecialidades.getSelectionModel().getSelectedItem()!=null){
        txtNombre.setText(((Especialidad) tbnEspecialidades.getSelectionModel().getSelectedItem()).getNombreEspecialidad());
        }
    }
    
    public void cargarDatos(){
        tbnEspecialidades.setItems(getEspecialidad());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Especialidad,Integer>("codigoEspecialidad"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Especialidad,String>("nombreEspecialidad"));
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
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
    
    public void desactivarControles(){
        txtNombre.setEditable(false);
        tbnEspecialidades.setDisable(false);
    }
    
    public void activarControles(){
        txtNombre.setEditable(true);
        tbnEspecialidades.setDisable(true);
    }
    
    public void limpiarControles(){
        txtNombre.setText(null);
        tbnEspecialidades.getSelectionModel().clearSelection();
    }
    
    public void guardar(){
        Especialidad registro = new Especialidad();
        registro.setNombreEspecialidad(txtNombre.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
            procedimiento.setString(1, registro.getNombreEspecialidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Especialidad buscar(int codigo){
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

    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarEspecialidad(?,?)}");
            Especialidad registro = (Especialidad) tbnEspecialidades.getSelectionModel().getSelectedItem();
            registro.setCodigoEspecialidad(((Especialidad) tbnEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
            registro.setNombreEspecialidad(txtNombre.getText());
            procedimiento.setInt(1, registro.getCodigoEspecialidad());
            procedimiento.setString(2, registro.getNombreEspecialidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void validarLetras(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
            if(!Character.isLetter(key))
                KeyEvent.consume();
    }
}
