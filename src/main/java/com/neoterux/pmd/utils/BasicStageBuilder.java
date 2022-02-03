package com.neoterux.pmd.utils;

import com.neoterux.pmd.utils.exceptions.StageBuildException;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class BasicStageBuilder extends StageBuilder{
    
    
    /**
     * Set a new Scene Factory to the Stage Builder, without this
     * cannot build a new Window.
     *
     * @param sceneFactory The factory object that return the Scene.
     * @return the Stage Builder
     */
    public BasicStageBuilder setSceneBuilder (Supplier<Scene> sceneFactory){
        Scene targetScene = requireNonNull(sceneFactory).get();
        if (targetScene == null)
            throw new StageBuildException("supplied Scene must not be null");
        this.windowScene = targetScene;
        return this;
    }
    
    /**
     * Execute a Scene Configurator with the result of the method
     * {@link BasicStageBuilder#setSceneBuilder}
     *
     * @param configurator The Scene configurator
     * @return the Stage Builder
     */
    public BasicStageBuilder sceneConfigurator (Consumer<Scene> configurator) {
        requireNonNull(configurator).accept(this.windowScene);
        return this;
    }
    
    /**
     * Set a new Stage Configurator that is executed before build.
     *
     * @param configurator the configurator of the stage
     * @return the Stage Build
     */
    @Override
    public BasicStageBuilder setStageConfigurator(Consumer<Stage> configurator){
        return (BasicStageBuilder) super.setStageConfigurator(configurator);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public BasicStageBuilder setImage (String imgUrl) {
        return super.setImage(imgUrl);
    }
    
   
}
