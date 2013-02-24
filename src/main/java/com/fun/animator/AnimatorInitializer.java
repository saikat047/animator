package com.fun.animator;

public class AnimatorInitializer {

    public static void init(AnimatorComponent component) {
        component.createComponents();
        component.createLayout();
        component.registerHandlers();
        component.initialize();
    }
}
