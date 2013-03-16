package com.fun.animator.image;

import java.awt.image.BufferedImage;

public interface ImageFilter {

    public BufferedImage filterColorBasedOnBackground(CombinedImage sourceImage, DepthImage backgroundDepthImage);
}
