package com.fun.animator.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundImagePanel extends ImagePanel {

    private StabilizedDepthImage backgroundStabilizerDepthImage = new StabilizedDepthImage();
    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();

    public BackgroundImagePanel(String purpose, Color color) {
        super(purpose, color);
        backgroundStabilizerDepthImage = new StabilizedDepthImage();
    }

    @Override
    protected BufferedImage getColorImage() {
        if (combinedImage == null) {
            return null;
        }
        backgroundStabilizerDepthImage.updateUnreadablePixels(combinedImage.getDepthImage());
        return depthImageTransformer.createColorImage(backgroundStabilizerDepthImage);
    }
}
