package com.neoterux.pmd.utils;

import com.neoterux.pmd.Launcher;
import com.neoterux.pmd.utils.exceptions.StageBuildException;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * <h1>Stage Builder class</h1>
 * This class let the user build a Stage without a lot of verbosity.
 *
 * @author Neoterux
 */
public abstract class StageBuilder {
    
    /**
     * The image of the current application
     */
    protected static Image ICON;
    
    /**
     * The Target Scene of the Stage
     */
    protected Scene windowScene;
    
    /**
     * The image of the target Stage, by default is the {@link StageBuilder#ICON}.
     */
    private Image stageImage = ICON;
    
    protected Consumer<Stage> stageConfigurator;
    
    protected StageBuilder(){}
    
    static {
        try {
            ICON = new Image(requireNonNull(Launcher.class.getResource("icon/icon_32.png")).toExternalForm());
        }catch (NullPointerException ioe) {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Retrieves the Icon of the current Stage.
     * @return the Image
     */
    protected Image getStageImage() {
        return this.stageImage;
    }
    
    /**
     * Set a new image to the target Stage.
     *
     * @param imgUrl The url of the Image
     * @return the Stage Builder
     */
    @SuppressWarnings("unchecked")
    public <T extends StageBuilder> T setImage(String imgUrl){
        this.stageImage = new Image(imgUrl);
        return (T) this;
    }
    
    public StageBuilder setStageConfigurator (Consumer<Stage> configurator){
        this.stageConfigurator = configurator;
        return this;
    }
    
    /**
     * Set the scene that would be used in the Builder.
     * @param windowScene the window Scene
     */
    public void setWindowScene (Scene windowScene) {
        this.windowScene = windowScene;
    }
    
    /**
     * Build a new Stage with the previous configurations created.
     *
     * @return The Final Stage
     * @throws com.neoterux.pmd.utils.exceptions.StageBuildException if something go bad while build
     */
    public Stage build (){
        if (this.windowScene == null)
            throw new StageBuildException("Scene is not set");
        Stage stage = new Stage();
        stage.setScene(this.windowScene);
        if (stageConfigurator != null)
            stageConfigurator.accept(stage);
        stage.getIcons().add(this.stageImage);
        return stage;
    }
}
