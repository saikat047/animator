package com.fun.animator.control;

import com.fun.animator.AbstractAnimatorComponent;
import com.fun.animator.AnimatorInitializer;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class ControlViewHolder extends AbstractAnimatorComponent {
    public ControlViewHolder() {
        AnimatorInitializer.init(this);
    }

    @Override
    public void createLayout() {
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(new Label("View for selected Control"));
        setRootPane(flowPane);
    }
}
