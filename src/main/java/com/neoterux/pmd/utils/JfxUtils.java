package com.neoterux.pmd.utils;

import com.neoterux.pmd.Launcher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public final class JfxUtils {
    private JfxUtils () {
    }
    
    public static Parent loadFxml (String name) throws IOException, NullPointerException {
        return getLoaderOf(name + ".fxml").load();
    }
    
    public static FXMLLoader getLoaderOf (String fxml) throws IOException, NullPointerException {
        return new FXMLLoader(Objects.requireNonNull(Launcher.class.getResource(fxml)));
    }
}
