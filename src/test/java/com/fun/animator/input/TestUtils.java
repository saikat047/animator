package com.fun.animator.input;

import java.awt.image.BufferedImage;

import com.fun.animator.image.DepthImage;
import com.fun.animator.image.DepthImageImpl;

public class TestUtils {
    private TestUtils() {}

    public static DepthImage createDepthImage(BufferedImage image) {
        DepthImage depthImage = new DepthImageImpl(image.getWidth(), image.getHeight());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                depthImage.setDepth(x, y, image.getRGB(x, y));
            }
        }
        return depthImage;
    }
}
