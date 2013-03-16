package com.fun.animator.image;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.cpp.opencv_core;

public class Images {

    private Images() {}

    public static BufferedImage createCopy(BufferedImage image) {
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resultImage.setRGB(x, y, image.getRGB(x, y));
            }
        }
        return resultImage;
    }

    public static DepthImage createDepthImage(opencv_core.IplImage image) {
        int width = image.width();
        int height = image.height();
        DepthImage depthImage = new DepthImageImpl(width, height);
        final BufferedImage bufferedImage = image.getBufferedImage();
        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height; y++) {
                depthImage.setDepth(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return depthImage;
    }
}
