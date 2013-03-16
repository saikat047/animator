package com.fun.animator.image;

import java.awt.image.BufferedImage;

public interface DepthImageTransformer {

    static final int DIFF_CENTIMETER = 0x80;
    // minimum depth: in 100 cm
    static final int DEPTH_MIN = 0x00003200;
    // maximum depth: 511 cm
    static final int DEPTH_MAX = 0x0000FFFF;

    BufferedImage createColorImage(DepthImage depthImage);
}
