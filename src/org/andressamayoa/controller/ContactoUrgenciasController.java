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
import org.andressamayoa.bean.ContactoUrgencia;
import org.andressamayoa.bean.Paciente;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class ContactoUrgenciasController implements Initializable{
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    private ObservableList<ContactoUrgencia> listaContactoUrgencia;
    private ObservableList<Paciente> listaPacientes;
   
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumero;
    @FXML private ComboBox cbmCodigoPaciente;
    @FXML private TableView tbnContactosUrgencias;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colPaciente;
    @FXML private TableColumn colNumero;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                tbnContactosUrgencias.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtNombres.getText()!=null&&txtApellidos!=null&&txtNumero!=null&&cbmCodigoPaciente.getSelectionModel().getSelectedItem()!=null){
                guardar();
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                cargarDatos();
                tipoOperacion = operaciones.NINGUNO;
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
                tbnContactosUrgencias.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnContactosUrgencias.getSelectionModel().getSelectedItem()!=null){
                    int respuesta;
                    respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de borrar este registro", "Seguro de eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia()?}");
                            procedimiento.setInt(1, ((ContactoUrgencia) tbnContactosUrgencias.getSelectionModel().getSelectedItem()).getCodigoContacto());
                            listaContactoUrgencia.remove(tbnContactosUrgencias.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tbnContactosUrgencias.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tbnContactosUrgencias.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro primero");
                }
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnContactosUrgencias.getSelectionModel().getSelectedItem()!=null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    cbmCodigoPaciente.setDisable(true);
                    tipoOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un registro primero");
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
                desactivarControles();
                tbnContactosUrgencias.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void cargarDatos(){
        tbnContactosUrgencias.setItems(getContactosUrgencias());
        colCodigo.setCellValueFactory(new PropertyValueFactory <ContactoUrgencia,Integer>("codigoContacto"));
        colNombres.setCellValueFactory(new PropertyValueFactory <ContactoUrgencia,String>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory <ContactoUrgencia,String>("apellido"));
        colNumero.setCellValueFactory(new PropertyValueFactory <ContactoUrgencia,String>("numero"));
        colPaciente.setCellValueFactory(new PropertyValueFactory <ContactoUrgencia,Integer>("codigoPaciente"));
    }
    
    public void seleccionar(){
        if(tbnContactosUrgencias.getSelectionModel().getSelectedItem()!=null){
            txtNombres.setText(((ContactoUrgencia) tbnContactosUrgencias.getSelectionModel().getSelectedItem()).getNombre());
            txtApellidos.setText(((ContactoUrgencia) tbnContactosUrgencias.getSelectionModel().getSelectedItem()).getApellido());
            txtNumero.setText(((ContactoUrgencia) tbnContactosUrgencias.getSelectionModel().getSelectedItem()).getNumero());
            cbmCodigoPaciente.getSelectionModel().select(buscarPacientes(((ContactoUrgencia) tbnContactosUrgencias.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cbmCodigoPaciente.setItems(getPacientes());
    }

    public ContactoUrgenciasController() {
    }
    
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void llamarPacientes(){
        escenarioPrincipal.pacientes();
    }
    
    public void limpiarControles(){
         txtNombres.setText(null);
         txtApellidos.setText(null);
         txtNumero.setText(null);   
         cbmCodigoPaciente.setValue(null);
         tbnContactosUrgencias.getSelectionModel().clearSelection();
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumero.setEditable(false);
        cbmCodigoPaciente.setDisable(true);
        tbnContactosUrgencias.setDisable(false);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumero.setEditable(true);
        cbmCodigoPaciente.setDisable(false);
        tbnContactosUrgencias.setDisable(true);
    }
    
    public void guardar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
            procedimiento.setInt(1, ((Paciente) cbmCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            procedimiento.setString(2, txtNombres.getText());
            procedimiento.setString(3, txtApellidos.getText());
            procedimiento.setString(4, txtNumero.getText());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ObservableList getContactosUrgencias(){
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarContactoUrgencia()}");
                ResultSet resultado = procedimiento.executeQuery();
                while(resultado.next()){
                    lista.add(new ContactoUrgencia(resultado.getInt("codigoContacto"),
                                                   resultado.getInt("codigoPaciente"),
                                                   resultado.getString("nombre"),
                                                   resultado.getString("apellido"),
                                                   resultado.getString("numeroContacto")
                                    
                    ));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaContactoUrgencia = FXCollections.observableList(lista);
    }
    
    public ObservableList getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                        resultado.getString("dpi"),
                        resultado.getString("nombres"),
                        resultado.getString("apellidos"),
                        resultado.getDate("fechaDeNacimiento"),
                        resultado.getInt("edad"),
                        resultado.getString("direccion"),
                        resultado.getString("ocupacion"),
                        resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPacientes = FXCollections.observableList(lista);
    }

    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarContactoUrgencia(?,?,?,?,?)}");
            ContactoUrgencia registro = new ContactoUrgencia();
            registro.setCodigoContacto(((ContactoUrgencia)tbnContactosUrgencias.getSelectionModel().getSelectedItem()).getCodigoContacto());
            registro.setCodigoPaciente(((Paciente) cbmCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            registro.setNombre(txtNombres.getText());
            registro.setApellido(txtApellidos.getText());
            registro.setNumero(txtNumero.getText());
            procedimiento.setInt(1, registro.getCodigoContacto());
            procedimiento.setInt(2, registro.getCodigoPaciente());
            procedimiento.setString(3, registro.getNombre());
            procedimiento.setString(4, registro.getApellido());
            procedimiento.setString(5, registro.getNumero());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Paciente buscarPacientes(int codigoMedico){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPacientes(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente(
                        registro.getInt("codigoPaciente"),
                        registro.getString("dpi"),
                        registro.getString("nombres"),
                        registro.getString("apellidos"),
                        registro.getDate("fechaDeNacimiento"),
                        registro.getInt("edad"),
                        registro.getString("direccion"),
                        registro.getString("ocupacion"),
                        registro.getString("sexo")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void validarNumeros(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isDigit(key)){
                if(txtNumero.getText().length()==8){
                    KeyEvent.consume();
                }
            }else 
                KeyEvent.consume();
            
        }catch(Exception e){
            //e.printStackTrace();
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
