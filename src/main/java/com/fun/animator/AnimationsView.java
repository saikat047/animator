package com.fun.animator;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class AnimationsView extends AbstractAnimatorComponent {
    public AnimationsView() {
        AnimatorInitializer.init(this);
    }

    @Override
    public void createLayout() {
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(new Label("List of Animations"));
        setRootPane(flowPane);
    }
}
