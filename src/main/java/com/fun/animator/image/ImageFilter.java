package com.fun.animator.image;

public interface ImageFilter {

    public DepthImage filterDepthsInBackground(DepthImage sourceImage, DepthImage backgroundDepthImage);
}
