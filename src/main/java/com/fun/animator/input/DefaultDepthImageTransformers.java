package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultDepthImageTransformers implements DepthImageTransformer {

    @Override
    public BufferedImage convertDepthImage(BufferedImage depthImage) {
        int maxDepth = Integer.MIN_VALUE;
        int minDepth = Integer.MAX_VALUE;
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                int rgbValue = depthImage.getRGB(x, y);
                if (rgbValue < minDepth) {
                    minDepth = rgbValue;
                }
                if (rgbValue > maxDepth) {
                    maxDepth = rgbValue;
                }
            }
        }
        final double maxDepthForColor = minDepth + 0.85 * (maxDepth - minDepth);
        // WARNING: THE ADDITION OF THE TWO INDIVIDUAL COLOR COMPONENTS BELOW MUST NEVER
        //          BE HIGHER THAN 255 WHICH IS THE MAXIMUM VALUE ALLOWED ON A SINGLE
        //          COLOR COMPONENT.
        final Color minColorForScalingZone = new Color(75, 75, 75);
        final Color scalingColorForScalingZone = new Color(50, 180, 0);

        BufferedImage transformedImage = new BufferedImage(depthImage.getWidth(), depthImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                final int depthValue = depthImage.getRGB(x, y);
                final int color;
                if (depthValue > maxDepthForColor) {
                    final double depthRatio = 1 - (depthValue - maxDepthForColor) / (maxDepth - maxDepthForColor);
                    color = new Color((int) (depthRatio * minColorForScalingZone.getRed()),
                                      (int) (depthRatio * minColorForScalingZone.getGreen()),
                                      (int) (depthRatio * minColorForScalingZone.getBlue())).getRGB();
                } else {
                    final double depthRatio = 1 - (depthValue - minDepth) / (maxDepthForColor - minDepth);
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
