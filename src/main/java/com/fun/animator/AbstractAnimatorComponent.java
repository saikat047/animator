package com.fun.animator;

import javafx.scene.layout.Pane;

public abstract class AbstractAnimatorComponent implements AnimatorComponent {
    private Pane rootPane;

    public final Pane getRootUIPane() {
        if (rootPane == null) {
            throw new IllegalStateException("No RootPane set");
        }
        return rootPane;
    }

    public final void setRootUIPane(Pane rootPane) {
        this.rootPane = rootPane;
    }

    @Override
    public void createComponents() {
    }

    @Override
    public void createLayout() {
    }

    @Override
    public void registerHandlers() {
    }

    @Override
    public void initialize() {
    }
}
