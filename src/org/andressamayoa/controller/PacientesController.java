package org.andressamayoa.controller;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.Paciente;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.andressamayoa.report.GenerarReporte;

public class PacientesController implements Initializable{
    private final String imagen = "/org/andressamayoa/images/";
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPacientes;
    private DatePicker fecha;
    
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDpi;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtSexo;
    @FXML private GridPane grpFecha;
    @FXML private TableView tbnPacientes;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colDpi;
    @FXML private TableColumn colFechaDeNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                    tbnPacientes.getSelectionModel().clearSelection();
                    limpiarControles();
                    activarControles();
                    btnNuevo.setText("Guardar");
                    btnEliminar.setText("Cancelar");
                    btnEditar.setDisable(true);
                    btnReporte.setDisable(true);
                    tipoOperacion = operaciones.GUARDAR;
                    break;
            case GUARDAR:
                if(txtNombre.getText()!=null&&txtApellido.getText()!=null&&txtOcupacion.getText()!=null&&txtDireccion.getText()!=null&&txtSexo!=null&&fecha.getSelectedDate()!=null){
                if(txtDpi.getText()==null)
                txtDpi.setText("N/A");                
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoOperacion = operaciones.NINGUNO;
                cargarDatos();
                llamarContactoUrgencias();
                JOptionPane.showMessageDialog(null, "Ingrese un contacto para el Paciente");
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
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de borrar el registro?","Eliminar paciente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){    
                        try{
                        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPacientes(?)}");
                        procedimiento.setInt(1, ((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                        procedimiento.execute();
                        listaPacientes.remove(tbnPacientes.getSelectionModel().getSelectedIndex());
                        limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        tbnPacientes.getSelectionModel().clearSelection();
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
                if(tbnPacientes.getSelectionModel().getSelectedItem()!=null){
                    activarControles();
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro antes");
                break;
            case ACTUALIZAR:
                actualizar();
                desactivarControles();
                limpiarControles();
                cargarDatos();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tbnPacientes.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tbnPacientes.getSelectionModel().clearSelection();
                tipoOperacion = operaciones.NINGUNO;
                break;
            case NINGUNO:
                ImprimirReporte();
                break;
        }
        
    }
    
    public void cargarDatos(){
        tbnPacientes.setItems(getPacientes());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("codigoPaciente"));
        colDpi.setCellValueFactory(new PropertyValueFactory<Paciente,String>("dpi"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente,String>("apellidos"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("direccion"));
        colFechaDeNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente,Date>("fechaDeNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("edad"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente,String>("sexo"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("ocupacion"));
    }   
    
    public void seleccionarDatos(){
        if(tbnPacientes.getSelectionModel().getSelectedItem()!=null){
            txtNombre.setText(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getNombres());
            txtApellido.setText(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getApellidos());
            txtDpi.setText(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getDpi());
            txtDireccion.setText(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getDireccion());
            txtOcupacion.setText(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getOcupacion());
            txtSexo.setText(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getSexo());
            fecha.setSelectedDate(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getFechaDeNacimiento());
        }
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/andressamayoa/resource/DatePicker.css");
        grpFecha.add(fecha, 0, 0);
        fecha.setDisable(true);
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
     
     public void llamarContactoUrgencias(){
        escenarioPrincipal.contactoUrgencias();
    }
     
     public void activarControles(){
         txtNombre.setEditable(true);
         txtApellido.setEditable(true);
         txtDpi.setEditable(true);
         txtDireccion.setEditable(true);
         txtOcupacion.setEditable(true);
         txtSexo.setEditable(true);
         fecha.setDisable(false);
         tbnPacientes.setDisable(true);
     }
     
     public void desactivarControles(){
        txtNombre.setEditable(false);
        txtApellido.setEditable(false);
        txtDpi.setEditable(false);
        txtOcupacion.setEditable(false);
        txtDireccion.setEditable(false);
        txtSexo.setEditable(false);
        fecha.setDisable(true);
        tbnPacientes.setDisable(false);
     }
     
     public void limpiarControles(){
        txtNombre.setText(null);
        txtApellido.setText(null);
        txtDpi.setText(null);
        txtOcupacion.setText(null);
        txtDireccion.setText(null);
        txtSexo.setText(null);
        fecha.setSelectedDate(null);
        tbnPacientes.getSelectionModel().clearSelection();
    }
     
     public void guardar(){
         Paciente registro = new Paciente();
         registro.setNombres(txtNombre.getText());
         registro.setApellidos(txtApellido.getText());
         registro.setDpi(txtDpi.getText());
         registro.setDireccion(txtDireccion.getText());
         registro.setOcupacion(txtOcupacion.getText());
         registro.setSexo(txtSexo.getText());
         registro.setFechaDeNacimiento(fecha.getSelectedDate());
         try{
             PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPacientes(?,?,?,?,?,?,?)}");
             procedimiento.setString(1, registro.getDpi());
             procedimiento.setString(2, registro.getNombres());
             procedimiento.setString(3, registro.getApellidos());
             procedimiento.setDate(4, new java.sql.Date(registro.getFechaDeNacimiento().getTime()));
             procedimiento.setString(5, registro.getDireccion());
             procedimiento.setString(6, registro.getOcupacion());
             procedimiento.setString(7, registro.getSexo());
             procedimiento.execute();
         }catch(Exception e){
             e.printStackTrace();
         }
     }

     public void ImprimirReporte(){
         Map parametros = new HashMap();
         parametros.put("ReportePacientes.jasper", null);
         parametros.put("Ruta", this.getClass().getResourceAsStream(imagen+"logo temporal.jpg"));
         GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de pacientes", parametros);
     }
     
     public void actualizar(){
         try{
             PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarPacientes(?,?,?,?,?,?,?,?)}");
             Paciente registro = (Paciente) tbnPacientes.getSelectionModel().getSelectedItem();
             registro.setCodigoPaciente(((Paciente) tbnPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
             registro.setDpi(txtDpi.getText());
             registro.setNombres(txtNombre.getText());
             registro.setApellidos(txtApellido.getText());
             registro.setDireccion(txtDireccion.getText());
             registro.setOcupacion(txtOcupacion.getText());
             registro.setSexo(txtSexo.getText());
             registro.setFechaDeNacimiento(fecha.getSelectedDate());
             procedimiento.setInt(1, registro.getCodigoPaciente());
             procedimiento.setString(2, registro.getDpi());
             procedimiento.setString(3, registro.getNombres());
             procedimiento.setString(4, registro.getApellidos());
             procedimiento.setDate(5,new java.sql.Date(registro.getFechaDeNacimiento().getTime()));
             procedimiento.setString(6, registro.getDireccion());
             procedimiento.setString(7, registro.getOcupacion());
             procedimiento.setString(8, registro.getSexo());
             procedimiento.execute();
             
         }catch(Exception e){
             e.printStackTrace();
         }
     }

      public void validarNumeros(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isDigit(key)){
                if(txtDpi.getText().length()==13){
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
          }
      }
}
