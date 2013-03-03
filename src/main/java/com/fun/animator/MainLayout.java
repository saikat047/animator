package com.fun.animator;

import com.fun.animator.control.ControlViewHolder;
import com.fun.animator.control.ControlsPane;
import com.fun.animator.toolbar.AnimatorToolBar;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

public class MainLayout extends AbstractAnimatorComponent {

    private AnimationView animationView;
    private ControlsPane controlsPanel;
    private ControlViewHolder controlViewPanel;
    private AnimationsView animationsView;
    private AnimatorToolBar toolBar;

    public MainLayout() {
        AnimatorInitializer.init(this);
    }

    @Override
    public void createComponents() {
        toolBar = createToolBar();
        animationView = createAnimationView();
        controlsPanel = createControlsPanel();
        controlViewPanel = createControlViewPanel();
        animationsView = createAnimationsView();
    }

    @Override
    public void createLayout() {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(toolBar.getToolBar());
        SplitPane mainPanel = new SplitPane();
        mainPanel.setOrientation(Orientation.HORIZONTAL);
        mainPanel.getItems().add(createLeftPanel());
        mainPanel.getItems().add(animationView.getRootUIPane());
        mainPanel.getItems().add(animationsView.getRootUIPane());
        mainPanel.setDividerPositions(0.2, 0.8);
        borderPane.setCenter(mainPanel);
        setRootUIPane(borderPane);
    }

    @Override
    public void registerHandlers() {
    }

    private AnimationsView createAnimationsView() {
        return new AnimationsView();
    }

    private AnimationView createAnimationView() {
        return new AnimationView();
    }

    private Node createLeftPanel() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().add(controlsPanel.getRootUIPane());
        splitPane.getItems().add(controlViewPanel.getRootUIPane());
        return splitPane;
    }

    private ControlViewHolder createControlViewPanel() {
        return new ControlViewHolder();
    }

    private ControlsPane createControlsPanel() {
        return new ControlsPane();
    }

    private AnimatorToolBar createToolBar() {
        return new AnimatorToolBar();
    }
}
