package com.fun.animator.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundImagePanel extends ImagePanel {

    private StabilizedDepthImage backgroundStabilizerDepthImage = new StabilizedDepthImage();
    private DepthImageTransformer depthImageTransformer = new DefaultDepthImageTransformers();

    public BackgroundImagePanel(String purpose, Color color) {
        super(purpose, color);
    }

    @Override
    public void inputFrameGrabbed(CombinedImage combinedImage) {
        backgroundStabilizerDepthImage.updateUnreadablePixels(combinedImage.getDepthImage());
        this.combinedImage = new CombinedImageImpl(combinedImage.getColorImage(), backgroundStabilizerDepthImage);
    }

    @Override
    protected BufferedImage getColorImage() {
        if (combinedImage == null) {
            return null;
        }
        return depthImageTransformer.createColorImage(backgroundStabilizerDepthImage);
    }
}
