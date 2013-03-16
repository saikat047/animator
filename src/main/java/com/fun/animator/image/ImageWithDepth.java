package com.fun.animator.image;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core;

public class ImageWithDepth implements Image {

    private final BufferedImage colorImage;
    private final BufferedImage depthImage;
    private final int width;
    private final int height;

    public ImageWithDepth(opencv_core.IplImage colorImage, double gamma, opencv_core.IplImage depthImage) {
        this(colorImage.getBufferedImage(gamma), depthImage == null ? null : depthImage.getBufferedImage());
    }

    public ImageWithDepth(BufferedImage colorImage, BufferedImage depthImage) {
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
        return new ImageWithDepth(Images.cloneImage(colorImage), depthImage == null ? null : Images.cloneImage(depthImage));
    }
}
