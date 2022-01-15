package com.neoterux.pmd.utils;

import com.neoterux.pmd.Launcher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public final class JfxUtils {
    private JfxUtils() {
    }

    public static Parent loadFxml(String name) throws IOException, NullPointerException {
        Parent p;
        p = FXMLLoader.load(Objects.
                requireNonNull(Launcher.class.getResource(name + ".fxml")));
        return p;
    }
}
