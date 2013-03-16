package com.fun.animator.input;

import java.awt.image.BufferedImage;

public interface ImageFilter {

    public BufferedImage filterImage(Image originalImage, Image backgroundDepthImage, boolean depthImage);
}
