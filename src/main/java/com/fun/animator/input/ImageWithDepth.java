package com.fun.animator.input;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core;

public class ImageWithDepth implements Image {

    // Kinect gives a maximum of 11 bit value.
    private static final int DEPTH_MASK = 0x0000FFFF;

    private final BufferedImage colorImage;
    private final BufferedImage depthImage;
    private final int width;
    private final int height;

    ImageWithDepth(opencv_core.IplImage colorImage, double gamma, opencv_core.IplImage depthImage) {
        this(colorImage.getBufferedImage(gamma), depthImage == null ? null : depthImage.getBufferedImage());
    }

    protected ImageWithDepth(BufferedImage colorImage, BufferedImage depthImage) {
        this.colorImage = colorImage;
        this.depthImage = depthImage;
        width = colorImage.getWidth();
        height = colorImage.getHeight();
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
}
