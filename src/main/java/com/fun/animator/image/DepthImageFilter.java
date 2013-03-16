package com.fun.animator.image;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.fun.animator.image.DepthImageTransformer;
import com.fun.animator.image.ImageFilter;

public class DepthImageFilter implements ImageFilter {

    private int minDiffWithBackGroundInCM = 1;

    @Override
    public BufferedImage filterImage(com.fun.animator.image.Image originalImage, com.fun.animator.image.Image backgroundDepthImage, boolean depthImage) {
        final int minDiffInKinectUnits = minDiffWithBackGroundInCM * DepthImageTransformer.DIFF_CENTIMETER;
        final BufferedImage resultImage = new BufferedImage(originalImage.getWidth(),
                                                      originalImage.getHeight(),
                                                      originalImage.getColorImageType());
        final BufferedImage originalColorImage = originalImage.getColorImage();
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                final int originalDepth = originalImage.getDepth(x, y) & com.fun.animator.image.Image.DEPTH_MASK;
                final int backgroundDepth = backgroundDepthImage.getDepth(x, y) & com.fun.animator.image.Image.DEPTH_MASK;
                if (originalDepth == DepthImageTransformer.DEPTH_MAX) {
                    resultImage.setRGB(x, y, Color.BLACK.getRGB());
                } else if ((originalDepth > DepthImageTransformer.DEPTH_MIN && originalDepth < DepthImageTransformer.DEPTH_MAX)
                           && backgroundDepth - originalDepth > minDiffInKinectUnits) {
                    resultImage.setRGB(x, y, originalColorImage.getRGB(x, y));
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