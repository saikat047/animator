package com.fun.animator.image;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core;

public class CombinedImageImpl implements CombinedImage {

    private BufferedImage colorImage;
    private DepthImage depthImage;

    public CombinedImageImpl(opencv_core.IplImage colorImage, opencv_core.IplImage depthImage) {
        this.colorImage = colorImage.getBufferedImage();
        this.depthImage = Images.createDepthImage(depthImage);
    }

    public CombinedImageImpl(BufferedImage colorImage, DepthImage depthImage) {
        this.colorImage = colorImage;
        this.depthImage = depthImage;
    }

    @Override
    public BufferedImage getColorImage() {
        return colorImage;
    }

    public DepthImage getDepthImage() {
        return depthImage;
    }

    @Override
    public CombinedImage deepCopy() {
        return new CombinedImageImpl(Images.createCopy(colorImage), depthImage == null ? null : depthImage.createCopy());
    }
}
