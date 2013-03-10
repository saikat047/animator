package com.fun.animator.input;

import java.awt.image.BufferedImage;

public interface Image {

    int getDepth(int x, int y);

    int getMaxDepth();

    int getMinDepth();

    int getWidth();

    int getHeight();

    int getColorImageType();

    BufferedImage getColorImage();

    BufferedImage getDepthImage();

    Image deepCopy();
}
