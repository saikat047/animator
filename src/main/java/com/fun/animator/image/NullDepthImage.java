package com.fun.animator.image;

public class NullDepthImage implements DepthImage {
    @Override
    public int getWidth() {
        return 640;
    }

    @Override
    public int getHeight() {
        return 480;
    }

    @Override
    public int getDepth(int x, int y) {
        return DepthImageTransformer.DEPTH_MAX;
    }

    @Override
    public void setDepth(int x, int y, int depth) {
    }

    @Override
    public DepthImage createCopy() {
        throw new RuntimeException("Method not implemented!");
    }
}
