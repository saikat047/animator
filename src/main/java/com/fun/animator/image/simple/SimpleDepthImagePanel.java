package com.fun.animator.image.simple;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.fun.animator.image.DefaultDepthImageTransformers;
import com.fun.animator.image.DepthImageTransformer;

public class SimpleDepthImagePanel extends SimpleImagePanel {

    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();

    public SimpleDepthImagePanel(String purpose, Color color) {
        super(purpose, color);
    }

    @Override
    protected BufferedImage getColorImage() {
        if (combinedImage == null) {
            return null;
        }
        return depthImageTransformer.createColorImage(combinedImage.getDepthImage());
    }
}
