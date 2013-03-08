package com.fun.animator.input;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefaultDepthImageTransformers implements DepthImageTransformer {

    private static final long INFINITY_DEPTH_VALUE = 0x00000000FFFFFFFFL;

    @Override
    public BufferedImage convertDepthImage(Image image) {
        // TODO saikat: Move the max/min depth calculation out of this method
        //              and accept these as parameters
        MinMaxInfo mmInfo = new MinMaxInfo();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                long depthValue = image.getDepth(x, y);
                if (depthValue != INFINITY_DEPTH_VALUE) {
                    mmInfo.update(depthValue);
                }
            }
        }
        final double maxDepthForColor = mmInfo.minDepth + 0.85 * (mmInfo.maxDepth - mmInfo.minDepth);
        System.out.println(String.format("minDepth: %d, maxDepth: %d, maxDepthForColor: %d",
                                         mmInfo.minDepth, mmInfo.maxDepth, (long) maxDepthForColor));
        // WARNING: THE ADDITION OF THE TWO INDIVIDUAL COLOR COMPONENTS BELOW MUST NEVER
        //          BE HIGHER THAN 255 WHICH IS THE MAXIMUM VALUE ALLOWED ON A SINGLE
        //          COLOR COMPONENT.
        final Color minColorForScalingZone = new Color(75, 75, 75);
        final Color scalingColorForScalingZone = new Color(50, 180, 0);

        BufferedImage transformedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                final long depthValue = image.getDepth(x, y);
                final int color;
                if (INFINITY_DEPTH_VALUE == depthValue) {
                    color = Color.BLACK.getRGB();
                } else if (depthValue > maxDepthForColor) {
                    final double depthRatio = 1 - (depthValue - maxDepthForColor) / (mmInfo.maxDepth - maxDepthForColor);
                    color = new Color((int) (depthRatio * minColorForScalingZone.getRed()),
                                      (int) (depthRatio * minColorForScalingZone.getGreen()),
                                      (int) (depthRatio * minColorForScalingZone.getBlue())).getRGB();
                } else {
                    double depthRatio = 1 - (depthValue - mmInfo.minDepth) / (maxDepthForColor - mmInfo.minDepth);
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

    private class MinMaxInfo {
        long minDepth = Long.MAX_VALUE;
        long maxDepth = 0;

        void update(long value) {
            if (value < minDepth) {
                minDepth = value;
            }
            if (value > maxDepth) {
                maxDepth = value;
            }
        }
    }
}
