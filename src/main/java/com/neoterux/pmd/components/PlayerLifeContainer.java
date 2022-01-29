package com.neoterux.pmd.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public final class PlayerLifeContainer extends TitledPane {
    
    public static final int HEART_COUNT = 5;
    
    private static final String SHAPE = "M23.6,0c-3.4,0-6.3,2.7-7.6,5.6C14.7,2.7,11.8,0,8.4,0C3.8,0,0,3.8,0,8.4c0,9" +
                                        ".4,9.5,11.9,16,21.2" +
                                        " c6.1-9.3,16-12.1,16-21.2C32,3.8,28.2,0,23.6,0z";
    
    private final FlowPane lifeContainer;
    
    private final Deque<Shape> availableHearts;
    
    private final Deque<Shape> damagedHearts;
    
    private final IntegerProperty lifeCount;
    
    private Consumer<Integer> onDamageListener;
    
    public PlayerLifeContainer (String name) {
        this.lifeCount = new SimpleIntegerProperty(HEART_COUNT);
        this.lifeContainer = new FlowPane();
        this.availableHearts = new ArrayDeque<>(HEART_COUNT);
        this.damagedHearts = new ArrayDeque<>(HEART_COUNT);
        this.setExpanded(true);
        this.setCollapsible(false);
        this.setText(name);
        this.lifeContainer.setHgap(10);
        this.setContent(lifeContainer);
        fillHearts();
    }
    
    private void fillHearts () {
        for (int i = 0; i < HEART_COUNT; i++) {
            SVGPath heart = new SVGPath();
            heart.setContent(SHAPE);
            heart.setFill(Color.CRIMSON);
            this.availableHearts.add(heart);
            this.lifeContainer.getChildren().add(heart);
        }
    }
    
    public int getLifeCount () {
        return lifeCount.get();
    }
    
    /**
     * Set a new Damage listener that receive the new life after damage.
     *
     * @param listener the listener to set.
     */
    public void setOnDamageListener (Consumer<Integer> listener) {
        this.onDamageListener = listener;
    }
    
    private int getHeartCount () {
        return this.availableHearts.size();
    }
    
    public IntegerProperty lifeCountProperty () {
        return this.lifeCount;
    }
    
    /**
     * Reduce the life into the container, and call the damage Listener.
     *
     * @return true if damage can be made.
     */
    @SuppressWarnings ("all")
    public boolean makeDamage () {
        if (!this.availableHearts.isEmpty()) {
            lifeCount.set(lifeCount.get() - 1);
            if (onDamageListener != null)
                onDamageListener.accept(lifeCount.get());
            Shape heart = availableHearts.pollLast();
            heart.setFill(Color.GRAY);
            damagedHearts.add(heart);
            return true;
        }
        return false;
    }
    
    public FlowPane getLifeContainer () {
        return this.lifeContainer;
    }
    
    public void reset () {
        while (!damagedHearts.isEmpty()) {
            Shape s = damagedHearts.poll();
            s.setFill(Color.RED);
            availableHearts.add(s);
        }
        lifeCount.set(HEART_COUNT);
    }
}
