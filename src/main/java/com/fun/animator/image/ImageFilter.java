package com.fun.animator.image;

import java.awt.image.BufferedImage;

import com.fun.animator.image.Image;

public interface ImageFilter {

    public BufferedImage filterImage(Image originalImage, Image backgroundDepthImage, boolean depthImage);
}
