package com.fun.animator.image;

public interface DepthImage {

    int DEPTH_MASK = 0x0000FFFF;

    int getWidth();

    int getHeight();

    int getDepth(int x, int y);

    void setDepth(int x, int y, int depth);

    DepthImage createCopy();
}
