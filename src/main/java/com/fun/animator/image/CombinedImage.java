package com.fun.animator.image;

import java.awt.image.BufferedImage;

public interface CombinedImage {

    BufferedImage getColorImage();

    DepthImage getDepthImage();

    CombinedImage deepCopy();
}
