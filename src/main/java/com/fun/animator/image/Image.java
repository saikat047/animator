package com.fun.animator.image;

import java.awt.image.BufferedImage;

public interface Image {

    // Kinect gives a maximum of 11 bit value.
    public final int DEPTH_MASK = 0x0000FFFF;

    int getDepth(int x, int y);

    int getWidth();

    int getHeight();

    int getColorImageType();

    BufferedImage getColorImage();

    BufferedImage getDepthImage();

    Image deepCopy();
}
