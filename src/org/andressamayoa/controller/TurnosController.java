package org.andressamayoa.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.andressamayoa.bean.MedicoEspecialidad;
import org.andressamayoa.bean.Paciente;
import org.andressamayoa.bean.ResponsableTurno;
import org.andressamayoa.bean.Turno;
import org.andressamayoa.db.Conexion;
import org.andressamayoa.sistema.Principal;

public class TurnosController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private operaciones tipoOperacion = operaciones.NINGUNO;
    private ObservableList<Turno> listaTurnos;
    private ObservableList<ResponsableTurno> listaResponsable;
    private ObservableList<Paciente> listaPacientes;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private DatePicker fechaCita;
    private DatePicker fechaTurno;
    
    @FXML private TextField txtPrecio;
    @FXML private GridPane grpFechaCita;
    @FXML private GridPane grpFechaTurno;
    @FXML private ComboBox cbmPacientes;
    @FXML private ComboBox cbmResponsableTurno;
    @FXML private ComboBox cbmMedicoEspecialidad;
    @FXML private TableView tbnTurnos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colPrecio;
    @FXML private TableColumn colPaciente;
    @FXML private TableColumn colResponsableTurno;
    @FXML private TableColumn colMedicoEspecialidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public void nuevo(){
        switch(tipoOperacion){
            case NINGUNO:
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                activarControles();
                limpiarControles();
                tipoOperacion = operaciones.NUEVO;
                break;
            case NUEVO:
                if(fechaCita.getSelectedDate()!=null&&fechaTurno.getSelectedDate()!=null
                   &&txtPrecio.getText()!=null&& cbmPacientes.getSelectionModel().getSelectedItem()!=null
                   &&cbmResponsableTurno.getSelectionModel().getSelectedItem()!=null
                   &&cbmMedicoEspecialidad.getSelectionModel().getSelectedItem()!=null){    
                    guardar();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    limpiarControles();
                    desactivarControles();
                    cargar();
                    tipoOperacion = operaciones.NINGUNO;
                }else
                    JOptionPane.showMessageDialog(null, "Llene todos los campos antes");
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoOperacion){
            case NUEVO:
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                limpiarControles();
                desactivarControles();
                tipoOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tbnTurnos.getSelectionModel().getSelectedItem()!=null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Esta seguro de eliminar este registro", "Seguro de eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta==JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTurno(?)}");
                            procedimiento.setInt(1, ((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
                            procedimiento.execute();
                            listaTurnos.remove(tbnTurnos.getSelectionModel().getSelectedIndex());
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else
                    JOptionPane.showMessageDialog(null, "Seleccione un registro primero");
                limpiarControles();
                break;
        }
    }
    
    public void editar(){
        switch(tipoOperacion){
            case NINGUNO:
                if(tbnTurnos.getSelectionModel().getSelectedItem()!=null){
                    btnEditar.setText("Guardar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cbmPacientes.setDisable(true);
                    cbmMedicoEspecialidad.setDisable(true);
                    cbmResponsableTurno.setDisable(true);
                    tipoOperacion = operaciones.EDITAR;
                }else
                    JOptionPane.showMessageDialog(null, "Seleccione un registro primero");
                break;
            case EDITAR:
                actualizar();
                cargar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                limpiarControles();
                desactivarControles();
                tipoOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch(tipoOperacion){
            case EDITAR:
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                limpiarControles();
                desactivarControles();
                tipoOperacion = operaciones.NINGUNO;
        }
    }
    
    public void cargar(){
        tbnTurnos.setItems(getTurnos());
        colCodigo.setCellValueFactory(new PropertyValueFactory <Integer,Turno> ("codigoTurno"));
        colPrecio.setCellValueFactory(new PropertyValueFactory <Float,Turno> ("valorCita"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory <Date,Turno> ("fechaCita"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory <Date,Turno> ("fechaTurno"));
        colPaciente.setCellValueFactory(new PropertyValueFactory <Integer,Turno> ("codigoPaciente"));
        colResponsableTurno.setCellValueFactory(new PropertyValueFactory <Integer,Turno> ("codigoResponsableTurno"));
        colMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory <Integer,Turno> ("codigoMedicoEspecialidad"));
    }
    
    public void seleccionar(){
        if(tbnTurnos.getSelectionModel().getSelectedItem()!=null){
            txtPrecio.setText(String.valueOf(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getValorCita()));
            fechaTurno.setSelectedDate(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getFechaTurno());
            fechaCita.setSelectedDate(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getFechaCita());
            cbmMedicoEspecialidad.getSelectionModel().select(buscarMedicoEspecialidad(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
            cbmPacientes.getSelectionModel().select(buscarPacientes(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
            cbmResponsableTurno.getSelectionModel().select(buscarResponsableTurno(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno()));
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargar();
        fechaCita = new DatePicker(Locale.ENGLISH);
        fechaCita.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaCita.getCalendarView().todayButtonTextProperty().set("Today");
        fechaCita.getCalendarView().setShowWeeks(false);
        fechaCita.getStylesheets().add("/org/andressamayoa/resource/DatePicker.css");
        grpFechaCita.add(fechaCita, 0, 0);
        fechaCita.setDisable(true);
        fechaTurno = new DatePicker(Locale.ENGLISH);
        fechaTurno.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaTurno.getCalendarView().todayButtonTextProperty().set("Today");
        fechaTurno.getCalendarView().setShowWeeks(false);
        fechaTurno.getStylesheets().add("/org/andressamayoa/resource/DatePicker.css");
        grpFechaTurno.add(fechaTurno, 0, 0);
        fechaTurno.setDisable(true);
        cbmPacientes.setItems(getPacientes());
        cbmMedicoEspecialidad.setItems(getMedicoEspecialidad());
        cbmResponsableTurno.setItems(getResponsableTurno());
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
    
    public void guardar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
            procedimiento.setDate(1,  new java.sql.Date(fechaTurno.getSelectedDate().getTime()));
            procedimiento.setDate(2, new java.sql.Date(fechaCita.getSelectedDate().getTime()));
            procedimiento.setFloat(3, Float.valueOf(txtPrecio.getText()));
            procedimiento.setInt(4, ((MedicoEspecialidad) cbmMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
            procedimiento.setInt(5, ((ResponsableTurno) cbmResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
            procedimiento.setInt(6, ((Paciente) cbmPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void actualizar(){
        Turno registro = new Turno();
        registro.setCodigoTurno(((Turno) tbnTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
        registro.setFechaCita(new java.sql.Date(fechaCita.getSelectedDate().getTime()));
        registro.setFechaTurno(new java.sql.Date(fechaTurno.getSelectedDate().getTime()));
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad) cbmMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        registro.setCodigoPaciente(((Paciente) cbmPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setCodigoResponsableTurno(((ResponsableTurno) cbmResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        registro.setValorCita(Float.valueOf(txtPrecio.getText()));
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTurno(?,?,?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, registro.getFechaTurno());
            procedimiento.setDate(3, registro.getFechaCita());
            procedimiento.setFloat(4, registro.getValorCita());
            procedimiento.setInt(5, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(6, registro.getCodigoResponsableTurno());
            procedimiento.setInt(7, registro.getCodigoPaciente());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void activarControles(){
        txtPrecio.setEditable(true);
        fechaCita.setDisable(false);
        fechaTurno.setDisable(false);
        cbmPacientes.setDisable(false);
        cbmMedicoEspecialidad.setDisable(false);
        cbmResponsableTurno.setDisable(false);
        tbnTurnos.setDisable(true);
    }
    
    public void desactivarControles(){
        txtPrecio.setEditable(false);
        fechaCita.setDisable(true);
        fechaTurno.setDisable(true);
        cbmPacientes.setDisable(true);
        cbmMedicoEspecialidad.setDisable(true);
        cbmResponsableTurno.setDisable(true);
        tbnTurnos.setDisable(false);
    }
    
    public void limpiarControles(){
        txtPrecio.setText(null);
        fechaCita.setSelectedDate(null);
        fechaTurno.setSelectedDate(null);
        cbmPacientes.setValue(null);
        cbmMedicoEspecialidad.setValue(null);
        cbmResponsableTurno.setValue(null);
        tbnTurnos.getSelectionModel().clearSelection();
        
    }
    
    public Paciente buscarPacientes(int codigo){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPacientes(?)}");
            procedimiento.setInt(1, codigo);
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
    
    public MedicoEspecialidad buscarMedicoEspecialidad(int codigo){
        MedicoEspecialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedicoEspecialidad(?)}");
            procedimiento.setInt(1, codigo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new MedicoEspecialidad(
                        registro.getInt("codigoMedicoEspecialidad"),
                        registro.getInt("codigoMedico"),
                        registro.getInt("codigoEspecialidad"),
                        registro.getInt("codigoHorario")
                );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ResponsableTurno buscarResponsableTurno(int codigo){
        ResponsableTurno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ResponsableTurno(registro.getInt("codigoResponsableTurno"),
                                                 registro.getString("nombreResponsable"),
                                                 registro.getString("telefonoResponsable"),
                                                 registro.getInt("codigoArea"),
                                                 registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ObservableList getTurnos(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTurno()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
            lista.add(new Turno(resultado.getInt("codigoTurno"), resultado.getDate("fechaTurno"),resultado.getDate("fechaCita"),
                                resultado.getFloat("valorCita"), resultado.getInt("codigoMedicoEspecialidad"), resultado.getInt("codigoResponsableTurno"),
                                resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTurnos = FXCollections.observableList(lista);
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

    public void validarNumeros1(KeyEvent KeyEvent){
        char key = KeyEvent.getCharacter().charAt(0);
        try{
            if(Character.isLetter(key)){
                KeyEvent.consume();
            }else 
            if(txtPrecio.getText().length()>=9){
                    KeyEvent.consume();
                }
        }catch(Exception e){
            //e.printStackTrace();
        }
    }
        
}
