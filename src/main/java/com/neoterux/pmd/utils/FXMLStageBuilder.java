package com.neoterux.pmd.utils;

import com.neoterux.pmd.utils.exceptions.StageBuildException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * <h1>FXML Stage Builder</h1>
 * A Stage builder that wrap the FXML loader operation
 * to fully customize the output of the Scene, Controller.
 * Works Only with {@link Controller} classes.
 *
 * @author Neoterux
 */
public class FXMLStageBuilder extends StageBuilder {
    
    /**
     * The FXML loader for the Scene
     */
    private FXMLLoader loader;
    
    /**
     * The controller of the Scene
     */
    private Object controller;
    
    /**
     * The min width of the
     */
    private SizeWraper minSize;
    private SizeWraper prefSize;
    private SizeWraper maxSize;

    /**
     * Creates a new Instance of a {@link FXMLStageBuilder}
     *
     * @param fxmlLocation the location of the FXML File
     *
     * @throws javafx.fxml.LoadException if something is bad while creating the {@link FXMLLoader}
     * @throws NullPointerException if the given {@literal fxmlLocation} is null
     */
    public FXMLStageBuilder(URL fxmlLocation) {
        this.loader = new FXMLLoader(Objects.requireNonNull(fxmlLocation));
        this.minSize = new SizeWraper();
        this.maxSize = new SizeWraper();
        this.prefSize = new SizeWraper();
    }
    
    /**
     * Creates a new FXMLStageBuilder from the {@literal context} class as the
     * root point of the resource at the specified {@literal  location}.
     *
     * @param context the root point.
     * @param location the relative location at {@literal context} to the FXML.
     */
    public FXMLStageBuilder(Class<?> context, String location) {
        this(context.getResource(location));
    }
    
    /**
     * This is a shortcut for the {@link FXMLLoader#setControllerFactory} method.
     *
     * @param factory the factory passed to the FXML loader
     * @return the FXML Stage Builder
     */
    public FXMLStageBuilder setControllerFactory(Callback<Class<?>, Object> factory){
        this.loader.setControllerFactory(factory);
        return this;
    }
    
    
    /**
     * Pass a specialized Controller to the loader,
     * that would have extra contents.
     * @param controller the Controller that'll be passed
     * @param <T> The type of the Specialized controller
     * @return the {@link FXMLStageBuilder}
     */
    public <T extends Controller> FXMLStageBuilder setController(T controller){
        this.loader.setController(controller);
        this.controller = controller;
        return this;
    }
    
    @Override
    public FXMLStageBuilder setStageConfigurator (Consumer<Stage> configurator) {
        return (FXMLStageBuilder) super.setStageConfigurator(configurator);
    }
    
    public FXMLStageBuilder setMinSize(double width, double height) {
        this.minSize.enabled = true;
        this.minSize.setSize(width,height);
        return this;
    }
    
    public FXMLStageBuilder setPrefSize(double width, double height) {
        this.prefSize.enabled = true;
        this.prefSize.setSize(width,height);
        return this;
    }
    
    public FXMLStageBuilder setMaxSize(double width, double height) {
        this.maxSize.enabled = true;
        this.maxSize.setSize(width,height);
        return this;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public FXMLStageBuilder setImage (String imgUrl) {
        return super.setImage(imgUrl);
    }
    
    
    /**
     * Apply the FXML operation into the inserted Stage.
     *
     * @param stage the Stage where the operation will be applied
     */
    public void applyTo(Stage stage) {
        Stage s = Objects.requireNonNull(stage);
        s.setScene(loadScene());
        applyController(s);
        applySizes(stage);
        if(stageConfigurator != null)
            this.stageConfigurator.accept(stage);
        stage.getIcons().add(getStageImage());
    }
    
    private Scene loadScene() {
        Parent root;
        try {
            root = loader.load();
        }catch (IOException ioe) {
            throw new StageBuildException("Cannot build the Stage", ioe);
        }
        return new Scene(root);
    }
    
    /**
     * Apply special things to the controller of the FXMLLoader if needed
     * @param stage the stage of this Builder
     */
    private void applyController(Stage stage) {
        Object controller = loader.getController();
        if (controller instanceof Controller ctrll){
            stage.setOnCloseRequest(ctrll::onWindowCloseAction);
            stage.setOnHidden(ctrll::onWindowHide);
            stage.setOnShown(ctrll::onWindowShowAction);
            ctrll.window = stage;
            ctrll.afterInstanciate();
        }
    }
    
    /**
     * Apply the setted sizes to the specific Stage.
     *
     * @param stage the stage where the sizes will be set.
     */
    private void applySizes(Stage stage) {
        if(minSize.enabled) {
            stage.setMinHeight(minSize.height);
            stage.setMinWidth(minSize.width);
        }if (prefSize.enabled){
            stage.setWidth(prefSize.width);
            stage.setHeight(prefSize.height);
        } if (maxSize.enabled){
            stage.setMaxWidth(maxSize.width);
            stage.setMaxHeight(maxSize.height);
        }
    }
    
    @Override
    public Stage build () {
        super.setWindowScene(loadScene());
        Stage s = super.build();
        applyController(s);
        applySizes(s);
        return s;
    }
    
    private static class SizeWraper {
        private boolean enabled;
        private double width;
        private double height;
        
        SizeWraper(){
            this.enabled = false;
        }
        public void setSize(double width, double height){
            this.width = width;
            this.height = height;
        }
        public boolean isEnabled(){
            return this.enabled;
        }
    }
}
