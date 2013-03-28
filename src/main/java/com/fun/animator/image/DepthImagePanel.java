package com.fun.animator.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DepthImagePanel extends ImagePanel {

    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();

    public DepthImagePanel(String purpose, Color color) {
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
