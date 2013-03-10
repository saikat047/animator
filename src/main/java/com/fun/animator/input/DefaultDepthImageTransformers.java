package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultDepthImageTransformers implements DepthImageTransformer {

    @Override
    public BufferedImage convertDepthImage(Image image) {
        // TODO saikat: Move the max/min depth calculation out of this method
        //              and accept these as parameters
        final int minDepth =image.getMinDepth();
        final int maxDepth =image.getMaxDepth();
        final double maxDepthForColor = minDepth + 0.85 * (maxDepth - minDepth);
        System.out.println(String.format("minDepth: %d, maxDepth: %d, maxDepthForColor: %d",
                                         minDepth, maxDepth, (long) maxDepthForColor));
        // WARNING: THE ADDITION OF THE TWO INDIVIDUAL COLOR COMPONENTS BELOW MUST NEVER
        //          BE HIGHER THAN 255 WHICH IS THE MAXIMUM VALUE ALLOWED ON A SINGLE
        //          COLOR COMPONENT.
        final Color minColorForScalingZone = new Color(75, 75, 75);
        final Color scalingColorForScalingZone = new Color(50, 180, 0);

        BufferedImage transformedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                final int depthValue = image.getDepth(x, y);
                final int color;
                if (depthValue > maxDepthForColor) {
                    final double depthRatio = 1 - (depthValue - maxDepthForColor) / (maxDepth - maxDepthForColor);
                    color = new Color((int) (depthRatio * minColorForScalingZone.getRed()),
                                      (int) (depthRatio * minColorForScalingZone.getGreen()),
                                      (int) (depthRatio * minColorForScalingZone.getBlue())).getRGB();
                } else {
                    double depthRatio = 1 - (depthValue - minDepth) / (maxDepthForColor - minDepth);
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
