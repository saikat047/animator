package com.fun.animator.image;

import java.awt.image.BufferedImage;

public interface DepthImageTransformer {

    static final int DIFF_CENTIMETER = 0xA3;
    static final int DEPTH_MIN = 0x00000000;
    static final int DEPTH_MAX = DepthImage.DEPTH_MASK;

    BufferedImage createColorImage(DepthImage depthImage);
}
