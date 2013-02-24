package com.fun.animator.control;

import com.fun.animator.AbstractAnimatorComponent;
import com.fun.animator.AnimatorInitializer;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class ControlsPane extends AbstractAnimatorComponent {

    public ControlsPane() {
        AnimatorInitializer.init(this);
    }

    @Override
    public void createLayout() {
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(new Label("List of Animation Controls"));
        setRootPane(flowPane);
    }
}
