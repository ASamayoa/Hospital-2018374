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
import org.andressamayoa.bean.Area;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class AreasController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Area> listaAreas;
    
    @FXML private TextField txtNombre;
    @FXML private TableView tbnAreas;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombre;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                    tbnAreas.getSelectionModel().clearSelection();
                    limpiarControles();
                    activarControles();
                    btnNuevo.setText("Guardar");
                    btnEliminar.setText("Cancelar");
                    btnEditar.setDisable(true);
                    btnReporte.setDisable(true);
                    tipoOperacion = operaciones.GUARDAR;
                    break;
            case GUARDAR:
                if(txtNombre.getText()!=null){
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
                    JOptionPane.showMessageDialog(null, "Llene todos los campos antes de guardar");
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoOperacion){
            case GUARDAR:
                tbnAreas.getSelectionModel().clearSelection();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnAreas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de borrar este registro?", "Eliminar Area", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
                            procedimiento.setInt(1, ((Area) tbnAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
                            procedimiento.execute();
                            listaAreas.remove(tbnAreas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                        tbnAreas.getSelectionModel().clearSelection();
                        limpiarControles();
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un registro");
                }    
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnAreas.getSelectionModel().getSelectedItem()!=null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    tipoOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null, "Seleccione un registro primero");
                break;
            case ACTUALIZAR:
                actualizar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                tbnAreas.getSelectionModel().clearSelection();
                limpiarControles();
                desactivarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void seleccionarElemento(){
        if(tbnAreas.getSelectionModel().getSelectedItem()!=null)
        txtNombre.setText(((Area) tbnAreas.getSelectionModel().getSelectedItem()).getNombreArea());
    }
    
    public void cargarDatos(){
        tbnAreas.setItems(getAreas());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Area,Integer>("codigoArea"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Area,String>("nombreArea"));
    }
    
    public ObservableList getAreas(){
        ArrayList<Area> lista = new ArrayList<Area>();
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarArea()}");
        ResultSet resultado =procedimiento.executeQuery();
        while(resultado.next()){
            lista.add(new Area(resultado.getInt("codigoArea"),
                resultado.getString("nombreArea")));
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaAreas = FXCollections.observableArrayList(lista);
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
        tbnAreas.setDisable(false);
    }
    
    public void activarControles(){
        txtNombre.setEditable(true);
        tbnAreas.setDisable(true);
    }
    
    public void limpiarControles(){
        txtNombre.setText(null);
        tbnAreas.getSelectionModel().clearSelection();
    }
    
    public void guardar(){
        Area registro = new Area();
        registro.setNombreArea(txtNombre.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarArea(?)}");
            procedimiento.setString(1, registro.getNombreArea());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Area buscar(int codigoArea){
        Area resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
            procedimiento.setInt(1,codigoArea);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Area(
                    registro.getInt("codigoArea"),
                    registro.getString("nombreArea"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void actualizar(){
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarArea(?,?)}");
        Area registro = (Area) tbnAreas.getSelectionModel().getSelectedItem();
        registro.setCodigoArea(((Area) tbnAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setNombreArea(txtNombre.getText());
        procedimiento.setInt(1, registro.getCodigoArea());
        procedimiento.setString(2, registro.getNombreArea());
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

