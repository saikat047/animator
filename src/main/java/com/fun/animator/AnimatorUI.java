package com.fun.animator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AnimatorUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MainLayout layout = new MainLayout();
        Scene scene = new Scene(layout.getRootUIPane());
        stage.setScene(scene);
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.show();
    }

    public static void main(String [] argv) {
        launch(argv);
    }
}
