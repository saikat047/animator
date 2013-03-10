package com.fun.animator.input;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core;

public class ImageWithDepth implements Image {

    // Kinect gives a maximum of 11 bit value.
    private static final int DEPTH_MASK = 0x00FFFFFF;

    private final BufferedImage colorImage;
    private final BufferedImage depthImage;
    private final int width;
    private final int height;
    private final int maxDepth;
    private final int minDepth;

    ImageWithDepth(opencv_core.IplImage colorImage, double gamma, opencv_core.IplImage depthImage) {
        this(colorImage.getBufferedImage(gamma), depthImage == null ? null : depthImage.getBufferedImage());
    }

    protected ImageWithDepth(BufferedImage colorImage, BufferedImage depthImage) {
        this.colorImage = colorImage;
        this.depthImage = depthImage;
        width = colorImage.getWidth();
        height = colorImage.getHeight();
        final DepthInfo depthInfo = new DepthInfo();
        if (depthImage != null) {
            for (int x = 0; x < depthImage.getWidth(); x++) {
                for (int y = 0; y < depthImage.getHeight(); y++) {
                    depthInfo.update(depthImage.getRGB(x, y));
                }
            }
            this.minDepth = depthInfo.getMinValue();
            this.maxDepth = depthInfo.getMaxValue();
        } else {
            minDepth = -1;
            maxDepth = -1;
        }
    }

    @Override
    public int getDepth(int x, int y) {
        return depthImage == null? 0 : depthImage.getRGB(x, y) & DEPTH_MASK;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int getColorImageType() {
        return colorImage.getType();
    }

    @Override
    public BufferedImage getColorImage() {
        return colorImage;
    }

    @Override
    public BufferedImage getDepthImage() {
        return depthImage;
    }

    @Override
    public Image deepCopy() {
        return new ImageWithDepth(cloneImage(colorImage), depthImage == null ? null : cloneImage(depthImage));
    }

    private BufferedImage cloneImage(BufferedImage image) {
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resultImage.setRGB(x, y, image.getRGB(x, y));
            }
        }
        return resultImage;
    }

    @Override
    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public int getMinDepth() {
        return minDepth;
    }

    private static class DepthInfo {
        private int minValue = Integer.MAX_VALUE;
        private int maxValue = 0;

        public DepthInfo() {
        }

        public void update(int depthValue) {
            final int value = depthValue & DEPTH_MASK;
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }
    }
}
