package com.fun.animator.input;

import java.awt.image.BufferedImage;

public interface Image {

    long getDepth(int x, int y);

    int getWidth();

    int getHeight();

    int getColorImageType();

    BufferedImage getColorImage();

    BufferedImage getDepthImage();

    Image deepCopy();
}
