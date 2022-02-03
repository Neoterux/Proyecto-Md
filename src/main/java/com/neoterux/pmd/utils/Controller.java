package com.neoterux.pmd.utils;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <h1>Controller class</h1>
 * This class holds some of the basic parameters of a window
 * and events to easily implements if necessary.
 *
 * //@param <VM> The ViewModel of the controller
 */
public abstract class Controller implements Initializable {
    
    /**
     * The window of the current Controller
     */
    protected Stage window;
    
    
    /**
     * An event that is fired when the window has a close request
     * @param event the event
     */
    public void onWindowCloseAction(WindowEvent event){}
    
    /**
     * This method is called after this controller is set up by the {@link FXMLStageBuilder}
     */
    protected abstract void afterInstanciate();
    
    @Override
    public void initialize (URL location, ResourceBundle resources) { }
    
    /**
     * An event that is fired when the window is showing.
     * @param event the event
     */
    public void onWindowShowAction(WindowEvent event) {}
    
    /**
     * An event that is fired when the window is hiding.
     * @param event the event
     */
    public void onWindowHide(WindowEvent event) {}
    
}
