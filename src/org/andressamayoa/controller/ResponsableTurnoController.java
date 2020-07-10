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
import org.andressamayoa.bean.Area;
import org.andressamayoa.bean.Cargo;
import org.andressamayoa.bean.ResponsableTurno;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class ResponsableTurnoController implements Initializable{
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaResponsable;
    private ObservableList<Cargo> listaCargo;
    private ObservableList<Area> listaAreas;
    
    
    @FXML TextField txtNombres;
    @FXML TextField txtTelefono;
    @FXML ComboBox cbmAreas;
    @FXML ComboBox cbmCargos;
    @FXML TableView tbnResponsablesTurnos;
    @FXML TableColumn colCodigo;
    @FXML TableColumn colNombre;
    @FXML TableColumn colTelefono;
    @FXML TableColumn colArea;
    @FXML TableColumn colCargo;
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
                tipoOperacion = operaciones.NUEVO;
                break;
            case NUEVO:
                if(txtNombres.getText()!=null&&txtTelefono.getText()!=null&&cbmAreas.getSelectionModel().getSelectedItem()!=null&&cbmCargos.getSelectionModel().getSelectedItem()!=null){
                    guardar();
                    limpiarControles();
                    desactivarControles();
                    cargarDatos();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoOperacion = operaciones.NINGUNO;
                }else
                    JOptionPane.showMessageDialog(null, "Llene todos los campos primero");
        }
    }
    
    public void eliminar(){
        switch(tipoOperacion){
            case NUEVO:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnResponsablesTurnos.getSelectionModel().getSelectedItem()!=null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Seguro de eliminar este registro?", "Seguro de eliminar?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
                            procedimiento.setInt(1, ((ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                            procedimiento.execute();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        tbnResponsablesTurnos.getSelectionModel().clearSelection();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un registro antes");
                    limpiarControles();
                    tbnResponsablesTurnos.getSelectionModel().clearSelection();
                }                    
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnResponsablesTurnos.getSelectionModel().getSelectedItem()!=null){
                    activarControles();
                    cbmAreas.setDisable(true);
                    cbmCargos.setDisable(true);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    tipoOperacion = operaciones.EDITAR;
                }else
                    JOptionPane.showMessageDialog(null, "Seleccione un registro primero");
                break;
            case EDITAR:
                actualizar();
                cargarDatos();
                desactivarControles();
                limpiarControles();
                tbnResponsablesTurnos.getSelectionModel().clearSelection();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case EDITAR:
                desactivarControles();
                limpiarControles();
                tbnResponsablesTurnos.getSelectionModel().clearSelection();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void seleccionar(){
        if(tbnResponsablesTurnos.getSelectionModel().getSelectedItem()!=null){
            txtNombres.setText(((ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem()).getNombreResponsable());
            txtTelefono.setText(((ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem()).getTelefonoResponsable());
            cbmAreas.getSelectionModel().select(buscarArea(((ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoArea()));
            cbmCargos.getSelectionModel().select(buscarCargo(((ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoCargo()));
        }
    }
    
    public void cargarDatos(){
        tbnResponsablesTurnos.setItems(getResponsableTurno());
        colCodigo.setCellValueFactory(new PropertyValueFactory <ResponsableTurno,Integer> ("codigoResponsableTurno"));
        colNombre.setCellValueFactory(new PropertyValueFactory <ResponsableTurno,String> ("nombreResponsable"));
        colTelefono.setCellValueFactory(new PropertyValueFactory <ResponsableTurno,String> ("telefonoResponsable"));
        colArea.setCellValueFactory(new PropertyValueFactory <ResponsableTurno,Integer> ("codigoArea"));
        colCargo.setCellValueFactory(new PropertyValueFactory <ResponsableTurno,Integer> ("codigoCargo"));
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cbmAreas.setItems(getAreas());
        cbmCargos.setItems(getCargos());
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
        txtNombres.setText(null);
        txtTelefono.setText(null);
        cbmAreas.setValue(null);
        cbmCargos.setValue(null);
        tbnResponsablesTurnos.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtTelefono.setEditable(true);
        cbmAreas.setDisable(false);
        cbmCargos.setDisable(false);
        tbnResponsablesTurnos.setDisable(true);
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtTelefono.setEditable(false);
        cbmAreas.setDisable(true);
        cbmCargos.setDisable(true);
        tbnResponsablesTurnos.setDisable(false);
    }
    
    public void guardar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno(?,?,?,?)}");
            procedimiento.setString(1, txtNombres.getText());
            procedimiento.setString(2, txtTelefono.getText());
            procedimiento.setInt(3, ((Area) cbmAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
            procedimiento.setInt(4, ((Cargo) cbmCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void actualizar(){
        ResponsableTurno registro = (ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarResponsableTurno(?,?,?,?,?)}");
            registro.setCodigoArea(((ResponsableTurno) tbnResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
            registro.setNombreResponsable(txtNombres.getText());
            registro.setTelefonoResponsable(txtTelefono.getText());
            registro.setCodigoArea(((Area) cbmAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
            registro.setCodigoCargo(((Cargo) cbmCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombreResponsable());
            procedimiento.setString(3, registro.getTelefonoResponsable());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ObservableList getResponsableTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsableTurno()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                                                resultado.getString("nombreResponsable"),
                                                resultado.getString("telefonoResponsable"),
                                                resultado.getInt("codigoArea"),
                                                resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaResponsable = FXCollections.observableArrayList(lista);
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

    public Area buscarArea(int codigoArea){
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
    
    public Cargo buscarCargo(int codigoCargo){
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

    public void validarLetras(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
            if(!Character.isLetter(key))
                KeyEvent.consume();
    }
    
    public void validarNumeros(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isDigit(key)){
                if(txtTelefono.getText().length()==8)
                    KeyEvent.consume();
            }
        }catch(Exception e){}
    }
}
