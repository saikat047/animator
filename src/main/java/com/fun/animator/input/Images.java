package com.fun.animator.input;

import java.awt.image.BufferedImage;

public class Images {
    private Images() {}

    public static BufferedImage cloneImage(BufferedImage image) {
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resultImage.setRGB(x, y, image.getRGB(x, y));
            }
        }
        return resultImage;
    }

    public static BufferedImage copyDepthImage(Image image) {
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getColorImageType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resultImage.setRGB(x, y, image.getDepth(x, y));
            }
        }
        return resultImage;
    }
}
