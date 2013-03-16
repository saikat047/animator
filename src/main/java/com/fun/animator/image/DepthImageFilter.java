package com.fun.animator.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DepthImageFilter implements ImageFilter {

    private int minDiffWithBackGroundInCM = 1;

    @Override
    public BufferedImage filterColorBasedOnBackground(CombinedImage sourceImage, DepthImage backgroundDepthImage) {
        final int minDiffInKinectUnits = minDiffWithBackGroundInCM * DepthImageTransformer.DIFF_CENTIMETER;
        final BufferedImage sourceColorImage = sourceImage.getColorImage();
        final DepthImage sourceDepthImage = sourceImage.getDepthImage();
        final BufferedImage resultImage = new BufferedImage(sourceColorImage.getWidth(),
                                                            sourceColorImage.getHeight(),
                                                            sourceColorImage.getType());
        for (int x = 0; x < sourceColorImage.getWidth(); x++) {
            for (int y = 0; y < sourceColorImage.getHeight(); y++) {
                final int originalDepth = sourceDepthImage.getDepth(x, y);
                final int backgroundDepth = backgroundDepthImage.getDepth(x, y);
                if (originalDepth == DepthImageTransformer.DEPTH_MAX) {
                    resultImage.setRGB(x, y, Color.BLACK.getRGB());
                } else if ((originalDepth > DepthImageTransformer.DEPTH_MIN && originalDepth < DepthImageTransformer.DEPTH_MAX)
                           && backgroundDepth - originalDepth > minDiffInKinectUnits) {
                    resultImage.setRGB(x, y, sourceColorImage.getRGB(x, y));
                } else {
                    resultImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return resultImage;
    }

    public int getMinDiffWithBackGroundInCM() {
        return minDiffWithBackGroundInCM;
    }

    public void setMinDiffWithBackGroundInCM(int minDiffWithBackGroundInCM) {
        this.minDiffWithBackGroundInCM = minDiffWithBackGroundInCM;
    }
}
