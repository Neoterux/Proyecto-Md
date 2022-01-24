package com.neoterux.pmd.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public non-sealed class ImageHolder extends WordHolder<ImageView> {
    
    /**
     * This would create a new ImageHolder from teh coded image,
     *
     * @param codedImage the coded Image
     */
    public ImageHolder (byte[] codedImage) {
        this(new ImageView());
        Image img = new Image(new ByteArrayInputStream(codedImage));
        getGraphic().setImage(img);
    }
    
    protected ImageHolder (ImageView graphic) {
        super(graphic, "image-holder");
    }
    
    @Override
    protected void configure () {
        getGraphic().fitHeightProperty().set(Consts.MIN_HEIGHT - Consts.RECT_HEIGHT);
    }
}
