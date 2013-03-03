package com.fun.animator;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class AnimationView extends AbstractAnimatorComponent {

    public AnimationView() {
        AnimatorInitializer.init(this);
    }

    @Override
    public void createLayout() {
        FlowPane rootPane = new FlowPane();
        rootPane.getChildren().add(new Label("Center Animation Tabbed Pane"));
        setRootUIPane(rootPane);
    }
}
