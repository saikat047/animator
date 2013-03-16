package com.fun.animator.image;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CompositeDepthImage implements DepthImage {

    private final List<DepthImage> depthImages;
    private final int numOfImages;
    private final Lock imageListLock = new ReentrantLock();

    public CompositeDepthImage(final int numOfImages) {
        this.numOfImages = numOfImages;
        depthImages = new LinkedList<DepthImage>();
    }

    public void add(DepthImage depthImage) {
        imageListLock.lock();
        try {
            depthImages.add(0, depthImage);
            if (depthImages.size() > numOfImages) {
                depthImages.remove(numOfImages);
            }
        } finally {
            imageListLock.unlock();
        }
    }

    public boolean isEmpty() {
        return depthImages.isEmpty();
    }

    @Override
    public int getDepth(int x, int y) {
        final int depth = depthImages.get(0).getDepth(x, y);
        if (depth < DepthImageTransformer.DEPTH_MAX) {
            return depth;
        }
        for (DepthImage depthImage : depthImages) {
            final int imageDepth = depthImage.getDepth(x, y);
            if (imageDepth < DepthImageTransformer.DEPTH_MAX) {
                return imageDepth;
            }
        }
        return DepthImageTransformer.DEPTH_MAX;
    }

    @Override
    public int getWidth() {
        return depthImages.get(0).getWidth();
    }

    @Override
    public int getHeight() {
        return depthImages.get(0).getHeight();
    }

    @Override
    public void setDepth(int x, int y, int depth) {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public DepthImage createCopy() {
        throw new RuntimeException("Method not implemented");
    }
}
