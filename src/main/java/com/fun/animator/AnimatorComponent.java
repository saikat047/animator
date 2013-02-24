package com.fun.animator;


public interface AnimatorComponent {

    public void createComponents();

    public void createLayout();

    public void registerHandlers();

    public void initialize();

    public javafx.scene.Parent getRootPane();
}
