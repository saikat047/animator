package com.fun.animator.image;

public class DepthImageImpl implements DepthImage, Cloneable {

    private final int DEPTH_MASK = 0x0000FFFF;

    private int width;
    private int height;
    private int [][] depthValues;

    public DepthImageImpl(int width, int height) {
        this.width = width;
        this.height = height;
        depthValues = new int[width][height];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getDepth(int x, int y) {
        return depthValues[x][y];
    }

    @Override
    public void setDepth(int x, int y, int depth) {
        depthValues[x][y] = depth & DEPTH_MASK;
    }

    public DepthImage createCopy() {
        DepthImageImpl image = new DepthImageImpl(width, height);
        image.depthValues = new int[width][height];
        for (int i = 0; i < width; i++) {
            System.arraycopy(depthValues[i], 0, image.depthValues[i], 0, height);
        }
        return image;
    }
}
