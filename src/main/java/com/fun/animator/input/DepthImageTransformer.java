package com.fun.animator.input;

import java.awt.image.BufferedImage;

public interface DepthImageTransformer {

    BufferedImage convertDepthImage(BufferedImage depthImage);
}
