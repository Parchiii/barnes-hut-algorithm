/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import Clases.ParticleConfig;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VentanaConfiguracionControlador implements Initializable {
    
    ParticleConfig configuracion;
    private MainWindowsControlador mainController;
    
    
    @FXML
    TextField inputMass;
    @FXML
    TextField inputCarga;
    @FXML
    private Button botonGuardar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configuracion = new ParticleConfig();
    }
    
    public void setMainController(MainWindowsControlador mainController){
        this.mainController = mainController;
        
        
        if (mainController != null) {
            this.configuracion = mainController.getConfiguracionActual();
            inputCarga.setText(String.valueOf(configuracion.getCarga()));
        }
    }
    
    @FXML
    private void aceptarConfiguracion(ActionEvent event) {
    try {
        double mass = Double.parseDouble(inputMass.getText());
        double carga = Double.parseDouble(inputCarga.getText());
        configuracion.setCarga(carga);
        configuracion.setMass(mass);
        
        if (mainController != null){
            mainController.actualizarConfiguracion(configuracion);
            }
        Stage stage = (Stage) botonGuardar.getScene().getWindow();
        stage.close();
        
        }catch (NumberFormatException e) {
            System.err.println("Error la carga debe ser un numero valido");
        }
    }
}
    
