package com.fun.animator.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultDepthImageTransformers implements DepthImageTransformer {

    private static final double DEPTH_MAX_FOR_COLOR = DEPTH_MIN + 0.95 * (DEPTH_MAX - DEPTH_MIN);

    /*
        Set a color of a pixel based on the depth value of it.
        The nearer the pixel is, the brighter the (green) color of it.
        The farther the pixel is, the darker the (green) color of it.
     */
    @Override
    public BufferedImage createColorImage(DepthImage depthImage) {
        // WARNING: THE ADDITION OF THE TWO INDIVIDUAL COLOR COMPONENTS BELOW MUST NEVER
        //          BE HIGHER THAN 255 WHICH IS THE MAXIMUM VALUE ALLOWED ON A SINGLE
        //          COLOR COMPONENT.
        final Color minColorForScalingZone = new Color(75, 75, 75);
        final Color scalingColorForScalingZone = new Color(50, 180, 0);

        BufferedImage transformedImage = new BufferedImage(depthImage.getWidth(), depthImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                final int depthValue = depthImage.getDepth(x, y);
                final int color;
                if (depthValue == DepthImageTransformer.DEPTH_MAX) {
                    color = Color.BLACK.getRGB();
                }
                else if (depthValue > DEPTH_MAX_FOR_COLOR) {
                    final double depthRatio = 1 - (depthValue - DEPTH_MAX_FOR_COLOR) / (DEPTH_MAX - DEPTH_MAX_FOR_COLOR);
                    color = new Color((int) (depthRatio * minColorForScalingZone.getRed()),
                                      (int) (depthRatio * minColorForScalingZone.getGreen()),
                                      (int) (depthRatio * minColorForScalingZone.getBlue())).getRGB();
                } else {
                    double depthRatio = 1 - (depthValue - DEPTH_MIN) / (DEPTH_MAX_FOR_COLOR - DEPTH_MIN);
                    depthRatio = Math.min(depthRatio, 1.0);
                    color = new Color((int) (minColorForScalingZone.getRed() + depthRatio * scalingColorForScalingZone.getRed()),
                                      (int) (minColorForScalingZone.getGreen() + depthRatio * scalingColorForScalingZone.getGreen()),
                                      (int) (minColorForScalingZone.getBlue() + depthRatio * scalingColorForScalingZone.getBlue()))
                            .getRGB();
                }
                transformedImage.setRGB(x, y, color);
            }
        }
        return transformedImage;
    }
}
