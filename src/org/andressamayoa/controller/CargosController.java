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
import org.andressamayoa.bean.Cargo;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class CargosController implements Initializable{
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Cargo> listaCargo;
    
    @FXML private TextField txtNombre;
    @FXML private TableView tbnCargos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombre;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
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
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tbnCargos.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnCargos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de borrar este registro?", "Eliminar cargo", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
                            procedimiento.setInt(1, ((Cargo) tbnCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
                            procedimiento.execute();
                            listaCargo.remove(tbnCargos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                        tbnCargos.getSelectionModel().clearSelection();
                        limpiarControles();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro");
                }
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnCargos.getSelectionModel().getSelectedItem()!=null){
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
                tbnCargos.getSelectionModel().clearSelection();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                tbnCargos.getSelectionModel().clearSelection();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void seleccionarElemento(){
        if(tbnCargos.getSelectionModel().getSelectedItem()!=null)
        txtNombre.setText(((Cargo) tbnCargos.getSelectionModel().getSelectedItem()).getNombreCargo());
    }
    
    public void cargarDatos(){
        tbnCargos.setItems(getCargos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Cargo,Integer>("codigoCargo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Cargo,String>("nombreCargo"));
    }
    
    public ObservableList getCargos(){
        ArrayList<Cargo> lista = new ArrayList<Cargo>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargo()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cargo(resultado.getInt("codigoCargo"),
                    resultado.getString("nombreCargo")));
            }
        }catch(Exception e){
        
        }
        return listaCargo = FXCollections.observableArrayList(lista);
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
        tbnCargos.setDisable(false);
    }
    
    public void activarControles(){
        txtNombre.setEditable(true);
        tbnCargos.setDisable(true);
    }
    
    public void limpiarControles(){
        txtNombre.setText(null);
        tbnCargos.getSelectionModel().clearSelection();
    }
    
    public void guardar(){
        Cargo registro = new Cargo();
        registro.setNombreCargo(txtNombre.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
            procedimiento.setString(1, registro.getNombreCargo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Cargo buscar(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Cargo(
                        registro.getInt("codigoCargo"),
                        registro.getString("nombreCargo")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }

    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarCargo(?,?)}");
            Cargo registro = (Cargo) tbnCargos.getSelectionModel().getSelectedItem();
            registro.setCodigoCargo(((Cargo) tbnCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
            registro.setNombreCargo(txtNombre.getText());
            procedimiento.setInt(1, registro.getCodigoCargo());
            procedimiento.setString(2, registro.getNombreCargo());
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
