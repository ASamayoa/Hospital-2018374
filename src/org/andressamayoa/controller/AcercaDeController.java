package org.andressamayoa.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.andressamayoa.sistema.Principal;

public class AcercaDeController implements Initializable{
   
    private static Principal acercaDe;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public static Principal getAcercaDe() {
        return acercaDe;
    }

    public static void setAcercaDe(Principal acercaDe) {
        AcercaDeController.acercaDe = acercaDe;
    }
    
    public void llamarMenuPrincipal()   {
        acercaDe.menuPrincipal();
    }
    
}