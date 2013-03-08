package com.fun.animator.input;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core;

public class ImageWithDepth implements Image {

    private static final long INFINITY_DEPTH_VALUE = 0x00000000FFFFFFFFL;

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
    public long getDepth(int x, int y) {
        return depthImage == null? 0 : depthImage.getRGB(x, y) & INFINITY_DEPTH_VALUE;
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
