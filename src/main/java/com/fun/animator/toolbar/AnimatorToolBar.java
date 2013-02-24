package com.fun.animator.toolbar;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

public class AnimatorToolBar {

    private final ToolBar toolBar;

    public AnimatorToolBar() {
        toolBar = new ToolBar();
        toolBar.getItems().add(new Button("Open"));
    }

    public ToolBar getToolBar() {
        return toolBar;
    }
}
