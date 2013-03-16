package com.fun.animator.image;

public interface ImageFilter {

    public DepthImage filterColorBasedOnBackground(DepthImage sourceImage, DepthImage backgroundDepthImage);
}
