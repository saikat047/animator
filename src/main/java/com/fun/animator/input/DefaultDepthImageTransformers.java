package com.fun.animator.input;

import java.awt.image.BufferedImage;

public class DefaultDepthImageTransformers implements DepthImageTransformer {

    @Override
    public BufferedImage convertDepthImage(BufferedImage depthImage) {
        return depthImage;
    }
}
