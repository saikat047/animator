package com.fun.animator.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DepthImageIO {
    public static void writeToFile(DepthImage depthImage, File outputFile) {
        BufferedImage image = new BufferedImage(depthImage.getWidth(), depthImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                image.setRGB(x, y, depthImage.getDepth(x, y));
            }
        }
        try {
            ImageIO.write(image, "PNG", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DepthImage readFromFile(File file) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DepthImageImpl depthImage = new DepthImageImpl(image.getWidth(), image.getHeight());
        for (int x = 0; x < depthImage.getWidth(); x++) {
            for (int y = 0; y < depthImage.getHeight(); y++) {
                depthImage.setDepth(x, y, image.getRGB(x, y));
            }
        }
        return depthImage;
    }
}
